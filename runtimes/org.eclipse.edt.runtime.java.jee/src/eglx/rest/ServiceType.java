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
package eglx.rest;
public enum ServiceType {
	TrueRest(1),
	EglRpc(2),
	EglDedicated(3);
	private final int value;
	private ServiceType(int value) {
		this.value = value;
	}
	private ServiceType() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
