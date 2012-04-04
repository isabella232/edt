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

import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.Type;

public class SequenceTypeImpl extends ParameterizedTypeImpl implements SequenceType {
	private static int Slot_length=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ParameterizedTypeImpl.totalSlots();
	}
	
	static {
		int offset = ParameterizedTypeImpl.totalSlots();
		Slot_length += offset;
	}
	@Override
	public Integer getLength() {
		return (Integer)slotGet(Slot_length);
	}
	
	@Override
	public void setLength(Integer value) {
		slotSet(Slot_length, value);
	}
	@Override
	// Assumes primitive types have the same classifier
	public boolean typeArgsEqual(Type type) {
		return type instanceof SequenceType 
			? this.getLength() == ((SequenceType)type).getLength()
			: false;
	}

	@Override
	String typeArgsSignature() {
		return "(" + getLength() + ")";
	}

}
