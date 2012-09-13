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

import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.statement.LValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.RValueValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

/**
 * @author Dave Murray
 */
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
	public boolean visit(CallStatement callStatement) {
		this.qualifier = callStatement.getInvocationTarget();
		canonicalFunctionName = callStatement.getInvocationTarget().getCanonicalString();
		
		if (!callStatement.hasArguments()) {
			return false;
		}
		
		if(callStatement.getArguments().size() != getFunctionParameterCount()){
			problemRequestor.acceptProblem(
					callStatement,
					IProblemRequestor.ARGUMENT_COUNT_NOT_EQUAL_PARAMETER_COUNT,
					new String[] {
							String.valueOf(callStatement.getArguments().size()),
							String.valueOf(getFunctionParameterCount()),
							functionBinding.getCaseSensitiveName()
					});
		}
		
		for(Node expr : callStatement.getArguments()) {
			checkArg((Expression)expr);
		}

		return false;
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
		Type argType = argExpr.resolveType();
		Member argmember = argExpr.resolveMember();
		
		if(argType == null && argmember == null) {
			return false;
		}
		
		if(!checkArgumentNotSetValuesExpression(argExpr)) {
			return false;
		}
		
		if(!checkSubstringNotUsedAsArgument(parameterBinding, argExpr)) {
			return false;
		}

		if(!checkArgumentUsedCorrectlyWithInAndOut(argExpr, parameterBinding, parameterType)) {
			return false;
		}
		
		boolean argMatchesParm = true;
				
		if(parameterBinding.getParameterKind() == ParameterKind.PARM_IN) {
			argMatchesParm = checkArgForInParameter(argExpr, argType, parameterBinding, parameterType, numArgs);
		}
		else if(parameterBinding.getParameterKind() == ParameterKind.PARM_OUT) {
			argMatchesParm = checkArgForOutParameter(argExpr, argType, parameterBinding, parameterType, numArgs);
		}
		else {
			argMatchesParm = checkArgForInOutParameter(argExpr, argType, parameterBinding, parameterType, numArgs);
		}
		
		if(!argMatchesParm) {
			return false;
		}
		
		checkNullPassedToNonNullable(argExpr, parameterType, parameterBinding);
				
		return false;
	}
	
	private void checkNullPassedToNonNullable(Expression argExpr, Type parameterType, FunctionParameter parameterBinding) {
		if (TypeUtils.Type_NULLTYPE.equals(argExpr.resolveType()) && parameterBinding != null && !parameterBinding.isNullable()) {
			problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.CANNOT_PASS_NULL,
					new String[] {
						parameterBinding.getCaseSensitiveName(),
						functionBinding.getCaseSensitiveName()
					});
		}
	}
	
	private boolean checkArgumentNotSetValuesExpression(final Expression argExpr) {
		final boolean[] result = new boolean[] {true};
		argExpr.accept(new DefaultASTVisitor() {
			@Override
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			@Override
			public boolean visit(SetValuesExpression setValuesExpression) {
				problemRequestor.acceptProblem(
					argExpr,
					IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_FUNC_ARG);
				result[0] = false;
				return false;
			}
		});
		return result[0];
	}
	
	private boolean checkSubstringNotUsedAsArgument(FunctionParameter parm, Expression argExpr) {
		if (parm != null && parm.getParameterKind() != ParameterKind.PARM_IN && argExpr instanceof SubstringAccess) {
			problemRequestor.acceptProblem(argExpr,
					IProblemRequestor.SUBSTRING_IMMUTABLE,
					new String[] {});
			return false;
		}
		
		return true;
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
		public void endVisitExpression(Expression expression) {
			handleExpressionThatIsNotNameOrLiteral(expression);
		}
		
		abstract void handleExpressionThatIsNotNameOrLiteral(Expression expression);
	} 
    
    private boolean checkArgumentUsedCorrectlyWithInAndOut(final Expression argExpr, final FunctionParameter parmBinding, Type parmType) {
    	Type argTypeBinding = argExpr.resolveType();
		if(argTypeBinding == null) {
    		return true;
    	}
    	
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
			return false;
		}
		
		if (expressionIsLiteralOrName[0] && parmBinding.getParameterKind() != ParameterKind.PARM_IN) {
			if(!checkArgNotConstantOrLiteral(argExpr, IProblemRequestor.FUNCTION_ARG_LITERAL_NOT_VALID_WITH_OUT_PARAMETER)) {
				return false;
			}
		}
		
    	return true;
    }
    
    private boolean checkArgNotConstantOrLiteral(Expression argExpr, final int problemKind) {
    	Member argDBinding = argExpr.resolveMember();
		if (argDBinding instanceof ConstantField) {
			problemRequestor.acceptProblem(
				argExpr,
				problemKind,
				new String[] {
					argExpr.getCanonicalString(),
					functionBinding.getCaseSensitiveName()
				});
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
    
    private boolean checkArgForInOrOutParameter(Expression argExpr, Type argType, FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	if (parmType instanceof ArrayType) {
    		return checkArgForInOrOutArrayParameter(argExpr, argType, funcParmBinding, (ArrayType)parmType);
    	}
    	
    	parmType = BindingUtil.resolveGenericType(parmType, qualifier);
    	argType = BindingUtil.resolveGenericType(argType, argExpr);
    	
    	if (!IRUtils.isMoveCompatible(parmType, funcParmBinding, argType, argExpr.resolveMember()) && !TypeUtils.isDynamicType(argType)) {
    		problemRequestor.acceptProblem(
    			argExpr,
    			IProblemRequestor.FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM,
				new String[] {
    				argExpr.getCanonicalString(),
					funcParmBinding.getCaseSensitiveName(),
					canonicalFunctionName,
					// arg can be a function, which has no type
					argType == null ? BindingUtil.getTypeName(argExpr.resolveMember()) : BindingUtil.getShortTypeString(argType, true),
					BindingUtil.getShortTypeString(parmType)
    			});
    		return false;
    	}
    	return true;
    }
    
    private boolean checkArgForInParameter(Expression argExpr, Type argType, FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	Member argDBinding = argExpr.resolveMember();
    	if(argDBinding != null) {
    		if(!new RValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr).validate()) {
    			return false;
    		}
    	}
    	
    	validateNotSuper(argExpr);
    	
    	return checkArgForInOrOutParameter(argExpr, argType, funcParmBinding, parmType, argNum);
    }
    
    private boolean checkArgForOutParameter(Expression argExpr, Type argType, final FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	Member argDBinding = argExpr.resolveMember();
    	if(argDBinding != null) {
    		if(!new LValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr, new LValueValidator.DefaultLValueValidationRules() {
    			@Override
     			public boolean canAssignToFunctionParmConst() {
     				return funcParmBinding.isConst();
     			}
    			@Override
     			public boolean canAssignToConstantVariables() {
     				return funcParmBinding.isConst();
     			}
    		}).validate()) {
    			return false;
    		}
    	}
    	validateNotThis(argExpr);
    	validateNotSuper(argExpr);
    	
    	//Cannot pass a value type to an reference type OUT parm
   		if (!isRefCompatForOutParm(argType, parmType)) {
    		problemRequestor.acceptProblem(
	    			argExpr,
	    			IProblemRequestor.FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM,
					new String[] {
	    				argExpr.getCanonicalString(),
						funcParmBinding.getCaseSensitiveName(),
						canonicalFunctionName,
						// arg can be a function, which has no type
						argType == null ? BindingUtil.getTypeName(argExpr.resolveMember()) : BindingUtil.getShortTypeString(argType, true),
						BindingUtil.getShortTypeString(parmType, true)
	    			});
    		return false;
   		}
   		
    	return checkArgForInOrOutParameter(argExpr, argType, funcParmBinding, parmType, argNum);
    }
    
    private boolean isRefCompatForOutParm(Type argType, Type parmType) {
    	if (argType != null && parmType != null) {
    		return TypeUtils.isReferenceType(argType) == TypeUtils.isReferenceType(parmType);
    	}
    	return true;
    }
    
    private boolean checkArgForInOrOutArrayParameter(Expression argExpr, Type argType, FunctionParameter funcParmBinding, ArrayType parmType) {
    	parmType = (ArrayType)BindingUtil.resolveGenericType(parmType, qualifier);
    	argType = BindingUtil.resolveGenericType(argType, argExpr);
    	
    	if (TypeUtils.isDynamicType(argType) || !IRUtils.isMoveCompatible(parmType, funcParmBinding, argType, argExpr.resolveMember())) {
    		problemRequestor.acceptProblem(
    			argExpr,
    			IProblemRequestor.FUNCTION_ARG_NOT_ASSIGNMENT_COMPATIBLE_WITH_PARM,
				new String[] {
    				argExpr.getCanonicalString(),
					funcParmBinding.getCaseSensitiveName(),
					canonicalFunctionName,
					// arg can be a function, which has no type
					argType == null ? BindingUtil.getTypeName(argExpr.resolveMember()) : BindingUtil.getShortTypeString(argType, true),
					BindingUtil.getShortTypeString(argType),
					BindingUtil.getShortTypeString(parmType)
    			});
    		return false;
    	}
    	return true;
    }
    
    private boolean checkArgForInOutParameter(Expression argExpr, Type argType, final FunctionParameter funcParmBinding, Type parmType, int argNum) {
    	Member argDBinding = argExpr.resolveMember();
    	if(argDBinding != null) {
    		if(!new RValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr).validate()) {
    			return false;
    		}    		
    		if(!new LValueValidator(problemRequestor, compilerOptions, argDBinding, argExpr, new LValueValidator.DefaultLValueValidationRules() {
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
    			return false;
    		}
    	}
    	
    	boolean argCompatible = true;
    	if (argType != null) {
    		argCompatible = TypeUtils.areCompatible(parmType.getClassifier(), argType.getClassifier());
    	}
    	else if (argDBinding != null) {
    		argCompatible = TypeUtils.areCompatible(parmType.getClassifier(), argDBinding);
    	}
    	if (argCompatible) {
    		argCompatible = argDBinding != null && argDBinding.isNullable() == funcParmBinding.isNullable();
    	}
    	
    	if (!argCompatible) {
    		problemRequestor.acceptProblem(
    			argExpr,
    			IProblemRequestor.FUNCTION_ARG_NOT_REFERENCE_COMPATIBLE_WITH_PARM,
				new String[] {
    				argExpr.getCanonicalString(),
					funcParmBinding.getCaseSensitiveName(),
					canonicalFunctionName,
					// arg can be a function, which has no type
					argType == null ? BindingUtil.getTypeName(argDBinding) : BindingUtil.getShortTypeString(argType, true),
					BindingUtil.getShortTypeString(parmType, true)
    			});
    		return false;
   		}
    	
    	validateNotThis(argExpr);
    	validateNotSuper(argExpr);
    	
    	return true;
    }
    
    private void validateNotThis(Expression expr) {
    	DefaultASTVisitor visitor = new DefaultASTVisitor() {
    		@Override
    		public boolean visit(org.eclipse.edt.compiler.core.ast.ThisExpression thisExpression) {
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
    		public boolean visit(org.eclipse.edt.compiler.core.ast.SuperExpression superExpression) {
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
