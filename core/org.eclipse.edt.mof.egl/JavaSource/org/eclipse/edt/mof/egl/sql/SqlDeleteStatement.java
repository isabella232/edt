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

import org.eclipse.edt.mof.egl.DeleteStatement;

public interface SqlDeleteStatement extends SqlIOStatement, DeleteStatement {
	SqlClause getDeleteClause();
	
	void setDeleteClause(SqlClause value);
	
	SqlClause getFromClause();
	
	void setFromClause(SqlClause value);
	
	Boolean noCursor();
	
	void setNoCursor(Boolean value);
	
	SqlClause getWhereClause();
	
	void setWhereClause(SqlClause value);
	
}
