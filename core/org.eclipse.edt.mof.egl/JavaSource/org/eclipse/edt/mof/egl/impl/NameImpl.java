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

import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;


public abstract class NameImpl extends ExpressionImpl implements Name {
	private static int Slot_id=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_id += offset;
	}
	@Override
	public String getId() {
		return (String)slotGet(Slot_id);
	}
	
	@Override
	public void setId(String value) {
		slotSet(Slot_id, value);
	}
	
	@Override
	public abstract NamedElement getNamedElement();

	@Override
	public List<String> getNameComponents() {
		// TODO: Default generated implementation
		return null;
	}
}
