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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.WithExpressionClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

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
		ITypeBinding targetType = statement.getSqlStmt().resolveTypeBinding();
		if (Binding.isValidBinding(targetType) && !isSqlStatement(targetType)) {
			problemRequestor.acceptProblem(statement.getSqlStmt(),
					IProblemRequestor.SQL_EXPR_HAS_WRONG_TYPE,
					new String[] {statement.getSqlStmt().getCanonicalString(), "eglx.persistence.sql.SQLStatement"});
			return;
		}
	}
	
	private void validateFrom() {
		// If FROM wasn't specified, there will be a validation error already from the parser.
		if (from != null) {
			ITypeBinding type = from.getExpression().resolveTypeBinding();
			if (Binding.isValidBinding(type) && !isDataSource(type)) {
				problemRequestor.acceptProblem(from.getExpression(),
						IProblemRequestor.SQL_EXPR_HAS_WRONG_TYPE,
						new String[] {from.getExpression().getCanonicalString(), "eglx.persistence.sql.SQLDataSource"});
				return;
			}
		}
	}
	
	private void validateWith() {
		// If WITH wasn't specified, there will be a validation error already from the parser.
		if (withExpression != null) {
			ITypeBinding type = withExpression.getExpression().resolveTypeBinding();
			if (Binding.isValidBinding(type) &&
					(ITypeBinding.PRIMITIVE_TYPE_BINDING != type.getKind()
						|| Primitive.STRING_PRIMITIVE != ((PrimitiveTypeBinding)type).getPrimitive().getType())) {
				problemRequestor.acceptProblem(withExpression.getExpression(),
						IProblemRequestor.SQL_EXPR_HAS_WRONG_TYPE,
						new String[] {withExpression.getExpression().getCanonicalString(), IEGLConstants.KEYWORD_STRING});
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
