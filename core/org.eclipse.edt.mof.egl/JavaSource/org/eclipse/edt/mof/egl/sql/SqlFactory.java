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
package org.eclipse.edt.mof.egl.sql;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.egl.sql.impl.SqlFactoryImpl;


public interface SqlFactory extends EFactory  {
	public static final SqlFactory INSTANCE = new SqlFactoryImpl();
	public String packageName = "org.eclipse.edt.mof.egl.sql";
	
	String SqlAddStatement = packageName+".SqlAddStatement";
	String SqlClause = packageName+".SqlClause";
	String SqlCloseStatement = packageName+".SqlCloseStatement";
	String SqlDeleteStatement = packageName+".SqlDeleteStatement";
	String SqlExecuteStatement = packageName+".SqlExecuteStatement";
	String SqlForEachStatement = packageName+".SqlForEachStatement";
	String SqlGetByKeyStatement = packageName+".SqlGetByKeyStatement";
	String SqlGetByPositionStatement = packageName+".SqlGetByPositionStatement";
	String SqlHostVariableToken = packageName+".SqlHostVariableToken";
	String SqlInputHostVariableToken = packageName+".SqlInputHostVariableToken";
	String SqlIOStatement = packageName+".SqlIOStatement";
	String SqlOpenStatement = packageName+".SqlOpenStatement";
	String SqlOutputHostVariableToken = packageName+".SqlOutputHostVariableToken";
	String SqlPrepareStatement = packageName+".SqlPrepareStatement";
	String SqlReplaceStatement = packageName+".SqlReplaceStatement";
	String SqlSelectNameToken = packageName+".SqlSelectNameToken";
	String SqlStringToken = packageName+".SqlStringToken";
	String SqlTableNameHostVariableToken = packageName+".SqlTableNameHostVariableToken";
	String SqlToken = packageName+".SqlToken";
	String SqlUpdateStatement = packageName+".SqlUpdateStatement";
	String SqlWhereCurrentOfToken = packageName+".SqlWhereCurrentOfToken";
	
	EClass getSqlAddStatementEClass();
	EClass getSqlClauseEClass();
	EClass getSqlCloseStatementEClass();
	EClass getSqlDeleteStatementEClass();
	EClass getSqlExecuteStatementEClass();
	EClass getSqlForEachStatementEClass();
	EClass getSqlGetByKeyStatementEClass();
	EClass getSqlGetByPositionStatementEClass();
	EClass getSqlHostVariableTokenEClass();
	EClass getSqlInputHostVariableTokenEClass();
	EClass getSqlIOStatementEClass();
	EClass getSqlOpenStatementEClass();
	EClass getSqlOutputHostVariableTokenEClass();
	EClass getSqlPrepareStatementEClass();
	EClass getSqlReplaceStatementEClass();
	EClass getSqlSelectNameTokenEClass();
	EClass getSqlStringTokenEClass();
	EClass getSqlTableNameHostVariableTokenEClass();
	EClass getSqlTokenEClass();
	EClass getSqlUpdateStatementEClass();
	EClass getSqlWhereCurrentOfTokenEClass();
	public SqlAddStatement createSqlAddStatement();
	public SqlClause createSqlClause();
	public SqlCloseStatement createSqlCloseStatement();
	public SqlDeleteStatement createSqlDeleteStatement();
	public SqlExecuteStatement createSqlExecuteStatement();
	public SqlForEachStatement createSqlForEachStatement();
	public SqlGetByKeyStatement createSqlGetByKeyStatement();
	public SqlGetByPositionStatement createSqlGetByPositionStatement();
	public SqlHostVariableToken createSqlHostVariableToken();
	public SqlInputHostVariableToken createSqlInputHostVariableToken();
	public SqlOpenStatement createSqlOpenStatement();
	public SqlOutputHostVariableToken createSqlOutputHostVariableToken();
	public SqlPrepareStatement createSqlPrepareStatement();
	public SqlReplaceStatement createSqlReplaceStatement();
	public SqlSelectNameToken createSqlSelectNameToken();
	public SqlStringToken createSqlStringToken();
	public SqlTableNameHostVariableToken createSqlTableNameHostVariableToken();
	public SqlUpdateStatement createSqlUpdateStatement();
	public SqlWhereCurrentOfToken createSqlWhereCurrentOfToken();
}
