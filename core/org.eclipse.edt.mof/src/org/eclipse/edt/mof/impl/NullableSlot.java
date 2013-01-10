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
package org.eclipse.edt.mof.impl;

public class NullableSlot extends Slot {

	boolean isNull = true;
	
	public Object getValue() {
		return isNull ? null : value;
	}
	public void set(Object value) {
		isNull = false;
		this.value = value;
	}
	
	public boolean isNull() {
		return isNull | value == null;
	}
	public void setIsNull(boolean value) {
		isNull = value;
	}

}
