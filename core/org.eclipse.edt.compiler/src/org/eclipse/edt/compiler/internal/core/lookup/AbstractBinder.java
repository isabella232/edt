/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import org.eclipse.edt.compiler.binding.SettingsBlockAnnotationBindingsCompletor;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.core.ast.UnaryExpression.Operator;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.utils.ExpressionParser;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Part;
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
    	return bindType(type, false);
    }

    public org.eclipse.edt.mof.egl.Type bindType(Type type, boolean bindingNewExpr) throws ResolutionException {
        switch(type.getKind()) {
            case Type.ARRAYTYPE:
                ArrayType arrayType = (ArrayType) type;
                org.eclipse.edt.mof.egl.Type elementType = bindType(arrayType.getElementType());
                org.eclipse.edt.mof.egl.ArrayType arrayTypeBinding = BindingUtil.getArrayType(elementType, arrayType.isNullable()); //TODO
                arrayType.setType(arrayTypeBinding);
                return arrayTypeBinding;

            case Type.NAMETYPE:
                NameType nameType = (NameType) type;
                org.eclipse.edt.mof.egl.Type result =  bindTypeName(nameType.getName());   // No need to set on type as it is delegated
                if (result != null && nameType.getArguments() != null && nameType.getArguments().size() > 0) {
                	org.eclipse.edt.mof.egl.Type parmType = getParameterizedType(nameType, bindingNewExpr);
                	if (parmType != result) {
                		result = parmType;
                		nameType.getName().setType(result);
                	}
                }
                return result;
                
            default: throw new RuntimeException("Shouldn't come into here");            
        }
    }
    
    private org.eclipse.edt.mof.egl.Type getParameterizedType(NameType nameType, boolean bindingNewExpr) throws ResolutionException {
    	org.eclipse.edt.mof.egl.Type baseType = nameType.resolveType();
    	
    	if (!BindingUtil.isParameterizableType(baseType)) {    		
    		if (bindingNewExpr) {
    			return baseType;
    		}
    		else {
            	throw new ResolutionException(nameType.getName().getOffset(), nameType.getName().getOffset() + nameType.getName().getLength(), IProblemRequestor.TYPE_IS_NOT_PARAMETERIZABLE, new String[] {nameType.getName().getCanonicalName()});
    		}
    	}
    	
    	ParameterizableType parmableType = (ParameterizableType) baseType;
   	
    	
		EClass parameterizedTypeClass = parmableType.getParameterizedType();
		if (parameterizedTypeClass != null) {
			ParameterizedType type = (ParameterizedType)parameterizedTypeClass.newInstance();
			List<String> args = getTypeArguments(nameType);
			if (args.size() > type.getMaxNumberOfParms() || args.size() < type.getMinNumberOfParms()) {
            	throw new ResolutionException(nameType.getOffset(), nameType.getOffset() + nameType.getLength(), IProblemRequestor.TYPE_ARGS_INVALID_SIZE, new String[] {nameType.getName().getCanonicalName()});
			}			
			type.setParameterizableType(parmableType);
			
			int argIndex = 0;
			for (String arg : args) {
				int fieldIndex = parameterizedTypeClass.getAllEFields().size() - type.getMaxNumberOfParms() + argIndex;
				try {
					EField field = parameterizedTypeClass.getAllEFields().get(fieldIndex);
					Object converted = MofFactory.INSTANCE.createFromString((EDataType)field.getEType(), arg);
					type.eSet(field, converted);
				} catch (Exception e) {
					Expression expr = nameType.getArguments().get(argIndex);
	            	throw new ResolutionException(expr.getOffset(), expr.getOffset() + expr.getLength(), IProblemRequestor.TYPE_ARG_NOT_VALID, new String[] {expr.getCanonicalString(), nameType.getName().getCanonicalName()});
				}
				argIndex = argIndex + 1;
			}	
			return type;
		}

    	return baseType; 
    }
    
    private List<String> getTypeArguments(NameType nameType) throws ResolutionException {
    	List<Expression> args = nameType.getArguments();
    	List<String> list = new ArrayList<String>();
    	for (Expression expr : args) {
    		final boolean[] isValid = new boolean[1];
    		final String[] value = new String[1];
    		final boolean[] negative = new boolean[1];
    		expr.accept(new DefaultASTVisitor() {
    			public boolean visit(IntegerLiteral integerLiteral) {
    				isValid[0] = true;
    				value [0] = integerLiteral.getValue();
    				return false;
    			}
    			public boolean visit(StringLiteral stringLiteral) {
    				isValid[0] = true;
    				value [0] = stringLiteral.getValue();
    				return false;
    			}
    			
    			public boolean visit(org.eclipse.edt.compiler.core.ast.BooleanLiteral booleanLiteral) {
    				isValid[0] = true;
    				value [0] = booleanLiteral.getValue();
    				return false;
    			}
    			
    			public boolean visit(DecimalLiteral decimalLiteral) {
    				isValid[0] = true;
    				value [0] = decimalLiteral.getValue();
    				return false;
    			}
    			
    			public boolean visit(FloatLiteral floatLiteral) {
    				isValid[0] = true;
    				value [0] = floatLiteral.getValue();
    				return false;
    			}
    			
    			public boolean visit(UnaryExpression unaryExpression) {
    				if (unaryExpression.getOperator() == Operator.MINUS) {
    					negative[0] = !negative[0];
    				}
    				else {
    					if (unaryExpression.getOperator() != Operator.PLUS) {
    						isValid[0] = false;
    						return false;
    					}
    				}
    				return true;
    					
    			}
			});
    		
    		if (!isValid[0]) {
            	throw new ResolutionException(expr.getOffset(), expr.getOffset() + expr.getLength(), IProblemRequestor.TYPE_ARG_NOT_VALID, new String[] {expr.getCanonicalString(), nameType.getName().getCanonicalName()});
    		}
    		if (negative[0]) {
    			value[0] = "-" + value[0];
    		}
    		list.add(value[0]);
    	}
    	
    	return list;
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
    
	private List<Member> removePrivateMembersNotInCurrentPart(List<Member> list) {
		if (list == null || currentBinding == null) {
			return list;
		}
		List<Member> result = new ArrayList<Member>();
		for (Member mbr : list) {
			if (mbr.getAccessKind() == AccessKind.ACC_PRIVATE) {
				if (isContainedBy(mbr, currentBinding)) {
					result.add(mbr);
				}
			}
			else {
				result.add(mbr);
			}
		}
		if (result.size() == 0) {
			return null;
		}
		return result;
	}
	
	private boolean isContainedBy(Member mbr, Element container) {
		if (mbr.getContainer() == container) {
			return true;
		}
		
		if (mbr.getContainer() instanceof org.eclipse.edt.mof.egl.Type && container instanceof org.eclipse.edt.mof.egl.Type) {
			if (((org.eclipse.edt.mof.egl.Type)mbr.getContainer()).equals((org.eclipse.edt.mof.egl.Type)container)) {
				return true;
			}
		}
		
		if (mbr.getContainer() instanceof Member) {
			return isContainedBy((Member)mbr.getContainer(), container);
		}
		
		return false;

	}

    public Object bindExpressionName(Name name, boolean isFunctionInvocation) throws ResolutionException {
        List<?> result = null;
        if(name.isSimpleName()) {
        	List<Member> mbrs = currentScope.findMember(name.getIdentifier());
        	mbrs = removePrivateMembersNotInCurrentPart(mbrs);
        	
            result = mbrs;
            
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
                result = findData(type, identifier); // validation will take care of invalid non-static references
                
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
                result = findData(type, identifier); // validation will take care of invalid non-static references
                
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
	                if((part.getAccessKind() == AccessKind.ACC_PRIVATE) && (!NameUtile.equals(part.getPackageName(), packageName))){
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
    		}
    	});
    	return;
    }
    
        

	protected List<Member> findData(org.eclipse.edt.mof.egl.Type aType, String identifier) {
		
		if (aType == null) {
			return null;
		}
		
		
		List<Member> result = null;
		
		org.eclipse.edt.mof.egl.Type type = aType.getClassifier();
		if (type != null && type.equals(currentBinding)) {
			result = BindingUtil.findMembers(type, identifier);
			result = removePrivateMembersNotInCurrentPart(result);
		}
		else {
			result = BindingUtil.findPublicMembers(type, identifier);
		}
		
		if (result != null) {
			return result;
		}
		
		org.eclipse.edt.mof.egl.Type baseType = BindingUtil.getBaseType(aType);
		if (aType != baseType) {
			return findData(baseType, identifier);
		}
		
		return null;

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
        for (Name name : classDataDeclaration.getNames()) {
             if (classDataDeclaration.hasSettingsBlock()) {
                if (name.resolveMember() != null) {
                    Member member = name.resolveMember();
                    Scope scope = new MemberScope(NullScope.INSTANCE, member);
                    AnnotationLeftHandScope annotationScope = new AnnotationLeftHandScope(scope, member, member.getType(), member);
                    Scope fcScope = functionContainerScope;
                    classDataDeclaration.getSettingsBlockOpt().accept(
                            new SettingsBlockAnnotationBindingsCompletor(fcScope, functionContainerBinding, annotationScope, dependencyRequestor,
                                    problemRequestor, compilerOptions));
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
    
	protected Annotation getAnnotation(AnnotationExpression annotationExpression, IProblemRequestor problemRequestor) {
		
		org.eclipse.edt.mof.egl.Type type = null;
		
		try {
			type = bindTypeName(annotationExpression.getName());
		} catch (ResolutionException e) {
            problemRequestor
            .acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            return null;
		}
		
		if (type == null || !(type instanceof AnnotationType)) {
			problemRequestor.acceptProblem(annotationExpression, IProblemRequestor.NOT_AN_ANNOTATION,
					new String[] { annotationExpression.getCanonicalString() });
			annotationExpression.getName().setType(null);
			return null;
		}
		
		Annotation ann;
		if (annotationExpression.getName().resolveElement() instanceof Annotation) {
			ann = (Annotation)annotationExpression.getName().resolveElement();
		}
		else {
			ann = (Annotation)((AnnotationType)type).newInstance();
			annotationExpression.getName().setElement(ann);
			annotationExpression.setAnnotation(ann);
		}
		annotationExpression.setType(type);
		annotationExpression.getName().setType(type);
		return ann;
		
	}

    
}
