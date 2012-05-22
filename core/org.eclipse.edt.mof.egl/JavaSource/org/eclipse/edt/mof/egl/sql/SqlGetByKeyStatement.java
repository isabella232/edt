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

import org.eclipse.edt.mof.egl.GetByKeyStatement;

public interface SqlGetByKeyStatement extends SqlIOStatement, GetByKeyStatement {
	SqlClause getCallClause();
	
	void setCallClause(SqlClause value);
	
	SqlClause getForUpdateOfClause();
	
	void setForUpdateOfClause(SqlClause value);
	
	SqlClause getFromClause();
	
	void setFromClause(SqlClause value);
	
	SqlClause getGroupByClause();
	
	void setGroupByClause(SqlClause value);
	
	SqlClause getHavingClause();
	
	void setHavingClause(SqlClause value);
	
	SqlClause getOrderByClause();
	
	void setOrderByClause(SqlClause value);
	
	SqlClause getSelectClause();
	
	void setSelectClause(SqlClause value);
	
	SqlClause getWhereClause();
	
	void setWhereClause(SqlClause value);
	
	Boolean isSingleRowSelect();
	
	void setIsSingleRowSelect(Boolean value);
	
	Boolean isForUpdate();
	
	void setIsForUpdate(Boolean value);
	
}
