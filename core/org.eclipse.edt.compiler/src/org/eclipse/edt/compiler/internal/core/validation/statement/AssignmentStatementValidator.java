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

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.AssignmentStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;


/**
 * @author Dave Murray
 */
public class AssignmentStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	private IPartBinding enclosingPart;
	
	public AssignmentStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, IPartBinding enclosingPart) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.enclosingPart = enclosingPart;
	}
	
	public boolean visit(AssignmentStatement assignmentStatement) {
		Assignment assignment = assignmentStatement.getAssignment();
		Expression lhs = assignment.getLeftHandSide();
		Expression rhs = assignment.getRightHandSide();
		ITypeBinding lhsBinding = lhs.resolveTypeBinding();
		ITypeBinding rhsBinding = rhs.resolveTypeBinding();
		IDataBinding lhsDataBinding = lhs.resolveDataBinding();
		IDataBinding rhsDataBinding = rhs.resolveDataBinding();
		
		return validateAssignment(assignmentStatement.getAssignment().getOperator(), lhs,rhs,lhsBinding,rhsBinding,lhsDataBinding,rhsDataBinding,false, DefaultBinder.isArithmeticAssignment(assignment));
	}
	
	public boolean validateAssignment(final Assignment.Operator assignmentOperator, final Expression lhs,final Expression rhs , ITypeBinding lhsBinding,
			final ITypeBinding rhsBinding,final IDataBinding lhsDataBinding ,final IDataBinding rhsDataBinding,boolean bDeclaration, boolean isArithmetic){
		return validateAssignment(assignmentOperator, lhs, rhs, lhsBinding, rhsBinding, lhsDataBinding, rhsDataBinding, bDeclaration, isArithmetic, new LValueValidator.DefaultLValueValidationRules());
	}
	
	public boolean validateAssignment(final Assignment.Operator assignmentOperator, final Expression lhs,final Expression rhs , ITypeBinding lhsBinding,
			ITypeBinding rhsBinding,final IDataBinding lhsDataBinding ,final IDataBinding rhsDataBinding,boolean bDeclaration, boolean isArithmetic, LValueValidator.ILValueValidationRules lvalueValidationRules){
		
		if (lhs instanceof SubstringAccess) {
			problemRequestor.acceptProblem(lhs,
					IProblemRequestor.SUBSTRING_IMMUTABLE,
					new String[] {});
		}
		
		
		// string += anything   is always compatible
		if(Assignment.Operator.PLUS == assignmentOperator &&
				isStringType(lhsBinding)) {
			
			
			if(StatementValidator.isValidBinding(lhsDataBinding)) {
				new LValueValidator(problemRequestor, compilerOptions, lhsDataBinding, lhs, lvalueValidationRules).validate();

				//Must validate the LHS as being on the rhs, because x += 2 is the same as x = x + 2;
				new RValueValidator(problemRequestor, compilerOptions, lhsDataBinding, lhs).validate();
			}

			
			if(StatementValidator.isValidBinding(rhsDataBinding)) {
				new RValueValidator(problemRequestor, compilerOptions, rhsDataBinding, rhs).validate();
			}
			
			return false;
		}

		
		if((Assignment.Operator.CONCAT == assignmentOperator) &&
		   StatementValidator.isValidBinding(lhsBinding) &&
		   ITypeBinding.ARRAY_TYPE_BINDING == lhsBinding.getKind()) {
			if(!StatementValidator.isValidBinding(rhsBinding) || ITypeBinding.ARRAY_TYPE_BINDING != rhsBinding.getKind()) {
				lhsBinding = ((ArrayTypeBinding) lhsBinding).getElementType();
			}
		}

		if(Assignment.Operator.XOR == assignmentOperator ||
				Assignment.Operator.OR == assignmentOperator ||
				Assignment.Operator.AND == assignmentOperator ||
				Assignment.Operator.LEFT_SHIFT == assignmentOperator ||
				Assignment.Operator.RIGHT_SHIFT_ARITHMETIC == assignmentOperator ||
				Assignment.Operator.RIGHT_SHIFT_LOGICAL == assignmentOperator) {
					rhsBinding = PrimitiveTypeBinding.getInstance(Primitive.INT);
			}

		if (StatementValidator.isValidBinding(lhsBinding) && lhsBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null &&
			StatementValidator.isValidBinding(rhsBinding) && rhsBinding.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null	){
			problemRequestor.acceptProblem(lhs,
					IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
					new String[] {IEGLConstants.RECORD_SUBTYPE_PSB_RECORD,IEGLConstants.RECORD_SUBTYPE_PSB_RECORD,
					lhs.getCanonicalString() + " = " + rhs.getCanonicalString()});
			return false;
		}
		
		if (StatementValidator.isValidBinding(rhsBinding) && 
			(rhsBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING || rhsBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING)){
			boolean isError = !StatementValidator.isValidBinding(lhsBinding) ;
			if (!isError){
				boolean isRecord = lhsBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || lhsBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING;
				boolean isChar = false;
				if (lhsBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
					Primitive type = ((PrimitiveTypeBinding)lhsBinding).getPrimitive();
					if (type == Primitive.CHAR ||
							type == Primitive.MBCHAR ||
							type == Primitive.HEX ||
							type == Primitive.ANY ||
							type == Primitive.DBCHARLIT){
						isChar = true;
					}
				}
				
				if (!isRecord && !isChar){
					isError = true;
				}
				
			}
			
			if (isError){
				problemRequestor.acceptProblem(rhs,
						IProblemRequestor.ASSIGNMENT_STATEMENT_RECORD_SOURCE_TARGET_MUST_BE,
						new String[]{rhs.getCanonicalString(),lhs.getCanonicalString()} );
				return false;
			}
		}
		
		if (StatementValidator.isValidBinding(lhsBinding) && lhsBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){
			if (StatementValidator.isValidBinding(rhsBinding) && rhsBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING){
				problemRequestor.acceptProblem(rhs,
						IProblemRequestor.FIXED_RECORD_ASSIGNED_TO_FLEXIBLE);
				return false;
			}
		}
		
		if (StatementValidator.isValidBinding(lhsBinding) && lhsBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING){
			if (StatementValidator.isValidBinding(rhsBinding) && rhsBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){
				problemRequestor.acceptProblem(rhs,
						IProblemRequestor.FLEXIBLE_RECORD_ASSIGNED_TO_FIXED);
				return false;
			}
			
			if (StatementValidator.isValidBinding(rhsBinding) && rhsBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
				Primitive type = ((PrimitiveTypeBinding)rhsBinding).getPrimitive();
				if (type != Primitive.CHAR &&
						type != Primitive.MBCHAR &&
						type != Primitive.HEX &&
						type != Primitive.ANY &&
						type != Primitive.DBCHARLIT &&
						!(type == Primitive.STRING && ((PrimitiveTypeBinding)rhsBinding).getLength() != 0)
						){
					problemRequestor.acceptProblem(rhs,
							IProblemRequestor.ASSIGNMENT_STATEMENT_RECORD_TARGET_SOURCE_CANNOT_BE,
							new String[]{lhs.getCanonicalString()} );
					return false;
					
				}
				
			}
		}
		
		if (StatementValidator.isValidBinding(lhsBinding) && lhsBinding.getKind() == ITypeBinding.DICTIONARY_BINDING ){
			final Expression expr = lhs;
			ErrorASTVisitor astVisitor = new ErrorASTVisitor(){
				public boolean visit(SimpleName simpleName) {
		        	if (StatementValidator.isValidBinding(simpleName.resolveTypeBinding()) && simpleName.resolveTypeBinding().getKind() == ITypeBinding.ARRAYDICTIONARY_BINDING){
		    			problemRequestor.acceptProblem(expr,
		    					IProblemRequestor.CANNOT_ASSIGN_TO_ARRAY_DICTIONARY_ELEMENTS);
		    			error = true;
		        	}
		            return false;
		        }
		    };
		    
			lhs.accept(astVisitor);
			
			if (astVisitor.hasError()){
				return false;
			}

		}
		
		if (StatementValidator.isValidBinding(lhsBinding) && StatementValidator.isValidBinding(rhsBinding)){
			if (StatementValidator.isArrayOrMultiplyOccuring(lhsDataBinding) && StatementValidator.isArrayOrMultiplyOccuring(rhsDataBinding)){
				if(ITypeBinding.MULTIPLY_OCCURING_ITEM == lhsBinding.getKind() && ITypeBinding.MULTIPLY_OCCURING_ITEM != rhsBinding.getKind() ||
				   ITypeBinding.MULTIPLY_OCCURING_ITEM == rhsBinding.getKind() && ITypeBinding.MULTIPLY_OCCURING_ITEM != lhsBinding.getKind()) {
	    			problemRequestor.acceptProblem(rhs,
	    					IProblemRequestor.ARRAYS_AND_OCCURED_ITEMS_ARE_NOT_COMPATIBLE,
	    					new String[]{lhsDataBinding.getCaseSensitiveName(),rhsDataBinding.getCaseSensitiveName()} );	
	    			return false;
				}
			}
		}

		if(StatementValidator.isValidBinding(lhsBinding) &&
	       ITypeBinding.MULTIPLY_OCCURING_ITEM == lhsBinding.getKind()) {
			if(StatementValidator.isValidBinding(rhsBinding) && !rhsBinding.isDynamic()) {
				problemRequestor.acceptProblem(rhs,
    				IProblemRequestor.OCCURED_ITEMS_ONLY_COMPATIBLE_WITH_ANY,
    				new String[]{lhs.getCanonicalString(),rhs.getCanonicalString()} );	
    			return false;
			}
		}
		
		if(StatementValidator.isValidBinding(rhsBinding) &&
	       ITypeBinding.MULTIPLY_OCCURING_ITEM == rhsBinding.getKind()) {
			if(StatementValidator.isValidBinding(lhsBinding) && !lhsBinding.isDynamic()) {
				problemRequestor.acceptProblem(rhs,
    				IProblemRequestor.OCCURED_ITEMS_ONLY_COMPATIBLE_WITH_ANY,
					new String[]{lhs.getCanonicalString(),rhs.getCanonicalString()} );	
    			return false;
			}
		}
		
		//Arithmetic assignments are already validated in DefaultBinder
		if (!isArithmetic) {
		
			boolean compatible = TypeCompatibilityUtil.isMoveCompatible(lhsBinding, rhsBinding, rhs, compilerOptions) ||
				StatementValidator.isValidBinding(rhsBinding) && (
					rhsBinding.isDynamic() ||
					TypeCompatibilityUtil.areCompatibleExceptions(rhsBinding, lhsBinding, compilerOptions)
				);
			if (!compatible ){//|| lhsBinding == null ||rhsBinding == null ){
				problemRequestor.acceptProblem(rhs,
						IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
						new String[] {lhsBinding != null ? StatementValidator.getShortTypeString(lhsBinding):lhs.getCanonicalString(),
						rhsBinding != null ? StatementValidator.getShortTypeString(rhsBinding):rhs.getCanonicalString(),
						lhs.getCanonicalString() + " = " + rhs.getCanonicalString()});
			}
		}
		
		if(StatementValidator.isValidBinding(lhsDataBinding)) {
			//concatenation assignmet is special case. myarr ::= element is really just an append, so we do not have to validate
			//the LHS of the assignment statement
			if (Assignment.Operator.CONCAT != assignmentOperator) {
				new LValueValidator(problemRequestor, compilerOptions, lhsDataBinding, lhs, lvalueValidationRules).validate();
			}
			
			//validate the LHS of all other types of assignments as if the LHS was on ther RHS. This is because expressions
			//like  x &= y is the same as x = x & y
			if (Assignment.Operator.ASSIGN != assignmentOperator) {
				new RValueValidator(problemRequestor, compilerOptions, lhsDataBinding, lhs).validate();
			}
		}
		
		
		
		if(StatementValidator.isValidBinding(rhsDataBinding)) {
			new RValueValidator(problemRequestor, compilerOptions, rhsDataBinding, rhs).validate();
		}
		
		if (Binding.isValidBinding(lhsBinding) && Binding.isValidBinding(rhsBinding)) {
			
			if (rhsBinding.getKind() == ITypeBinding.NIL_BINDING && !lhsBinding.isNullable()) {
				problemRequestor.acceptProblem(lhs,
						IProblemRequestor.CANNOT_ASSIGN_NULL,
						new String[] {lhs.getCanonicalString()});
			}
		}
		
		return false;
	}
	
	private String getCanonicalStringNoSubscripts(Expression expr) {
		final String[] result = new String[] {null};
		expr.accept(new AbstractASTExpressionVisitor() {
			public boolean visitExpression(Expression expression) {
				result[0] = expression.getCanonicalString();
				return false;
			}
			
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			
			public boolean visit(ArrayAccess arrayAccess) {
				arrayAccess.getArray().accept(this);
				return false;
			}
			
			public boolean visit(SubstringAccess substringAccess) {
				substringAccess.getPrimary().accept(this);
				return false;
			}
		});
		return result[0];
	}
	
	private class ErrorASTVisitor extends AbstractASTVisitor{
		boolean error = false;
		public boolean hasError(){
			return error;
		}
	}
	
	private boolean isStringType(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false;
		}
		return type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING &&
		       Primitive.isStringType(((PrimitiveTypeBinding) type).getPrimitive());
	}

}
