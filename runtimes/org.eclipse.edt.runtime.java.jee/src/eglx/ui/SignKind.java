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
package eglx.ui;
public enum SignKind {
	leading(1),
	none(2),
	parens(3),
	trailing(4);
	private final int value;
	private SignKind(int value) {
		this.value = value;
	}
	private SignKind() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
