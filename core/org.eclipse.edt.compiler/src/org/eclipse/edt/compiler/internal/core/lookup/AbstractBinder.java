/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.BytesLiteral;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PrimitiveTypeLiteral;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author winghong
 */
public abstract class AbstractBinder extends AbstractASTVisitor {
    
    protected Scope currentScope;
    protected IDependencyRequestor dependencyRequestor;
    protected Part currentBinding;
    protected String packageName;
    protected ICompilerOptions compilerOptions;
    protected boolean bindingCallTarget;
        
    public AbstractBinder(Scope currentScope, String packageName, IDependencyRequestor dependencyRequestor, ICompilerOptions compilerOptions) {
        this.currentScope = currentScope;
        this.packageName = packageName;
        this.currentBinding = null;
        this.dependencyRequestor = dependencyRequestor;
        this.compilerOptions = compilerOptions;
    }

    public AbstractBinder(Scope currentScope, Part currentBinding, IDependencyRequestor dependencyRequestor, ICompilerOptions compilerOptions) {
        this.currentScope = currentScope;
        this.packageName = currentBinding.getPackageName();
        this.currentBinding = currentBinding;
        this.dependencyRequestor = dependencyRequestor;
        this.compilerOptions = compilerOptions;
    }

    public org.eclipse.edt.mof.egl.Type bindType(Type type) throws ResolutionException {
        switch(type.getKind()) {
            case Type.ARRAYTYPE:
                ArrayType arrayType = (ArrayType) type;
                org.eclipse.edt.mof.egl.Type elementType = bindType(arrayType.getElementType());
                org.eclipse.edt.mof.egl.ArrayType arrayTypeBinding = BindingUtil.getArrayType(elementType, false); //TODO
                arrayType.setType(arrayTypeBinding);
                return arrayTypeBinding;

            case Type.NAMETYPE:
                NameType nameType = (NameType) type;
                //TODO handle parameterized types
                return bindTypeName(nameType.getName());   // No need to set on type as it is delegated
                
            default: throw new RuntimeException("Shouldn't come into here");            
        }
    }
    
    public org.eclipse.edt.mof.egl.Type bindTypeName(Name name) throws ResolutionException {
        org.eclipse.edt.mof.egl.Type result = null;
        if(name.isSimpleName()) {
            List<org.eclipse.edt.mof.egl.Type> types = currentScope.findType(name.getIdentifier());
            
            if (types == null) {
            	result = BindingUtil.findAliasedType(name.getIdentifier());
            }
            else { 
            	if (types.size() > 1) {
                    name.setBindAttempted(true);
                    dependencyRequestor.recordName(name);
                    int[] errorOffsets = getLastIdentifierOffsets(name);
                	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.TYPE_IS_AMBIGUOUS, new String[] {name.getCanonicalName()});
            	}
            	else {
            		result = types.get(0);
            	}
            }
            
        }
        else {
        	QualifiedName qualifiedName = (QualifiedName) name;
            IPackageBinding packageBinding = bindPackageName(qualifiedName.getQualifier());
        
            Part part = BindingUtil.getPart(packageBinding.resolveType(qualifiedName.getIdentifier()));
            
            if(part != null){
                if(BindingUtil.isPrivate(part) && !NameUtile.equals(part.getPackageName(), packageName)){
                    result = null;
                }
                else {
                	result = part;
                }
            }
        }

