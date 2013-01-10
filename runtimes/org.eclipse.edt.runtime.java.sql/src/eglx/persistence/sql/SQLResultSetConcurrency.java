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
public enum SQLResultSetConcurrency {
	CONCUR_READ_ONLY(1),
	CONCUR_UPDATABLE(2);
	private final int value;
	private SQLResultSetConcurrency(int value) {
		this.value = value;
	}
	private SQLResultSetConcurrency() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
