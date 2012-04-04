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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.ParameterizableType;


public class ParameterizableTypeImpl extends EGLClassImpl implements ParameterizableType {
	private static int Slot_parameterizedType=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EGLClassImpl.totalSlots();
	}
	
	static {
		int offset = EGLClassImpl.totalSlots();
		Slot_parameterizedType += offset;
	}
	@Override
	public EClass getParameterizedType() {
		return (EClass)slotGet(Slot_parameterizedType);
	}
	
	@Override
	public void setParameterizedType(EClass value) {
		slotSet(Slot_parameterizedType, value);
	}
	
	@Override
	public boolean isNativeType() {
		return true;
	}
}
