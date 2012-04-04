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
	
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.NoCursorClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.core.ast.WithInlineDLIClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;


	
	/**
	 * @author Craig Duval
	 */
	public class ReplaceStatementValidator extends IOStatementValidator {
		
		public ReplaceStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			super(problemRequestor, compilerOptions);
		}
		
		//TODO validate iorecord properties keyitem and length
		public boolean visit(final ReplaceStatement areplaceStatement) {
			Expression expr = areplaceStatement.getRecord();
			final ITypeBinding typeBinding = expr.resolveTypeBinding();
			if (StatementValidator.isValidBinding(typeBinding)){
				isSQLTarget = typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null;
			}
			
			StatementValidator.validateIOTargetsContainer(areplaceStatement.getIOObjects(),problemRequestor);
			final Node[] nocursorNode = new Node[1];
			final Node[] inlineSqlNode = new Node[1];
			areplaceStatement.accept(new AbstractASTVisitor(){
				Node inlinesql = null;
				Node inlinedli = null;
				Node from = null;
				UsingPCBClause pcbClause = null;
				
//				public boolean visit(FromResultSetClause fromExpressionClause) {
//					from = fromExpressionClause;
//					EGLNameValidator.validate(fromExpressionClause.getResultSetID(), EGLNameValidator.RESULT_SET_ID, problemRequestor, fromExpressionClause, compilerOptions);
//					return false;
//				}
				
				public boolean visit(WithInlineSQLClause withInlineSQLClause) {
					inlineSqlNode[0] = withInlineSQLClause;
					if (inlinesql != null){
						problemRequestor.acceptProblem(withInlineSQLClause,
								IProblemRequestor.DUPE_INLINE_SQL,
								new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase()});
					}else{
						inlinesql = withInlineSQLClause;
						if (StatementValidator.isClauseEmpty(withInlineSQLClause.getSqlStmt().getValue())){
							problemRequestor.acceptProblem(withInlineSQLClause,
									IProblemRequestor.EMPTY_SQL_STRING,
									new String[]{IEGLConstants.KEYWORD_REPLACE.toUpperCase()});
						}
						else {
							SQLStatementValidator.checkReplaceClauses(withInlineSQLClause, areplaceStatement.getSqlInfo().getParser(), problemRequestor);							
						}
					}					
					return false;
				}
				
				public boolean visit(WithInlineDLIClause withInlineDLIClause) {
					if (inlinedli != null){
						problemRequestor.acceptProblem(withInlineDLIClause,
								IProblemRequestor.DUPE_INLINE_DLI,
								new String[] { IEGLConstants.KEYWORD_REPLACE.toUpperCase()});
					}else{
						inlinedli = withInlineDLIClause;
					}					
					return false;
				}
				
				public boolean visit (UsingPCBClause usingPCBClause){
					pcbClause = usingPCBClause;
					return false;
				}
				
				public boolean visit(NoCursorClause noCursorClause) {
					nocursorNode[0] = noCursorClause;
					checkIsSQLTarget(IEGLConstants.KEYWORD_NOCURSOR, noCursorClause);
					checkMutuallyExclusiveClauses(NO_CURSOR_USINGKEYS, IEGLConstants.KEYWORD_NOCURSOR, noCursorClause);
					return false;
				}
				
				public boolean visit(UsingKeysClause usingKeysClause) {
					checkIsSQLTarget(IEGLConstants.KEYWORD_USINGKEYS, usingKeysClause);
					checkMutuallyExclusiveClauses(NO_CURSOR_USINGKEYS, IEGLConstants.KEYWORD_USINGKEYS, usingKeysClause);
					return false;
				}
							
				public void endVisit (ReplaceStatement replaceStatement){
					Expression expr = replaceStatement.getRecord();
					ITypeBinding typeBinding = expr.resolveTypeBinding();
					if (StatementValidator.isValidBinding(typeBinding)){
						if (!isSQLTarget &&
							typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") == null &&
							typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") == null &&
							typeBinding.getAnnotation(EGLIODLI, "DLISegment") == null){
//							problemRequestor.acceptProblem(expr,
//									IProblemRequestor.REPLACE_STATEMENT_TARGET_NOT_RECORD,
//									new String[] {expr.getCanonicalString()});
						}
						
						if (typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") != null ||
							typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null){
//								if (inlinesql != null ){
//									problemRequestor.acceptProblem(inlinesql,
//										IProblemRequestor.SQL_CLAUSES_OR_OPTIONS_ON_REPLACE_WITH_NON_SQL_REC,
//										new String[] { IEGLConstants.KEYWORD_WITH + " " + IEGLConstants.SQLKEYWORD_SQL });
//								}
//								
//								if (from != null){
//									problemRequestor.acceptProblem(from,
//											IProblemRequestor.SQL_CLAUSES_OR_OPTIONS_ON_REPLACE_WITH_NON_SQL_REC,
//											new String[] { IEGLConstants.KEYWORD_FROM });
//								}
						}
					}
					if (replaceStatement.getDliInfo() != null) {
						DLIStatementValidator validator = new DLIStatementValidator(replaceStatement, problemRequestor, 4);
						validator.validateDLI(pcbClause);
					}

				}
				});
			
			if(isSQLTarget && typeBinding.getAnnotation(((IPartBinding) typeBinding).getSubType()).findData(IEGLConstants.PROPERTY_KEYITEMS) == IBinding.NOT_FOUND_BINDING) {
				//It is not valid to have a sql record with no keys on the statement if the nocursor is specified, and the user does not code his own sql statement
				if (nocursorNode[0] != null && inlineSqlNode[0] == null) {
					problemRequestor.acceptProblem(nocursorNode[0], IProblemRequestor.NOCURSOR_REQUIRES_KEY_ITEM, new String[] {typeBinding.getCaseSensitiveName()});
				}
			}

			return false;
		}


		
	}
	
	


