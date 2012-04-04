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

import org.eclipse.edt.mof.egl.NamedElement;

public abstract class NamedElementImpl extends ElementImpl implements NamedElement {
	private static int Slot_name=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ElementImpl.totalSlots();
	}
	
	static {
		int offset = ElementImpl.totalSlots();
		Slot_name += offset;
	}
	@Override
	public String getName() {
		return (String)slotGet(Slot_name);
	}
	
	@Override
	public void setName(String value) {
		slotSet(Slot_name, value);
	}
	
	
	@Override
	public String getId() {
		return getName();
	}
}
