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
package org.eclipse.edt.mof.eglx.persistence.sql.validation;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.WithExpressionClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLResourceKeys;

public class PrepareStatementValidator extends AbstractSqlStatementValidator {
	
	PrepareStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	WithInlineSQLClause withInline;
	WithExpressionClause withExpression;
	FromOrToExpressionClause from;
	
	public PrepareStatementValidator(PrepareStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super();
		this.statement = statement;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void validate() {
		initialize();
		
		validateTarget();
		validateFrom();
		validateWith();
	}
	
	private void validateTarget() {
		Type targetType = statement.getSqlStmt().resolveType();
		if (targetType != null && !Utils.isSQLStatement(targetType)) {
			problemRequestor.acceptProblem(statement.getSqlStmt(),
					SQLResourceKeys.SQL_EXPR_HAS_WRONG_TYPE,
					IMarker.SEVERITY_ERROR,
					new String[] {statement.getSqlStmt().getCanonicalString(), "eglx.persistence.sql.SQLStatement"},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
	}
	
	private void validateFrom() {
		// If FROM wasn't specified, there will be a validation error already from the parser.
		if (from != null) {
			Type type = from.getExpression().resolveType();
			if (type != null && !Utils.isSQLDataSource(type)) {
				problemRequestor.acceptProblem(from.getExpression(),
						SQLResourceKeys.SQL_EXPR_HAS_WRONG_TYPE,
						IMarker.SEVERITY_ERROR,
						new String[] {from.getExpression().getCanonicalString(), "eglx.persistence.sql.SQLDataSource"},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
		}
	}
	
	private void validateWith() {
		// If WITH wasn't specified, there will be a validation error already from the parser.
		if (withExpression != null) {
			Type type = withExpression.getExpression().resolveType();
			if (type != null && !type.equals(TypeUtils.Type_STRING)) {
				problemRequestor.acceptProblem(withExpression.getExpression(),
						SQLResourceKeys.SQL_EXPR_HAS_WRONG_TYPE,
						IMarker.SEVERITY_ERROR,
						new String[] {withExpression.getExpression().getCanonicalString(), IEGLConstants.KEYWORD_STRING},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
		}
	}
	
	private void initialize() {
		statement.accept(new AbstractASTVisitor() {
			public boolean visit(WithInlineSQLClause withInlineSQLClause) {
				if (withInline == null && withExpression == null) {
					withInline = withInlineSQLClause;
				}
				else {
					problemRequestor.acceptProblem(withInlineSQLClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_PREPARE.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});

				}
				return false;
			}
			
			public boolean visit(WithExpressionClause withExpressionClause) {
				if (withInline == null && withExpression == null) {
					withExpression = withExpressionClause;
				}
				else {
					problemRequestor.acceptProblem(withExpressionClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_PREPARE.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});
				}
				return false;
				
			}
			
			public boolean visit(FromOrToExpressionClause clause) {
				if (from == null) {
					from = clause;
				}
				else {
					problemRequestor.acceptProblem(clause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_PREPARE.toUpperCase(), IEGLConstants.KEYWORD_FROM.toUpperCase()});
				}
				return false;
			}
		});
	}
}