        if(result == null) {
            name.setBindAttempted(true);
            dependencyRequestor.recordName(name);
            int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.TYPE_CANNOT_BE_RESOLVED, new String[] {name.getCanonicalName()});
        }

        dependencyRequestor.recordType(result);
        name.setType(result);
        return result;
    }
    
    public Object bindExpressionName(Name name) throws ResolutionException {
    	return bindExpressionName(name, false);
    }
    
    public Object bindExpressionName(Name name, boolean isFunctionInvocation) throws ResolutionException {
        List<?> result = null;
        if(name.isSimpleName()) {
            result = currentScope.findMember(name.getIdentifier());
            
            if(result == null) {
            	result = currentScope.findType(name.getIdentifier());
            }
        }
        else {
            QualifiedName qualifiedName = (QualifiedName) name;
            Name qualifier = qualifiedName.getQualifier();
            String identifier = qualifiedName.getIdentifier();
            
            Object binding = bindUnknownName(qualifier);
            	
            if(binding instanceof Member) {            	
                Member member = (Member) binding;
            	org.eclipse.edt.mof.egl.Type type = BindingUtil.getType(member);
                result = findData(type, identifier);
                if (result == null) {
                	Member dynMbr = BindingUtil.createExplicitDynamicAccessMember(type, qualifiedName.getCaseSensitiveIdentifier());
                	if (dynMbr != null) {
                		List<Member> list = new ArrayList<Member>();
                		list.add(dynMbr);
                		result = list;
                	}
                }
            }
            else if(binding instanceof org.eclipse.edt.mof.egl.Type) {
                org.eclipse.edt.mof.egl.Type type = (org.eclipse.edt.mof.egl.Type) binding;
                result = findStaticData(type, identifier);
                
                if(result == null) {
                	try {
                		binding = bindPackageName(qualifier);
                	}
                	catch(ResolutionException e) {
                	}
                }
            }
            if(binding instanceof IPackageBinding) {
                IPackageBinding packageBinding = (IPackageBinding) binding;
                if (identifier != null && identifier.length() > 0){
                	dependencyRequestor.recordSimpleName(identifier);
                }
                
                Part resolvedPart = BindingUtil.getPart(packageBinding.resolveType(identifier));
                if(resolvedPart != null) {
                	if(!NameUtile.equals(resolvedPart.getPackageName(), packageName) && BindingUtil.isPrivate(resolvedPart)){
                		dependencyRequestor.recordName(new QualifiedName((Name)qualifier, name.getIdentifier(), qualifier.getOffset(), name.getOffset() + name.getLength()));
                        result = null;
                	}
                	else {
                		if (resolvedPart instanceof FunctionPart) {
                			dependencyRequestor.recordTopLevelFunction((FunctionPart)resolvedPart);
                		}
                		List<Part> tempList = new ArrayList<Part>();
                		tempList.add(resolvedPart);
                		result = tempList;
                    }
                }else{
                	dependencyRequestor.recordName(new QualifiedName((Name)qualifier, name.getIdentifier(), qualifier.getOffset(), name.getOffset() + name.getLength()));	                	
               	 	result = null;
                }
        	}
        }
                            
        if(result == null){
        	name.setBindAttempted(true);
         	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {name.getCanonicalName()});
        }
        else if(result.size() > 1) {
        	        	
        	if (!isFunctionSet(result) || !isFunctionInvocation) {
        		name.setBindAttempted(true);       		
        		int[] errorOffsets = getLastIdentifierOffsets(name);
        		throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS, new String[] {name.getCanonicalName()});
        	}
        }        
        
        if(result.size() == 1) {
        	Object value = result.get(0);
        	name.setElement(value);
        	org.eclipse.edt.mof.egl.Type type = BindingUtil.getType(value);
        	name.setType(type);
        	if (type != null) {
            	dependencyRequestor.recordType(type);
        	}
        	return value;
        }
        else {
            name.setElement(result);
            return result;
        }
    }
    
    private int[] getLastIdentifierOffsets(Name name) {
		if(name.isQualifiedName()) {
			String id = name.getIdentifier();
			int endOffset = name.getOffset()+name.getLength();
			return new int[] {endOffset-id.length(), endOffset};
		}
		return new int[] {name.getOffset(), name.getOffset() + name.getLength()};
	}

	private List<Member> findStaticData(org.eclipse.edt.mof.egl.Type type, String identifier) {
		
		List <Member> allMbrs = findData(type, identifier);
		if (allMbrs == null) {
			return null;
		}
		
		//when binding a call target, allow references to non-static members
		if (bindingCallTarget) {
			return allMbrs;
		}
		
		List <Member> staticMbrs = new ArrayList<Member>();
		
		for (Member mbr : allMbrs) {
			if (mbr.isStatic()) {
				staticMbrs.add(mbr);
			}
		}
		
		if (staticMbrs.isEmpty()) {
			return null;
		}
		return staticMbrs;
	}
    
	public IPackageBinding bindPackageName(Name name) throws ResolutionException {
		IPackageBinding result;
        if(name.isSimpleName()) {
            result = currentScope.findPackage(name.getIdentifier());
        }
        else {
        	QualifiedName qualifiedName = (QualifiedName) name;
        	IPackageBinding packageBinding = bindPackageName(qualifiedName.getQualifier());
           
            result = packageBinding.resolvePackage(qualifiedName.getIdentifier());
        }
        
        if(result == null){
        	dependencyRequestor.recordName(name);
        	//TODO: create and use message that says name can't be resolved to package
        	throw new ResolutionException(name, IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {name.getCanonicalName()});
        }
        
        name.setElement(result);
        dependencyRequestor.recordPackageBinding(result);
        
        return result;
    } 
        
    private Object bindUnknownName(Name name) throws ResolutionException {
        String identifier = name.getIdentifier();
        List<?> result = null;
        
        if(name.isSimpleName()) {
            // Find variable first
            List<Member> mbrs = currentScope.findMember(identifier);  
            result = mbrs;
            
            if(mbrs != null && mbrs.size() == 1) {
            	Member mbr = mbrs.get(0);       		
        		org.eclipse.edt.mof.egl.Type type = BindingUtil.getType(mbr);
        	    name.setType(type);
        	    if(type != null) {
        	    	dependencyRequestor.recordType(type);
        	    }
        	}
            
            // Then find type
            if(result == null) {  
            	List<org.eclipse.edt.mof.egl.Type> types = currentScope.findType(identifier);           	
                result = types;
                if(types != null && types.size() == 1) {
                	org.eclipse.edt.mof.egl.Type type = types.get(0);
                	name.setType(type);
                }
            }
            
            // Then find package
            if(result == null) {
                IPackageBinding pkg = currentScope.findPackage(identifier);
                if (pkg != null) {
                	List<IPackageBinding> list =  new ArrayList<IPackageBinding>();
                	list.add(pkg);
                	result = list;
                }
            }
    	}
        else {
        	QualifiedName qualifiedName = (QualifiedName) name;
            Name qualifier = qualifiedName.getQualifier();
            
            Object object = bindUnknownName(qualifier);
		            
            if(object == null) {
            	return null;
            }
        
            if(object instanceof Member) {
                Member member = (Member) object;
                
            	org.eclipse.edt.mof.egl.Type type = BindingUtil.getType(member);
                result = findData(type, identifier);
                if (result == null) {
                	Member dynMbr = BindingUtil.createExplicitDynamicAccessMember(type, qualifiedName.getCaseSensitiveIdentifier());
                	if (dynMbr != null) {
                		List<Member> list = new ArrayList<Member>();
                		list.add(dynMbr);
                		result = list;
                	}
                }

            }
            else if(object instanceof org.eclipse.edt.mof.egl.Type) {
                org.eclipse.edt.mof.egl.Type type = (org.eclipse.edt.mof.egl.Type) object;
                result = findStaticData(type, identifier);
                
                if(result == null) {
                	try {
                		object = bindPackageName(qualifier);
                	}
                	catch(ResolutionException e) {
                	}
                }
            }
            
            if(object instanceof IPackageBinding) {
                IPackageBinding packageBinding = (IPackageBinding) object; 
                
                // Find type within the package first
                Part part = BindingUtil.getPart(packageBinding.resolveType(identifier));
                
                if(part != null){
	                if(((IPartBinding)result).isPrivate() && (!NameUtile.equals(((IPartBinding)result).getPackageName(), packageName))){
	                    result = null;
	                }
	                else {
	                	name.setType(part); 
	                	List<Part> list = new ArrayList<Part>();
	                	list.add(part);
	                	result = list;
	                }
	            }
                
                // Then find package
                if(result == null) {
                	
                    IPackageBinding pkg = packageBinding.resolvePackage(identifier);
                    if (pkg != null) {
                    	List<IPackageBinding> list =  new ArrayList<IPackageBinding>();
                    	list.add(pkg);
                    	result = list;
                    }
                }
            }
        }
        
        if(result == null){
        	name.setBindAttempted(true);
        	dependencyRequestor.recordName(name);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {name.getCanonicalName()});
        }
        else if(result.size() > 1){
        	name.setBindAttempted(true);
        	dependencyRequestor.recordName(name);
        	int[] errorOffsets = getLastIdentifierOffsets(name);
        	throw new ResolutionException(errorOffsets[0], errorOffsets[1], IProblemRequestor.TYPE_IS_AMBIGUOUS, new String[] {name.getCanonicalName()});
        }
        
        
    	Object value = result.get(0);
    	name.setElement(value);
    	org.eclipse.edt.mof.egl.Type type = BindingUtil.getType(value);
    	name.setType(type);
    	if (type != null) {
            dependencyRequestor.recordType(type);
    	}
        return value;
    }
    
    public void bindInvocationTarget(Expression invocationTarget) {
    	final IASTVisitor thisVisitor = this;
    	invocationTarget.accept(new AbstractASTExpressionVisitor() {
    		public void endVisit(StringLiteral stringLiteral) {
    			bindFromString(stringLiteral, stringLiteral.getValue());
    		}
    		
    		private void bindFromString(Expression expr, String value) {
    			org.eclipse.edt.mof.egl.Type result = null;
    			Name name = new ExpressionParser(compilerOptions).parseAsName(value);
    			if(name != null) {
	    			try {
	    				result = bindTypeName(name);
				        if(result != null) {
				        	dependencyRequestor.recordType(result);
				        }
	    			}
	    			catch(ResolutionException e) {
	    				//ignore
	    			}
    			}
    			expr.setType(result);
			}

			public void endVisitExpression(Expression expression) {
    			expression.accept(thisVisitor);
    			
    			Member mbr = expression.resolveMember();
    			if(mbr instanceof ConstantField) {
 					bindFromString(expression, ((ConstantField) mbr).getValue().getValue());
    			}   			
    		}
    	});
    	return;
    }
    
        

	protected List<Member> findData(org.eclipse.edt.mof.egl.Type type, String identifier) {
		
		type = BindingUtil.getBaseType(type);
		if (type != null && type.equals(currentBinding)) {
			return BindingUtil.findMembers(type, identifier);
		}
		else {
			return BindingUtil.findPublicMembers(type, identifier);
		}
    }
	
    protected static PrimitiveTypeLiteral getConstantValue(Expression expr) {
    	ConstantValueCreator constValueCreator = new ConstantValueCreator();
		expr.accept(constValueCreator);
		return constValueCreator.constantValue;
    }
    
    private static class ConstantValueCreator extends DefaultASTVisitor {
    	PrimitiveTypeLiteral constantValue;
    	boolean isNegative = false;
    	
		public ConstantValueCreator() {
		}
    	
    	public boolean visit(IntegerLiteral integerLiteral) {
    		String str = integerLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.IntegerLiteral lit = IrFactory.INSTANCE.createIntegerLiteral();
    		lit.setIsNegated(isNegative);
    		lit.setValue(str);
    		org.eclipse.edt.mof.egl.Type type;
    		switch (integerLiteral.getLiteralKind()) {
			case LiteralExpression.BIGINT_LITERAL:
    			type = TypeUtils.Type_BIGINT;
				break;
			case LiteralExpression.INTEGER_LITERAL:
    			type = TypeUtils.Type_INT;
				break;
			case LiteralExpression.SMALLINT_LITERAL:
    			type = TypeUtils.Type_SMALLINT;
				break;
			default:
    			type = TypeUtils.Type_INT;
			}
    		lit.setType(type);
    		
			return false;
		}
    	
		public boolean visit(FloatLiteral floatLiteral) {
    		String str = floatLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.FloatingPointLiteral lit = IrFactory.INSTANCE.createFloatingPointLiteral();
    		lit.setIsNegated(isNegative);
    		lit.setValue(str);
    		org.eclipse.edt.mof.egl.Type type;
    		switch (floatLiteral.getLiteralKind()) {
			case LiteralExpression.FLOAT_LITERAL:
    			type = TypeUtils.Type_FLOAT;
				break;
			case LiteralExpression.SMALLFLOAT_LITERAL:
    			type = TypeUtils.Type_SMALLFLOAT;
				break;
			default:
    			type = TypeUtils.Type_FLOAT;
			}

    		lit.setType(type);
    		constantValue = lit;
    		
			return false;
		}
		
		public boolean visit(DecimalLiteral decimalLiteral) {
    		String str = decimalLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.DecimalLiteral lit = IrFactory.INSTANCE.createDecimalLiteral();
    		lit.setIsNegated(isNegative);
    		lit.setValue(str);
    		constantValue = lit;
    		
			return false;
		}
		
		public boolean visit(StringLiteral stringLiteral) {
    		String str = stringLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.StringLiteral lit = IrFactory.INSTANCE.createStringLiteral();
    		lit.setValue(str);
    		lit.setIsHex(stringLiteral.isHex());
    		constantValue = lit;
    		
			return false;
		}
		
		public boolean visit(HexLiteral stringLiteral) {
    		String str = stringLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.HexLiteral lit = IrFactory.INSTANCE.createHexLiteral();
    		lit.setValue(str);
    		constantValue = lit;
    		
			return false;
		}
		
		public boolean visit(CharLiteral stringLiteral) {
    		String str = stringLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.CharLiteral lit = IrFactory.INSTANCE.createCharLiteral();
    		lit.setValue(str);
    		lit.setIsHex(stringLiteral.isHex());
    		constantValue = lit;
    		
			return false;
		}
		
		public boolean visit(DBCharLiteral stringLiteral) {
    		String str = stringLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.DBCharLiteral lit = IrFactory.INSTANCE.createDBCharLiteral();
    		lit.setValue(str);
    		lit.setIsHex(stringLiteral.isHex());
    		constantValue = lit;
    		
			return false;
		}
		
		public boolean visit(MBCharLiteral stringLiteral) {
    		String str = stringLiteral.getValue();
    		
    		org.eclipse.edt.mof.egl.MBCharLiteral lit = IrFactory.INSTANCE.createMBCharLiteral();
    		lit.setValue(str);
    		lit.setIsHex(stringLiteral.isHex());
    		constantValue = lit;
    		
			return false;
		}
		
		public boolean visit(org.eclipse.edt.compiler.core.ast.BooleanLiteral booleanLiteral) {
			BooleanLiteral lit = IrFactory.INSTANCE.createBooleanLiteral();
			lit.setBooleanValue(booleanLiteral.booleanValue().booleanValue());
			constantValue = lit;
			return false;
		}
		
		public boolean visit(org.eclipse.edt.compiler.core.ast.BytesLiteral bytesLiteral) {
    		String str = bytesLiteral.getValue();

    		BytesLiteral lit = IrFactory.INSTANCE.createBytesLiteral();
    		lit.setValue(str);
			constantValue = lit;
			return false;
		};

		public boolean visit(UnaryExpression unaryExpression) {
			if(unaryExpression.getOperator() == UnaryExpression.Operator.MINUS) {
				isNegative = !isNegative;
			}
			return true;
		}
		
		//TODO handle array literals when mof constant fields support them
    }
    
    protected static void setBindAttemptedForNames(Node node) {
    	node.accept(new AbstractASTVisitor() {
    		public void endVisit(SimpleName simpleName) {
				simpleName.setBindAttempted(true);
			}
			public void endVisit(QualifiedName qualifiedName) {
				qualifiedName.setBindAttempted(true);
			}
    	});
    }
    
        
    protected void processSettingsBlock(ClassDataDeclaration classDataDeclaration, Part functionContainerBinding, Scope functionContainerScope, IProblemRequestor problemRequestor) {
        
        boolean annotationFoundUsingScope = false;
        for (Name name : classDataDeclaration.getNames()) {
             if (classDataDeclaration.hasSettingsBlock()) {
                if (name.resolveMember() != null) {
                    Member member = name.resolveMember();
                    Scope scope = new MemberScope(currentScope, member);
                    AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(scope, member, member.getType(), member);
                    annotationScope.setAnnotationFoundUsingThisScope(annotationFoundUsingScope);
                    Scope fcScope = functionContainerScope;
                    classDataDeclaration.getSettingsBlockOpt().accept(
                            new SettingsBlockAnnotationBindingsCompletor(fcScope, functionContainerBinding, annotationScope, dependencyRequestor,
                                    problemRequestor, compilerOptions));
                    annotationFoundUsingScope = annotationScope.isAnnotationFoundUsingThisScope();
                }
            }
        }
    }
    
    public boolean isFunctionSet(List<?> list) {
    	if (list == null) {
    		return false;
    	}
    	
    	for (Object obj : list) {
    		if (!(obj instanceof Function)) {
    			return false;
    		}
    	}
    	return true;
    }
    
}
