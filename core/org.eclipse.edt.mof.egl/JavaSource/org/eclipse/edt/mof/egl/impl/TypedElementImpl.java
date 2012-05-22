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

import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public abstract class TypedElementImpl extends NamedElementImpl implements TypedElement {
	private static int Slot_type=0;
	private static int Slot_isNullable=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + NamedElementImpl.totalSlots();
	}
	
	static {
		int offset = NamedElementImpl.totalSlots();
		Slot_type += offset;
		Slot_isNullable += offset;
	}
	@Override
	public Type getType() {
		return (Type)slotGet(Slot_type);
	}
	
	@Override
	public void setType(Type value) {
		slotSet(Slot_type, value);
	}
	
	@Override
	public boolean isNullable() {
		return (Boolean)slotGet(Slot_isNullable);
	}
	
	@Override
	public void setIsNullable(boolean value) {
		slotSet(Slot_isNullable, value);
	}
	
}
