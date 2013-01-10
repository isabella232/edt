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
package eglx.services;
public enum ServiceKind {
	EGL(1),
	WEB(2),
	NATIVE(3),
	REST(4);
	private final int value;
	private ServiceKind(int value) {
		this.value = value;
	}
	private ServiceKind() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
