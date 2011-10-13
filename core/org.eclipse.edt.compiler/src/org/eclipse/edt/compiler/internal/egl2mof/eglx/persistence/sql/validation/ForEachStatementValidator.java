/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public class ForEachStatementValidator extends AbstractSqlStatementValidator {
	ForEachStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	FromOrToExpressionClause from;

	public ForEachStatementValidator(ForEachStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super();
		this.statement = statement;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void validate() {
		initialize();
		
		validateIsEntityOrMapsToColumns(statement.getTargets(), problemRequestor);
		validateFrom();
	}
	
	private void validateFrom() {
		// If FROM wasn't specified, there will be a validation error already from the parser.
		if (from != null) {
			ITypeBinding type = from.getExpression().resolveTypeBinding();
			if (Binding.isValidBinding(type) && !isResultSet(type)) {
				problemRequestor.acceptProblem(from.getExpression(),
						IProblemRequestor.SQL_EXPR_HAS_WRONG_TYPE,
						new String[] {from.getExpression().getCanonicalString(), "eglx.persistence.sql.SQLResultSet"});
				return;
			}
		}
	}
	
	private void initialize() {
		statement.accept(new AbstractASTVisitor() {
			public boolean visit(FromOrToExpressionClause clause) {
				if (from == null) {
					from = clause;
				}
				else {
					problemRequestor.acceptProblem(clause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_FOREACH.toUpperCase(), IEGLConstants.KEYWORD_FROM.toUpperCase()});
				}
				return false;
			}
		});
	}
}
