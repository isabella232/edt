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
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.WithExpressionClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLResourceKeys;

public class OpenStatementValidator extends AbstractSqlStatementValidator{
	OpenStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	ForUpdateClause forUpdate;
	IntoClause into;
	WithInlineSQLClause withInline;
	WithExpressionClause withExpression;
	UsingClause using;
	UsingKeysClause usingKeys;
	ForExpressionClause forExpression;
	FromOrToExpressionClause from;
	
	public OpenStatementValidator(OpenStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
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
		validateFor();
		validateInto();
	}
	
	private void validateTarget() {
		Type targetType = statement.getResultSet().resolveType();
		if (targetType != null && !Utils.isSQLResultSet(targetType)) {
			problemRequestor.acceptProblem(statement.getResultSet(),
					SQLResourceKeys.SQL_EXPR_HAS_WRONG_TYPE,
					IMarker.SEVERITY_ERROR,
					new String[] {statement.getResultSet().getCanonicalString(), "eglx.persistence.sql.SQLResultSet"},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
	}
	
	private void validateFrom() {
		if (from != null) {
			// Must be of type SQLDataSource
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
		boolean isSqlStatement = false;
		
		if (withExpression != null) {
			Type type = withExpression.getExpression().resolveType();
			if (type != null && !Utils.isSQLStatement(type)) {
				problemRequestor.acceptProblem(withExpression.getExpression(),
						SQLResourceKeys.SQL_EXPR_HAS_WRONG_TYPE,
						IMarker.SEVERITY_ERROR,
						new String[] {withExpression.getExpression().getCanonicalString(), "eglx.persistence.sql.SQLStatement"},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
			else {
				isSqlStatement = true;
			}
		}

		// if no FROM clause is specified the WITH clause must be specified and must be 
		// referencing an expression of type SQLStatement.  In other words if a prepared 
		// statement is available there is no need for referencing the explicit datasource
		if (from == null && !isSqlStatement) {
			problemRequestor.acceptProblem(statement,
					SQLResourceKeys.SQL_WITH_STMT_REQUIRED,
					IMarker.SEVERITY_ERROR,
					new String[] {"eglx.persistence.sql.SQLStatement"},
					SQLResourceKeys.getResourceBundleForKeys());
		}
	}
	
	private void validateFor() {
		if (forExpression != null) {
			//If no WITH clause is specified the FOR clause can be specified 
			if (withExpression != null || withInline != null) {
				problemRequestor.acceptProblem(forExpression,
						SQLResourceKeys.SQL_FOR_NOT_ALLOWED,
						IMarker.SEVERITY_ERROR,
						new String[] {},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
			
			Type type = forExpression.getExpression().resolveType();
			if (type != null && (!isEntityWithID(type)
					// TODO associations not supported yet. when they are, change it to the commented out line.
					|| isAssociationExpression(forExpression.getExpression())
//						&& !isAssociationExpression(forExpression.getExpression())
					)) {
				problemRequestor.acceptProblem(forExpression.getExpression(),
						SQLResourceKeys.SQL_FOR_TYPE_INVALID,
						IMarker.SEVERITY_ERROR,
						new String[] {forExpression.getExpression().getCanonicalString()},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
		}
	}
	
	private void validateInto() {
		if (into != null) {
			// INTO not currently part of the spec.
			problemRequestor.acceptProblem(into, SQLResourceKeys.SQL_INTO_NOT_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {}, SQLResourceKeys.getResourceBundleForKeys());
		}
	}
	
	
	private void initialize() {
		statement.accept(new AbstractASTVisitor() {
			public boolean visit(ForUpdateClause forUpdateClause) {
				if (forUpdate == null) {
					forUpdate = forUpdateClause;
				}
				else {
					problemRequestor.acceptProblem(forUpdateClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_FORUPDATE.toUpperCase()});
				}
				
				return false;
			}
			
			public boolean visit(IntoClause intoClause) {
				if (into == null) {
					into = intoClause;
				}
				else {
					problemRequestor.acceptProblem(intoClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_INTO.toUpperCase()});
				}
				return false;
			}
			
			public boolean visit(WithInlineSQLClause withInlineSQLClause) {
				if (withInline == null && withExpression == null) {
					withInline = withInlineSQLClause;
				}
				else {
					problemRequestor.acceptProblem(withInlineSQLClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});

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
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});
				}
				return false;
				
			}
			
			public boolean visit(UsingClause usingClause) {
				if (using == null) {
					using = usingClause;
				}
				else {
					problemRequestor.acceptProblem(usingClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_USING.toUpperCase()});
				}
				return false;
			}
			
			public boolean visit(UsingKeysClause usingKeysClause) {
				if (usingKeys == null) {
					usingKeys = usingKeysClause;
				}
				else {
					problemRequestor.acceptProblem(usingKeysClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_USINGKEYS.toUpperCase()});
				}
				return false;
			}
			
			public boolean visit(ForExpressionClause forExpressionClause) {
				if (forExpression == null) {
					forExpression = forExpressionClause;
				}
				else {
					problemRequestor.acceptProblem(forExpressionClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_FOR.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_FROM.toUpperCase()});
				}
				return false;
			}
		});
	}
}
