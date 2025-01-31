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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.ElementAnnotations;

public class ElementAnnotationsImpl extends ElementImpl implements ElementAnnotations {
	private static int Slot_index=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ElementImpl.totalSlots();
	}
	
	static {
		int offset = ElementImpl.totalSlots();
		Slot_index += offset;
	}
	@Override
	public Integer getIndex() {
		return (Integer)slotGet(Slot_index);
	}
	
	@Override
	public void setIndex(Integer value) {
		slotSet(Slot_index, value);
	}
	
}
