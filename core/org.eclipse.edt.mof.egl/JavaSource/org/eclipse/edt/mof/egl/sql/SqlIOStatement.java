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

import java.util.List;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IOStatement;


public interface SqlIOStatement extends IOStatement {
	String getPreparedStatementId();
	
	void setPreparedStatementId(String value);
	
	Expression getDataSource();
	
	void setDataSource(Expression value);
	
	List<Expression> getIntoExpressions();
	
	Boolean hasExplicitSql();
	
	void setHasExplicitSql(Boolean value);
	
	String getSqlString();
	
	void setSqlString(String value);
	
	
	public List<SqlClause> getSqlClauses();
}
