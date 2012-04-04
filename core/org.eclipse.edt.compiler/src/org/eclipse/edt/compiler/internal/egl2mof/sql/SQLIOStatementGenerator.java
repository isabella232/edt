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
package org.eclipse.edt.compiler.internal.egl2mof.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.ForUpdateClause;
import org.eclipse.edt.compiler.core.ast.FromExpressionClause;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.InlineSQLStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SingleRowClause;
import org.eclipse.edt.compiler.core.ast.WithIDClause;
import org.eclipse.edt.compiler.internal.egl2mof.DefaultIOStatementGenerator;
import org.eclipse.edt.compiler.internal.sql.ItemNameToken;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.compiler.internal.sql.SQLInfo;
import org.eclipse.edt.compiler.internal.sql.Token;
import org.eclipse.edt.compiler.internal.sql.WhereCurrentOfToken;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.sql.SqlAddStatement;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlDeleteStatement;
import org.eclipse.edt.mof.egl.sql.SqlExecuteStatement;
import org.eclipse.edt.mof.egl.sql.SqlFactory;
import org.eclipse.edt.mof.egl.sql.SqlForEachStatement;
import org.eclipse.edt.mof.egl.sql.SqlGetByKeyStatement;
import org.eclipse.edt.mof.egl.sql.SqlGetByPositionStatement;
import org.eclipse.edt.mof.egl.sql.SqlHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlOpenStatement;
import org.eclipse.edt.mof.egl.sql.SqlPrepareStatement;
import org.eclipse.edt.mof.egl.sql.SqlReplaceStatement;
import org.eclipse.edt.mof.egl.sql.SqlToken;
import org.eclipse.edt.mof.egl.sql.SqlWhereCurrentOfToken;
import org.eclipse.edt.mof.serialization.IEnvironment;


public class SQLIOStatementGenerator extends DefaultIOStatementGenerator {
	
	SqlFactory factory = SqlFactory.INSTANCE;
	final SQLIOStatementGenerator generator = this;

	
	public SQLIOStatementGenerator() {
		super(null);
	}
	
	public SQLIOStatementGenerator(IEnvironment env) {
		super(env);
	}


	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement node) {
		super.visit(node);
		final SqlAddStatement stmt = (SqlAddStatement)stack.peek();
		
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(FromOrToExpressionClause clause) {
				clause.getExpression().accept(generator);
				stmt.setDataSource((Expression) stack.pop());
				return false;
			}
			public boolean visit(InlineSQLStatement sqlStmt) {
				stmt.setSqlString(sqlStmt.getValue());
				return false;
			}
		});
		
		if (node.getSqlInfo() != null) {
			HashMap<String, List<SqlToken>> map = createSqlTokensMap(node.getSqlInfo());
			stmt.setColumnsClause(createSqlClause(SQLConstants.COLUMNS, map));
			stmt.setInsertIntoClause(createSqlClause(SQLConstants.INSERT_INTO, map));
			stmt.setValuesClause(createSqlClause(SQLConstants.VALUES, map));
		}
		return false;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement node) {
		super.visit(node);
		final SqlDeleteStatement stmt = (SqlDeleteStatement)stack.peek();
		
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit( FromOrToExpressionClause clause) {
				clause.getExpression().accept(generator);
				stmt.setDataSource((Expression) stack.pop());
				return false;
			}
			public boolean visit(org.eclipse.edt.compiler.core.ast.NoCursorClause noCursorClause) {
				stmt.setNoCursor(true);
				return false;
			}

			public boolean visit(org.eclipse.edt.compiler.core.ast.WithInlineSQLClause withInlineSQLClause) {
				stmt.setHasExplicitSql(true);
				stmt.setSqlString(withInlineSQLClause.getSqlStmt().getValue());
				return false;
			}
		});
		if (node.getSqlInfo() != null) {
			HashMap<String, List<SqlToken>> map = createSqlTokensMap(node.getSqlInfo());
			stmt.setDeleteClause(createSqlClause(SQLConstants.DELETE, map));
			stmt.setFromClause(createSqlClause(SQLConstants.FROM, map));
			stmt.setWhereClause(createSqlClause(SQLConstants.WHERE, map));
//			if (stmt.getResultSetIdentifier() == null && map.get(SQLConstants.WHERE_CURRENT_OF_CLAUSE) != null) {
//				List<SqlToken> tokens = map.get(SQLConstants.WHERE_CURRENT_OF_CLAUSE);
//				if (tokens.size() > 0) {
//					// Should only be one token in this list.
//					SqlWhereCurrentOfToken whereToken = (SqlWhereCurrentOfToken) tokens.get(0);
//					stmt.setDataSourceIdentifier(whereToken.getResultSetIdentifier());
//				}
//			}
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement node) {
		final SqlExecuteStatement stmt = factory.createSqlExecuteStatement();
		stack.push(stmt);
//		stmt.setIsUpdate(node.isUpdate());
//		stmt.setIsDelete(node.isDelete());
//		stmt.setIsInsert(node.isInsert());
//		stmt.setPreparedStatementId(node.getPreparedStatementID());
		
		final IASTVisitor stmtGenerator =  this;
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingClause usingClause) {
				Iterator i = usingClause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(this);
					stmt.getUsingExpressions().add((Expression)stack.pop());
				}
				return false;
			};

			public boolean visit(org.eclipse.edt.compiler.core.ast.ForExpressionClause forExpressionClause) {
				forExpressionClause.getExpression().accept(stmtGenerator);
				stmt.getTargets().add((Expression)stack.pop());
				return false;
			}

		});
		if (node.getSqlInfo() != null) {
			HashMap<String, List<SqlToken>> map = createSqlTokensMap(node.getSqlInfo());
			stmt.setSqlClause(createSqlClause(SQLConstants.EXECUTE, map));
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement forEachStatement) {

		super.visit(forEachStatement);
		final SqlForEachStatement forEachStmt = (SqlForEachStatement) stack.peek();
		stack.push(forEachStmt);
		if (forEachStatement.hasSQLRecord()) {
			forEachStatement.getSQLRecord().accept(this);
			forEachStmt.getTargets().add((Expression)stack.pop());
		} else if (forEachStatement.hasResultSet()) {
			forEachStatement.getResultSet().accept(this);
			forEachStmt.setDataSource((Expression)stack.pop());
		}

		forEachStatement.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(IntoClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					forEachStmt.getIntoExpressions().add((Expression)stack.pop());
				}
				return false;
			};

		});


		StatementBlock block = irFactory.createStatementBlock();
		// TODO: set source info
