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

import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EType;

public class EFieldImpl extends EMemberImpl implements EField {
	private static final long serialVersionUID = 1L;

	private static int Slot_isTransient = 0;
	private static int Slot_containment = 1;
	private static int Slot_initialValue = 2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + EMemberImpl.totalSlots();
	}
	
	static { 
		int offset = EMemberImpl.totalSlots();
		Slot_isTransient += offset;
		Slot_containment += offset;
		Slot_initialValue += offset;
	}
	
	transient Integer slotIndex;
	
	public boolean isTransient() {
		return (Boolean)slotGet(Slot_isTransient);
	}
	
	public void setIsTransient(boolean value) {
		slotSet(Slot_isTransient, value);
	}

	
	@Override
	public boolean getContainment() {
		return (Boolean)slotGet(Slot_containment);
	}

	@Override
	public void setContainment(boolean value) {
		slotSet(Slot_containment, value);
	}

	
	@Override
	public Object getInitialValue() {
		Object value = slotGet(Slot_initialValue);
		if (value instanceof String) {
			EType type = getEType();
			if (type instanceof EDataType) {
				String typeName = ((EDataType)type).getJavaClassName();
				if (typeName != null){
					if (typeName.equals("java.lang.Integer")) {
						value = Integer.valueOf((String)value);
					} else if (typeName.equals("java.lang.Boolean")) {
						value = Boolean.valueOf((String)value);
					}
				}
			}
		}
		return value;
	}

	@Override
	public void setInitialValue(Object value) {
		slotSet(Slot_initialValue, value);
	}

	public boolean isField() {
		return true;
	}
	
	public String toStringHeader() {
		return getName() + " : " + getTypeSignature();
	}
}
