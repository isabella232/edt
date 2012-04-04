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

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * @author Dave Murray
 */
public class ForEachStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
	
	private IProblemRequestor problemRequestor;
	
	public ForEachStatementValidator(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
	}
	
	private void checkTargetIsSQLRecord(Expression expr) {
		ITypeBinding tBinding = expr.resolveTypeBinding();
		if(tBinding != null && tBinding.getAnnotation(EGLIOSQL, "SQLRecord") == null) {
			problemRequestor.acceptProblem(
				expr,
				IProblemRequestor.STATEMENT_TARGET_NOT_SQL_RECORD,
				new String[] {expr.getCanonicalString()});
		}
	}
	
	public boolean visit(ForEachStatement forEachStatement) {
		if(forEachStatement.hasSQLRecord()) {
			checkTargetIsSQLRecord(forEachStatement.getSQLRecord());
		}
		
		forEachStatement.accept(new AbstractASTVisitor(){
			public boolean visit (IntoClause intoClause){
				intoClause.accept(new AbstractASTExpressionVisitor(){
					public boolean visitExpression(Expression expression) {
						StatementValidator.validateItemInIntoClause(expression,problemRequestor);
					    return false;
					}
					
					});
				return false;
			}
			});
		return false;
	}
}
