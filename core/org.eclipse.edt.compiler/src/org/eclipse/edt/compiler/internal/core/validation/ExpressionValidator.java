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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BytesLiteral;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.IsNotExpression;
import org.eclipse.edt.compiler.core.ast.LiteralExpression;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.SuperExpression;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionArgumentValidator;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.AssignmentStatementValidator;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

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
		// Both the lhs and the rhs can be a ternary expression, in which case both of the ternary's possible results must be checked
		// against the other side.
		Map<Expression, Type> lhsMap = new HashMap<Expression, Type>();
		Map<Expression, Type> rhsMap = new HashMap<Expression, Type>();
		TypeValidator.collectExprsForTypeCompatibility(binaryExpression.getFirstExpression(), lhsMap);
		TypeValidator.collectExprsForTypeCompatibility(binaryExpression.getSecondExpression(), rhsMap);
		
		for (Map.Entry<Expression, Type> lhsEntry : lhsMap.entrySet()) {
			NamedElement lhsElement = DefaultBinder.getOperandType(lhsEntry.getKey());
			if (lhsElement != null) {
				for (Map.Entry<Expression, Type> rhsEntry : rhsMap.entrySet()) {
					NamedElement rhsElement = DefaultBinder.getOperandType(rhsEntry.getKey());
					if (rhsElement != null) {
						Operation op = IRUtils.getBinaryOperation(lhsElement, rhsElement, binaryExpression.getOperator().toString());
						if (op != null) {
							// If the parameters are generic, we need to validate the arg type vs the resolved parm type.
							Expression errorNode = null;
							
							// The type that we resolve with is whichever type defined the operation. This could be the lhs or the rhs (array :: mystring, or mystring :: array).
							Type resolvingType = lhsEntry.getValue();
							if (resolvingType == null || resolvingType.getClassifier() == null || !resolvingType.getClassifier().equals(op.getType().getClassifier())) {
								resolvingType = rhsEntry.getValue();
							}
							
							if (BindingUtil.isUnresolvedGenericType(op.getParameters().get(0).getType())) {
								Type t = BindingUtil.resolveGenericType(op.getParameters().get(0).getType(), resolvingType);
								if (!IRUtils.isMoveCompatible(t, op.getParameters().get(0), lhsEntry.getValue(), lhsEntry.getKey().resolveMember())) {
									errorNode = lhsEntry.getKey();
								}
							}
							if (errorNode == null && BindingUtil.isUnresolvedGenericType(op.getParameters().get(1).getType())) {
								Type t = BindingUtil.resolveGenericType(op.getParameters().get(1).getType(), resolvingType);
								if (!IRUtils.isMoveCompatible(t, op.getParameters().get(1), rhsEntry.getValue(), rhsEntry.getKey().resolveMember())) {
									errorNode = rhsEntry.getKey();
								}
							}
							
							if (errorNode != null) {
								problemRequestor.acceptProblem(errorNode, IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
										new String[]{
												lhsEntry.getValue() != null ? BindingUtil.getShortTypeString(lhsEntry.getValue()) : lhsEntry.getKey().getCanonicalString(),
												rhsEntry.getValue() != null ? BindingUtil.getShortTypeString(rhsEntry.getValue()) : rhsEntry.getKey().getCanonicalString(),
												lhsEntry.getKey().getCanonicalString() + " " + binaryExpression.getOperator().toString() + " " + rhsEntry.getKey().getCanonicalString()});
							}
						}
						else {
							problemRequestor.acceptProblem(lhsEntry.getKey(), IProblemRequestor.MISSING_OPERATION_FOR_BINARY_EXPRESSION,
									new String[]{
											lhsEntry.getKey().getCanonicalString(), rhsEntry.getKey().getCanonicalString(), binaryExpression.getOperator().toString(),
											lhsEntry.getKey().getCanonicalString() + " " + binaryExpression.getOperator().toString() + " " + rhsEntry.getKey().getCanonicalString()});
						}
					}
				}
			}
		}
	}
	
	@Override
	public void endVisit(UnaryExpression unaryExpression) {
		Expression operand = unaryExpression.getExpression();
		Type operandType = operand.resolveType();
		if (operandType != null) {
			Operation op = IRUtils.getUnaryOperation(operandType.getClassifier(), unaryExpression.getOperator().toString());
			if (op == null) {
				problemRequestor.acceptProblem(operand, IProblemRequestor.MISSING_OPERATION_FOR_UNARY_EXPRESSION,
						new String[] {operand.getCanonicalString(), unaryExpression.getOperator().toString()});
			}
		}
	}
	
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
						Map<Expression, Type> exprMap = new HashMap<Expression, Type>();
						TypeValidator.collectExprsForTypeCompatibility(arrayType.getInitialSize(), exprMap);
						for (Map.Entry<Expression, Type> entry : exprMap.entrySet()) {
							Type tBinding = entry.getValue();
							if (tBinding != null) {
								if (!IRUtils.isMoveCompatible(TypeUtils.Type_INT, tBinding, entry.getKey().resolveMember())) {
									problemRequestor.acceptProblem(
										arrayType.getInitialSize(),
										IProblemRequestor.ARRAY_SIZE_LESS_THAN_ZERO,
										new String[] {arrayType.getInitialSize().getCanonicalString()});
								}
							}
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
	}
	
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
		
		if (element == null && (target instanceof ThisExpression || target instanceof SuperExpression)) {
			// Will be set on the invocation, not the target.
			element = functionInvocation.resolveElement();
			if (element instanceof FunctionMember) {
				returnType = ((FunctionMember)element).getReturnType();
			}
		}
		
		if (element instanceof FunctionMember || element instanceof Delegate) {
			// returnType is required when the invocation is not part of a FunctionInvocationStatement ("voidFunc();" good, "x int = voidFunc();" bad).
			if (returnType == null && !(functionInvocation.getParent() instanceof FunctionInvocationStatement)) {
				problemRequestor.acceptProblem(
						functionInvocation,
						IProblemRequestor.FUNCTION_MUST_RETURN_TYPE,
						new String[] {target.getCanonicalString()});
			}
			
			if (element instanceof Delegate) {
				functionInvocation.accept(new FunctionArgumentValidator((Delegate)element, problemRequestor, compilerOptions));
			}
			else if (element instanceof FunctionMember) {
				functionInvocation.accept(new FunctionArgumentValidator((FunctionMember)element, problemRequestor, compilerOptions));
			}
		}
		else if (element != null) {
			problemRequestor.acceptProblem(
					target,
					IProblemRequestor.FUNCTION_INVOCATION_TARGET_NOT_FUNCTION_OR_DELEGATE);
		}
	}
	
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
	}
	
	@Override
	public void endVisit(IsAExpression isAExpression) {
		checkTypeForIsaOrAs(isAExpression.getType());
	}
	
	@Override
	public void endVisit(AsExpression asExpression) {
		if (asExpression.hasType()) {
			checkTypeForIsaOrAs(asExpression.getType());
			
			Type toType = asExpression.getType().resolveType();
			if (toType != null) {
				Map<Expression, Type> exprMap = new HashMap<Expression, Type>();
				TypeValidator.collectExprsForTypeCompatibility(asExpression.getExpression(), exprMap);
				
				for (Map.Entry<Expression, Type> entry : exprMap.entrySet()) {
					if (entry.getValue() != null) {
						if (!IRUtils.isMoveCompatible(toType, entry.getValue(), entry.getKey().resolveMember())) {
							problemRequestor.acceptProblem(
								entry.getKey(),
								IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
								new String[] {
										BindingUtil.getShortTypeString(entry.getValue(), true),
										BindingUtil.getShortTypeString(toType, true),
										entry.getKey().getCanonicalString() + " as " + asExpression.getType().getCanonicalName()
								});
						}
					}
				}
			}
		}
	}
	
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
	
	@Override
	public void endVisit(IntegerLiteral integerLiteral) {
		String strVal = getSign(integerLiteral.getParent(), false) + integerLiteral.getValue();
		if (integerLiteral.getLiteralKind() == LiteralExpression.BIGINT_LITERAL) {
			try {
				Long.parseLong(strVal, 10);
			}
			catch (NumberFormatException nfe) {
				problemRequestor.acceptProblem(
					integerLiteral,
					IProblemRequestor.BIGINT_LITERAL_OUT_OF_RANGE,
					new String[] {strVal});
			}
		}
		else if (integerLiteral.getLiteralKind() == LiteralExpression.SMALLINT_LITERAL) {
			try {
				Short.parseShort(strVal, 10);
			}
			catch (NumberFormatException nfe) {
				problemRequestor.acceptProblem(
					integerLiteral,
					IProblemRequestor.SMALLINT_LITERAL_OUT_OF_RANGE,
					new String[] {strVal});
			}
		}
		else {
			try {
				Integer.parseInt(strVal, 10);
			}
			catch (NumberFormatException nfe) {
				problemRequestor.acceptProblem(
					integerLiteral,
					IProblemRequestor.INTEGER_LITERAL_OUT_OF_RANGE,
					new String[] {strVal});
			}
		}
	}
	
	@Override
	public void endVisit(DecimalLiteral decimalLiteral) {
		String strVal = decimalLiteral.getValue();
		
		if (strVal.length() > (strVal.indexOf('.') == -1 ? 32 : 33)) {
			problemRequestor.acceptProblem(
				decimalLiteral,
				IProblemRequestor.DECIMAL_LITERAL_OUT_OF_RANGE,
				new String[] {strVal});
		}
	}
	
	@Override
	public void endVisit(FloatLiteral floatLiteral) {
		try {
			if (floatLiteral.getLiteralKind() == LiteralExpression.SMALLFLOAT_LITERAL) {
				String strVal = floatLiteral.getValue();
				if (Float.isInfinite(Float.parseFloat(strVal))) {
					problemRequestor.acceptProblem(
						floatLiteral,
						IProblemRequestor.SMALLFLOAT_LITERAL_OUT_OF_RANGE,
						new String[] { strVal });
				}
			}
			else {
				String strVal = floatLiteral.getValue();
				if (Double.isInfinite(Double.parseDouble(strVal))) {
					problemRequestor.acceptProblem(
						floatLiteral,
						IProblemRequestor.FLOATING_POINT_LITERAL_OUT_OF_RANGE,
						new String[] { strVal });
				}
			}
		}
		catch( NumberFormatException e ) {
			// should be syntax error, so ignore			
		}
	}
	
	@Override
	public void endVisit(BytesLiteral bytesLiteral) {
		if (bytesLiteral.getValue().length() % 2 != 0) {
			problemRequestor.acceptProblem(bytesLiteral, IProblemRequestor.BYTES_LITERAL_LENGTH_MUST_BE_EVEN, new String[] {bytesLiteral.getCanonicalString()});
		}
	}
	
	@Override
	public void endVisit(IsNotExpression isNotExpression) {
		problemRequestor.acceptProblem(
				isNotExpression,
				IProblemRequestor.IS_NOT_UNSUPPORTED,
				new String[] {});
	}
	
	private String getSign(Node node, boolean hasNegativeSign) {
    	if (node instanceof ParenthesizedExpression) {
    		return getSign(node.getParent(), hasNegativeSign);
    	}
    	if (node instanceof UnaryExpression) {
    		UnaryExpression unary = (UnaryExpression) node;
    		if (unary.getOperator() == UnaryExpression.Operator.MINUS) {
    			return getSign(unary.getParent(), !hasNegativeSign);
    		}
    		else {
        		if (unary.getOperator() == UnaryExpression.Operator.PLUS) {
        			return getSign(unary.getParent(), hasNegativeSign);
        		}
    		}
    	}
    	
    	if (hasNegativeSign) {
    		return "-";
    	}
    	return "";
    }
	
	@Override
	public void endVisit(SubstringAccess substringAccess) {
		org.eclipse.edt.mof.egl.Type type = substringAccess.getPrimary().resolveType();
		if (type != null) {
			Operation op = IRUtils.getOperation(type.getClassifier(), Operation.SUBSTRING);
			if (op == null) {
				problemRequestor.acceptProblem(substringAccess, IProblemRequestor.MISSING_OPERATION_FOR_SUBSTRING,
						new String[]{substringAccess.getPrimary().getCanonicalString()});
			}
		}
		checkSubstringIndex(substringAccess.getExpr(), substringAccess);
    	checkSubstringIndex(substringAccess.getExpr2(), substringAccess);
	}
	
	private void checkSubstringIndex(final Expression index, SubstringAccess parentAccess) {
		Type tBinding = index.resolveType();
		if (tBinding != null) {
			boolean typeIsValid = false;
			if (TypeUtils.Type_ANY.equals((tBinding)) || TypeUtils.isNumericType(tBinding)) {
				if (tBinding instanceof FixedPrecisionType) {
					typeIsValid = ((FixedPrecisionType)tBinding).getDecimals() == 0;
				}
				else {
					typeIsValid = true;
				}
			}
			if (!typeIsValid) {
				problemRequestor.acceptProblem(
					index,
					IProblemRequestor.SUBSTRING_INDEX_NOT_INTEGER,
					new String[] {index.getCanonicalString(), parentAccess.getCanonicalString()});
			}
		}
	}
	
	@Override
	public void endVisit(FieldAccess fieldAccess) {
		if (fieldAccess.getPrimary().resolveType() instanceof org.eclipse.edt.mof.egl.ArrayType
				&& !(fieldAccess.resolveMember() instanceof FunctionMember)) {
			problemRequestor.acceptProblem(
					fieldAccess.getPrimary(),
					IProblemRequestor.ARRAY_ACCESS_NOT_SUBSCRIPTED,
					new String[] {fieldAccess.getPrimary().getCanonicalString()});
		}
		
		final boolean[] dynamicAccessUsed = {false};
		fieldAccess.getPrimary().accept(new AbstractASTVisitor() {
			@Override
			public boolean visit(ArrayAccess arrayAccess) {
				if (arrayAccess.getIndices().size() == 1) {
		    		Type indexType = ((Expression) arrayAccess.getIndices().get(0)).resolveType();
		    		if (indexType != null && TypeUtils.isTextType(indexType)) {
		    			dynamicAccessUsed[0] = true;
		    		}
	    		}
				return true;
			};
		});
		
		if (dynamicAccessUsed[0]) {
			problemRequestor.acceptProblem(fieldAccess, IProblemRequestor.DOT_ACCESS_USED_AFTER_DYNAMIC);
		}
	};
	
	@Override
	public void endVisit(QualifiedName qualifiedName) {
		if (qualifiedName.getQualifier().resolveType() instanceof org.eclipse.edt.mof.egl.ArrayType
				&& !(qualifiedName.resolveMember() instanceof Function)) {
			problemRequestor.acceptProblem(
					qualifiedName.getQualifier(),
					IProblemRequestor.ARRAY_ACCESS_NOT_SUBSCRIPTED,
					new String[] {qualifiedName.getQualifier().getCanonicalName()});
		}
	}
	
	@Override
	public void endVisit(org.eclipse.edt.compiler.core.ast.TernaryExpression ternaryExpression) {
		// The first expr is the condition and must be boolean.
		Type conditionType = ternaryExpression.getFirstExpr().resolveType();
		if (conditionType != null && !conditionType.equals(TypeUtils.Type_BOOLEAN)) {
			problemRequestor.acceptProblem(
					ternaryExpression.getFirstExpr(),
					IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
					new String[] {
						BindingUtil.getShortTypeString(conditionType),
						IEGLConstants.KEYWORD_BOOLEAN,
						ternaryExpression.getFirstExpr().getCanonicalString()
					});
		}
	};
	
	@Override
	public void endVisit(ArrayAccess arrayAccess) {
		if (arrayAccess.getIndices().size() > 1) {
			problemRequestor.acceptProblem(
					arrayAccess,
					IProblemRequestor.MULTI_INDICES_NOT_SUPPORTED,
					new String[]{arrayAccess.getCanonicalString()});
		}
		else {
			Expression index = arrayAccess.getIndices().get(0);
			Type indexType = index.resolveType();
			Type arrayType = arrayAccess.getArray().resolveType();
			if (indexType != null) {
				if (TypeUtils.isTextType(indexType)) {
					// Dynamic access.
					if (arrayType != null && !BindingUtil.isDynamicallyAccessible(arrayType)) {
						problemRequestor.acceptProblem(
		    					arrayAccess.getArray(),
								IProblemRequestor.NON_DYNAMIC_ACCESS_ACCESSED_DYNAMICALLY,
								new String[]{arrayAccess.getArray().getCanonicalString()});
					}
				}
				else {
					// Array access.
					boolean indexTypeIsValid = false;
					if (TypeUtils.Type_ANY.equals((indexType)) || TypeUtils.isNumericType(indexType)) {
						if (indexType instanceof FixedPrecisionType) {
							indexTypeIsValid = ((FixedPrecisionType)indexType).getDecimals() == 0;
						}
						else {
							indexTypeIsValid = true;
						}
					}			
					if (!indexTypeIsValid) {
						problemRequestor.acceptProblem(
	    						index,
								IProblemRequestor.SUBSCRIPT_MUST_BE_INTEGER_ITEM,
								new String[] {index.getCanonicalString(), arrayAccess.getCanonicalString()});
					}
					
					if (arrayType != null && (!TypeUtils.Type_ANY.equals(arrayType) && !(arrayType instanceof org.eclipse.edt.mof.egl.ArrayType))) {
						problemRequestor.acceptProblem(
		    					arrayAccess.getArray(),
								IProblemRequestor.NON_ARRAY_ACCESS_SUBSCRIPTED,
								new String[] {
		    						arrayAccess.getArray().getCanonicalString()
		    					});
					}
				}
			}
		}
	}
	
	@Override
	public boolean visit(SetValuesExpression setValuesExpression) {
		// Do not validate exprs inside annotations or stereotypes. That is already covered in the annotation code, which has different rules.
		return !(setValuesExpression.getExpression().resolveType() instanceof AnnotationType); // StereotypeType subclasses this, no need to check both
	}
}
