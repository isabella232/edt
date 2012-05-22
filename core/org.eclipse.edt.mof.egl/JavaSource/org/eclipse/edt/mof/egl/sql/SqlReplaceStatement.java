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

import org.eclipse.edt.mof.egl.ReplaceStatement;

public interface SqlReplaceStatement extends SqlIOStatement, ReplaceStatement {
	SqlClause getSetClause();
	
	void setSetClause(SqlClause value);
	
	SqlClause getFromClause();
	
	void setFromClause(SqlClause value);
	
	SqlClause getUpdateClause();
	
	void setUpdateClause(SqlClause value);
	
	SqlClause getWhereClause();
	
	void setWhereClause(SqlClause value);
	
	Boolean noCursor();
	
	void setNoCursor(Boolean value);
	
}
