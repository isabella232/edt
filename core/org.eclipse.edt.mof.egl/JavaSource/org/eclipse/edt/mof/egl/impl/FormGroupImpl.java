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

import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.FormGroup;


public class FormGroupImpl extends PartImpl implements FormGroup {
	private static int Slot_forms=0;
	private static int Slot_nestedForms=1;
	private static int Slot_usedForms=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + PartImpl.totalSlots();
	}
	
	static {
		int offset = PartImpl.totalSlots();
		Slot_forms += offset;
		Slot_nestedForms += offset;
		Slot_usedForms += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Form> getForms() {
		return (List<Form>)slotGet(Slot_forms);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Form> getNestedForms() {
		return (List<Form>)slotGet(Slot_nestedForms);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Form> getUsedForms() {
		return (List<Form>)slotGet(Slot_usedForms);
	}

	@Override
	public Form getForm(String name) {
		for (Form form : getForms()) {
			if (form.getName().equalsIgnoreCase(name)) {
				return form;
			}
		}
		return null;
	}
	
}
