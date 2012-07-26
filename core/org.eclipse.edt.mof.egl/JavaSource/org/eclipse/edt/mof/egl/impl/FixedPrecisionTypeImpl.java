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

import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Type;

public class FixedPrecisionTypeImpl extends ParameterizedTypeImpl implements FixedPrecisionType {
	private static int Slot_length=0;
	private static int Slot_decimals=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ParameterizedTypeImpl.totalSlots();
	}
	
	static {
		int offset = ParameterizedTypeImpl.totalSlots();
		Slot_length += offset;
		Slot_decimals += offset;
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
	public Integer getDecimals() {
		return (Integer)slotGet(Slot_decimals);
	}
	
	@Override
	public void setDecimals(Integer value) {
		slotSet(Slot_decimals, value);
	}
	
	@Override
	// Assumes primitive types have the same classifier
	public boolean typeArgsEqual(Type type) {
		return (type instanceof FixedPrecisionType)
			? (this.getLength() == ((FixedPrecisionType)type).getLength()
			  && this.getDecimals() == ((FixedPrecisionType)type).getDecimals())
			: false;
	}

	@Override
	String typeArgsSignature() {
		if (getLength() != null && getLength() != 0)
			return "(" + getLength() + PrimArgDelimiter + getDecimals() + ")";
		else
			return super.typeArgsSignature();
	}

	@Override
	public int getMaxNumberOfParms() {
		return 2;
	}

}
