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

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class ForStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	
	public ForStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
	}
	
	@Override
	public boolean visit(final ForStatement forStatement) {
		forStatement.accept(new AbstractASTExpressionVisitor() {
			@Override
			public boolean visitExpression(Expression expr) {
				Map<Expression, Type> rhsExprMap = new HashMap<Expression, Type>();
				TypeValidator.collectExprsForTypeCompatibility(expr, rhsExprMap);
				for (Map.Entry<Expression, Type> entry : rhsExprMap.entrySet()) {
					Type tBinding = entry.getValue();
					if (tBinding != null){
						if (!IRUtils.isMoveCompatible(TypeUtils.Type_INT, tBinding, entry.getKey().resolveMember())) {
							if (expr == forStatement.getCounterVariable() || expr == forStatement.getVariableDeclarationName()) {
								problemRequestor.acceptProblem(entry.getKey(),
										IProblemRequestor.FOR_STATEMENT_COUNTER_MUST_BE_INT);
							}
							else {
								String insert = "";
								if (expr == forStatement.getFromIndex()) {
									insert = "start";
								}
								else if (expr == forStatement.getEndIndex()) {
									insert = "end";
								}
								else if (expr == forStatement.getDeltaExpression()) {
									insert = "delta";
								}
								
								if (insert.length() > 0) {
									problemRequestor.acceptProblem(entry.getKey(),
										IProblemRequestor.FOR_STATEMENT_EXPR_MUST_BE_INT,
										new String[]{insert});
								}
							}
						}
					}
				}
			    return false;
			}
		});
		
		return false;
	}
}
