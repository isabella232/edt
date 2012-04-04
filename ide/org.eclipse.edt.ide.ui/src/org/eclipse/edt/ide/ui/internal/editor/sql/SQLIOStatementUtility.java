/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor.sql;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.NoCursorClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.WithIDClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sqltokenizer.EGLSQLParser;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.EGLLogger;

public class SQLIOStatementUtility {
	public static SQLIOStatementActionInfo getAddSQLIoStatementActionInfo(IEGLDocument document, Node node) {
		SQLIOStatementActionInfo info = new SQLIOStatementActionInfo();
		info.setDocument(document);
		if (node == null) {
			return info;
		}
		
		try {
			setupActionInfo(node, info);
		} catch (Exception e) {
			e.printStackTrace();
			EGLLogger.log(SQLIOStatementUtility.class, e);
		}
		return info;
	}
	
	private static SQLIOStatementActionInfo setupActionInfo(Node node, final SQLIOStatementActionInfo info) {
		
		final ICompilerOptions compileOptions = DefaultCompilerOptions.getInstance();	
        info.setCompilerOption(compileOptions);
		if (node instanceof AddStatement) {
			AddStatement addStatement = (AddStatement) node;
			info.setIOType(SQLConstants.ADD_IO_TYPE.toUpperCase());
			info.setStatement(addStatement);
			addStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					info.setSqlStatementNode(inlineSQL);
					return false;
				}
			});
		} else if (node instanceof ExecuteStatement) {
			ExecuteStatement executeStatement = (ExecuteStatement) node;
			info.setStatement(executeStatement);
			info.setIOType(SQLConstants.EXECUTE_IO_TYPE.toUpperCase());
			/*if (executeStatement.hasInlineSQLStatement() || executeStatement.isPreparedStatement()) {
				if (executeStatement.hasInlineSQLStatement()) {
					info.setSqlStatement(executeStatement.getInlineSQLStatement().getValue());
					info.setSqlStatementNode(executeStatement.getInlineSQLStatement());
				} else {
					info.setHasPreparedStatementReference(true);
				}
			}*/
		} else if (node instanceof GetByKeyStatement) {
			GetByKeyStatement getStatement = (GetByKeyStatement) node;
			getStatement.accept(new AbstractASTVisitor() {
				public boolean visit(ForUpdateClause forUpdate) {
					info.setIOType(SQLConstants.GET_FORUPDATE_IO_TYPE.toUpperCase());
					return false;
				}
				public boolean visit(WithIDClause prepareID) {
					info.setHasPreparedStatementReference(true);
					return false;
				}
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					info.setSqlStatementNode(inlineSQL);
					return false;
				}
				public boolean visit(IntoClause intoClause) {
					info.setIntoClauseNode(intoClause);
					return false;
				}
				public boolean visit(UsingKeysClause keysClause) {
					info.setUsingKeysNode(keysClause);
					return false;
				}
				
				public boolean visit(UsingClause keysClause) {
					info.setUsingExisted(true);
					return false;
				}
			});
			if (info.getIOType() == null) {
				info.setIOType(SQLConstants.GET_IO_TYPE.toUpperCase());
			}
			info.setStatement(getStatement);
		} else if (node instanceof OpenStatement) {
			OpenStatement openStatement = (OpenStatement) node;
			info.setStatement(openStatement);
			openStatement.accept(new AbstractASTVisitor() {
				public boolean visit(ForUpdateClause forUpdate) {
					info.setIOType(SQLConstants.OPEN_FORUPDATE_IO_TYPE.toUpperCase());
					return false;
				}
				public boolean visit(ForExpressionClause forExpressionClause) {
					info.setForExpressionClause(forExpressionClause);
					return false;
				}
				public boolean visit(WithIDClause prepareID) {
					info.setShouldAddSQLStatement(false);
					info.setHasPreparedStatementReference(true);
					return false;
				}
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setShouldAddSQLStatement(false);
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					info.setSqlStatementNode(inlineSQL);
					EGLSQLParser parser = new EGLSQLParser(info.getSqlStatement(), IEGLConstants.KEYWORD_OPEN, compileOptions);
					String selectClause = parser.getSelectClause();
					if (selectClause != null) {
						info.setOpenWithSelectStatement(true);
					}
					return false;
				}
				public boolean visit(IntoClause intoClause) {
					info.setIntoClauseNode(intoClause);
					return false;
				}
				
				public boolean visit(UsingClause keysClause) {
					info.setUsingExisted(true);
					return false;
				}
			});
			if (info.getIOType() == null) {
				info.setIOType(SQLConstants.OPEN_IO_TYPE.toUpperCase());
			}
		} else if (node instanceof ReplaceStatement) {
			ReplaceStatement replaceStatement = (ReplaceStatement) node;
			info.setIOType(SQLConstants.REPLACE_IO_TYPE.toUpperCase());
			info.setStatement(replaceStatement);
			replaceStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					info.setSqlStatementNode(inlineSQL);
					return false;
				}
				public boolean visit(NoCursorClause noCursor) {
					info.setNoCursor(true);
					return false;
				}
			});
		} else if (node instanceof GetByPositionStatement) {
			GetByPositionStatement getByPositionStatement = (GetByPositionStatement) node;
			info.setIOType(SQLConstants.GET_BY_POSITION_IO_TYPE.toUpperCase());
			if (getByPositionStatement.isNextDirection()) {
				info.setGetNext(true);
			}
			info.setStatement(getByPositionStatement);
			getByPositionStatement.accept(new AbstractASTVisitor() {
				public boolean visit(IntoClause intoClause) {
					info.setIntoClauseNode(intoClause);
					return false;
				}
			});
		} else if (node instanceof CloseStatement) {
			info.setIOType(SQLConstants.CLOSE_IO_TYPE.toUpperCase());
			info.setStatement((CloseStatement) node);
		} else if (node instanceof DeleteStatement) {
			DeleteStatement deleteStatement = (DeleteStatement) node;
            info.setIOType(SQLConstants.DELETE_IO_TYPE.toUpperCase());
			info.setStatement(deleteStatement);
			deleteStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					info.setSqlStatementNode(inlineSQL);
					return false;
				}
				public boolean visit(NoCursorClause noCursor) {
					info.setNoCursor(true);
					return false;
				}
			});
		}
		return info;
	}
}
