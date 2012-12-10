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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.AssignmentStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.FunctionMember;
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
	
	@Override
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
		
		if (lhsType != null) {
			Map<Expression, Type> resolvedRHSMap = new HashMap<Expression, Type>();
			Map<Expression, Type> errors = new HashMap<Expression, Type>();
			
			Map<Expression, Type> exprMap = new HashMap<Expression, Type>();
			TypeValidator.collectExprsForTypeCompatibility(rhs, exprMap);
			
			// For complex assignments like "x &= y" we must treat it as if it was coded "x = x & y". To do this, retrieve the operation and use its type.
			if (assignmentOperator != Assignment.Operator.ASSIGN) {
				String symbol = assignmentOperator.toString().substring(0, assignmentOperator.toString().length() - 1);
				
				for (Map.Entry<Expression, Type> entry : exprMap.entrySet()) {
					Operation op = IRUtils.getBinaryOperation(lhsType.getClassifier(), entry.getValue() == null ? entry.getKey().resolveMember() : entry.getValue().getClassifier(), symbol);
					if (op != null) {
						// If the parameters are generic, we need to validate the arg type vs the resolved parm type (which comes from the lhs type).
						boolean parmsValid = true;
						if (BindingUtil.isUnresolvedGenericType(op.getParameters().get(0).getType())) {
							Type t = BindingUtil.resolveGenericType(op.getParameters().get(0).getType(), lhsType);
							parmsValid = BindingUtil.isMoveCompatible(t, op.getParameters().get(0), lhsType, lhs);
						}
						if (parmsValid && BindingUtil.isUnresolvedGenericType(op.getParameters().get(1).getType())) {
							Type t = BindingUtil.resolveGenericType(op.getParameters().get(1).getType(), lhsType);
							parmsValid = BindingUtil.isMoveCompatible(t, op.getParameters().get(1), entry.getValue(), entry.getKey());
						}
						
						if (parmsValid) {
							Type opType = op.getType();
							if (BindingUtil.isUnresolvedGenericType(opType)) {
								opType = BindingUtil.resolveGenericType(opType, lhsType);
							}
							resolvedRHSMap.put(entry.getKey(), opType);
						}
						else {
							errors.put(entry.getKey(), entry.getValue());
						}
					}
					else {
						errors.put(entry.getKey(), entry.getValue());
					}
				}
			}
			else {
				// Just check each expr below.
				resolvedRHSMap = exprMap;
			}
			
			if (resolvedRHSMap.size() == 0 && !(rhsMember instanceof FunctionMember)) {
				if (rhsType != null) {
					errors.put(rhs, rhsType);
				}
			}
			else {
				for (Map.Entry<Expression, Type> entry : resolvedRHSMap.entrySet()) {
					if (!BindingUtil.isMoveCompatible(lhsType, lhsMember, entry.getValue(), entry.getKey())) {
						errors.put(entry.getKey(), entry.getValue());
					}
				}
			}
			
			for (Map.Entry<Expression, Type> entry : errors.entrySet()) {
				// Could be we're assigning null to an array access, e.g. "nullableArray[1] = null;". Nullability comes from the array qualifier in this case.
				if (lhs instanceof ArrayAccess && TypeUtils.Type_NULLTYPE.equals(entry.getValue()) && lhsMember != null && !lhsMember.isNullable()) {
					Type qualType = ((ArrayAccess)lhs).getArray().resolveType();
					if (qualType instanceof ArrayType && ((ArrayType)qualType).elementsNullable()) {
						continue;
					}
				}
				
				problemRequestor.acceptProblem(entry.getKey(),
						IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
						new String[] {lhsType != null ? BindingUtil.getShortTypeString(lhsType) : lhs.getCanonicalString(),
						BindingUtil.getShortTypeString(entry.getKey(), entry.getValue()),
						lhs.getCanonicalString() + " " + assignmentOperator.toString() + " " + entry.getKey().getCanonicalString()});
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
		
		return false;
	}
}
