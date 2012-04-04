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
package org.eclipse.edt.mof.egl.impl;

import java.util.List;

import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.FormField;
import org.eclipse.edt.mof.egl.FormGroup;


public abstract class FormImplBase extends RecordImpl implements Form {
	private static int Slot_container=0;
	private static int Slot_formFields=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + RecordImpl.totalSlots();
	}
	
	static {
		int offset = RecordImpl.totalSlots();
		Slot_container += offset;
		Slot_formFields += offset;
	}
	@Override
	public FormGroup getContainer() {
		return (FormGroup)slotGet(Slot_container);
	}
	
	@Override
	public void setContainer(FormGroup value) {
		slotSet(Slot_container, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<FormField> getFormFields() {
		return (List<FormField>)slotGet(Slot_formFields);
	}
	
}
