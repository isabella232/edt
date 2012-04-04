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

import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.ProgramParameter;


public class ProgramImpl extends EGLClassImpl implements Program {
	private static int Slot_parameters=0;
	private static int Slot_isCallable=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + EGLClassImpl.totalSlots();
	}
	
	static {
		int offset = EGLClassImpl.totalSlots();
		Slot_parameters += offset;
		Slot_isCallable += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ProgramParameter> getParameters() {
		return (List<ProgramParameter>)slotGet(Slot_parameters);
	}
	
	@Override
	public Boolean isCallable() {
		return (Boolean)slotGet(Slot_isCallable);
	}
	
	@Override
	public void setIsCallable(Boolean value) {
		slotSet(Slot_isCallable, value);
	}
	
	@Override
	public boolean isInstantiable() {
		return false;
	}
	

}
