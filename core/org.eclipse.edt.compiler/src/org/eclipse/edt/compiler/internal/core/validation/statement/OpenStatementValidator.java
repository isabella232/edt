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
	
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.WithIDClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;



	
	/**
	 * @author Craig Duval
	 */
	public class OpenStatementValidator extends IOStatementValidator {
		OpenStatement openStatement;
		
		public OpenStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			super(problemRequestor, compilerOptions);
		}
		
		public boolean visit(final OpenStatement aopenStatement) {
			openStatement = aopenStatement;
			
			StatementValidator.validateIOTargetsContainer(aopenStatement.getIOObjects(),problemRequestor);
			aopenStatement.accept(new AbstractASTVisitor(){
				Node forupdate = null;
				Node into = null;
				Node using = null;
				Node usingkeys = null;
				Node withid = null;
				WithInlineSQLClause inlinesql = null;
				Node foroption = null;
				Node sqlprepare = null;
				
				public boolean visit(ForExpressionClause forExpressionClause) {
					Expression expr = forExpressionClause.getExpression();
					ITypeBinding typeBinding = expr.resolveTypeBinding();
					if (StatementValidator.isValidBinding(typeBinding)){
						if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
							typeBinding = typeBinding.getBaseType();
						}
						
						if (typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") == null){
//							problemRequestor.acceptProblem(expr,
//									IProblemRequestor.OPEN_FOR_TARGET_NOT_SQL_RECORD,
//									new String[] {expr.getCanonicalString()});
						}
					}	
					
					if (foroption != null){
						problemRequestor.acceptProblem(forExpressionClause,
								IProblemRequestor.DUPE_OPTION,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_FOR.toUpperCase()});
					}else{
						foroption = forExpressionClause;
					}
					return false;
					
					
				}
				
				public boolean visit(ForUpdateClause forUpdateClause) {
					if (forupdate != null){
						problemRequestor.acceptProblem(forUpdateClause,
								IProblemRequestor.DUPE_OPTION,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_FORUPDATE.toUpperCase()});
					}else{
						forupdate = forUpdateClause;
					}
					return false;
				}
				
				public boolean visit(IntoClause intoClause) {
					if (into != null){
						problemRequestor.acceptProblem(intoClause,
								IProblemRequestor.DUPE_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_INTO.toUpperCase()});
					}else{
						into = intoClause;
						intoClause.accept(new AbstractASTExpressionVisitor(){
							public boolean visitExpression(Expression expression) {
								StatementValidator.validateItemInIntoClause(expression,problemRequestor);
							    return false;
							}
							
							});						
					}
					return false;
				}
				
				public boolean visit(UsingClause usingClause) {
					if (using != null){
						problemRequestor.acceptProblem(usingClause,
								IProblemRequestor.DUPE_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_USING.toUpperCase()});
					}else{
						using = usingClause;
						StatementValidator.validateNodesInUsingClause(usingClause.getExpressions(), problemRequestor);					
					}					
					return false;
				}
				
				public boolean visit(UsingKeysClause usingKeysClause) {
					if (usingkeys != null){
						problemRequestor.acceptProblem(usingKeysClause,
								IProblemRequestor.DUPE_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase(), IEGLConstants.KEYWORD_USINGKEYS.toUpperCase()});
					}else{
						usingkeys = usingKeysClause;
						
						final List sqlRecordObjectDataBindings = StatementValidator.getSQLRecordIOObjects(aopenStatement);
						
						usingKeysClause.accept(new AbstractASTExpressionVisitor(){
							IDataBinding qualifierDBinding;
							
							public boolean visitExpression(Expression expression) {
								StatementValidator.validateNodeAsDataItemReferences(expression,problemRequestor);
								
								if(sqlRecordObjectDataBindings.contains(qualifierDBinding)) {
									problemRequestor.acceptProblem(
										expression,
										IProblemRequestor.USINGKEYS_ITEM_IN_SQL_RECORD_ARRAY_IO_TARGET,
										new String[] {
											expression.getCanonicalString()	
										});
								}
								
							    return false;
							}
							
							public boolean visitName(Name name) {
								name.accept(new DefaultASTVisitor() {
									public boolean visit(SimpleName simpleName) {
										qualifierDBinding = (IDataBinding) simpleName.getAttribute(Name.IMPLICIT_QUALIFIER_DATA_BINDING);
										return false;
									}
									
									public boolean visit(QualifiedName qualifiedName) {
										qualifierDBinding = qualifiedName.getQualifier().resolveDataBinding();
										return false;
									}
								});
								return visitExpression(name);
							}
							
							public boolean visit(FieldAccess fieldAccess) {
								qualifierDBinding = fieldAccess.getPrimary().resolveDataBinding();
								return visitExpression(fieldAccess);
							}
							
							});
					}					
					return false;
				}
				
				public boolean visit(WithIDClause withIDClause) {
					if (withid != null){
						problemRequestor.acceptProblem(withIDClause,
								IProblemRequestor.DUPE_PREPARED_STMT_REFERENCE,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase()});
					}else{
						withid = withIDClause;
						sqlprepare = withIDClause;
					}
					
					EGLNameValidator.validate(withIDClause.getID(), EGLNameValidator.RESULT_SET_ID, problemRequestor, withIDClause, compilerOptions);
					return false;
				}
				
				public boolean visit(WithInlineSQLClause withInlineSQLClause) {
					if (inlinesql != null){
						problemRequestor.acceptProblem(withInlineSQLClause,
								IProblemRequestor.DUPE_INLINE_SQL,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase()});
					}else{
						if (StatementValidator.isClauseEmpty(withInlineSQLClause.getSqlStmt().getValue())){
							problemRequestor.acceptProblem(withInlineSQLClause,
									IProblemRequestor.EMPTY_SQL_STRING,
									new String[]{IEGLConstants.KEYWORD_OPEN.toUpperCase()});
						}
						else {
							sqlprepare = withInlineSQLClause;
							inlinesql = withInlineSQLClause;
							SQLStatementValidator.checkGetAndOpenClauses(withInlineSQLClause, aopenStatement.getSqlInfo().getParser(), 
									problemRequestor, IEGLConstants.KEYWORD_OPEN, isOpenArray()); 
						}
					}					
					return false;
				}
					
				private boolean isOpenArray() {
					if (openStatement == null) {
						return false;
					}
					List list = openStatement.getIOObjects();
					
					if (list.size() > 0) {
						Expression expr = (Expression)list.get(0);
						ITypeBinding tb = expr.resolveTypeBinding();
						if (Binding.isValidBinding(tb) && tb.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
							return true;
						}
					}
					return false;
				}

				public void endVisit(OpenStatement openStatement){
					if (usingkeys != null && foroption == null){
						problemRequestor.acceptProblem(usingkeys,
								IProblemRequestor.CANT_HAVE_USINGKEYS_WITHOUT_SQLRECORD,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase()});
					}
					
					if (forupdate != null && inlinesql != null){
						if (aopenStatement.getSqlInfo().getParser().getCallClause() != null) {
							problemRequestor.acceptProblem(forupdate, IProblemRequestor.OPEN_FORUPDATE_USED_WITH_SQL_CALL);
						} 
						else {
							SQLStatementValidator.checkForUpdateClause(inlinesql, 
									aopenStatement.getSqlInfo().getParser().getSqlClauseKeywordsUsed(), 
									problemRequestor, 
									IEGLConstants.KEYWORD_OPEN);
						}
					}
					
					if (into != null && inlinesql != null){
						if (aopenStatement.getSqlInfo().getParser().getCallClause() != null) {
							problemRequestor.acceptProblem(into, 
									IProblemRequestor.CANT_HAVE_INTO_WITH_CALL, 
									new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase()});
						} 
					}
					
					if (inlinesql != null && withid != null){
						problemRequestor.acceptProblem(sqlprepare,
								IProblemRequestor.CANT_HAVE_BOTH_INLINE_SQL_PREPARED_STMT_REF,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase()});
					}	
					
					if (inlinesql == null && withid == null && foroption == null){
						problemRequestor.acceptProblem(openStatement,
								IProblemRequestor.MUST_HAVE_SQLSTMT_OR_FOR_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase()});
					}
					
					if (using != null && withid == null){
						problemRequestor.acceptProblem(using,
								IProblemRequestor.CANT_HAVE_USING_WITHOUT_PREPARED_STMT_REF,
								new String[] { IEGLConstants.KEYWORD_OPEN.toUpperCase()});
					}
					openStatement = null;
				}
				});
			
//			EGLNameValidator.validate(aopenStatement.getResultSetID(), EGLNameValidator.RESULT_SET_ID, problemRequestor, aopenStatement, compilerOptions);
			
			return false;
		}


	}
	
	


