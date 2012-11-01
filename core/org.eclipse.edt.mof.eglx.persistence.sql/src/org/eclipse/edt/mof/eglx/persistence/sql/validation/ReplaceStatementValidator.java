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
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.WithExpressionClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLResourceKeys;

public class ReplaceStatementValidator extends AbstractSqlStatementValidator {
	ReplaceStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	UsingClause using;
	UsingKeysClause usingKeys;
	FromOrToExpressionClause to;
	WithInlineSQLClause withInline;
	WithExpressionClause withExpression;
	ForExpressionClause forExpression;
	
	public ReplaceStatementValidator(ReplaceStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super();
		this.statement = statement;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void validate() {
		initialize();
		
		validateTarget();
		validateDataSource();
		validateFor();
	}
	
	private void validateTarget() {
		Expression target = statement.getRecord();
		Type targetType = target.resolveType();
		if (targetType == null) {
			return;
		}
		if (TypeUtils.isPrimitive(targetType)) {
			targetType = getContainingType(target.resolveMember());
		}
		
		// Target must be a data expression.
		if (!isDataExpr(target)
				//TODO arrays for replace not yet supported.
				|| targetType instanceof ArrayType
				) {
			problemRequestor.acceptProblem(target,
					SQLResourceKeys.SQL_TARGET_NOT_DATA_EXPR,
					IMarker.SEVERITY_ERROR,
					new String[] {},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		//TODO associations not yet supported.
		if (isAssociationExpression(target)) {
			problemRequestor.acceptProblem(target,
					SQLResourceKeys.SQL_ENTITY_ASSOCIATIONS_NOT_SUPPORTED,
					IMarker.SEVERITY_ERROR,
					new String[] {},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		// Target must map to 1 table.
		if (targetType != null && !isSingleTable(targetType)) {
			problemRequestor.acceptProblem(target,
					SQLResourceKeys.SQL_SINGLE_TABLE_REQUIRED,
					IMarker.SEVERITY_ERROR,
					new String[]{},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		if (to != null) {
			// If data source is SQLDataSource and there's no WITH or USING, target must have @Id on a field.
			if (targetType != null
					&& (withExpression == null && withInline == null && Utils.isSQLDataSource(to.getExpression().resolveType()))
					&& !hasID(targetType)) {
				problemRequestor.acceptProblem(target,
						SQLResourceKeys.SQL_NO_ID_IN_TARGET_TYPE,
						IMarker.SEVERITY_ERROR,
						new String[] {targetType.getTypeSignature()},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
		}
	}
	
	private void validateFor() {
		if (forExpression != null) {
			// FOR not allowed when USING or WITH is specified.
			if (using != null || withExpression != null || withInline != null) {
				problemRequestor.acceptProblem(forExpression,
						SQLResourceKeys.SQL_FOR_NOT_ALLOWED,
						IMarker.SEVERITY_ERROR,
						new String[] {},
						SQLResourceKeys.getResourceBundleForKeys());
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
	
	private void validateDataSource() {
		if (to != null) {
			Type type = to.getExpression().resolveType();
			if (Utils.isSQLResultSet(type)) {
				// USING and WITH not allowed.
				if (withInline != null || withExpression != null || forExpression != null || using != null) {
					problemRequestor.acceptProblem(statement,
							SQLResourceKeys.SQL_NO_WITH_USING,
							IMarker.SEVERITY_ERROR,
							new String[]{"eglx.persistence.sql.SQLResultSet"},
							SQLResourceKeys.getResourceBundleForKeys());
					return;
				}
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
							new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});

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
							new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.KEYWORD_USING.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.KEYWORD_USINGKEYS.toUpperCase()});
				}
				return false;
			}
			
			public boolean visit(FromOrToExpressionClause clause) {
				if (to == null) {
					to = clause;
				}
				else {
					problemRequestor.acceptProblem(clause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.KEYWORD_TO.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase(), IEGLConstants.KEYWORD_FOR.toUpperCase()});
				}
				return false;
			}
		});
	}
}
