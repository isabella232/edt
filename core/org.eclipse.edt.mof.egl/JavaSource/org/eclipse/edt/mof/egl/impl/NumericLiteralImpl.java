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

import org.eclipse.edt.mof.egl.NumericLiteral;

public abstract class NumericLiteralImpl extends PrimitiveTypeLiteralImpl implements NumericLiteral {
	private static int Slot_isNegated=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + PrimitiveTypeLiteralImpl.totalSlots();
	}
	
	static {
		int offset = PrimitiveTypeLiteralImpl.totalSlots();
		Slot_isNegated += offset;
	}
	@Override
	public Boolean isNegated() {
		return (Boolean)slotGet(Slot_isNegated);
	}
	
	@Override
	public void setIsNegated(Boolean value) {
		slotSet(Slot_isNegated, value);
	}
	
	@Override
	public String getValue() {
		if (isNegated()) {
			return ("-" + super.getValue());
		}
		
		return super.getValue();
	}
	
	@Override
	public void setValue(String value) {
		if (value.startsWith("+")) {
			setValue(value.substring(1));
			return;
		}
		
		if (value.startsWith("-")) {
			setIsNegated(!isNegated());
			setValue(value.substring(1));
			return;
		}
		
		super.setValue(value);
	}
	
	@Override
	public String getUnsignedValue() {
		return super.getValue();
	}
	
}