//		setSourceInfoOn(block, forEachStatement);
		forEachStmt.setBody(block);

		for (Node node : (List<Node>)forEachStatement.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}

		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement node) {
		super.visit(node);
		final SqlGetByKeyStatement stmt = (SqlGetByKeyStatement)stack.peek();

		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(ForUpdateClause clause) {
				stmt.setIsForUpdate(true);
//				stmt.setDataSourceIdentifier(clause.getID());
				return false;
			};

			public boolean visit(SingleRowClause clause) {
				stmt.setIsSingleRowSelect(true);
				return false;
			};

			public boolean visit(WithIDClause clause) {
				stmt.setPreparedStatementId(clause.getID());
				stmt.setHasExplicitSql(true);
				return false;
			};

			public boolean visit(IntoClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getIntoExpressions().add((Expression)stack.pop());
				}
				return false;
			};
			
			public boolean visit(FromOrToExpressionClause clause) {
				clause.getExpression().accept(generator);
				stmt.setDataSource((Expression) stack.pop());
				return false;
			}

			public boolean visit(org.eclipse.edt.compiler.core.ast.WithInlineSQLClause withInlineSQLClause) {
				stmt.setHasExplicitSql(true);
				stmt.setSqlString(withInlineSQLClause.getSqlStmt().getValue());
				return false;
			}
		});

		if (node.getSqlInfo() != null) {
			HashMap<String, List<SqlToken>> map = createSqlTokensMap(node.getSqlInfo());
			stmt.setCallClause(createSqlClause(IEGLConstants.KEYWORD_CALL, map));
			stmt.setSelectClause(createSqlClause(SQLConstants.SELECT, map));			
			stmt.setFromClause(createSqlClause(SQLConstants.FROM, map));			
			stmt.setWhereClause(createSqlClause(SQLConstants.WHERE, map));
			stmt.setGroupByClause(createSqlClause(SQLConstants.GROUP_BY, map));
			stmt.setHavingClause(createSqlClause(SQLConstants.HAVING, map));
			stmt.setOrderByClause(createSqlClause(SQLConstants.ORDER_BY, map));
			stmt.setForUpdateOfClause(createSqlClause(SQLConstants.FOR_UPDATE_OF, map));			
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByPositionStatement node) {
		super.visit(node);
		final SqlGetByPositionStatement stmt = (SqlGetByPositionStatement)stack.peek();

		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.IntoClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getIntoExpressions().add((Expression)stack.pop());
				}
				return false;
			};						
		});
		
		if (node.hasFromExpr()) {
			node.getFromExpr().accept(this);
			stmt.setDataSource((Expression)stack.pop());
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement node) {
		final SqlOpenStatement stmt = factory.createSqlOpenStatement();
		stack.push(stmt);

		stmt.setIsHold(node.hasHold());
		stmt.setIsScroll(node.hasScroll());
		
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(ForUpdateClause clause) {
				stmt.setIsForUpdate(true);
				return false;
			};

			public boolean visit(FromOrToExpressionClause clause) {
				clause.getExpression().accept(generator);
				stmt.setDataSource((Expression) stack.pop());
				return false;
			}

			public boolean visit(WithIDClause clause) {
				stmt.setPreparedStatementId(clause.getID());
				stmt.setHasExplicitSql(true);
				return false;
			};

			public boolean visit(IntoClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getIntoExpressions().add((Expression)stack.pop());
				}
				return false;
			};

			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getUsingExpressions().add((Expression)stack.pop());
				}
				return false;
			};

			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingKeysClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getUsingKeyExpressions().add((Expression)stack.pop());
				}
				return false;
			};

			public boolean visit(org.eclipse.edt.compiler.core.ast.ForExpressionClause forExpressionClause) {
				forExpressionClause.getExpression().accept(generator);
				stmt.getTargets().add((Expression)stack.pop());
				return false;
			}

			public boolean visit(org.eclipse.edt.compiler.core.ast.WithInlineSQLClause withInlineSQLClause) {
				stmt.setHasExplicitSql(true);
				stmt.setSqlString(withInlineSQLClause.getSqlStmt().getValue());
				return false;
			}
		});

		node.getResultSet().accept(this);
		stmt.setResultSet((Expression)stack.pop());
		
		if (node.getSqlInfo() != null) {
			HashMap<String, List<SqlToken>> map = createSqlTokensMap(node.getSqlInfo());
			stmt.setCallClause(createSqlClause(IEGLConstants.KEYWORD_CALL, map));
			stmt.setSelectClause(createSqlClause(SQLConstants.SELECT, map));			
			stmt.setFromClause(createSqlClause(SQLConstants.FROM, map));			
			stmt.setWhereClause(createSqlClause(SQLConstants.WHERE, map));
			stmt.setGroupByClause(createSqlClause(SQLConstants.GROUP_BY, map));
			stmt.setHavingClause(createSqlClause(SQLConstants.HAVING, map));
			stmt.setOrderByClause(createSqlClause(SQLConstants.ORDER_BY, map));
			stmt.setForUpdateOfClause(createSqlClause(SQLConstants.FOR_UPDATE_OF, map));			
			
			// TODO: What is different from FOR_UPDATE_OF
//			stmt.setForUpdateClause(clause);	
		}
		
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement node) {
		final SqlPrepareStatement stmt = factory.createSqlPrepareStatement();
		stack.push(stmt);
//		stmt.setPreparedStatementId(node.getPreparedStatementID());

		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(FromExpressionClause clause) {
				clause.getExpression().accept(generator);
				stmt.setFromExpression((Expression)stack.pop());
				return false;
			};

			public boolean visit(ForExpressionClause clause) {
				clause.getExpression().accept(generator);
				stmt.setForExpression((Expression)stack.pop());
				return false;
			}
		});
		
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement node) {
		super.visit(node);
		final SqlReplaceStatement stmt = (SqlReplaceStatement)stack.peek();

		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(FromOrToExpressionClause clause) {
				clause.getExpression().accept(generator);
				stmt.setDataSource((Expression) stack.pop());
				return false;
			}

			public boolean visit(org.eclipse.edt.compiler.core.ast.NoCursorClause noCursorClause) {
				stmt.setNoCursor(true);
				return false;
			}

			public boolean visit(org.eclipse.edt.compiler.core.ast.WithInlineSQLClause withInlineSQLClause) {
				stmt.setHasExplicitSql(true);
				return false;
			}
		});

		if (node.getSqlInfo() != null) {
			HashMap<String, List<SqlToken>> map = createSqlTokensMap(node.getSqlInfo());
			stmt.setSetClause(createSqlClause(SQLConstants.SET, map));
			stmt.setUpdateClause(createSqlClause(SQLConstants.UPDATE, map));
			stmt.setWhereClause(createSqlClause(SQLConstants.WHERE, map));
			
//			if (stmt.getResultSetIdentifier() == null && map.get(SQLConstants.WHERE_CURRENT_OF_CLAUSE) != null) {
//				List<SqlToken> tokens = map.get(SQLConstants.WHERE_CURRENT_OF_CLAUSE);
//				if (tokens.size() > 0) {
//					// Should only be one token in this list.
//					SqlWhereCurrentOfToken whereToken = (SqlWhereCurrentOfToken) tokens.get(0);
//					stmt.setDataSourceIdentifier(whereToken.getResultSetIdentifier());
//				}
//			}
		}
		return false;
	}
	
	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.AddStatement stmt) {
		return factory.getSqlAddStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.CloseStatement stmt) {
		return factory.getSqlCloseStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.DeleteStatement stmt) {
		return factory.getSqlDeleteStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ExecuteStatement stmt) {
		return factory.getSqlExecuteStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ForEachStatement stmt) {
		return factory.getSqlForEachStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.GetByKeyStatement stmt) {
		return factory.getSqlGetByKeyStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.GetByPositionStatement stmt) {
		return factory.getSqlGetByPositionStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.OpenStatement stmt) {
		return factory.getSqlOpenStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.PrepareStatement stmt) {
		return factory.getSqlPrepareStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ReplaceStatement stmt) {
		return factory.getSqlReplaceStatementEClass();
	}



	private HashMap<String, List<SqlToken>> createSqlTokensMap(SQLInfo sqlinfo) {
		HashMap<String, List<SqlToken>> map = new HashMap<String, List<SqlToken>>();
		HashMap clauseMap = sqlinfo.getClauseMap();
		Iterator i = clauseMap.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			Token[] tokens = (Token[]) clauseMap.get(key);
			map.put(key, convertSqlTokens(tokens));
		}
		return map;
	}

	private SqlToken convertSqlToken(Token oldToken) {
		if (oldToken.isItemNameToken()) {
			ItemNameToken itemNameToken = (ItemNameToken) oldToken;
			if (itemNameToken.getExpression() != null) {
				itemNameToken.getExpression().accept(this);
			}
			SqlHostVariableToken newToken = null;
			if (itemNameToken.isInputHostVariableToken()) {
				newToken = factory.createSqlInputHostVariableToken();
				newToken.setString(oldToken.getString());
				newToken.setSqlString(oldToken.getSQLString());
			} else {
				if (itemNameToken.isOutputHostVariableToken()) {
					newToken = factory.createSqlOutputHostVariableToken();
					newToken.setString(oldToken.getString());
					newToken.setSqlString(oldToken.getSQLString());
	
				} else {
					if (itemNameToken.isTableNameHostVariableToken()) {
						newToken = factory.createSqlTableNameHostVariableToken();
						newToken.setString(oldToken.getString());
						newToken.setSqlString(oldToken.getSQLString());
					}
				}
			}
			if (newToken != null) {
				newToken.setHostVarExpression((LHSExpr)stack.pop());
			}
			return newToken;
		}
		if (oldToken.isSelectNameToken()) {
			SqlToken newToken = factory.createSqlSelectNameToken();
			newToken.setString(oldToken.getString());
			newToken.setSqlString(oldToken.getSQLString());
			return newToken;
		}
		if (oldToken.isStringToken()) {
			SqlToken newToken = factory.createSqlStringToken();
			newToken.setString(oldToken.getString());
			newToken.setSqlString(oldToken.getSQLString());
			return newToken;
		}
		if (oldToken.isWhereCurrentOfToken()) {
			SqlWhereCurrentOfToken whereToken = factory.createSqlWhereCurrentOfToken();
			whereToken.setString(oldToken.getString());
			whereToken.setSqlString(oldToken.getSQLString());
			whereToken.setResultSetIdentifier(((WhereCurrentOfToken) oldToken).getResultSetIdentifier());
			return whereToken;
		}
		return null;
	}

	private List<SqlToken> convertSqlTokens(Token[] oldTokens) {
		if (oldTokens == null) {
			return null;
		}
	
		List<SqlToken> list = new ArrayList<SqlToken>();
		for (int i = 0; i < oldTokens.length; i++) {
			list.add(convertSqlToken(oldTokens[i]));
		}
	
		return list;
	}

	private SqlClause createSqlClause(String name, Map<String, List<SqlToken>> map) {
		if (map.get(name) != null) {
			SqlClause clause = factory.createSqlClause();
			clause.setName(name);
			clause.getTokens().addAll(map.get(name));
			return clause;
		}
		return null;
	}
	

}
