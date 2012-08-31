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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.AssignmentStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class AssignmentStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	
	public AssignmentStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public boolean visit(AssignmentStatement assignmentStatement) {
		Assignment assignment = assignmentStatement.getAssignment();
		Expression lhs = assignment.getLeftHandSide();
		Expression rhs = assignment.getRightHandSide();
		Type lhsType = lhs.resolveType();
		Type rhsType = rhs.resolveType();
		Member lhsMember = lhs.resolveMember();
		Member rhsMember = rhs.resolveMember();
		
		return validateAssignment(assignmentStatement.getAssignment().getOperator(), lhs, rhs, lhsType, rhsType, lhsMember, rhsMember);
	}
	
	public boolean validateAssignment(Assignment.Operator assignmentOperator, Expression lhs, Expression rhs, Type lhsType, Type rhsType, Member lhsMember, Member rhsMember) {
		return validateAssignment(assignmentOperator, lhs, rhs, lhsType, rhsType, lhsMember, rhsMember, new LValueValidator.DefaultLValueValidationRules());
	}
	
	public boolean validateAssignment(Assignment.Operator assignmentOperator, Expression lhs, Expression rhs, Type lhsType, Type rhsType, Member lhsMember, Member rhsMember, LValueValidator.ILValueValidationRules lvalueValidationRules) {
		
		if (lhs instanceof SubstringAccess) {
			problemRequestor.acceptProblem(lhs,
					IProblemRequestor.SUBSTRING_IMMUTABLE,
					new String[] {});
		}
		
		if (rhsType != null && lhsType != null) {
			// For complex assignments like "x &= y" we must treat it as if it was coded "x = x & y". To do this, retrieve the operation and use its type.
			Type resolvedRHSType = null;
			if (assignmentOperator != Assignment.Operator.ASSIGN) {
				String symbol = assignmentOperator.toString().substring(0, assignmentOperator.toString().length() - 1);
				Operation op = IRUtils.getBinaryOperation(lhsType.getClassifier(), rhsType.getClassifier(), symbol);
				if (op != null) {
					// If the parameters are generic, we need to validate the arg type vs the resolved parm type (which comes from the lhs type).
					boolean parmsValid = true;
					if (BindingUtil.isUnresolvedGenericType(op.getParameters().get(0).getType())) {
						Type t = BindingUtil.resolveGenericType(op.getParameters().get(0).getType(), lhsType);
						parmsValid = IRUtils.isMoveCompatible(t, lhsType, lhsMember);
					}
					if (parmsValid && BindingUtil.isUnresolvedGenericType(op.getParameters().get(1).getType())) {
						Type t = BindingUtil.resolveGenericType(op.getParameters().get(1).getType(), lhsType);
						parmsValid = IRUtils.isMoveCompatible(t, rhsType, rhsMember);
					}
					
					if (parmsValid) {
						Type opType = op.getType();
						if (BindingUtil.isUnresolvedGenericType(opType)) {
							opType = BindingUtil.resolveGenericType(opType, lhsType);
						}
						resolvedRHSType = opType;
					}
				}
			}
			else {
				resolvedRHSType = BindingUtil.resolveGenericType(rhsType, rhs);
			}
			
			if (resolvedRHSType == null || (!IRUtils.isMoveCompatible(lhsType, resolvedRHSType, rhsMember) && !TypeUtils.isDynamicType(resolvedRHSType))) {
				problemRequestor.acceptProblem(rhs,
						IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
						new String[] {lhsType != null ? StatementValidator.getShortTypeString(lhsType) : lhs.getCanonicalString(),
						rhsType != null ? StatementValidator.getShortTypeString(rhsType):rhs.getCanonicalString(),
						lhs.getCanonicalString() + " " + assignmentOperator.toString() + " " + rhs.getCanonicalString()});
			}
		}
		
		if (lhsMember != null) {
			// Concatenation assignmet is special case. myarr ::= element is really just an append, so we do not have to validate the LHS.
			if (assignmentOperator != Assignment.Operator.CONCAT) {
				new LValueValidator(problemRequestor, compilerOptions, lhsMember, lhs, lvalueValidationRules).validate();
			}
			
			// Validate the LHS of complex assignments as if the LHS was on ther RHS. This is because expressions like x &= y is the same as x = x & y
			if (assignmentOperator != Assignment.Operator.ASSIGN) {
				new RValueValidator(problemRequestor, compilerOptions, lhsMember, lhs).validate();
			}
		}
		
		if (rhsMember != null) {
			new RValueValidator(problemRequestor, compilerOptions, rhsMember, rhs).validate();
		}
		
		
		if (lhsMember != null && rhsType != null) {
			if (TypeUtils.Type_NULLTYPE.equals(rhsType) && !lhsMember.isNullable()) {
				problemRequestor.acceptProblem(lhs,
						IProblemRequestor.CANNOT_ASSIGN_NULL,
						new String[] {lhs.getCanonicalString()});
			}
		}
		
		return false;
	}
}
