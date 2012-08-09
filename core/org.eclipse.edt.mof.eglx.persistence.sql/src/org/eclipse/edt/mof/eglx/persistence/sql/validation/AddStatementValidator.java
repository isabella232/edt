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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

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
				ITypeBinding type = expr.resolveTypeBinding();
				if (isEntity(type)) {
					// Associations are not yet supported.
					if (isAssociationExpression(expr)) {
						problemRequestor.acceptProblem(expr,
								IProblemRequestor.SQL_ENTITY_ASSOCIATIONS_NOT_SUPPORTED,
								new String[] {});
						return;
					}
					isEntity = true;
				}
			}
		}
		
		if (!isEntity && !mapsToColumns(exprs)) {
			int[] offsets = getOffsets(exprs);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					IProblemRequestor.SQL_TARGET_MUST_BE_ENTITY_OR_COLUMNS,
					new String[] {});
			return;
		}
		else if (!isEntity && forExpression == null && !mapsToSingleTable(exprs)) {
			// FOR required when the columns do not map to a single table.
			int[] offsets = getOffsets(exprs);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					IProblemRequestor.SQL_STMT_REQUIRED_FOR_NON_SINGLE_TABLE,
					new String[] {IEGLConstants.KEYWORD_FOR});
			return;
		}
		
		ITypeBinding targetType = getTargetType();
		if (Binding.isValidBinding(targetType) && !isSingleTable(targetType)) {
			int[] offsets = getOffsets(exprs);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					IProblemRequestor.SQL_SINGLE_TABLE_REQUIRED,
					new String[]{});
			return;
		}
	}
	
	private void validateFor() {
		if (forExpression != null) {
			if (to != null) {
				// When the data source is a result set, FOR is not allowed.
				ITypeBinding type = to.getExpression().resolveTypeBinding();
				if (Binding.isValidBinding(type) && isResultSet(type)) {
					problemRequestor.acceptProblem(forExpression,
							IProblemRequestor.SQL_FOR_NOT_ALLOWED_WITH_DATA_SOURCE_TYPE,
							new String[] {"eglx.persistence.sql.SQLResultSet"});
					return;
				}
			}
			
			ITypeBinding type = forExpression.getExpression().resolveTypeBinding();
			if (Binding.isValidBinding(type)) {
				if (!isEntityWithID(type)
						// TODO associations not supported yet. when they are, change it to the commented out line.
						|| isAssociationExpression(forExpression.getExpression())
//							&& !isAssociationExpression(forExpression.getExpression())
						) {
					problemRequestor.acceptProblem(forExpression.getExpression(),
							IProblemRequestor.SQL_FOR_TYPE_INVALID,
							new String[] {forExpression.getExpression().getCanonicalString()});
					return;
				}
				
				// The type of the expression must match exactly the type of the action target.
				ITypeBinding targetType = getTargetType();
				if (Binding.isValidBinding(targetType) && !targetType.equals(type)) {
					problemRequestor.acceptProblem(forExpression.getExpression(),
							IProblemRequestor.SQL_FOR_AND_TARGET_TYPES_MUST_MATCH,
							new String[] {forExpression.getExpression().getCanonicalString(), type.getPackageQualifiedName(), targetType.getPackageQualifiedName()});
					return;
				}
			}
		}
	}
	
	private ITypeBinding getTargetType() {
		ITypeBinding type = null;
		List targets = statement.getTargets();
		int size = targets.size();
		if (size > 0) {
			Expression e = (Expression)targets.get(0);
			type = e.resolveTypeBinding();
			if (Binding.isValidBinding(type) && (size != 1 || !isEntity(type))) {
				type = null;
				if (mapsToSingleTable(targets)) {
					IDataBinding data = e.resolveDataBinding();
					if (Binding.isValidBinding(data)) {
						type = data.getDeclaringPart();
					}
				}
			}
		}
		
		if (Binding.isValidBinding(type) && type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			type = ((ArrayTypeBinding)type).getElementType();
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
