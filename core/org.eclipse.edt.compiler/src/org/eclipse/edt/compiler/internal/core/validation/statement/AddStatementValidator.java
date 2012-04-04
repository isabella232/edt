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
	
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.core.ast.WithInlineDLIClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


	
	/**
	 * @author Craig Duval
	 */
	public class AddStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants {
		
		private IProblemRequestor problemRequestor;
		
		public AddStatementValidator(IProblemRequestor problemRequestor) {
			this.problemRequestor = problemRequestor;
		}
		
		boolean targetIsDLISegmentOrSegmentArray = false;
	
		//TODO validate iorecord properties keyitem and length
		public boolean visit(final AddStatement aaddStatement) {
			StatementValidator.validateIOTargetsContainer(aaddStatement.getIOObjects(),problemRequestor);
			
			aaddStatement.accept(new AbstractASTExpressionVisitor(){
				int count = 0;
				boolean isdli = false;
				
				public boolean visit (UsingPCBClause clause){
					return false;
				}
				
				public boolean visit(org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause clause) {
					return false;
				}
				
				public boolean visitExpression(Expression expression) {
					ITypeBinding typeBinding = expression.resolveTypeBinding();
					if (!StatementValidator.isValidBinding(typeBinding))
						return false;
					
					if (++count > 1){
						if (!isdli || typeBinding.getAnnotation(EGLIODLI, "DLISegment") == null){
							problemRequestor.acceptProblem(expression,
									IProblemRequestor.MULTIPLE_TARGETS_MUST_ALL_BE_DLISEGMENT_SCALARS,
									new String[] {IEGLConstants.KEYWORD_ADD} );
						}
					}else{
						isdli = typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null;
					}
					
					validateTargetType(expression);
					
					if(!targetIsDLISegmentOrSegmentArray) {					
						targetIsDLISegmentOrSegmentArray = typeBinding.getBaseType().getAnnotation(EGLIODLI, "DLISegment") != null;
					}
					
					return false;
				}
				});

			aaddStatement.accept(new AbstractASTVisitor(){
				Node inlinesql = null;
				Node inlinedli = null;
				UsingPCBClause pcbClause = null;
							
				public boolean visit (UsingPCBClause usingPCBClause){
					pcbClause = usingPCBClause;
					
					if(!targetIsDLISegmentOrSegmentArray) {
						problemRequestor.acceptProblem(
							usingPCBClause,
							IProblemRequestor.IO_CLAUSE_REQUIRES_DLISEGMENT_TARGET,
							new String[] {
								IEGLConstants.KEYWORD_USINGPCB	
							});
					}
					return false;
				}
				
				public boolean visit(WithInlineDLIClause withInlineDLIClause) {
					if (inlinedli != null){
						problemRequestor.acceptProblem(withInlineDLIClause,
								IProblemRequestor.DUPE_INLINE_DLI,
								new String[] { IEGLConstants.KEYWORD_ADD.toUpperCase()});
					}else{
						inlinedli = withInlineDLIClause;
					}
					
					Expression expr = (Expression)aaddStatement.getTargets().get(0);
					ITypeBinding typeBinding = expr.resolveTypeBinding();
					if (!StatementValidator.isValidBinding(typeBinding)){
						return false;
					}
					
					if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
						typeBinding = typeBinding.getBaseType();
					}
					
					if (typeBinding.getAnnotation(EGLIODLI, "DLISegment") == null){
						problemRequestor.acceptProblem(withInlineDLIClause,
								IProblemRequestor.IO_CLAUSE_REQUIRES_DLISEGMENT_TARGET,
								new String[]{IEGLConstants.KEYWORD_WITH + " #" + IEGLConstants.PROPERTY_DLI});
					}
					
					return false;
				}
				
				public boolean visit(WithInlineSQLClause withInlineSQLClause) {
					if (inlinesql != null){
						problemRequestor.acceptProblem(withInlineSQLClause,
								IProblemRequestor.DUPE_INLINE_SQL,
								new String[] { IEGLConstants.KEYWORD_ADD.toUpperCase()});
					}else{
						inlinesql = withInlineSQLClause;
						if (StatementValidator.isClauseEmpty(withInlineSQLClause.getSqlStmt().getValue())){
							problemRequestor.acceptProblem(withInlineSQLClause,
									IProblemRequestor.EMPTY_SQL_STRING,
									new String[]{IEGLConstants.KEYWORD_ADD.toUpperCase()});
						}
						else {
							if(aaddStatement.getSqlInfo() != null) {
							   SQLStatementValidator.checkAddClauses(withInlineSQLClause, aaddStatement.getSqlInfo().getParser(), problemRequestor);
							}
						}
					}			
					
					Expression expr = (Expression)aaddStatement.getTargets().get(0);
					ITypeBinding typeBinding = expr.resolveTypeBinding();
					if (!StatementValidator.isValidBinding(typeBinding)){
						return false;
					}
					
//					if (typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") == null){
//						problemRequestor.acceptProblem(withInlineSQLClause,
//								IProblemRequestor.ADD_STATEMENT_WITH_USED_WITHOUT_SQL_RECORD);
//					}
					
					return false;
				}
				
				public void endVisit(AddStatement addStatement) {
					if (addStatement.getDliInfo() != null) {
						DLIStatementValidator validator = new DLIStatementValidator(addStatement, problemRequestor, 1);
						validator.validateDLI(pcbClause);
					}
				}
			});
			
			return false;
		}

		protected void validateTargetType(Expression expression){
			boolean isValid = false;
			ITypeBinding typeBinding = expression.resolveTypeBinding();
			IDataBinding dataBinding = expression.resolveDataBinding();
			
			if (StatementValidator.isValidBinding(typeBinding) && StatementValidator.isValidBinding(dataBinding)){
				if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
					ITypeBinding arrayBinding = typeBinding.getBaseType();
					if (!StatementValidator.isValidBinding(arrayBinding))
						return;
					if (arrayBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null ||
						arrayBinding.getAnnotation(EGLIOFILE, "CSVRecord") != null ||
						arrayBinding.getAnnotation(EGLIODLI, "DLISegment") != null){
						isValid = true;
					}
					
				}else if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING || 
						typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING	){
					
						if (typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null ||
							typeBinding.getAnnotation(EGLIOMQ, "MQRecord") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "SerialRecord") != null ||
							typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "CSVRecord") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") != null){
							isValid = true;
						}
						
				}
				
				if (!isValid){
					
//					problemRequestor.acceptProblem(expression,
//							IProblemRequestor.ADD_STATEMENT_TARGET_NOT_RECORD,
//							new String[] {dataBinding.getCaseSensitiveName()});
				}
			}
		}
		
	
	}
	
	


