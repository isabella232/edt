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
package eglx.persistence.sql;

public class SQLWarning extends SQLException {
	private static final long serialVersionUID = 10L;
	
	public SQLWarning nextWarning;
	
	public SQLWarning() {
		super();
	}
	
	public void ezeInitialize() {
		super.ezeInitialize();
		nextWarning = null;
	}
	
	public SQLWarning getNextWarning() {
		return nextWarning;
	}
	public void setNextWarning(SQLWarning warn) {
		this.nextWarning = warn;
	}
}
