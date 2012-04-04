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
	package org.eclipse.edt.compiler.internal.core.validation.statement;
	
	import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.InlineSQLStatement;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

	
	/**
	 * @author Craig Duval
	 */
	public class ExecuteStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		
		public ExecuteStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final ExecuteStatement executeStatement) {
			StatementValidator.validateIOTargetsContainer(executeStatement.getIOObjects(),problemRequestor);
			checkForDuplicateOptions(executeStatement);
			checkUsingList(executeStatement);
			checkIOObjects(executeStatement);
			
//			if (executeStatement.hasInlineSQLStatement()){
//				if (executeStatement.getInlineSQLStatement() != null && StatementValidator.isClauseEmpty(((InlineSQLStatement)executeStatement.getInlineSQLStatement()).getValue())){
//					problemRequestor.acceptProblem(executeStatement,
//							IProblemRequestor.EMPTY_SQL_STRING,
//							new String[] {IEGLConstants.KEYWORD_EXECUTE.toUpperCase()});
//				}
//			}
			
//			String preparedStmtID = executeStatement.getPreparedStatementID();
//			if(preparedStmtID != null) {
//				EGLNameValidator.validate(preparedStmtID, EGLNameValidator.RESULT_SET_ID, problemRequestor, executeStatement, compilerOptions);
//			}
			
			return false;
		}
	
		protected void checkUsingList(final ExecuteStatement executeStatement){
			executeStatement.accept(new AbstractASTVisitor(){
				boolean visitedUsing = false;
				public boolean visit(UsingClause usingClause) {
					if (visitedUsing){
						return false;
					}
//					if (!executeStatement.isPreparedStatement()){
//						problemRequestor.acceptProblem(usingClause,
//								IProblemRequestor.CANT_HAVE_USING_WITHOUT_PREPARED_STMT_REF,
//								new String[] { IEGLConstants.KEYWORD_EXECUTE.toUpperCase()});
//					}
					//check types of children
					StatementValidator.validateNodesInUsingClause(usingClause.getExpressions(),problemRequestor);
					visitedUsing  = true;
					return false;
				}
				
				});
		}
		
		protected void checkIOObjects(ExecuteStatement executeStatement){
			executeStatement.accept(new AbstractASTVisitor(){
				public boolean visit(ForExpressionClause forExpressionClause) {
						ITypeBinding typeBinding = forExpressionClause.getExpression().resolveTypeBinding();
						if (StatementValidator.isValidBinding(typeBinding)){
							if (typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") == null){
//								problemRequestor.acceptProblem(forExpressionClause.getExpression(),
//										IProblemRequestor.OPEN_FOR_TARGET_NOT_SQL_RECORD,
//										new String[] {forExpressionClause.getExpression().getCanonicalString()});								
							}
							

						}
					return false;
				}
				
				});
			
		}
		
		protected void checkForDuplicateOptions(ExecuteStatement execStmt){
			execStmt.accept(new AbstractASTVisitor(){
				boolean using = false;
				boolean forexpr = false;
				
				public boolean visit(ForExpressionClause forExpressionClause) {
					if (forexpr){
						problemRequestor.acceptProblem(forExpressionClause,
								IProblemRequestor.DUPE_OPTION,
								new String[] {IEGLConstants.KEYWORD_EXECUTE.toUpperCase(), IEGLConstants.KEYWORD_FOR.toUpperCase()});
					}
					
					forexpr = true;
					return false;
				}
				
				public boolean visit(UsingClause usingClause) {
					if (using){
						problemRequestor.acceptProblem(usingClause,
								IProblemRequestor.DUPE_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_EXECUTE.toUpperCase(), IEGLConstants.KEYWORD_USING.toUpperCase()});
					}
					
					using = true;
					return false;
				}
				
				});
			
	}
	
		
	}
	
	


