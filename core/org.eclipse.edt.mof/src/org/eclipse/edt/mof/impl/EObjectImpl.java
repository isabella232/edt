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
package org.eclipse.edt.mof.impl;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EObject;

public class EObjectImpl extends InternalEObject implements EObject {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_eType = 0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots;
	}
					
	public Object eGet(EField field) {
		return internalGet(field);
	}
	
	public void eSet(EField field, Object value) {
		internalSet(field, value);
	}
	
	public Object eGet(String name) {
		return internalGet(name);
	}

	public void eSet(String name, Object value) {
		internalSet(name, value);
	}
	
	public Object eGet(Integer index) {
		return internalGet(index);
	}

	public void eSet(Integer index, Object value) {
		internalSet(index, value);
	}

	public boolean isNull(EField field) {
		return internalIsNull(field);
	}

	public EClass getEClass() {
		return (EClass) slotGet(Slot_eType);
	}
	
	public void setEClass(EClass type) {
		slotSet(Slot_eType, type);
	}

	public String toStringHeader() {
		return "Instance of: " + getEClass().getETypeSignature();
	}

}
