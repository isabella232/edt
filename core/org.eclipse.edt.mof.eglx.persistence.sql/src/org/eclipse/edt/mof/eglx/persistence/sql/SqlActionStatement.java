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
package org.eclipse.edt.mof.eglx.persistence.sql;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IOStatement;


public interface SqlActionStatement extends IOStatement {
	Expression getPreparedStatement();
	
	void setPreparedStatement(Expression expr);
	
	Boolean hasExplicitSql();
	
	void setHasExplicitSql(Boolean value);
	
	String getSqlString();
	
	void setSqlString(String value);
	
}
