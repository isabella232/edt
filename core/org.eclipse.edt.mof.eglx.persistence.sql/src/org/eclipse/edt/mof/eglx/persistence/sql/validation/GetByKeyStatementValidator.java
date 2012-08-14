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

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.WithExpressionClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.type.TypeValidator;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLResourceKeys;

public class GetByKeyStatementValidator extends AbstractSqlStatementValidator {
	GetByKeyStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	UsingClause using;
	UsingKeysClause usingKeys;
	FromOrToExpressionClause from;
	WithInlineSQLClause withInline;
	WithExpressionClause withExpression;
	IntoClause into;
	
	public GetByKeyStatementValidator(GetByKeyStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super();
		this.statement = statement;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void validate() {
		initialize();
		
		validateTargets();
		validateFrom();
		validateInto();
		
		//TODO when associations are supported, add the following validation:
		// "If the action target is an association_expr no WITH clause is allowed and the data source must be of type SQLDataSource, i.e. it cannot be an SQLResultSet."
	}
	
	private void validateTargets() {
		// target can be a data expression (record, dictionary, etc), or a scalar list of primitives that map to table columns.
		boolean isDataExpr = false;
		List targets = statement.getTargets();
		if (targets.size() == 1) {
			Object o = targets.get(0);
			if (o instanceof Expression) {
				Expression expr = (Expression)o;
				if (isDataExpr(expr)) {
					// Associations are not yet supported.
					if (isAssociationExpression(expr)) {
						problemRequestor.acceptProblem(expr,
								SQLResourceKeys.SQL_ENTITY_ASSOCIATIONS_NOT_SUPPORTED,
								IMarker.SEVERITY_ERROR,
								new String[] {},
								SQLResourceKeys.getResourceBundleForKeys());
						return;
					}
					
					// If it's a nullable entity, it must have a public default constructor.
					if ((expr instanceof TypedElement && ((TypedElement)expr).isNullable()) && isEntity(expr.resolveType()) && !TypeValidator.hasPublicDefaultConstructor(expr.resolveType())) {
						problemRequestor.acceptProblem(expr,
								SQLResourceKeys.SQL_NULLABLE_TARGET_MISSING_DEFAULT_CONSTRUCTOR,
								IMarker.SEVERITY_ERROR,
								new String[] {expr.getCanonicalString(), expr.resolveType().getTypeSignature()},
								SQLResourceKeys.getResourceBundleForKeys());
						return;
					}
					
					isDataExpr = true;
				}
			}
		}
		
		if (!isDataExpr && !mapsToColumns(targets)) {
			int[] offsets = getOffsets(targets);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					SQLResourceKeys.SQL_TARGET_MUST_BE_DATA_EXPR_OR_COLUMNS,
					true,
					new String[] {},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
		else if (!isDataExpr && withExpression == null && withInline == null && !(mapsToSingleTable(targets) || isFromResultSet()) ) {
			// WITH required when the columns do not map to a single table.
			int[] offsets = getOffsets(targets);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					SQLResourceKeys.SQL_STMT_REQUIRED_FOR_NON_SINGLE_TABLE,
					true,
					new String[] {IEGLConstants.KEYWORD_WITH},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		if (using == null && withExpression == null && withInline == null) {
			// When no USING or WITH, a field in the type of the target must have @Id.
			Type targetType = getTargetType(isDataExpr);
			if (targetType != null && !hasID(targetType)) {
				int[] offsets = getOffsets(targets);
				problemRequestor.acceptProblem(offsets[0], offsets[1],
						SQLResourceKeys.SQL_NO_ID_IN_TARGET_TYPE,
						true,
						new String[] {targetType.getTypeSignature()},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
		}
	}
	
	private Type getTargetType(boolean isDataExpr) {
		Type type = null;
		List targets = statement.getTargets();
		if (isDataExpr || mapsToSingleTable(targets)) {
			Expression e = (Expression)targets.get(0);
			type = e.resolveType();
			if (type != null && TypeUtils.isPrimitive(type)) {
				type = getContainingType(e.resolveMember());
			}
		}
		
		if (type instanceof ArrayType) {
			type = ((ArrayType)type).getElementType();
		}
		
		return type;
	}
	
	private boolean isFromResultSet() {
		if (from != null) {
			// When WITH is specified, FROM must be SQLDataSource.
			return Utils.isSQLResultSet(from.getExpression().resolveType());
		}
		return false;
	}
	
	private void validateFrom() {
		if (from != null && (withExpression != null || withInline != null)) {
			// When WITH is specified, FROM must be SQLDataSource.
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
	
	private void validateInto() {
		if (into != null) {
			// INTO not currently part of the spec.
			problemRequestor.acceptProblem(into, SQLResourceKeys.SQL_INTO_NOT_ALLOWED, IMarker.SEVERITY_ERROR, new String[] {}, SQLResourceKeys.getResourceBundleForKeys());
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
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});

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
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_USING.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_USINGKEYS.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_FROM.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_INTO.toUpperCase()});
				}
				return false;
			}
		});
	}
}
