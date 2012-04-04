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
package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation;

import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public class ForEachStatementValidator extends AbstractSqlStatementValidator {
	ForEachStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	FromOrToExpressionClause from;
	IntoClause into;

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
		validateInto();
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
	
	private void validateInto() {
		if (into != null) {
			// INTO not currently part of the spec.
			problemRequestor.acceptProblem(into, IProblemRequestor.SQL_INTO_NOT_ALLOWED, new String[] {});
		}
	}
	
	private void initialize() {
		statement.accept(new AbstractASTVisitor() {
			
			public boolean visit(ForEachStatement forEachStatement) {
				
				visit(forEachStatement.getTargets());
				forEachStatement.getResultSet().accept(this);
				visit(forEachStatement.getForeachOptions());
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
			
			public boolean visit(IntoClause intoClause) {
				if (into == null) {
					into = intoClause;
				}
				else {
					problemRequestor.acceptProblem(intoClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_FOREACH.toUpperCase(), IEGLConstants.KEYWORD_INTO.toUpperCase()});
				}
				return false;
			}
		});
	}
}
