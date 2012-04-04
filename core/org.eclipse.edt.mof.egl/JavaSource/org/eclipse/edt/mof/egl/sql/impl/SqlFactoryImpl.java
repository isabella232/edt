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
package org.eclipse.edt.mof.egl.sql.impl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.sql.SqlAddStatement;
import org.eclipse.edt.mof.egl.sql.SqlClause;
import org.eclipse.edt.mof.egl.sql.SqlCloseStatement;
import org.eclipse.edt.mof.egl.sql.SqlDeleteStatement;
import org.eclipse.edt.mof.egl.sql.SqlExecuteStatement;
import org.eclipse.edt.mof.egl.sql.SqlFactory;
import org.eclipse.edt.mof.egl.sql.SqlForEachStatement;
import org.eclipse.edt.mof.egl.sql.SqlGetByKeyStatement;
import org.eclipse.edt.mof.egl.sql.SqlGetByPositionStatement;
import org.eclipse.edt.mof.egl.sql.SqlHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlInputHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlOpenStatement;
import org.eclipse.edt.mof.egl.sql.SqlOutputHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlPrepareStatement;
import org.eclipse.edt.mof.egl.sql.SqlReplaceStatement;
import org.eclipse.edt.mof.egl.sql.SqlSelectNameToken;
import org.eclipse.edt.mof.egl.sql.SqlStringToken;
import org.eclipse.edt.mof.egl.sql.SqlTableNameHostVariableToken;
import org.eclipse.edt.mof.egl.sql.SqlUpdateStatement;
import org.eclipse.edt.mof.egl.sql.SqlWhereCurrentOfToken;
import org.eclipse.edt.mof.impl.EFactoryImpl;


public class SqlFactoryImpl extends EFactoryImpl implements SqlFactory {
	@Override
	public EClass getSqlAddStatementEClass() {
		return (EClass)getTypeNamed(SqlAddStatement);
	}
	
	@Override
	public EClass getSqlClauseEClass() {
		return (EClass)getTypeNamed(SqlClause);
	}
	
	@Override
	public EClass getSqlCloseStatementEClass() {
		return (EClass)getTypeNamed(SqlCloseStatement);
	}
	
	@Override
	public EClass getSqlDeleteStatementEClass() {
		return (EClass)getTypeNamed(SqlDeleteStatement);
	}
	
	@Override
	public EClass getSqlExecuteStatementEClass() {
		return (EClass)getTypeNamed(SqlExecuteStatement);
	}
	
	@Override
	public EClass getSqlForEachStatementEClass() {
		return (EClass)getTypeNamed(SqlForEachStatement);
	}
	
	@Override
	public EClass getSqlGetByKeyStatementEClass() {
		return (EClass)getTypeNamed(SqlGetByKeyStatement);
	}
	
	@Override
	public EClass getSqlGetByPositionStatementEClass() {
		return (EClass)getTypeNamed(SqlGetByPositionStatement);
	}
	
	@Override
	public EClass getSqlHostVariableTokenEClass() {
		return (EClass)getTypeNamed(SqlHostVariableToken);
	}
	
	@Override
	public EClass getSqlInputHostVariableTokenEClass() {
		return (EClass)getTypeNamed(SqlInputHostVariableToken);
	}
	
	@Override
	public EClass getSqlIOStatementEClass() {
		return (EClass)getTypeNamed(SqlIOStatement);
	}
	
	@Override
	public EClass getSqlOpenStatementEClass() {
		return (EClass)getTypeNamed(SqlOpenStatement);
	}
	
	@Override
	public EClass getSqlOutputHostVariableTokenEClass() {
		return (EClass)getTypeNamed(SqlOutputHostVariableToken);
	}
	
	@Override
	public EClass getSqlPrepareStatementEClass() {
		return (EClass)getTypeNamed(SqlPrepareStatement);
	}
	
	@Override
	public EClass getSqlReplaceStatementEClass() {
		return (EClass)getTypeNamed(SqlReplaceStatement);
	}
	
	@Override
	public EClass getSqlSelectNameTokenEClass() {
		return (EClass)getTypeNamed(SqlSelectNameToken);
	}
	
