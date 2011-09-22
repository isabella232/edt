package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation;

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
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public class OpenStatementValidator {
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
