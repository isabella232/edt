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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.EClassProxy;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;


public class EClassProxyImpl extends EGLClassImpl implements EClassProxy {
	private static int Slot_proxiedEClassName=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EGLClassImpl.totalSlots();
	}
	
	static {
		int offset = EGLClassImpl.totalSlots();
		Slot_proxiedEClassName += offset;
	}
	
	
	@Override
	public String getProxiedEClassName() {
		return (String)slotGet(Slot_proxiedEClassName);
	}

	@Override
	public EClass getProxiedEClass() {
		String name = getProxiedEClassName();
		if (name != null && name.length() > 0) {
			try {
				return (EClass)Environment.getCurrentEnv().find(name);
			} catch (MofObjectNotFoundException e) {
			} catch (DeserializationException e) {
			}
		}
		return null;
	}

	@Override
	public void setProxiedEClassName(String value) {
		slotSet(Slot_proxiedEClassName, value);
	}
	
}
