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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.ReturnStatement;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Type;

public class ReturnStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	
	public ReturnStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
	}
	
	@Override
	public boolean visit(final ReturnStatement returnStatement) {
		Node current = returnStatement.getParent();
		final FunctionMember[] fBinding = new Function[1];
		ParentASTVisitor visitor = new ParentASTVisitor() {
			@Override
			public boolean visit(NestedFunction nFunction) {
				bcontinue = false;
				fBinding[0] = (Function)nFunction.getName().resolveMember();
				if (fBinding[0] != null) {
					returnField = ((Function)fBinding[0]).getReturnField();
				}
				return false;
			}
		};

		while ((current != null) && visitor.canContinue() ) {
			current.accept(visitor);
			current = current.getParent();
		}	
		
		if (returnStatement.getParenthesizedExprOpt() != null && !visitor.hasReturnType()){
			problemRequestor.acceptProblem(returnStatement,
					IProblemRequestor.RETURN_VALUE_WO_RETURN_DEF);
		}
		
		validateNoSetValues(returnStatement);
		
		if (visitor.hasReturnType() && returnStatement.getParenthesizedExprOpt() != null) {
			Map<Expression, Type> rhsExprMap = new HashMap<Expression, Type>();
			TypeValidator.collectExprsForTypeCompatibility(returnStatement.getParenthesizedExprOpt(), rhsExprMap);
			
			for (Map.Entry<Expression, Type> entry : rhsExprMap.entrySet()) {
				boolean compatible = BindingUtil.isMoveCompatible(visitor.getReturnType(), visitor.getReturnField(), entry.getValue(), entry.getKey());
				if (!compatible) {
					problemRequestor.acceptProblem(entry.getKey(),
							IProblemRequestor.RETURN_STATEMENT_TYPE_INCOMPATIBLE,
							new String[] {
									BindingUtil.getShortTypeString(entry.getKey(), entry.getValue()),
									BindingUtil.getShortTypeString(visitor.getReturnType())});
				}
			}
		}
		
		if (returnStatement.getParenthesizedExprOpt() != null) {
			returnStatement.getParenthesizedExprOpt().accept(new DefaultASTVisitor() {
				@Override
				public boolean visit(AnnotationExpression annotationExpression) {
					problemRequestor.acceptProblem(
						annotationExpression.getOffset(),
						annotationExpression.getOffset()+1,
						IMarker.SEVERITY_ERROR,
						IProblemRequestor.UNEXPECTED_TOKEN,
						new String[] {"@"});
					return false;
				}
				@Override
				public boolean visit(ParenthesizedExpression parenthesizedExpression) {
					return true;
				}
			});
		}
		
		return false;
	}

	protected void validateNoSetValues(ReturnStatement returnStatement){
		returnStatement.accept(new AbstractASTVisitor(){
			@Override
			 public boolean visit(SetValuesExpression setValuesExpression) {
				problemRequestor.acceptProblem(setValuesExpression,
						IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_RETURN_ARG);
				return false;
			 }
		});

	}
	
	private class ParentASTVisitor extends AbstractASTVisitor {
		Field returnField;
		boolean bcontinue;
		public ParentASTVisitor (){
			bcontinue = true;
		}
		
		public boolean hasReturnType(){
			return returnField != null && returnField.getType() != null;
		}
		
		public Type getReturnType() {
			return returnField == null ? null : returnField.getType();
		}
		
		public Field getReturnField() {
			return returnField;
		}
		
		public boolean canContinue(){
			return bcontinue;
		}
	}
}
