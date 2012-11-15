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

import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.ForwardStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.IsNotExpression;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.MoveStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.ObjectExpression;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.ReturningToNameClause;
import org.eclipse.edt.compiler.core.ast.ReturnsDeclaration;
import org.eclipse.edt.compiler.core.ast.SQLLiteral;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.SuperExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.TypeLiteralExpression;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.SubType;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public abstract class DefaultBinder extends AbstractBinder {
    
    protected IProblemRequestor problemRequestor;
       
	private boolean bindingFunctionInvocationTarget;
    
    public DefaultBinder(Scope currentScope, Part currentBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, currentBinding, dependencyRequestor, compilerOptions);
        this.problemRequestor = problemRequestor;
        this.compilerOptions = compilerOptions;
    }

    /**
     * This method binds names that are expression names
     * Clients must be careful to prevent this method from being called on names that are not expression names
     * e.g. package names, types names
     * Usually the owner of these names have their own visitor and that visit method should return false 
     * to prevent this method from being called
     */
    public boolean visit(QualifiedName qualifiedName) {
    	if(!qualifiedName.isBindAttempted()) {
	        try {        	
	            bindExpressionName(qualifiedName, bindingFunctionInvocationTarget);
	        } catch (ResolutionException e) {
	        	qualifiedName.setBindAttempted(true);
	        	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
	        }
    	}
        return false;
    }
    
    protected void handleNameResolutionException(ResolutionException e) {
    	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    }

    /**
     * This methods binds names that are expression names
     * See method above for more info
     */
    public boolean visit(SimpleName simpleName) {    	
        // In some cases (particular for names of part declarations), the simple name has already been bound
        if(!simpleName.isBindAttempted()) {
            try {
            	bindExpressionName(simpleName, bindingFunctionInvocationTarget);	            	
            } catch (ResolutionException e) {
                handleNameResolutionException(e);
            }
            return false;
        }
        return false;
    }
           
	public boolean visit(FunctionInvocation functionInvocation) {
		bindingFunctionInvocationTarget = true;
		functionInvocation.getTarget().accept(this);
		bindingFunctionInvocationTarget = false;
				
        for(Expression expr : functionInvocation.getArguments()) {
        	expr.accept(this);
        }
		
        return false;
	}
	
	public boolean visit(SuperExpression superExpression) {
		if (superExpression.isBindAttempted()) {
	        return false;
	    }
		
		Scope scopeForThis = currentScope.getScopeForKeywordThis();
		if(scopeForThis instanceof FunctionContainerScope) {
			Part part = ((FunctionContainerScope) scopeForThis).getPart();
			if (part instanceof SubType) {
				SubType sub = (SubType) part;
				if (sub.getSuperTypes().size() > 0) {
					superExpression.setType(sub.getSuperTypes().get(0));
					return false;
				}
			}
		}	
		superExpression.setBindAttempted(true);
		problemRequestor.acceptProblem(superExpression, IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {superExpression.getCanonicalString()});
		return false;
	}
	
	
	public boolean visit(ThisExpression thisExpression) {
	    if (thisExpression.isBindAttempted()) {
	        return false;
	    }
		Scope scopeForThis = currentScope.getScopeForKeywordThis();
		if(scopeForThis instanceof FunctionContainerScope) {
			thisExpression.setType(((FunctionContainerScope) scopeForThis).getPart());
		}
		else {
			thisExpression.setBindAttempted(true);
			problemRequestor.acceptProblem(thisExpression, IProblemRequestor.VARIABLE_NOT_FOUND, new String[] {thisExpression.getCanonicalString()});
		}
		return false;
	}
	
	public boolean visit(TypeLiteralExpression typeLiteralExpression) {
		try {
			bindType(typeLiteralExpression.getType());
		}
		catch(ResolutionException e) {
			handleNameResolutionException(e);
		}
		return false;
	}

	public boolean visit(ArrayAccess arrayAccess) {
				
		return true;
	}
	
    public void endVisit(ArrayAccess arrayAccess) {
        if (arrayAccess.isBindAttempted()) {
            return;
        }
    	org.eclipse.edt.mof.egl.Type type = arrayAccess.getArray().resolveType();
    	if (type == null) {
    		arrayAccess.setBindAttempted(true);
    	}
    	else {
			if (type instanceof org.eclipse.edt.mof.egl.ArrayType) {
				arrayAccess.setType(((org.eclipse.edt.mof.egl.ArrayType)type).getElementType());
			}
    		else {
        		if (BindingUtil.isDynamicallyAccessible(type)) {
        			arrayAccess.setType(BindingUtil.getEAny());
        			arrayAccess.setMember(BindingUtil.createDynamicAccessMember(type, arrayAccess.getIndices().get(0).getCanonicalString()));
        		}
    			else {
    	    		arrayAccess.setBindAttempted(true); 
    			}
    		}
    	}
    }
        
        
	public void endVisit(SubstringAccess substringAccess) {
		org.eclipse.edt.mof.egl.Type type = substringAccess.getPrimary().resolveType();
		if (type == null) {
			substringAccess.setBindAttempted(true);
		}
		else {
    		Operation op = IRUtils.getOperation(type.getClassifier(), "[:");
    		if (op == null) {
    			substringAccess.setBindAttempted(true);
    		}
    		else {
    			substringAccess.setType(op.getType());
    		}
		}
	}
		
     public void endVisit(final FieldAccess fieldAccess) {
        if (fieldAccess.isBindAttempted()) {
            return;
        }
        
        org.eclipse.edt.mof.egl.Type type = fieldAccess.getPrimary().resolveType();
        if (type == null) {
        	fieldAccess.setBindAttempted(true);
        	return;
        }
        Member member = BindingUtil.createExplicitDynamicAccessMember(type, fieldAccess.getCaseSensitiveID());
        if (member != null) {
        	fieldAccess.setMember(member);
        	fieldAccess.setType(member.getType());
        	return;
        }
        
        List<Member> mbrs = findData(type, fieldAccess.getID());
        if (mbrs == null) {
        	int endOffset = fieldAccess.getOffset() + fieldAccess.getLength();
        	problemRequestor.acceptProblem(
        		endOffset - fieldAccess.getID().length(),
        		endOffset,
				IProblemRequestor.VARIABLE_NOT_FOUND,
				new String[] {fieldAccess.getCanonicalString()});
        	fieldAccess.setBindAttempted(true);
        	return;
        }
        if (mbrs.size() > 1) {
        	if (!isFunctionSet(mbrs) || !isFunctionInvocationTarget(fieldAccess)) {
        		problemRequestor.acceptProblem(
	        			fieldAccess,
						IProblemRequestor.VARIABLE_ACCESS_AMBIGUOUS,
						new String[] {fieldAccess.getCanonicalString()});
        		fieldAccess.setBindAttempted(true);
        		return;
        	}
        	fieldAccess.setElement(mbrs);
        	return;
        }
        
        member = mbrs.get(0);
        fieldAccess.setMember(member);
        org.eclipse.edt.mof.egl.Type mbrType = BindingUtil.getType(mbrs.get(0));
        fieldAccess.setType(mbrType);
        dependencyRequestor.recordType(mbrType);
    }
     
    private boolean isFunctionInvocationTarget(Expression expr) {
    	if (expr.getParent() instanceof FunctionInvocation) {
    		return ( ((FunctionInvocation)expr.getParent()).getTarget() == expr );
    	}
    	if (expr.getParent() instanceof ParenthesizedExpression) {
    		return isFunctionInvocationTarget((ParenthesizedExpression)expr.getParent());
    	}
    	return false;
    }
    
    
    public void endVisit(SimpleName simpleName) {
	}
    
    public void endVisit(QualifiedName qualifiedName) {
	}
        	
	public boolean visit(OpenStatement openStatement) {
		return true;
	}
	
	public void endVisit(OpenStatement openStatement) {
	}
	
	public boolean visit(GetByKeyStatement openStatement) {
		return true;
	}
	
	public void endVisit(final GetByKeyStatement getByKeyStatement) {
	}
	
	public void endVisit(final DeleteStatement getByKeyStatement) {
	}
	
	public boolean visit(GetByPositionStatement openStatement) {
		return true;
	}
	
	public void endVisit(final GetByPositionStatement openStatement) {
	}
	
	public void endVisit(final AddStatement openStatement) {
	}
	
	public void endVisit(final ReplaceStatement openStatement) {
	}
	
	public boolean visit(ExecuteStatement openStatement) {
		return true;
	}
	
	public void endVisit(ExecuteStatement openStatement) {
	}
	
	public boolean visit(IntoClause intoClause) {
		return true;		
	}
	
	public void endVisit(IntoClause intoClause) {
	}
	
	public boolean visit(IsAExpression isAExpression) {
		isAExpression.getExpression().accept(this);
		try {
			Type typeAST = isAExpression.getType();
			bindType(typeAST);
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		}
		return false;
	}
	
	public boolean visit(AsExpression asExpression) {
		asExpression.getExpression().accept(this);
		try {
			if(asExpression.hasType()) {
				Type typeAST = asExpression.getType();
				bindType(typeAST);
			}
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		}
		return false;
	}
		
	public static NamedElement getOperandType(Expression expr) {
		if (expr instanceof FunctionInvocation) {
			// Don't return the function, return its type.
			if (expr.resolveType() != null) {
				return expr.resolveType().getClassifier();
			}
			return null;
		}
		
		Object element = expr.resolveElement();
		if (element instanceof Function) {
			return (Function)element;
		}
		else {
			org.eclipse.edt.mof.egl.Type type = expr.resolveType();
			if (type != null) {
				return type.getClassifier();
			}
		}	
		return null;
	}

	public boolean visit(NewExpression newExpression) {
		try {
		    if (!newExpression.isBindAttempted()) {
		        newExpression.setType(bindType(newExpression.getType(), true));	
		    }
		}
		catch(ResolutionException e) {
			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
			newExpression.setBindAttempted(true);
		}
				
		if(newExpression.hasSettingsBlock()) {			
			org.eclipse.edt.mof.egl.Type type = newExpression.resolveType();
		    if (type != null) {		    	
		          TypeScope newScope = new TypeScope(NullScope.INSTANCE, type);
		            newExpression.getSettingsBlock().accept(
		                    new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
		    }
		}
		
		Type type = newExpression.getType();
		while (type != null && type.isArrayType()) {
			ArrayType arrayType = (ArrayType)type;
			
			if (arrayType.hasInitialSize()) {
				arrayType.getInitialSize().accept(this);
			}
			
			type = arrayType.getElementType();
		}
		
		if(newExpression.getType().getBaseType() instanceof NameType) {
			NameType nameType = (NameType)newExpression.getType().getBaseType();
			if (nameType.hasArguments()) {
				for(Expression expr : nameType.getArguments()) {
					expr.accept(this);
				}
			}
		}		
		return false;
	}
		
	public void endVisit(NewExpression newExpression) {
		org.eclipse.edt.mof.egl.Type targetType = newExpression.getType().resolveType();
		if(targetType != null) {
			
			//any arugments for a parameterizable type are part of the type and not constructor arguments
			if(!BindingUtil.isParameterizableType(targetType) && newExpression.getType() instanceof NameType && ((NameType)newExpression.getType()).hasArguments()) {
				
				List<Expression> arguments = ((NameType)newExpression.getType()).getArguments();
				Constructor cons = null;
				if (targetType.getClassifier() instanceof EGLClass) {
					List<Constructor> constructors = getConstructors(targetType);
					((NameType) newExpression.getType()).getName().setAttribute(Name.OVERLOADED_FUNCTION_SET, constructors);
					
					cons = IRUtils.resolveConstructorReferenceFromArgTypes((EGLClass)targetType.getClassifier(), getArgumentTypes(arguments), false);
				}
				if (cons == null) {
					if (arguments.size() > 0) {
						problemRequestor.acceptProblem(
							newExpression.getType(),
							IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
							new String[] {
								newExpression.getType().getCanonicalName()
							});
					}
				}
				else {
					newExpression.setConstructor(cons);
					if (BindingUtil.isPrivate(cons)) {
						if (!targetType.equals(currentScope.getPart())) {
							problemRequestor.acceptProblem(newExpression.getType(),
									IProblemRequestor.PRIVATE_CONSTRUCTOR,
								new String[] {newExpression.getType().getCanonicalName()});
						}
					}											
				}					
			}
		}
	}
	
	private List<NamedElement> getArgumentTypes(List<Expression> functionArguments) {
		List<NamedElement> list = new ArrayList<NamedElement>();
		for (Expression expr : functionArguments) {
			list.add(getOperandType(expr));
		}
		return list;
	}
	
	private List<Constructor> getConstructors(org.eclipse.edt.mof.egl.Type type) {
		if (type instanceof StructPart) {
			return ((StructPart)type).getConstructors();
		}
		return null;
	}

	public boolean visit(CloseStatement closeStatement) {
		//The target might be a resultSetID, so don't issue error
		IProblemRequestor pRequestor = problemRequestor;
		problemRequestor = NullProblemRequestor.getInstance();
		closeStatement.getExpr().accept(this);
		problemRequestor = pRequestor;		
		return false;
	}
	

	public boolean visit(final IsNotExpression isNotExpression) {
		isNotExpression.getFirstExpression().accept(this);
		return false;
	}
	
	public boolean visit(ReturningToNameClause returningToNameClause) {
		try {
			bindTypeName(returningToNameClause.getName());
		}
		catch(ResolutionException e) {
		}

		return false;	
	}
	
	
	public boolean visit(final ForwardStatement forwardStatement) {
		if (forwardStatement.hasForwardTarget()){
			final Expression expr = forwardStatement.getForwardTarget();
			if(forwardStatement.isForwardToURL()) {
				expr.accept(this);
			}
			else {
				bindInvocationTarget(expr);
			}
		}
	
		for(Node arg : forwardStatement.getArguments()) {
			arg.accept(this);
		}
		for(Node opt : forwardStatement.getForwardOptions()) {
			opt.accept(this);
		}
		return false;
	}

	/*
	 * Type inference logic -- code which sets type binding for Expression ASTs
	 */
	
	public void endVisit(ParenthesizedExpression parenthesizedExpression) {
		parenthesizedExpression.setType(parenthesizedExpression.getExpression().resolveType());
	}
	
	public void endVisit(UnaryExpression unaryExpression) {
		Expression operand = unaryExpression.getExpression();
		org.eclipse.edt.mof.egl.Type operandType = operand.resolveType();
		if (operandType == null) {
			unaryExpression.setBindAttempted(true);
		}
		else {
			Operation op = IRUtils.getUnaryOperation(operandType.getClassifier(), unaryExpression.getOperator().toString());
			if (op == null) {
				unaryExpression.setBindAttempted(true);
			}
			else {
				unaryExpression.setType(op.getType());
			}
		}		
	}
	
	public void endVisit(BinaryExpression binaryExpression) {
		Expression operand1 = binaryExpression.getFirstExpression();
		Expression operand2 = binaryExpression.getSecondExpression();
		NamedElement elem1 = getOperandType(operand1);
		NamedElement elem2 = getOperandType(operand2);
		if (elem1 != null && elem2 != null) {
			Operation op = IRUtils.getBinaryOperation(elem1, elem2, binaryExpression.getOperator().toString());
			if (op != null) {
				binaryExpression.setType(op.getType());
			}
		}
	}
	
	public void endVisit(org.eclipse.edt.compiler.core.ast.TernaryExpression ternaryExpression) {
		org.eclipse.edt.mof.egl.Type secondType = ternaryExpression.getSecondExpr().resolveType();
		org.eclipse.edt.mof.egl.Type thirdType = ternaryExpression.getThirdExpr().resolveType();
		if (secondType != null && thirdType != null) {
			ternaryExpression.setType(IRUtils.getCommonSupertype(secondType, thirdType));
		}
		else {
			if((secondType != null || ternaryExpression.getSecondExpr().resolveMember() != null) && (thirdType != null || ternaryExpression.getThirdExpr().resolveMember() != null)) {
				ternaryExpression.setType(TypeUtils.Type_ANY);
			}
			else {
				ternaryExpression.setBindAttempted(true);
			}
		}
	}
	
	public void endVisit(Assignment assignment) {}
	
	public void endVisit(IsAExpression isAExpression) {
		isAExpression.setType(TypeUtils.Type_BOOLEAN);
	}
	
	public void endVisit(final AsExpression asExpression) {
		if(asExpression.hasType()) {
			org.eclipse.edt.mof.egl.Type type = asExpression.getType().resolveType();
			if (type == null) {
				asExpression.setBindAttempted(true);
			}
			else {
				asExpression.setType(type);
			}	
		}
	}
	
	public void endVisit(final IsNotExpression isNotExpression) {
		isNotExpression.setType(TypeUtils.Type_BOOLEAN);
	}
				
	public void endVisit(IntegerLiteral integerLiteral) {
		if(!integerLiteral.isBindAttempted()){
			
			switch (integerLiteral.getLiteralKind()) {
				case LiteralExpression.BIGINT_LITERAL:
					integerLiteral.setType(TypeUtils.Type_BIGINT);
					break;
				case LiteralExpression.SMALLINT_LITERAL:
					integerLiteral.setType(TypeUtils.Type_SMALLINT);
					break;
				default:
					integerLiteral.setType(TypeUtils.Type_INT);
				}
		}
	}
	
	public void endVisit(DecimalLiteral decimalLiteral) {
		if(!decimalLiteral.isBindAttempted()){
			int len = decimalLiteral.getValue().length();
			int dec = len - (decimalLiteral.getValue().indexOf('.') + 1);
			decimalLiteral.setType(IRUtils.getEGLType(MofConversion.Type_Decimal, len-1, dec));
		}
	}
	
	public void endVisit(FloatLiteral floatLiteral) {
		if(!floatLiteral.isBindAttempted()){
			
			if (floatLiteral.getLiteralKind() == LiteralExpression.SMALLFLOAT_LITERAL) {
				floatLiteral.setType(TypeUtils.Type_SMALLFLOAT);
			}
			else {
				floatLiteral.setType(TypeUtils.Type_FLOAT);
			}
		}
	}
	
	public void endVisit(BooleanLiteral booleanLiteral) {
		if(!booleanLiteral.isBindAttempted()){
			booleanLiteral.setType(TypeUtils.Type_BOOLEAN);
		}
	}
	
	public void endVisit(StringLiteral stringLiteral) {
		if(!stringLiteral.isBindAttempted()){
			stringLiteral.setType(IRUtils.getEGLType(MofConversion.Type_String, stringLiteral.getValue().length()));
		}
	}
	
	public void endVisit(org.eclipse.edt.compiler.core.ast.BytesLiteral bytesLiteral) {
		if(!bytesLiteral.isBindAttempted()){
			bytesLiteral.setType(IRUtils.getEGLType(MofConversion.Type_Bytes, bytesLiteral.getValue().length() / 2));
		}
	}
	
	public void endVisit(SQLLiteral sqlLiteral) {
	}
	
	public void endVisit(TypeLiteralExpression typeLiteralExpression) {
	}
		
	public void endVisit(ObjectExpression objExpr) {
		if (!objExpr.isBindAttempted()) {
			objExpr.setType(TypeUtils.Type_ANY);
		}
	}
	
	private org.eclipse.edt.mof.egl.Type getCommonElementType(ArrayLiteral arrayLiteral) {
		if (arrayLiteral.getExpressions().isEmpty()) {
			return null;
		}
		
		org.eclipse.edt.mof.egl.Type elemType = null;
		for (Expression exp : arrayLiteral.getExpressions()) {
			if (exp.resolveMember() instanceof Function) {
				return null;
			}
			org.eclipse.edt.mof.egl.Type type = exp.resolveType();
			if (type == null) {
				return null;
			}
			if (elemType == null) {
				elemType = type;
			}
			else {
				if (!(exp instanceof org.eclipse.edt.mof.egl.NullLiteral) && !elemType.equals(type)) {
					elemType = IRUtils.getCommonSupertype(elemType, type);
					if (elemType == null) {
						return null;
					}
				}
			}
		}
		return elemType;
	}
	
	private boolean containsNull(ArrayLiteral arrayLiteral) {
		for (Expression exp: arrayLiteral.getExpressions()) {
			if (exp instanceof NullLiteral) {
				return true;
			}
		}
		return false;
	}
	
	
	public void endVisit(ArrayLiteral arrayLiteral) {
		if(!arrayLiteral.isBindAttempted()){
			org.eclipse.edt.mof.egl.Type elemType = getCommonElementType(arrayLiteral);
			if (elemType == null) {
				elemType = TypeUtils.Type_NULLTYPE;
			}
			arrayLiteral.setType(BindingUtil.getArrayType(elemType, containsNull(arrayLiteral)));
		}
	}

	public void endVisit(NullLiteral nilLiteral) {
		if (!nilLiteral.isBindAttempted()) {
			nilLiteral.setType(TypeUtils.Type_NULLTYPE);
		}
	}
	
	public void endVisit(AnnotationExpression annotationExpression) {
		annotationExpression.setType(annotationExpression.getName().resolveType());
	}
	
    protected static class SetValuesExpressionCompletor extends DefaultBinder {

        TypeScope leftHandScope;

        public SetValuesExpressionCompletor(Scope currentScope, Part currentBinding, TypeScope leftHandScope,
                IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
            super(currentScope, currentBinding, dependencyRequestor, problemRequestor, compilerOptions);
            this.leftHandScope = leftHandScope;
        }

        public boolean visit(SettingsBlock settingsBlock) {
            return true;
        }

        public boolean visit(Assignment assignment) {
            Scope saved = currentScope;
            currentScope = leftHandScope;
            assignment.getLeftHandSide().accept(this);
            currentScope = saved;
            assignment.getRightHandSide().accept(this);
            return false;
        }

        public boolean visit(SetValuesExpression setValuesExpression) {
            Scope saved = currentScope;
            currentScope = leftHandScope;
            setValuesExpression.getExpression().accept(this);
            currentScope = saved;
            final org.eclipse.edt.mof.egl.Type type = setValuesExpression.getExpression().resolveType();
            if (type != null) {
                TypeScope newScope = new TypeScope(NullScope.INSTANCE, type);
                setValuesExpression.getSettingsBlock().accept(
                        new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
            } else {
            	setBindAttemptedForNames(setValuesExpression.getSettingsBlock());
            }
            return false;
        }

        public boolean visit(SuperExpression superExpression) {
        	//This is not really right, but it is not legal
            superExpression.setType(leftHandScope.getType());
            return false;
        }

        public boolean visit(ThisExpression thisExpression) {
            thisExpression.setType(leftHandScope.getType());
            return false;
        }
    }
	
	public boolean visit(SetValuesExpression setValuesExpression) {
        setValuesExpression.getExpression().accept(this);
        org.eclipse.edt.mof.egl.Type type = setValuesExpression.getExpression().resolveType();
        if (type != null) {
            TypeScope newScope = new TypeScope(NullScope.INSTANCE, type);
            setValuesExpression.getSettingsBlock().accept(
                    new SetValuesExpressionCompletor(currentScope, currentBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions));
        } else if (setValuesExpression.getExpression().resolveElement() != null) {
            setValuesExpression.getSettingsBlock().accept(this);
        }
        
		return true;
	}
	
	public boolean visit(FunctionInvocationStatement functionInvocationStatement) {
		return true;
	}
	
	public void endVisit(FunctionInvocationStatement functionInvocationStatement) {
	}
	
	public void endVisit(FunctionInvocation functionInvocation) {
		
		if (functionInvocation.isBindAttempted()) {
			return;
		}
		
		Object elem = functionInvocation.getTarget().resolveElement();
		if (elem instanceof Function) {
			Function func = (Function)elem;
			functionInvocation.setMember(func);
			functionInvocation.setType(func.getType());
			dependencyRequestor.recordType(func.getType());
			return;
		}
		if (elem instanceof Constructor) {
			Constructor cons = (Constructor)elem;
			functionInvocation.setMember(cons);
			functionInvocation.setType(cons.getType());
			dependencyRequestor.recordType(cons.getType());
			return;
		}
		if (functionInvocation.getTarget().resolveType() instanceof Delegate) {
			Delegate del = (Delegate) functionInvocation.getTarget().resolveType();
			functionInvocation.setElement(del);
			functionInvocation.setType(del.getReturnType());
			dependencyRequestor.recordType(del.getReturnType());
			return;
		}
		
		//Handle this() and super()
		if (functionInvocation.getTarget() instanceof ThisExpression || functionInvocation.getTarget() instanceof SuperExpression) {
			org.eclipse.edt.mof.egl.Type type = functionInvocation.getTarget().resolveType();
			if (type != null) {
				List<Constructor> constructors = getConstructors(type);
				Constructor cons = null;				
				if (constructors != null) {
					functionInvocation.getTarget().setAttributeOnName(Name.OVERLOADED_FUNCTION_SET, cons);
					cons = IRUtils.resolveConstructorReferenceFromArgTypes((EGLClass)type.getClassifier(), getArgumentTypes(functionInvocation.getArguments()), false);
				}
				if (cons == null || (BindingUtil.isPrivate(cons) &&  functionInvocation.getTarget() instanceof SuperExpression)) {
					if (functionInvocation.getArguments().size() > 0) {
						problemRequestor.acceptProblem(
							functionInvocation,
							IProblemRequestor.MATCHING_CONSTRUCTOR_CANNOT_BE_FOUND,
							new String[] {
								BindingUtil.getName(type)
							});
						functionInvocation.setBindAttempted(true);
					}
				}
				else {
					functionInvocation.setMember(cons);
					functionInvocation.setType(cons.getType());
				}
			}
			return;
		}

		//If there is a list, then it must be a list of function. 
		if (elem instanceof List) {
			functionInvocation.getTarget().setAttributeOnName(Name.OVERLOADED_FUNCTION_SET, elem);
			
			@SuppressWarnings("unchecked")
			List<Function> functions = (List<Function>)elem;
			List<Function> functionsWithSameNumberArgs = new ArrayList();
			for (Function function : functions) {
				if (function.getParameters().size() == functionInvocation.getArguments().size()) {
					functionsWithSameNumberArgs.add(function);
				}
			}
			functions = TypeUtils.getBestFitFunctionMember(functionsWithSameNumberArgs, (NamedElement[])getArgumentTypes(functionInvocation.getArguments()).toArray(new NamedElement[0]));
			if (functions.size() == 0) {
        		problemRequestor.acceptProblem(
	        			functionInvocation.getTarget(),
	        			IProblemRequestor.NO_FUNCTIONS_MATCH_ARGUMENTS,
	        			new String[] {
	        				functionInvocation.getTarget().getCanonicalString()
	        			});
				functionInvocation.setBindAttempted(true);
				functionInvocation.getTarget().setMember(null);
				return;
			}
			if (functions.size() > 1) {
        		problemRequestor.acceptProblem(
	        			functionInvocation.getTarget(),
	        			IProblemRequestor.MULTIPLE_OVERLOADED_FUNCTIONS_MATCH_ARGUMENTS,
	        			new String[] {
	        				functionInvocation.getTarget().getCanonicalString()
	        			});
				functionInvocation.setBindAttempted(true);
				functionInvocation.getTarget().setMember(null);
				return;
			}
			
			Function function = functions.get(0);
			functionInvocation.getTarget().setMember(function);
			functionInvocation.setType(function.getType());
			dependencyRequestor.recordType(function.getType());

		}
	}
	
	
	public void endVisit(MoveStatement moveStatement) {}
	
	public void endVisit(final CloseStatement closeStatement) {}

	public void endVisit(SetValuesExpression setValuesExpression) {}
				            
	public void endVisit(ClassDataDeclaration classDataDeclaration) {}
	
	public void endVisit(final FunctionDataDeclaration functionDataDeclaration) {}
	
	public void endVisit(FunctionParameter functionParameter) {}
	
	public void endVisit(ProgramParameter programParameter) {}
	
	public void endVisit(StructureItem structureItem) {}	
	
	public void endVisit(ReturnsDeclaration returnDecl) {
	}	
	
	public void endVisit(CallStatement callStatement) {
	}
	
		            
 }
