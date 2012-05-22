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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.OpenStatement;

public interface SqlOpenStatement extends SqlGetByKeyStatement, OpenStatement {
	Boolean isHold();
	
	void setIsHold(Boolean value);
	
	Boolean isScroll();
	
	void setIsScroll(Boolean value);
	
	Expression getResultSet();
	
	void setResultSet(Expression value);
}
