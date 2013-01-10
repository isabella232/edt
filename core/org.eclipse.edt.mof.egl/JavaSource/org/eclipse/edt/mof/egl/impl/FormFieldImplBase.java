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
package org.eclipse.edt.mof.egl.impl;

import java.util.List;

import org.eclipse.edt.mof.egl.ElementAnnotations;
import org.eclipse.edt.mof.egl.FormField;


public abstract class FormFieldImplBase extends FieldImpl implements FormField {
	private static int Slot_elementAnnotations=0;
	private static int Slot_occurs=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + FieldImpl.totalSlots();
	}
	
	static {
		int offset = FieldImpl.totalSlots();
		Slot_elementAnnotations += offset;
		Slot_occurs += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ElementAnnotations> getElementAnnotations() {
		return (List<ElementAnnotations>)slotGet(Slot_elementAnnotations);
	}
	
	@Override
	public Integer getOccurs() {
		return (Integer)slotGet(Slot_occurs);
	}
	
	@Override
	public void setOccurs(Integer value) {
		slotSet(Slot_occurs, value);
	}
	
}
