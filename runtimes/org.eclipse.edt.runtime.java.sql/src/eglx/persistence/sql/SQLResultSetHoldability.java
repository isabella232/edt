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
package eglx.persistence.sql;
public enum SQLResultSetHoldability {
	HOLD_CURSORS_OVER_COMMIT(1),
	CLOSE_CURSORS_AT_COMMIT(2);
	private final int value;
	private SQLResultSetHoldability(int value) {
		this.value = value;
	}
	private SQLResultSetHoldability() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