	@Override
	public EClass getSqlStringTokenEClass() {
		return (EClass)getTypeNamed(SqlStringToken);
	}
	
	@Override
	public EClass getSqlTableNameHostVariableTokenEClass() {
		return (EClass)getTypeNamed(SqlTableNameHostVariableToken);
	}
	
	@Override
	public EClass getSqlTokenEClass() {
		return (EClass)getTypeNamed(SqlToken);
	}
	
	@Override
	public EClass getSqlUpdateStatementEClass() {
		return (EClass)getTypeNamed(SqlUpdateStatement);
	}
	
	@Override
	public EClass getSqlWhereCurrentOfTokenEClass() {
		return (EClass)getTypeNamed(SqlWhereCurrentOfToken);
	}
	
	@Override
	public SqlAddStatement createSqlAddStatement() {
		return (SqlAddStatement)getSqlAddStatementEClass().newInstance();
	}
	
	@Override
	public SqlClause createSqlClause() {
		return (SqlClause)getSqlClauseEClass().newInstance();
	}
	
	@Override
	public SqlCloseStatement createSqlCloseStatement() {
		return (SqlCloseStatement)getSqlCloseStatementEClass().newInstance();
	}
	
	@Override
	public SqlDeleteStatement createSqlDeleteStatement() {
		return (SqlDeleteStatement)getSqlDeleteStatementEClass().newInstance();
	}
	
	@Override
	public SqlExecuteStatement createSqlExecuteStatement() {
		return (SqlExecuteStatement)getSqlExecuteStatementEClass().newInstance();
	}
	
	@Override
	public SqlForEachStatement createSqlForEachStatement() {
		return (SqlForEachStatement)getSqlForEachStatementEClass().newInstance();
	}
	
	@Override
	public SqlGetByKeyStatement createSqlGetByKeyStatement() {
		return (SqlGetByKeyStatement)getSqlGetByKeyStatementEClass().newInstance();
	}
	
	@Override
	public SqlGetByPositionStatement createSqlGetByPositionStatement() {
		return (SqlGetByPositionStatement)getSqlGetByPositionStatementEClass().newInstance();
	}
	
	@Override
	public SqlHostVariableToken createSqlHostVariableToken() {
		return (SqlHostVariableToken)getSqlHostVariableTokenEClass().newInstance();
	}
	
	@Override
	public SqlInputHostVariableToken createSqlInputHostVariableToken() {
		return (SqlInputHostVariableToken)getSqlInputHostVariableTokenEClass().newInstance();
	}
	
	@Override
	public SqlOpenStatement createSqlOpenStatement() {
		return (SqlOpenStatement)getSqlOpenStatementEClass().newInstance();
	}
	
	@Override
	public SqlOutputHostVariableToken createSqlOutputHostVariableToken() {
		return (SqlOutputHostVariableToken)getSqlOutputHostVariableTokenEClass().newInstance();
	}
	
	@Override
	public SqlPrepareStatement createSqlPrepareStatement() {
		return (SqlPrepareStatement)getSqlPrepareStatementEClass().newInstance();
	}
	
	@Override
	public SqlReplaceStatement createSqlReplaceStatement() {
		return (SqlReplaceStatement)getSqlReplaceStatementEClass().newInstance();
	}
	
	@Override
	public SqlSelectNameToken createSqlSelectNameToken() {
		return (SqlSelectNameToken)getSqlSelectNameTokenEClass().newInstance();
	}
	
	@Override
	public SqlStringToken createSqlStringToken() {
		return (SqlStringToken)getSqlStringTokenEClass().newInstance();
	}
	
	@Override
	public SqlTableNameHostVariableToken createSqlTableNameHostVariableToken() {
		return (SqlTableNameHostVariableToken)getSqlTableNameHostVariableTokenEClass().newInstance();
	}
	
	@Override
	public SqlUpdateStatement createSqlUpdateStatement() {
		return (SqlUpdateStatement)getSqlUpdateStatementEClass().newInstance();
	}
	
	@Override
	public SqlWhereCurrentOfToken createSqlWhereCurrentOfToken() {
		return (SqlWhereCurrentOfToken)getSqlWhereCurrentOfTokenEClass().newInstance();
	}
	
}
