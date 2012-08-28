/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.AssignmentStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

/*
TODO Remaining expressions to port from the old DefaultBinder:
array access
substring access
field access
is not?
in
ternary
all the literals including arrays
*/
public class ExpressionValidator extends AbstractASTVisitor {
	
	IPartBinding declaringPart;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	public ExpressionValidator(IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.declaringPart = declaringPart;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	@Override
	public void endVisit(BinaryExpression binaryExpression) {
		Expression operand1 = binaryExpression.getFirstExpression();
		Expression operand2 = binaryExpression.getSecondExpression();
		NamedElement elem1 = getOperandType(operand1);
		NamedElement elem2 = getOperandType(operand2);
		if (elem1 != null && elem2 != null) {
			boolean valid = false;
			Operation op = IRUtils.getBinaryOperation(elem1, elem2, binaryExpression.getOperator().toString());
			if (op != null) {
				// If the parameters are generic, we need to validate the arg type vs the generic's expected type.
				valid = true;
				if (op.getParameters().get(0).getType() instanceof GenericType) {
					Type t = ((GenericType)op.getParameters().get(0).getType()).resolveTypeParameter(operand1.resolveType());
					valid = IRUtils.isMoveCompatible(t, operand1.resolveType(), operand1.resolveMember());
				}
				if (valid && op.getParameters().get(1).getType() instanceof GenericType) {
					Type t = ((GenericType)op.getParameters().get(1).getType()).resolveTypeParameter(operand1.resolveType());
					valid = IRUtils.isMoveCompatible(t, operand2.resolveType(), operand2.resolveMember());
				}
			}
			
			if (!valid) {
				problemRequestor.acceptProblem(binaryExpression, IProblemRequestor.MISSING_OPERATION_FOR_EXPRESSION,
						new String[]{operand1.getCanonicalString(), operand2.getCanonicalString(), binaryExpression.getOperator().toString()});
			}
		}
	};
	
	@Override
	public void endVisit(UnaryExpression unaryExpression) {
		Expression operand = unaryExpression.getExpression();
		Type operandType = operand.resolveType();
		if (operandType != null) {
			Operation op = IRUtils.getUnaryOperation(operandType.getClassifier(), unaryExpression.getOperator().toString());
			if (op == null) {
				problemRequestor.acceptProblem(operand, IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION, new String[] {operand.getCanonicalString()});
			}
		}
	};
	
	@Override
	public void endVisit(NewExpression newExpression) {
		org.eclipse.edt.compiler.core.ast.Type type = newExpression.getType();
		if (type.resolveType() == null) {
			return;
		}
		
		TypeValidator.validate(type, declaringPart, problemRequestor, compilerOptions);
		
		if (type.isArrayType()) {
			ArrayType arrayType = (ArrayType)type;
			
			// When it's an array and not of initial size 0, the root type must be instantiable.
			if (arrayType.hasInitialSize() && !BindingUtil.isZeroLiteral(arrayType.getInitialSize())) {
				// For multidim arrays, the inner-most ArrayType will contain the nullable flag for the root type (e.g. int?[][][]).
				ArrayType innerArrayType = arrayType;
				while (innerArrayType.getElementType().isArrayType()) {
					innerArrayType = (ArrayType)innerArrayType.getElementType();
				}
				
				TypeValidator.validateInstantiatable(arrayType.getBaseType(), declaringPart, innerArrayType.isNullable(), problemRequestor);
			}
			
			// Initial size must not be negative.
			final boolean[] hasInitialSize = new boolean[1];
			newExpression.getType().accept(new AbstractASTVisitor() {
				@Override
				public boolean visit(ArrayType arrayType) {
					if (arrayType.hasInitialSize()) {
						final boolean sizeIsValid[] = new boolean[] {false};
						arrayType.getInitialSize().accept(new DefaultASTVisitor() {
							@Override
							public boolean visit(IntegerLiteral integerLiteral) {
								sizeIsValid[0] = true;
								return false;
							}
						});
						
						if (sizeIsValid[0]) {
							hasInitialSize[0] = true;
						}
						else {
							problemRequestor.acceptProblem(
								arrayType.getInitialSize(),
								IProblemRequestor.ARRAY_SIZE_LESS_THAN_ZERO,
								new String[] {arrayType.getInitialSize().getCanonicalString()});
						}
					}
					return true;
				}
			});
			
			if (hasInitialSize[0] && newExpression.hasSettingsBlock()) {
				//Disallow a new expression for an array that specifies an initial size and specifies entries in a settings block:
				//new int[5] {1,2,3}
				final Node[] errorNode = new Node[1];
				newExpression.getSettingsBlock().accept(new AbstractASTExpressionVisitor() {
					@Override
		    		public boolean visit(Assignment assignment) {
		    			return false;
		    		}
		    		@Override
		    		public boolean visit(AnnotationExpression annotationExpression) {
		    			return false;
		    		}
		    		@Override
		    		public boolean visit(SetValuesExpression setValuesExpression) {
						return false; 			
		    		}
		    		@Override
		    		public boolean visitExpression(Expression expression) {
		    			if (errorNode[0] != null) {
		    				return false;
		    			}
		    			errorNode[0] = expression;
		    			return false;
		    		}
		    		
		    	});
		    	if (errorNode[0] != null) {
		    		problemRequestor.acceptProblem(errorNode[0],IProblemRequestor.POSITIONAL_PROPERTY_NOT_ALLOWED_WITH_INITIAL_SIZE, IMarker.SEVERITY_ERROR, new String[] {});
		    	}
			}
		}
		else if (type.isNameType()) {
			// If there were arguments then the binder already validated they were valid for a public constructor.
			// No arguments means we need to make sure it can be instantiated.
			if (!((NameType)type).hasArguments()) {
				TypeValidator.validateInstantiatable(type, declaringPart, false, problemRequestor);
			}
		}
		
		if (newExpression.hasSettingsBlock()) {
			newExpression.getSettingsBlock().accept(new DefaultASTVisitor() {
				@Override
				public boolean visit(SettingsBlock settingsBlock) {
					return true;
				}
				@Override
				public boolean visit(Assignment assignment) {
					validateAssignment(assignment);
					return false;
				}
			});
		}
	};
	
	@Override
	public void endVisit(FunctionInvocation functionInvocation) {
		Expression target = functionInvocation.getTarget();
		
		Object element = null;
		Type returnType = null;
		
		if (target.resolveElement() instanceof FunctionMember) {
			element = target.resolveElement();
			returnType = ((FunctionMember)element).getType();
		}
		else if (target.resolveType() instanceof Delegate) {
			element = target.resolveType();
			returnType = ((Delegate)element).getReturnType();
		}
		
		if (element == null) {
			problemRequestor.acceptProblem(
					target,
					IProblemRequestor.FUNCTION_INVOCATION_TARGET_NOT_FUNCTION_OR_DELEGATE);
			return;
		}
		
		// returnType is required when the invocation is not part of a FunctionInvocationStatement ("voidFunc();" good, "x int = voidFunc();" bad).
		if (returnType == null && !(functionInvocation.getParent() instanceof FunctionInvocationStatement)) {
			problemRequestor.acceptProblem(
					functionInvocation,
					IProblemRequestor.FUNCTION_MUST_RETURN_TYPE,
					new String[] {target.getCanonicalString()});
		}
		
		//TODO finish cleaning up FunctionArgumentValidator to remove compile errors
//		if (element instanceof Delegate) {
//			functionInvocation.accept(new FunctionArgumentValidator((Delegate)element, problemRequestor, compilerOptions));
//		}
//		else if (element instanceof FunctionMember) {
//			functionInvocation.accept(new FunctionArgumentValidator((FunctionMember)element, problemRequestor, compilerOptions));
//		}
	};
	
	@Override
	public void endVisit(SetValuesExpression setValuesExpression) {
		setValuesExpression.getSettingsBlock().accept(new DefaultASTVisitor() {
			@Override
			public boolean visit(SettingsBlock settingsBlock) {
				return true;
			}
			@Override
			public boolean visit(Assignment assignment) {
				validateAssignment(assignment);
				return false;
			}
		});
		
		if (setValuesExpression.getExpression().resolveType() instanceof Delegate || setValuesExpression.getExpression().resolveMember() instanceof FunctionMember) {
			problemRequestor.acceptProblem(
					setValuesExpression.getSettingsBlock(),
					IProblemRequestor.SETTINGS_BLOCK_NOT_ALLOWED,
					new String[] {});
		}
	}
	
	private void validateAssignment(Assignment assignment) {
		Expression leftHandSide = assignment.getLeftHandSide();
		Type lhType = leftHandSide.resolveType();
		
		Expression rightHandSide = assignment.getRightHandSide();
		Type rhType = rightHandSide.resolveType();
		
		if (lhType != null && rhType != null && !(lhType instanceof AnnotationType)) {
			new AssignmentStatementValidator(problemRequestor, compilerOptions, declaringPart).validateAssignment(
					assignment.getOperator(), leftHandSide, rightHandSide, lhType, rhType, leftHandSide.resolveMember(), rightHandSide.resolveMember());
		}
	};
	
	@Override
	public void endVisit(SettingsBlock settingsBlock) {
		Node parent = settingsBlock.getParent();
		
		final boolean[] invalid = new boolean[1];
		parent.accept(new DefaultASTVisitor() {
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.IfStatement ifStatement) {
				invalid[0] = true;
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.ElseBlock elseBlock) {
				invalid[0] = true;
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.WhileStatement whileStatement) {
				invalid[0] = true;
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.WhenClause whenClause) {
				invalid[0] = true;
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.TryStatement tryStatement) {
				invalid[0] = true;
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.OnExceptionBlock onExceptionBlock) {
				invalid[0] = true;
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.ForStatement forStatement) {
				invalid[0] = true;
				return false;
			}
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement forEachStatement) {
				invalid[0] = true;
				return false;
			}
		});
		
