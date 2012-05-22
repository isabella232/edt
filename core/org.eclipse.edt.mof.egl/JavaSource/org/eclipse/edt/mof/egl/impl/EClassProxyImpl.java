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
import org.eclipse.edt.mof.egl.EClassProxy;


public class EClassProxyImpl extends EGLClassImpl implements EClassProxy {
	private static int Slot_proxiedEClass=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EGLClassImpl.totalSlots();
	}
	
	static {
		int offset = EGLClassImpl.totalSlots();
		Slot_proxiedEClass += offset;
	}
	@Override
	public EClass getProxiedEClass() {
		return (EClass)slotGet(Slot_proxiedEClass);
	}
	
	@Override
	public void setProxiedEClass(EClass value) {
		slotSet(Slot_proxiedEClass, value);
	}
	
}
