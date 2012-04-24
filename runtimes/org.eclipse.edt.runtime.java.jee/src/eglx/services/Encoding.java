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
package eglx.services;
public enum Encoding {
	NONE(1),
	JSON(2),
	XML(3),
	_FORM(4),
	USE_CONTENTTYPE(5);
	private final int value;
	private Encoding(int value) {
		this.value = value;
	}
	private Encoding() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