		if (invalid[0]) {
			problemRequestor.acceptProblem(
					settingsBlock,
					IProblemRequestor.SETTINGS_BLOCK_NOT_ALLOWED,
					new String[] {});
		}
	}
	
	@Override
	public void endVisit(IsAExpression isAExpression) {
		checkTypeForIsaOrAs(isAExpression.getType());
	};
	
	@Override
	public void endVisit(AsExpression asExpression) {
		if (asExpression.hasType()) {
			checkTypeForIsaOrAs(asExpression.getType());
			
			Type fromType = asExpression.getExpression().resolveType();
			Type toType = asExpression.getType().resolveType();
			if (fromType != null && toType != null) {
				if (!IRUtils.isMoveCompatible(toType, fromType, asExpression.getExpression().resolveMember())) {
					problemRequestor.acceptProblem(
						asExpression,
						IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
						new String[] {
							StatementValidator.getTypeString(fromType),
							StatementValidator.getTypeString(toType),
							asExpression.getCanonicalString()
						});
				}
			}
		}
	};
	
	private void checkTypeForIsaOrAs(org.eclipse.edt.compiler.core.ast.Type type) {
		TypeValidator.validate(type, declaringPart, problemRequestor, compilerOptions);
		
		org.eclipse.edt.compiler.core.ast.Type tempType = type;
		while (tempType.isArrayType()) {
			if (((ArrayType)tempType).hasInitialSize()) {
				problemRequestor.acceptProblem(
					((ArrayType)tempType).getInitialSize(),
					IProblemRequestor.ARRAY_SIZE_NOT_ALLOWED_IN_ISA_OR_AS);
			}
			
			tempType = ((ArrayType)tempType).getElementType();
		}
	}
	
	protected NamedElement getOperandType(Expression expr) {
		Object element = expr.resolveElement();
		if (element instanceof Function) {
			return (Function)element;
		}
		else {	
			if (expr.resolveType() != null) {
				return (Classifier)expr.resolveType().getClassifier();
			}
		}	
		return null;
	}
}
