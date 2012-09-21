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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.EEnum;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EMember;


public class EEnumImpl extends EDataTypeImpl implements EEnum {
	private static int Slot_entries=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EDataTypeImpl.totalSlots();
	}
	
	static {
		int offset = EDataTypeImpl.totalSlots();
		Slot_entries += offset;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EEnumLiteral> getLiterals() {
		return (List<EEnumLiteral>)slotGet(Slot_entries);
	}
	
	
	@Override
	public EEnumLiteral getEEnumLiteral(int value) {
		for (EEnumLiteral literal : getLiterals()) {
			if (literal.getValue() == value) return literal;
		}
		return null;
	}

	@Override
	public EEnumLiteral getEEnumLiteral(String name) {
		for (EEnumLiteral literal : getLiterals()) {
			if (literal.getCaseSensitiveName().equalsIgnoreCase(name)) return literal;
		}
		return null;
	}


	@Override
	public void addMember(EMember mbr) {
		if (mbr instanceof EEnumLiteral) {
			if (((EEnumLiteral)mbr).getValue() == 0 && getLiterals().size() > 0) {
				((EEnumLiteral)mbr).setValue(getLiterals().size());
			}
			getLiterals().add((EEnumLiteral)mbr);
			mbr.setDeclarer(this);
		}
		else {
			throw new IllegalArgumentException("Object " + mbr.toString() + " is not a valid member type for " + this.toString());
		}
	}	
	
}
