/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.eunit.runtime;
public enum ServiceBindingType {
	DEDICATED(1),
	DEVELOP(2),
	DEPLOYED(3);
	private final int value;
	private ServiceBindingType(int value) {
		this.value = value;
	}
	private ServiceBindingType() {
		value = -1;
	}
	public int getValue() {
		return value;
	}
}
