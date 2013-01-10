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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EMember;
import org.eclipse.edt.mof.EMemberContainer;
import org.eclipse.edt.mof.EType;

public abstract class EMemberImpl extends ENamedElementImpl implements EMember {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_declarer = 0;
	private static int Slot_eType = 1;
	private static int Slot_nullable = 2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + ENamedElementImpl.totalSlots();
	}
	
	static { 
		int offset = ENamedElementImpl.totalSlots();
		Slot_declarer += offset;
		Slot_eType += offset;
		Slot_nullable += offset;
	}
	
	@Override
	public EMemberContainer getDeclarer() {
		return (EMemberContainer)slotGet(Slot_declarer);
	}


	@Override
	public void setDeclarer(EMemberContainer container) {
		slotSet(Slot_declarer, container);
	}

	@Override
	public void setEType(EType type) {
		slotSet(Slot_eType, type);	
	}

	@Override
	public EType getEType() {
		return (EType)slotGet(Slot_eType);
	}

	@Override
	public boolean isNullable() {
		return (Boolean)slotGet(Slot_nullable);
	}

	@Override
	public void setIsNullable(boolean value) {
		slotSet(Slot_nullable, value);
	}

	@Override
	public String getTypeSignature() {
		StringBuffer signature = new StringBuffer();
		signature.append(getEType().getETypeSignature());
		if (isNullable()) {
			signature.append('?');
		}
		return signature.toString();
	}
}
