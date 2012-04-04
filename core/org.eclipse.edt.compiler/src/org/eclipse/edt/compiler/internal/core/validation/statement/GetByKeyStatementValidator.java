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
	
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.core.ast.SingleRowClause;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.UsingPCBClause;
import org.eclipse.edt.compiler.core.ast.WithIDClause;
import org.eclipse.edt.compiler.core.ast.WithInlineDLIClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

	
	/**
	 * @author Craig Duval
	 */
	public class GetByKeyStatementValidator extends IOStatementValidator {
		GetByKeyStatement keyStatement;
		
		public GetByKeyStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			super(problemRequestor, compilerOptions);
		}

		//TODO validate iorecord properties keyitem and length
		public boolean visit(final GetByKeyStatement agetByKeyStatement) {
			keyStatement = agetByKeyStatement;

			StatementValidator.validateIOTargetsContainer(agetByKeyStatement.getIOObjects(),problemRequestor);
			validateMultipleTargets(agetByKeyStatement);
			
			agetByKeyStatement.accept(new AbstractASTVisitor(){
				Node bFORUPDATE = null;
				Node bSINGLEROW = null;
				Node bINTO = null;
				Node bUSING = null;
				Node bUSINGKEYS = null;
				Node bPREPAREID = null;
				WithInlineSQLClause bINLINESQL = null;
				WithInlineDLIClause bINLINEDLI = null;
				UsingPCBClause pcbClause = null;
				
				ArrayList list = new ArrayList();
				
				public boolean visit(ForUpdateClause forUpdateClause) {
					if (bFORUPDATE != null){
						problemRequestor.acceptProblem(forUpdateClause,
								IProblemRequestor.DUPE_OPTION,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(),IEGLConstants.KEYWORD_FORUPDATE.toUpperCase() });
					}else {					
						bFORUPDATE = forUpdateClause;
						list.add(bFORUPDATE);
					}
					
					if(forUpdateClause.hasID()) {
						EGLNameValidator.validate(forUpdateClause.getID(), EGLNameValidator.RESULT_SET_ID, problemRequestor, forUpdateClause, compilerOptions);
					}
					
					return false;
				}
				
				public boolean visit (UsingPCBClause usingPCBClause){
					pcbClause = usingPCBClause;
					return false;
				}
				
				public boolean visit(SingleRowClause singleRowClause) {
					if (bSINGLEROW != null){
						problemRequestor.acceptProblem(singleRowClause,
								IProblemRequestor.DUPE_OPTION,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(),IEGLConstants.KEYWORD_SINGLEROW.toUpperCase() });
					}else {
						bSINGLEROW = singleRowClause;
						list.add(bSINGLEROW);
					}
					return false;
				}
				
				public boolean visit(IntoClause intoClause) {
					if (bINTO != null){
						problemRequestor.acceptProblem(intoClause,
								IProblemRequestor.DUPE_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(),IEGLConstants.KEYWORD_INTO.toUpperCase() });
					}else {
						bINTO = intoClause;
						list.add(bINTO);
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
					if (bUSING != null){
						problemRequestor.acceptProblem(usingClause,
								IProblemRequestor.DUPE_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(),IEGLConstants.KEYWORD_USING.toUpperCase() });
					}else {
						bUSING = usingClause;
						list.add(bUSING);
						StatementValidator.validateNodesInUsingClause(usingClause.getExpressions(), problemRequestor);						
					}
					return false;
				}
				
				public boolean visit(UsingKeysClause usingKeysClause) {
					if (bUSINGKEYS != null){
						problemRequestor.acceptProblem(usingKeysClause,
								IProblemRequestor.DUPE_CLAUSE,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(),IEGLConstants.KEYWORD_USINGKEYS.toUpperCase() });
					}else {
						bUSINGKEYS = usingKeysClause;
						list.add(bUSINGKEYS);
						
						final List sqlRecordObjectDataBindings = StatementValidator.getSQLRecordIOObjects(agetByKeyStatement);
						
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
					if (bPREPAREID != null){
						problemRequestor.acceptProblem(withIDClause,
								IProblemRequestor.DUPE_PREPARED_STMT_REFERENCE,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase() });
					}else {
						bPREPAREID = withIDClause;
						list.add(bPREPAREID);
					}
					
					EGLNameValidator.validate(withIDClause.getID(), EGLNameValidator.RESULT_SET_ID, problemRequestor, withIDClause, compilerOptions);
					return false;
				}
				
				public boolean visit(WithInlineSQLClause withInlineSQLClause) {
					if (bINLINESQL != null){
						problemRequestor.acceptProblem(withInlineSQLClause,
								IProblemRequestor.DUPE_INLINE_SQL,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase() });
					}else{
						if (StatementValidator.isClauseEmpty(withInlineSQLClause.getSqlStmt().getValue())){
							problemRequestor.acceptProblem(withInlineSQLClause,
									IProblemRequestor.EMPTY_SQL_STRING,
									new String[]{IEGLConstants.KEYWORD_GET.toUpperCase()});
						}
						else {
							bINLINESQL = withInlineSQLClause;
							list.add(bINLINESQL);
							SQLStatementValidator.checkGetAndOpenClauses(withInlineSQLClause, agetByKeyStatement.getSqlInfo().getParser(), 
									problemRequestor, IEGLConstants.KEYWORD_GET, isGetArray()); 
						}
					}
					return false;
				}
				
				private boolean isGetArray() {
					if (keyStatement == null) {
						return false;
					}
					List list = keyStatement.getTargets();
					
					if (list.size() > 0) {
						Expression expr = (Expression)list.get(0);
						ITypeBinding tb = expr.resolveTypeBinding();
						if (Binding.isValidBinding(tb) && tb.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
							return true;
						}
					}
					return false;
				}
				
				public boolean visit(WithInlineDLIClause withInlineDLIClause) {
					if (bINLINEDLI != null){
						problemRequestor.acceptProblem(withInlineDLIClause,
								IProblemRequestor.DUPE_INLINE_DLI,
								new String[] { IEGLConstants.KEYWORD_GET.toUpperCase() });
					}else{
						bINLINEDLI = withInlineDLIClause;
						list.add(bINLINESQL);
					}
					return false;
				}
				
				public void endVisit(GetByKeyStatement getByKeyStatement) {
					if (bFORUPDATE != null && bSINGLEROW != null){
						problemRequestor.acceptProblem(
								list.indexOf(bFORUPDATE) < list.indexOf(bSINGLEROW)? bSINGLEROW: bFORUPDATE,
								IProblemRequestor.CANT_HAVE_BOTH_FORUPDATE_SINGLEROW,
								new String[] {IEGLConstants.KEYWORD_GET.toUpperCase()});
					}
					
					if (bFORUPDATE != null && bINLINESQL != null){
						SQLStatementValidator.checkForUpdateClause(bINLINESQL, 
								agetByKeyStatement.getSqlInfo().getParser().getSqlClauseKeywordsUsed(), 
								problemRequestor, 
								IEGLConstants.KEYWORD_GET);
					}
					
					if (bPREPAREID != null && bSINGLEROW != null){
						problemRequestor.acceptProblem(
								list.indexOf(bPREPAREID) < list.indexOf(bSINGLEROW)? bSINGLEROW: bPREPAREID,
								IProblemRequestor.CANT_HAVE_BOTH_SINGLEROW_PREPARED_STMT_ID,
								new String[] {IEGLConstants.KEYWORD_GET.toUpperCase()});
					}
					
					if (bPREPAREID != null && bINLINESQL != null){
						problemRequestor.acceptProblem(
								list.indexOf(bPREPAREID) < list.indexOf(bINLINESQL)? bSINGLEROW: bPREPAREID,
								IProblemRequestor.CANT_HAVE_BOTH_INLINE_SQL_PREPARED_STMT_REF,
								new String[] {IEGLConstants.KEYWORD_GET.toUpperCase()});
					}
					
					if (getByKeyStatement.getDliInfo() != null) {
						DLIStatementValidator validator = new DLIStatementValidator(getByKeyStatement, problemRequestor, 2);
						validator.validateGetByKeyDLI(pcbClause, bFORUPDATE != null);
					}
				
					if (getByKeyStatement.getTargets().size() == 0){
						if (bFORUPDATE != null){
							problemRequestor.acceptProblem(bFORUPDATE,
									IProblemRequestor.CANT_HAVE_FORUPDATE_WITHOUT_SQLRECORD,
									new String[] {IEGLConstants.KEYWORD_GET.toUpperCase()});
						}
						
						if (bUSINGKEYS != null){
							problemRequestor.acceptProblem(bUSINGKEYS,
									IProblemRequestor.CANT_HAVE_USINGKEYS_WITHOUT_SQLRECORD,
									new String[] {IEGLConstants.KEYWORD_GET.toUpperCase()});
						}
						
						if (bINTO == null || (bPREPAREID == null && bINLINESQL == null) ){
							problemRequestor.acceptProblem(getByKeyStatement,
									IProblemRequestor.MUST_HAVE_INTO_AND_SQL_INFO,
									new String[] {IEGLConstants.KEYWORD_GET.toUpperCase()});
						}
					}else{
						for (int i = 0; i < getByKeyStatement.getTargets().size(); i++){
							Expression expr = (Expression)getByKeyStatement.getTargets().get(i);
							final IDataBinding dataBinding = expr.resolveDataBinding();
							ITypeBinding typeBinding = expr.resolveTypeBinding();
							if (StatementValidator.isValidBinding(typeBinding)){
								
								if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING){
									if (bFORUPDATE != null){
										problemRequestor.acceptProblem(expr,
												IProblemRequestor.FORUPDATE_NOT_ALLOWED_WITH_ARRAY_TARGET);
									}
									final ITypeBinding baseBinding = typeBinding.getBaseType();
									if (StatementValidator.isValidBinding(baseBinding)){
										if (baseBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING ||
											baseBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING){
												if (baseBinding.getAnnotation(EGLIOSQL, "SQLRecord")!= null){
													if (bINTO != null){
														if (StatementValidator.isValidBinding(dataBinding)){
															bINTO.accept(new AbstractASTExpressionVisitor(){
															    public boolean visit(SimpleName simpleName) {
															    	if (simpleName.getAttribute(Name.IMPLICIT_QUALIFIER_DATA_BINDING) != dataBinding){
															    		addError(simpleName);
															    	}
															    	return false;
															    }
															    
															    public boolean visit(QualifiedName qualifiedName) {
															    	Name name = qualifiedName;
															    	IDataBinding db = null;
															    	while (name != null){
															    		db = name.resolveDataBinding();
															    		if (!StatementValidator.isValidBinding(db)){
															    			return false;
															    		}
															    		if (db == dataBinding){
															    			break;
															    		}else{
															    			db = null;
															    		}
															    		
															    		if (name.isQualifiedName()){
															    			name = ((QualifiedName)name).getQualifier();
															    		}else{
															    			break;
															    		}
															    	}
															    	
															    	if (db == null){
															    		addError(qualifiedName);
															    	}
															        return false;
															    }
															    
															    public boolean visit(FieldAccess fieldAccess) {
															        return true;
															    }
															    
															    public boolean visit(ArrayAccess arrayAccess) {
															    	if (arrayAccess.getArray().resolveDataBinding() != dataBinding){
															    		addError(arrayAccess);
															    	}
															    		
															        return false;
															    }
															    
															    protected void addError(Expression node){
															    	problemRequestor.acceptProblem(node,
									                        				IProblemRequestor.INVALID_INTO_ITEM_FOR_GET_SQL_RECORD_ARRAY,
																			new String[] {node.getCanonicalString()});
															    }
															    
																public boolean visitExpression(Expression expression) {
																    return true;
																}
																});
														}
													}
												}
												else {
//													if (bUSINGKEYS != null){
//														problemRequestor.acceptProblem(bUSINGKEYS,
//																IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//																new String[]{IEGLConstants.KEYWORD_USINGKEYS});
//													}
//													
//													if (bINLINESQL != null){
//														problemRequestor.acceptProblem(bINLINESQL,
//																IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//																new String[]{IEGLConstants.KEYWORD_WITH + " #" + IEGLConstants.SQLKEYWORD_SQL});
//													}
//
//													if (bUSING != null){
//														problemRequestor.acceptProblem(bUSING,
//																IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//																new String[]{IEGLConstants.KEYWORD_USING });
//													}
//													
//													if (bSINGLEROW != null){
//														problemRequestor.acceptProblem(bSINGLEROW,
//																IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//																new String[]{IEGLConstants.KEYWORD_SINGLEROW});
//													}
//													
//													if (bPREPAREID != null){
//														problemRequestor.acceptProblem(bPREPAREID,
//																IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//																new String[]{IEGLConstants.KEYWORD_WITH });
//													}
												}
										}
											
									}
													
								}else if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING ||
									typeBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING){
									if (typeBinding.getAnnotation(EGLIOSQL, "SQLRecord")== null){
//											if (bSINGLEROW != null){
//												problemRequestor.acceptProblem(bSINGLEROW,
//														IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//														new String[]{IEGLConstants.KEYWORD_SINGLEROW});
//											}
//											
//											if (bUSINGKEYS != null){
//												problemRequestor.acceptProblem(bUSINGKEYS,
//														IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//														new String[]{IEGLConstants.KEYWORD_USINGKEYS});
//											}
//											
//											if (bINLINESQL != null){
//												problemRequestor.acceptProblem(bINLINESQL,
//														IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//														new String[]{IEGLConstants.KEYWORD_WITH + " #" + IEGLConstants.SQLKEYWORD_SQL});
//											}
//											
//											if (bPREPAREID != null){
//												problemRequestor.acceptProblem(bPREPAREID,
//														IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//														new String[]{IEGLConstants.KEYWORD_WITH });
//											}
//											
//											if (bINTO != null){
//												problemRequestor.acceptProblem(bINTO,
//														IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//														new String[]{IEGLConstants.KEYWORD_INTO });
//											}
//											
//											if (bUSING != null){
//												problemRequestor.acceptProblem(bUSING,
//														IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//														new String[]{IEGLConstants.KEYWORD_USING });
//											}
//											
//											if (bFORUPDATE != null &&
//												typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") == null &&
//												typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") == null &&
//												typeBinding.getAnnotation(EGLIODLI, "dliSegment")== null){
//												problemRequestor.acceptProblem(bFORUPDATE,
//														IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//														new String[]{IEGLConstants.KEYWORD_FORUPDATE });
//											}
										}
								}
							}
						}
					}
				}
				});
			keyStatement = null;
			return false;
		}

		private void validateMultipleTargets(GetByKeyStatement keyStatement){
			int count = 0;
			boolean isdli = false;
			List targetList = keyStatement.getTargets();
			Iterator iter = targetList.iterator();
			while(iter.hasNext()){
				Expression expr = (Expression)iter.next();
				ITypeBinding typeBinding = expr.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)){
					if (++count > 1){
						if (!isdli || typeBinding.getAnnotation(EGLIODLI, "dliSegment") == null){
							problemRequestor.acceptProblem(expr,
									IProblemRequestor.MULTIPLE_TARGETS_MUST_ALL_BE_DLISEGMENT_SCALARS,
									new String[] {IEGLConstants.KEYWORD_GET} );
						}
					}else{
						isdli = typeBinding.getAnnotation(EGLIODLI, "dliSegment") != null;
					}
										
				}
				
				validateTargetType(expr);
			}
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
							typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") != null ||							
							typeBinding.getAnnotation(EGLIODLI, "DLISegment") != null ||
							typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") != null){
							isValid = true;
						}
						
				}
				
				if (!isValid){
					
//					problemRequestor.acceptProblem(expression,
//							IProblemRequestor.GET_BY_KEY_STATEMENT_TARGET_NOT_RECORD,
//							new String[] {dataBinding.getCaseSensitiveName()});
				}
			}
		}
	}
	
	


