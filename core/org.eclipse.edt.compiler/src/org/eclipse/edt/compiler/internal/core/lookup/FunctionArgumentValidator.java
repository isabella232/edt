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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.SuperExpression;
import org.eclipse.edt.compiler.core.ast.TernaryExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.statement.LValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.RValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FunctionArgumentValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private NamedElement functionBinding;
	private String canonicalFunctionName;
	private Iterator<FunctionParameter> parameterIter;
	private int numArgs;
	private Expression qualifier;

	private ICompilerOptions compilerOptions;
	
	public FunctionArgumentValidator(Delegate delegateBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.functionBinding = delegateBinding;
		this.parameterIter = delegateBinding.getParameters().iterator();
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public FunctionArgumentValidator(FunctionMember functionBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.functionBinding = functionBinding;
		this.parameterIter = functionBinding.getParameters().iterator();
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	@Override
	public boolean visit(FunctionInvocation functionInvocation) {
		this.qualifier = functionInvocation;
		functionInvocation.getTarget().accept(new DefaultASTVisitor() {
			@Override
			public boolean visit(SimpleName simpleName) {
				canonicalFunctionName = simpleName.getCanonicalName();
				return false;
			}
			@Override
			public boolean visit(QualifiedName qualifiedName) {
				String canonicalName = qualifiedName.getCanonicalName();
				canonicalFunctionName = canonicalName.substring(canonicalName.lastIndexOf('.')+1);
				return false;
			}
		});
		if(canonicalFunctionName == null) {
			canonicalFunctionName = functionInvocation.getTarget().getCanonicalString();
		}
		for(Iterator iter = functionInvocation.getArguments().iterator(); iter.hasNext();) {
			checkArg((Expression) iter.next());
		}
		return false;
	}
	
	@Override
	public void endVisit(FunctionInvocation functionInvocation) {
		checkCorrectNumberArguments(functionInvocation.getTarget());
	}
	
	@Override
	public boolean visit(CallStatement callStatement) {
		this.qualifier = callStatement.getInvocationTarget();
		canonicalFunctionName = callStatement.getInvocationTarget().getCanonicalString();
		
		if (!callStatement.hasArguments()) {
			return false;
		}
		
		for(Node expr : callStatement.getArguments()) {
			checkArg((Expression)expr);
		}

		return false;
	}
	
	@Override
	public void endVisit(CallStatement callStatement) {
		checkCorrectNumberArguments(callStatement.getInvocationTarget());
	}
	
	private void checkCorrectNumberArguments(Expression errorNode) {
		int parmCount = getFunctionParameterCount();
		if (parmCount != numArgs) {
			problemRequestor.acceptProblem(
            		errorNode,
					IProblemRequestor.ROUTINE_MUST_HAVE_X_ARGS,
					new String[] {
            			canonicalFunctionName,
						Integer.toString(parmCount)
					}
            	);
		}
	}
	
	private int getFunctionParameterCount(){
		if(functionBinding instanceof Delegate){
			return ((Delegate)functionBinding).getParameters().size();
		}
		if(functionBinding instanceof FunctionMember){
			return ((FunctionMember)functionBinding).getParameters().size();
		}
		return -1;
	}
	public boolean checkArg(Expression argExpr) {
		numArgs += 1;
		if(!parameterIter.hasNext()) {			
			return false;
		}
		
		FunctionParameter parameterBinding = parameterIter.next();
		Type parameterType = parameterBinding.getType();
		
		// Set values exprs have no type or member so do this check before the next check below
		if (!checkArgumentNotSetValuesExpression(argExpr)) {
			return false;
		}
		
		if (argExpr.resolveType() == null && argExpr.resolveMember() == null) {
			return false;
		}
		
		// An argument can be a ternary, which really has 2 (or more) args to validate.
		Map<Expression, Type> argMap = new HashMap<Expression, Type>();
		TypeValidator.collectExprsForTypeCompatibility(argExpr, argMap);
		
		if (!checkSubstringNotUsedAsArgument(parameterBinding, argMap)) {
			return false;
		}
		
		if (!checkArgumentUsedCorrectlyWithInAndOut(argMap, parameterBinding, parameterType)) {
			return false;
		}
		
		switch (parameterBinding.getParameterKind()) {
			case PARM_IN:
				checkArgForInParameter(argMap, parameterBinding, parameterType, numArgs);
				break;
			case PARM_OUT:
				checkArgForOutParameter(argMap, parameterBinding, parameterType, numArgs);
				break;
			case PARM_INOUT:
				checkArgForInOutParameter(argMap, parameterBinding, parameterType, numArgs);
				break;
		}
		
		return false;
	}
	
	private boolean checkArgumentNotSetValuesExpression(final Expression argExpr) {
		final boolean[] result = new boolean[] {true};
		argExpr.accept(new DefaultASTVisitor() {
			@Override
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			@Override
			public boolean visit(TernaryExpression ternaryExpression) {
				ternaryExpression.getSecondExpr().accept(this);
				ternaryExpression.getThirdExpr().accept(this);
				return false;
			}
			@Override
			public boolean visit(SetValuesExpression setValuesExpression) {
				problemRequestor.acceptProblem(
					setValuesExpression,
					IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_FUNC_ARG);
				result[0] = false;
				return false;
			}
		});
		return result[0];
	}
	
	private boolean checkSubstringNotUsedAsArgument(FunctionParameter parm, Map<Expression, Type> argMap) {
		boolean result = true;
		if (parm != null && parm.getParameterKind() != ParameterKind.PARM_IN) {
			for (Expression argExpr : argMap.keySet()) {
				 if (argExpr instanceof SubstringAccess) {
					problemRequestor.acceptProblem(argExpr,
							IProblemRequestor.SUBSTRING_IMMUTABLE,
							new String[] {});
					result = false;
				}
			}
		}
		
		return result;
	}

    private abstract static class NonLiteralAndNonNameExpressionVisitor extends AbstractASTExpressionVisitor {
    	@Override
		public void endVisit(ParenthesizedExpression parenthesizedExpression) {
			parenthesizedExpression.getExpression().accept(this);
		}
		
		@Override
		public void endVisitName(Name name) {}
		@Override
		public void endVisit(ArrayAccess arrayAccess) {}
		@Override
		public void endVisit(SubstringAccess substringAccess) {}
		@Override
		public void endVisit(FieldAccess fieldAccess) {}
		@Override
		public void endVisit(SuperExpression superExpression) {}
		@Override
		public void endVisit(ThisExpression thisExpression) {}
		@Override
		public void endVisitLiteral(LiteralExpression literal) {}
		@Override
		public void endVisitExpression(Expression expression) {
			handleExpressionThatIsNotNameOrLiteral(expression);
		}
		
		abstract void handleExpressionThatIsNotNameOrLiteral(Expression expression);
	} 
    
    private boolean checkArgumentUsedCorrectlyWithInAndOut(Map<Expression, Type> argMap, final FunctionParameter parmBinding, Type parmType) {
    	boolean result = true;
    	for (Map.Entry<Expression, Type> entry : argMap.entrySet()) {
			if (entry.getValue() == null) {
	    		continue;
	    	}
			
			Expression argExpr = entry.getKey();
	    	final boolean[] expressionIsLiteralOrName = new boolean[] {true};
	    	final boolean[] foundError = new boolean[] {false};
			argExpr.accept(new NonLiteralAndNonNameExpressionVisitor() {
				@Override
				void handleExpressionThatIsNotNameOrLiteral(Expression expression) {
					if(parmBinding.getParameterKind() != ParameterKind.PARM_IN) {
						problemRequestor.acceptProblem(
							expression,
							IProblemRequestor.FUNCTION_ARG_REQUIRES_IN_PARAMETER,
							new String[] {
								expression.getCanonicalString(),
								functionBinding.getCaseSensitiveName()
							});
						foundError[0] = true;
					}
					expressionIsLiteralOrName[0] = false;
				}
			});
			
			if (foundError[0]) {
				result = false;
			}
			else if (expressionIsLiteralOrName[0] && parmBinding.getParameterKind() != ParameterKind.PARM_IN) {
				if(!checkArgNotConstantOrLiteral(argExpr, parmBinding)) {
					result = false;
				}
			}
    	}
		
    	return result;
    }
    
    private boolean checkArgNotConstantOrLiteral(Expression argExpr, FunctionParameter parmBinding) {
    	final int problemKind = parmBinding.getParameterKind() == ParameterKind.PARM_INOUT
    			? IProblemRequestor.FUNCTION_ARG_LITERAL_NOT_VALID_WITH_INOUT_PARAMETER
    			: IProblemRequestor.FUNCTION_ARG_LITERAL_NOT_VALID_WITH_OUT_PARAMETER;
    	
    	Name constName = LValueValidator.findConstName(argExpr);
		Member constMember = constName == null ? null : constName.resolveMember();
		if (constMember != null) {
			boolean canPassConst = false;
			if (parmBinding.getParameterKind() == ParameterKind.PARM_INOUT && parmBinding.isConst()) {
				canPassConst = true;
			}
			else {
				// Value types means every part of the field (including accesses) are constant. For reference types it's just the field declaration that's constant.
				if (constName != argExpr && !(constMember.getType() != null && TypeUtils.isValueType(constMember.getType()))) {
					canPassConst = true;
				}
			}
			
			if (!canPassConst) {
				problemRequestor.acceptProblem(
					argExpr,
					problemKind,
					new String[] {
						argExpr.getCanonicalString(),
						functionBinding.getCaseSensitiveName()
					});
			}
			return false;
		}
		final boolean[] foundError = new boolean[] {false};
		argExpr.accept(new AbstractASTExpressionVisitor() {
			@Override
			public void endVisitName(Name name) {}
			@Override
			public void endVisit(ArrayAccess arrayAccess) {}
			@Override
			public void endVisit(FieldAccess fieldAccess) {}
			@Override
			public void endVisit(SubstringAccess substringAccess) {}
			@Override
			public void endVisit(SuperExpression superExpression) {}
			@Override
			public void endVisit(ThisExpression thisExpression) {};
			@Override
			public void endVisitExpression(Expression expression) {
				problemRequestor.acceptProblem(
					expression,
					problemKind,
					new String[] {
						expression.getCanonicalString(),
						functionBinding.getCaseSensitiveName()
					});
				foundError[0] = true;
			}
		});
		
		return !foundError[0];
    }
    
    private boolean checkArgForInOrOutParameter(Map<Expression, Type> argMap, FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	boolean result = true;
    	for (Map.Entry<Expression, Type> entry : argMap.entrySet()) {
    		Expression argExpr = entry.getKey();
    		Type argType = entry.getValue();
    		parmType = BindingUtil.resolveGenericType(parmType, qualifier);
	    	
    		if (!BindingUtil.isMoveCompatible(parmType, funcParmBinding, argType, argExpr)) {
	    		// Generic type parms are defined as EAny (see EList.appendElement). Therefore the binding does not have the nullable flag set.
	    		// When passing in 'null', We have to use the qualifier's type to check if the elements are nullable.
	    		if (TypeUtils.Type_NULLTYPE.equals(argType) && !funcParmBinding.isNullable() && BindingUtil.isUnresolvedGenericType(funcParmBinding.getType())) {
					// The qualifier's type will tell us if it's nullable.
					Type qualType = BindingUtil.getTypeForGenericQualifier(qualifier);
					if (qualType instanceof ArrayType && ((ArrayType)qualType).elementsNullable()) {
						continue;
					}
	    		}
	    		
	    		problemRequestor.acceptProblem(
	    			argExpr,
	    			IProblemRequestor.FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM,
					new String[] {
	    				argExpr.getCanonicalString(),
						funcParmBinding.getCaseSensitiveName(),
						canonicalFunctionName,
						// arg can be a function, which has no type
						BindingUtil.getShortTypeString(argExpr, argType),
						BindingUtil.getShortTypeString(parmType)
	    			});
	    		result = false;
	    	}
    	}
    	return result;
    }
    
    private boolean checkArgForInParameter(Map<Expression, Type> argMap, FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	boolean result = true;
    	for (Map.Entry<Expression, Type> entry : argMap.entrySet()) {
    		Expression argExpr = entry.getKey();
	    	Member argDBinding = argExpr.resolveMember();
	    	if (argDBinding != null) {
	    		if (!new RValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr).validate()) {
	    			result = false;
	    		}
	    	}
	    	
	    	validateNotSuper(argExpr);
    	}
    	
    	if (!result) {
    		return false;
    	}
    	
    	return checkArgForInOrOutParameter(argMap, funcParmBinding, parmType, argNum);
    }
    
    private boolean checkArgForOutParameter(Map<Expression, Type> argMap, final FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	boolean result = true;
    	for (Map.Entry<Expression, Type> entry : argMap.entrySet()) {
    		Expression argExpr = entry.getKey();
    		Member argMember = argExpr.resolveMember();
    		
        	if(argMember != null) {
        		if(!new LValueValidator(problemRequestor, compilerOptions, argMember, argExpr, new LValueValidator.DefaultLValueValidationRules() {
        			@Override
         			public boolean canAssignToFunctionParmConst() {
         				return funcParmBinding.isConst();
         			}
        			@Override
         			public boolean canAssignToConstantVariables() {
         				return funcParmBinding.isConst();
         			}
        		}).validate()) {
        			result = false;
            		continue;
        		}
        	}
        	
        	validateNotThis(argExpr);
        	validateNotSuper(argExpr);
    	}
    	
    	if (!result) {
    		return false;
    	}
    	
    	return checkArgForInOrOutParameter(argMap, funcParmBinding, parmType, argNum);
    }
    
    private boolean checkArgForInOutParameter(Map<Expression, Type> argMap, final FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	boolean result = true;
    	for (Map.Entry<Expression, Type> entry : argMap.entrySet()) {
    		Expression argExpr = entry.getKey();
    		Type argType = entry.getValue();
	    	Member argMember = argExpr.resolveMember();
	    	
	    	if (argMember != null) {
	    		if(!new RValueValidator(problemRequestor, compilerOptions, argMember, argExpr).validate()) {
	    			result = false;
		    		continue;
	    		}    		
	    		if(!new LValueValidator(problemRequestor, compilerOptions, argMember, argExpr, new LValueValidator.DefaultLValueValidationRules() {
	    			@Override
	    			public boolean canAssignToFunctionReferences() {
	    				return true;
	    			}
	    			@Override
	     			public boolean canAssignToConstantVariables() {
	      				return true;
	     			}
	    			@Override
	     			public boolean canAssignToFunctionParmConst() {
	     				return funcParmBinding.isConst();
	     			}
	    		}).validate()) {
	    			result = false;
		    		continue;
	    		}
	    	}
	    	
	    	boolean argCompatible = argMember == null || argMember.isNullable() == funcParmBinding.isNullable();
	    	if (argCompatible) {
		    	if (argType != null) {
		    		argCompatible = BindingUtil.isReferenceCompatible(parmType, argType);
		    	}
		    	else if (argMember != null) {
		    		argCompatible = TypeUtils.areCompatible(parmType.getClassifier(), argMember);
		    	}
	    	}
	    	
	    	if (!argCompatible) {
	    		problemRequestor.acceptProblem(
	    			argExpr,
	    			IProblemRequestor.FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM,
					new String[] {
	    				argExpr.getCanonicalString(),
						funcParmBinding.getCaseSensitiveName(),
						canonicalFunctionName,
						// Use getTypeName() so that nullability is included in the message, since nullability must match ("string is not compatible with string" would look odd)
						BindingUtil.getTypeName(argMember, argType),
						BindingUtil.getTypeName(funcParmBinding)
	    			});
	    		result = false;
	    		continue;
	   		}
	    	
	    	validateNotThis(argExpr);
	    	validateNotSuper(argExpr);
    	}
    	
    	return result;
    }
    
    private void validateNotThis(Expression expr) {
    	DefaultASTVisitor visitor = new DefaultASTVisitor() {
    		@Override
    		public boolean visit(ThisExpression thisExpression) {
		    		problemRequestor.acceptProblem(
			    			thisExpression,
			    			IProblemRequestor.FUNCTION_ARG_CANNOT_BE_THIS,
							new String[] {});
		    		return false;	
	    		}
    	};
    	expr.accept(visitor);
    }
    
    private void validateNotSuper(Expression expr) {
    	DefaultASTVisitor visitor = new DefaultASTVisitor() {
    		@Override
    		public boolean visit(SuperExpression superExpression) {
		    		problemRequestor.acceptProblem(
			    			superExpression,
			    			IProblemRequestor.FUNCTION_ARG_CANNOT_BE_SUPER,
							new String[] {});
		    		return false;	
	    		}
    	};
    	expr.accept(visitor);
    }
}
