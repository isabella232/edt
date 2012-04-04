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

import org.eclipse.edt.mof.egl.EGLClass;

public class EGLClassImpl extends LogicAndDataPartImpl implements EGLClass {
	private static int Slot_isAbstract=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + LogicAndDataPartImpl.totalSlots();
	}
	
	static {
		int offset = LogicAndDataPartImpl.totalSlots();
		Slot_isAbstract += offset;
	}
	@Override
	public Boolean isAbstract() {
		return (Boolean)slotGet(Slot_isAbstract);
	}
	
	@Override
	public void setIsAbstract(Boolean value) {
		slotSet(Slot_isAbstract, value);
	}

	@Override
	public boolean isInstantiable() {
		return true;
	}
	
	
}
