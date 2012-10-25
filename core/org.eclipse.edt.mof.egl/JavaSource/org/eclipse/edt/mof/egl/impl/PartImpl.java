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

import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Stereotype;

public abstract class PartImpl extends ClassifierImpl implements Part {
	private static int Slot_accessKind=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ClassifierImpl.totalSlots();
	}
	
	static {
		int offset = ClassifierImpl.totalSlots();
		Slot_accessKind += offset;
	}
	@Override
	public AccessKind getAccessKind() {
		return (AccessKind)slotGet(Slot_accessKind);
	}
	
	@Override
	public void setAccessKind(AccessKind value) {
		slotSet(Slot_accessKind, value);
	}
	
	@Override
	public String getFullyQualifiedName() {
		return getTypeSignature();
	}
	
	@Override
	public boolean isNativeType() {
		// Default implementation
		return true;
	}
			
 }
