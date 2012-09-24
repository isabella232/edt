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

import org.eclipse.edt.mof.egl.ConstantFormField;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Form;
import org.eclipse.edt.mof.egl.FormField;
import org.eclipse.edt.mof.egl.FormGroup;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.VariableFormField;
import org.eclipse.edt.mof.utils.EList;


public class FormImpl extends FormImplBase implements Form {
	
	@Override
	public void addMember(Member mbr) {
		if (mbr instanceof FormField) {
			getFormFields().add((FormField)mbr);
		}
		else {
			throw new IllegalArgumentException(mbr.toString() + " is not a valid Member for Type: " + this.getFullyQualifiedName());
		}
		mbr.setContainer(this);
	}
	
	@Override
	public String getFullyQualifiedName() {
		FormGroup fg = getContainer();
		return fg == null ? super.getFullyQualifiedName() : fg.getFullyQualifiedName() + Type.NestedPartDelimiter + getCaseSensitiveName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConstantFormField> getConstantFields() {
		EList list = new EList();
		for (Field field : getStructuredFields()) {
			if (field instanceof ConstantFormField) {
				list.add(field);
			}
		}
		list.setReadOnly(true);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<VariableFormField> getVariableFields() {
		EList list = new EList();
		for (Field field : getStructuredFields()) {
			if (field instanceof VariableFormField) {
				list.add(field);
			}
		}
		list.setReadOnly(true);
		return list;
	}

	@Override
	public String getMofSerializationKey() {
		if (getContainer() == null) {
			return super.getMofSerializationKey();
		}
		else {
			return getContainer().getMofSerializationKey() + Type.NestedPartDelimiter + getCaseSensitiveName();
		}
	}
	
}
