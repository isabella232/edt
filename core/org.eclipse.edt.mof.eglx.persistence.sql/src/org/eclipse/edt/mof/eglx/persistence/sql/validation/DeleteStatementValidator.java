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
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
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

public class DeleteStatementValidator extends AbstractSqlStatementValidator {
	DeleteStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	UsingClause using;
	UsingKeysClause usingKeys;
	FromOrToExpressionClause from;
	WithInlineSQLClause withInline;
	WithExpressionClause withExpression;
	ForExpressionClause forExpression;
	
	public DeleteStatementValidator(DeleteStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
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
		Expression target = statement.getTarget();
		if (target != null) {
			Type targetType = target.resolveType();
			if (targetType != null && TypeUtils.isPrimitive(targetType)) {
				targetType = getContainingType(target.resolveMember());
			}
			
			// Target must be a data expression.
			if (!isDataExpr(target)
					//TODO arrays for delete not yet supported.
					|| (targetType instanceof ArrayType)
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
			
			if (from != null) {
				// If data source is SQLDataSource and there's no WITH or USING, target must have @Id on a field.
				if (targetType != null
						&& (withExpression == null && withInline == null && using == null && Utils.isSQLDataSource(from.getExpression().resolveType()))
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
	}
	
	private void validateDataSource() {
		if (statement.getTarget() == null && from != null) {
			
			Type type = from.getExpression().resolveType();
			if (type != null && Utils.isSQLDataSource(type)) {
				// WITH is required.
				if (withInline == null && withExpression == null) {
					problemRequestor.acceptProblem(statement,
							SQLResourceKeys.SQL_WITH_STMT_REQUIRED_FOR_DELETE,
							IMarker.SEVERITY_ERROR,
							new String[]{"eglx.persistence.sql.SQLDataSource"},
							SQLResourceKeys.getResourceBundleForKeys());
					return;
				}
			}
			else if (type != null && Utils.isSQLResultSet(type)) {
				// USING, WITH, and FOR not allowed.
				if (withInline != null || withExpression != null || forExpression != null || using != null) {
					problemRequestor.acceptProblem(statement,
							SQLResourceKeys.SQL_NO_WITH_FOR_USING,
							IMarker.SEVERITY_ERROR,
							new String[]{"eglx.persistence.sql.SQLResultSet"},
							SQLResourceKeys.getResourceBundleForKeys());
					return;
				}
			}
		}
	}
	
	private void validateFor() {
		if (forExpression != null) {
			if (statement.getTarget() == null) {
				// For not allowed if there's no target.
				problemRequestor.acceptProblem(forExpression,
						SQLResourceKeys.SQL_FOR_NOT_ALLOWED_WITHOUT_TARGET,
						IMarker.SEVERITY_ERROR,
						new String[] {},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
			else {
				if (withExpression != null || withInline != null) {
					problemRequestor.acceptProblem(statement,
							SQLResourceKeys.SQL_DELETE_FOR_OR_WITH,
							IMarker.SEVERITY_ERROR,
							new String[] {},
							SQLResourceKeys.getResourceBundleForKeys());
					return;
				}
				
				Type type = forExpression.getExpression().resolveType();
				if (type != null && (!isEntityWithID(type)
						// TODO associations not supported yet. when they are, change it to the commented out line.
						|| isAssociationExpression(forExpression.getExpression())
//							&& !isAssociationExpression(forExpression.getExpression())
						)) {
					problemRequestor.acceptProblem(forExpression.getExpression(),
							SQLResourceKeys.SQL_FOR_TYPE_INVALID,
							IMarker.SEVERITY_ERROR,
							new String[] {forExpression.getExpression().getCanonicalString()},
							SQLResourceKeys.getResourceBundleForKeys());
					return;
				}
				
				if (type != null) {
					// The type of the expression must match exactly the type of the action target.
					Type targetType = statement.getTarget().resolveType();
					if (targetType != null && !targetType.equals(type)) {
						problemRequestor.acceptProblem(forExpression.getExpression(),
								SQLResourceKeys.SQL_FOR_AND_TARGET_TYPES_MUST_MATCH,
								IMarker.SEVERITY_ERROR,
								new String[] {forExpression.getExpression().getCanonicalString(), type.getTypeSignature(), targetType.getTypeSignature()},
								SQLResourceKeys.getResourceBundleForKeys());
						return;
					}
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
							new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});

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
							new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.KEYWORD_USING.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.KEYWORD_USINGKEYS.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.KEYWORD_FROM.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase(), IEGLConstants.KEYWORD_FOR.toUpperCase()});
				}
				return false;
			}
		});
	}
}
