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
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.NoCursorClause;
import org.eclipse.edt.compiler.core.ast.Node;
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
	public class DeleteStatementValidator extends IOStatementValidator {
		
		public DeleteStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			super(problemRequestor, compilerOptions);
		}
	
		//TODO validate iorecord properties keyitem and length
		public boolean visit(final DeleteStatement adeleteStatement) {
			StatementValidator.validateIOTargetsContainer(adeleteStatement.getIOObjects(),problemRequestor);
			final Expression expr = adeleteStatement.getTarget();
			validateTargetType(expr);

			final Node[] nocursorNode = new Node[1];
			final Node[] inlineSqlNode = new Node[1];

			adeleteStatement.accept(new AbstractASTVisitor(){
				UsingPCBClause pcbClause = null;
				Node inlinesql = null;
				Node inlinedli = null;
				
//				public boolean visit(FromResultSetClause fromResultSetClause) {
//					checkIsSQLTarget(IEGLConstants.KEYWORD_FROM, fromResultSetClause);
//					EGLNameValidator.validate(fromResultSetClause.getResultSetID(), EGLNameValidator.RESULT_SET_ID, problemRequestor, fromResultSetClause, compilerOptions);
//					return true;
//				}
				
				public boolean visit(WithInlineSQLClause withInlineSQLClause) {
					inlineSqlNode[0] = withInlineSQLClause;
					checkIsSQLTarget(IEGLConstants.KEYWORD_WITH + " " + IEGLConstants.SQLKEYWORD_SQL, withInlineSQLClause);
					if (inlinesql != null){
						problemRequestor.acceptProblem(withInlineSQLClause,
								IProblemRequestor.DUPE_INLINE_SQL,
								new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase()});
					}else{
						inlinesql = withInlineSQLClause;
						if (StatementValidator.isClauseEmpty(withInlineSQLClause.getSqlStmt().getValue())){
							problemRequestor.acceptProblem(withInlineSQLClause,
									IProblemRequestor.EMPTY_SQL_STRING,
									new String[]{IEGLConstants.KEYWORD_DELETE.toUpperCase()});
						}
						else {
							SQLStatementValidator.checkDeleteClauses(withInlineSQLClause, adeleteStatement.getSqlInfo().getParser(), problemRequestor);							
						}
					}					
					return false;
				}
				
				public boolean visit(WithInlineDLIClause withInlineDLIClause) {
					if (inlinedli != null){
						problemRequestor.acceptProblem(withInlineDLIClause,
								IProblemRequestor.DUPE_INLINE_DLI,
								new String[] { IEGLConstants.KEYWORD_DELETE.toUpperCase()});
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
								
				public void endVisit(DeleteStatement deleteStatement) {
					if (deleteStatement.getDliInfo() != null) {
						DLIStatementValidator validator = new DLIStatementValidator(deleteStatement, problemRequestor, 5);
						validator.validateDLI(pcbClause);
					}
				}
			});
			
			if (expr != null) {
				ITypeBinding typeBinding = expr.resolveTypeBinding();
				if(isSQLTarget && typeBinding.getAnnotation(((IPartBinding) typeBinding).getSubType()).findData(IEGLConstants.PROPERTY_KEYITEMS) == IBinding.NOT_FOUND_BINDING) {
					//It is not valid to have a sql record with no keys on the statement if the nocursor is specified, and the user does not code his own sql statement
					if (nocursorNode[0] != null && inlineSqlNode[0] == null) {
						problemRequestor.acceptProblem(nocursorNode[0], IProblemRequestor.NOCURSOR_REQUIRES_KEY_ITEM, new String[] {typeBinding.getCaseSensitiveName()});
					}
				}
			}

			
			return false;
		}

		protected void validateTargetType(Expression expression){
			boolean isValid = false;
			if (expression == null) {
				return;
			}
			
			ITypeBinding typeBinding = expression.resolveTypeBinding();
			
			if (StatementValidator.isValidBinding(typeBinding) ){
				if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING || 
						typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING	){
					
						isSQLTarget = typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null;
					
						if (isSQLTarget ||
							typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") != null ||
							typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") != null){
							isValid = true;
						}
						
				}
				
				if (!isValid){
//					problemRequestor.acceptProblem(expression,
//							IProblemRequestor.DELETE_STATEMENT_RECORD_IS_INVALID_TYPE,
//							new String[] {expression.getCanonicalString(),expression.getCanonicalString()});
				}
			}
		}
		
		protected boolean isClauseEmpty(String clause){
			for (int i = 0; i < clause.length(); i++){
				if (!Character.isWhitespace(clause.charAt(i))){
					return false;
				}
			}
			
			return true;
		}
	
	}
	
	


