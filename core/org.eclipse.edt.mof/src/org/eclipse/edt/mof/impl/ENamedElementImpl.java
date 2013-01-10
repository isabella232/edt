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

import org.eclipse.edt.mof.ENamedElement;

public abstract class ENamedElementImpl extends EModelElementImpl implements ENamedElement {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_name = 0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EModelElementImpl.totalSlots();
	}
	
	static { 
		int offset = EModelElementImpl.totalSlots();
		Slot_name += offset;
	}
		
	@Override
	public String getName() {
		return (String)slotGet(Slot_name);
	}

	@Override
	public void setName(String name) {
		slotSet(Slot_name, name);
	}
	
	@Override
	public String getCaseSensitiveName() {
		return (String)slotGet(Slot_name);
	}
}
