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

import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;

public class PartNameImpl extends TypeNameImpl implements PartName {

	private static int Slot_packageName=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + TypeNameImpl.totalSlots();
	}
	
	static {
		int offset = TypeNameImpl.totalSlots();
		Slot_packageName += offset;
	}
	@Override
	public String getPackageName() {
		return (String)slotGet(Slot_packageName);
	}
	
	@Override
	public void setPackageName(String value) {
		slotSet(Slot_packageName, value);
	}

	
	@Override
	public String getFullyQualifiedName() {
		return getPackageName() + "." + getId();
	}

	@Override
	public Part getPart() {
		return (Part)getType();
	}

	@Override
	public Part resolvePart() {
		return getPart();
	}	
}
