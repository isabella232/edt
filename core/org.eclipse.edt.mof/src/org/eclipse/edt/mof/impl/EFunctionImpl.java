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

import java.util.List;

import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EParameter;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.utils.EList;


public class EFunctionImpl extends EMemberImpl implements EFunction {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_eParameters = 0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EMemberImpl.totalSlots();
	}
	
	static { 
		int offset = EMemberImpl.totalSlots();
		Slot_eParameters += offset;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EParameter> getEParameters() {
		if (slotGet(Slot_eParameters) == null) {
			slotSet(Slot_eParameters, new EList<EParameter>());
		}
		return (List<EParameter>)slotGet(Slot_eParameters);
	}

	@Override
	public EType getReturnType() {
		return getEType();
	}

	@Override
	public String getSignature() {
		StringBuffer signature = new StringBuffer();
		signature.append(getName());
		signature.append('(');
		for (int i = 0; i < getEParameters().size(); i++) {
			signature.append(getEParameters().get(i).getEType().getETypeSignature());
			if (i < getEParameters().size()-1) {
				signature.append(", ");
			}
		}
		signature.append(')');
		return signature.toString();
	}

	@Override
	public void addMember(EMember mbr) {
		if (mbr instanceof EParameter) {
			getEParameters().add((EParameter)mbr);
			((EParameter)mbr).setFunction(this);
		}
		else {
			throw new IllegalArgumentException("Object " + mbr.toString() + " is not a valid member type for " + this.toString());
		}
	}
}
