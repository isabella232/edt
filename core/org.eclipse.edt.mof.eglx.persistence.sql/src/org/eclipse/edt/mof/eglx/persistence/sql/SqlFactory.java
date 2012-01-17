/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.persistence.sql;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.egl.GetByPositionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.impl.SqlFactoryImpl;

public interface SqlFactory extends EFactory  {
	public static final SqlFactory INSTANCE = new SqlFactoryImpl();
	public String packageName = "org.eclipse.edt.mof.eglx.persistence.sql";
	
	String SqlAddStatement = packageName+".SqlAddStatement";
	String SqlCloseStatement = packageName+".SqlCloseStatement";
	String SqlDeleteStatement = packageName+".SqlDeleteStatement";
	String SqlExecuteStatement = packageName+".SqlExecuteStatement";
	String SqlForEachStatement = packageName+".SqlForEachStatement";
	String SqlGetByKeyStatement = packageName+".SqlGetByKeyStatement";
	String SqlActionStatement = packageName+".SqlActionStatement";
	String SqlOpenStatement = packageName+".SqlOpenStatement";
	String SqlPrepareStatement = packageName+".SqlPrepareStatement";
	String SqlReplaceStatement = packageName+".SqlReplaceStatement";
	String SqlUpdateStatement = packageName+".SqlUpdateStatement";
	
	EClass getSqlAddStatementEClass();
	EClass getSqlCloseStatementEClass();
	EClass getSqlDeleteStatementEClass();
	EClass getSqlExecuteStatementEClass();
	EClass getSqlForEachStatementEClass();
	EClass getSqlGetByKeyStatementEClass();
	EClass getSqlActionStatementEClass();
	EClass getSqlOpenStatementEClass();
	EClass getSqlPrepareStatementEClass();
	EClass getSqlReplaceStatementEClass();
	EClass getSqlUpdateStatementEClass();
	EClass getGetByPositionStatementEClass();
	
	public SqlAddStatement createSqlAddStatement();
	public SqlActionStatement createSqlActionStatement();
	public SqlCloseStatement createSqlCloseStatement();
	public SqlDeleteStatement createSqlDeleteStatement();
	public SqlExecuteStatement createSqlExecuteStatement();
	public SqlForEachStatement createSqlForEachStatement();
	public SqlGetByKeyStatement createSqlGetByKeyStatement();
	public SqlOpenStatement createSqlOpenStatement();
	public SqlPrepareStatement createSqlPrepareStatement();
	public SqlReplaceStatement createSqlReplaceStatement();
	public SqlUpdateStatement createSqlUpdateStatement();
	
	public GetByPositionStatement createGetByPositionStatement();

}
