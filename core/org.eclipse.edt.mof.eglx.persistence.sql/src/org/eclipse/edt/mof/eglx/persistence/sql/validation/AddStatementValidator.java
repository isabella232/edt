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
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLResourceKeys;

public class AddStatementValidator extends AbstractSqlStatementValidator {
	AddStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	FromOrToExpressionClause to;
	ForExpressionClause forExpression;
	
	public AddStatementValidator(AddStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super();
		this.statement = statement;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void validate() {
		initialize();
		
		validateTarget();
		validateFor();
	}
	
	private void validateTarget() {
		List exprs = statement.getTargets();
		boolean isEntity = false;
		
		// target can be a single Entity, or a scalar list of primitives that map to table columns.
		if (exprs.size() == 1) {
			Object o = exprs.get(0);
			if (o instanceof Expression) {
				Expression expr = (Expression)o;
				Type type = expr.resolveType();
				if (isEntity(type)) {
					// Associations are not yet supported.
					if (isAssociationExpression(expr)) {
						problemRequestor.acceptProblem(expr,
								SQLResourceKeys.SQL_ENTITY_ASSOCIATIONS_NOT_SUPPORTED,
								IMarker.SEVERITY_ERROR,
								new String[] {},
								SQLResourceKeys.getResourceBundleForKeys());
						return;
					}
					isEntity = true;
				}
			}
		}
		
		if (!isEntity && !mapsToColumns(exprs)) {
			int[] offsets = getOffsets(exprs);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					SQLResourceKeys.SQL_TARGET_MUST_BE_ENTITY_OR_COLUMNS,
					true,
					new String[] {},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
		else if (!isEntity && forExpression == null && !mapsToSingleTable(exprs)) {
			// FOR required when the columns do not map to a single table.
			int[] offsets = getOffsets(exprs);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					SQLResourceKeys.SQL_STMT_REQUIRED_FOR_NON_SINGLE_TABLE,
					true,
					new String[] {IEGLConstants.KEYWORD_FOR},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		Type targetType = getTargetType();
		if (targetType != null && !isSingleTable(targetType)) {
			int[] offsets = getOffsets(exprs);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					SQLResourceKeys.SQL_SINGLE_TABLE_REQUIRED,
					true,
					new String[]{},
					SQLResourceKeys.getResourceBundleForKeys());
			return;
		}
	}
	
	private void validateFor() {
		if (forExpression != null) {
			if (to != null) {
				// When the data source is a result set, FOR is not allowed.
				Type type = to.getExpression().resolveType();
				if (type != null && Utils.isSQLResultSet(type)) {
					problemRequestor.acceptProblem(forExpression,
							SQLResourceKeys.SQL_FOR_NOT_ALLOWED_WITH_DATA_SOURCE_TYPE,
							IMarker.SEVERITY_ERROR,
							new String[] {"eglx.persistence.sql.SQLResultSet"},
							SQLResourceKeys.getResourceBundleForKeys());
					return;
				}
			}
			
			Type type = forExpression.getExpression().resolveType();
			if (type != null) {
				if (!isEntityWithID(type)
						// TODO associations not supported yet. when they are, change it to the commented out line.
						|| isAssociationExpression(forExpression.getExpression())
//							&& !isAssociationExpression(forExpression.getExpression())
						) {
					problemRequestor.acceptProblem(forExpression.getExpression(),
							SQLResourceKeys.SQL_FOR_TYPE_INVALID,
							IMarker.SEVERITY_ERROR,
							new String[] {forExpression.getExpression().getCanonicalString()},
							SQLResourceKeys.getResourceBundleForKeys());
					return;
				}
				
				// The type of the expression must match exactly the type of the action target.
				Type targetType = getTargetType();
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
	
	private Type getTargetType() {
		Type type = null;
		List targets = statement.getTargets();
		int size = targets.size();
		if (size > 0) {
			Expression e = (Expression)targets.get(0);
			type = e.resolveType();
			if (type != null && (size != 1 || !isEntity(type))) {
				type = null;
				if (mapsToSingleTable(targets)) {
					type = getContainingType(e.resolveMember());
				}
			}
		}
		
		if (type instanceof ArrayType) {
			type = ((ArrayType)type).getElementType();
		}
		
		return type;
	}
	
	private void initialize() {
		statement.accept(new AbstractASTVisitor() {
			public boolean visit(FromOrToExpressionClause clause) {
				if (to == null) {
					to = clause;
				}
				else {
					problemRequestor.acceptProblem(clause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_ADD.toUpperCase(), IEGLConstants.KEYWORD_TO.toUpperCase()});
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
							new String[] { IEGLConstants.KEYWORD_ADD.toUpperCase(), IEGLConstants.KEYWORD_FOR.toUpperCase()});
				}
				return false;
			}
		});
	}
}
