/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.persistence.sql.gen;

import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Type;

public interface DummyExpression extends LHSExpr {	
	String getExpr();
	void setExpr(String expr);
	
	void setType(Type type);
}