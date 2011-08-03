/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eglx.services;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
public enum ServiceKind {
	EGL(1),
	WEB(2),
	NATIVE(3),
	REST(4);
	private final int value;
	ServiceKind(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
