/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.persistence.sql.impl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.GetByPositionStatement;
import org.eclipse.edt.mof.egl.IrFactoryBase;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlAddStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlCloseStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlDeleteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlExecuteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlFactory;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlForEachStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlGetByKeyStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlOpenStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlPrepareStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlReplaceStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlUpdateStatement;
import org.eclipse.edt.mof.impl.EFactoryImpl;


public class SqlFactoryImpl extends EFactoryImpl implements SqlFactory {
	@Override
	public EClass getSqlAddStatementEClass() {
		return (EClass)getTypeNamed(SqlAddStatement);
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
	public EClass getSqlActionStatementEClass() {
		return (EClass)getTypeNamed(SqlActionStatement);
	}
	
	@Override
	public EClass getSqlOpenStatementEClass() {
		return (EClass)getTypeNamed(SqlOpenStatement);
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
	public EClass getSqlUpdateStatementEClass() {
		return (EClass)getTypeNamed(SqlUpdateStatement);
	}
	
	@Override
	public EClass getGetByPositionStatementEClass() {
		return (EClass)getTypeNamed(IrFactoryBase.GetByPositionStatement);
	}
	
	@Override
	public SqlAddStatement createSqlAddStatement() {
		return (SqlAddStatement)getSqlAddStatementEClass().newInstance();
	}
	
	@Override
	public SqlAddStatement createSqlActionStatement() {
		return (SqlAddStatement)getSqlActionStatementEClass().newInstance();
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
	public SqlOpenStatement createSqlOpenStatement() {
		return (SqlOpenStatement)getSqlOpenStatementEClass().newInstance();
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
	public SqlUpdateStatement createSqlUpdateStatement() {
		return (SqlUpdateStatement)getSqlUpdateStatementEClass().newInstance();
	}

	@Override
	public GetByPositionStatement createGetByPositionStatement() {
		return (GetByPositionStatement)getGetByPositionStatementEClass().newInstance();
	}
	
}
