/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.messages.SQLResourceKeys;

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
		
		validateTarget();
		validateFrom();
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
	}
	
	private void validateFrom() {
		// If FROM wasn't specified, there will be a validation error already from the parser.
		if (from != null) {
			Type type = from.getExpression().resolveType();
			if (type != null && !Utils.isSQLResultSet(type)) {
				problemRequestor.acceptProblem(from.getExpression(),
						SQLResourceKeys.SQL_EXPR_HAS_WRONG_TYPE,
						IMarker.SEVERITY_ERROR,
						new String[] {from.getExpression().getCanonicalString(), "eglx.persistence.sql.SQLResultSet"},
						SQLResourceKeys.getResourceBundleForKeys());
				return;
			}
		}
	}
	
	private void initialize() {
		statement.accept(new AbstractASTVisitor() {
			
			public boolean visit(ForEachStatement forEachStatement) {
				
				visit(forEachStatement.getTargets());
				forEachStatement.getResultSet().accept(this);
				return false;
			}
			private void visit(List<Node> list) {
				for (Node node : list) {
					node.accept(this);
				}
			}
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
