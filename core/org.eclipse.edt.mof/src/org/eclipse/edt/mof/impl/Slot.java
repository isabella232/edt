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


public class Slot implements Cloneable {

	Object value;
	
	public Object get() {
		return value;
	}
	public void set(Object value) {
		this.value = value;
	}
	
	public boolean isNull() {
		return value == null;
	}
	
	public String toString() {
		if (value != null) {
			return "slot: " + value.toString();
		}
		else {
			return "slot: null";
		}
	}
	
	public Object clone() {
		Slot cloned;
		try {
			cloned = this.getClass().newInstance();
		} catch (Exception e) {
			cloned = new Slot();
		}
		return cloned;
	}
}
