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
package org.eclipse.edt.mof.impl;

import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EMetadataType;
import org.eclipse.edt.mof.MofFactory;


public class EMetadataTypeImpl extends EClassImpl implements EMetadataType {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_targets = 0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EClassImpl.totalSlots();
	}
	
	static { 
		int offset = EClassImpl.totalSlots();
		Slot_targets += offset;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<EClass> getTargets() {
		return (List<EClass>) slotGet(Slot_targets);
	}
	
	@Override
	public List<EClass> getSuperTypes() {
		if (super.getSuperTypes().isEmpty()) {
			super.getSuperTypes().add(MofFactory.INSTANCE.getEMetadataObjectClass());
		}
		return super.getSuperTypes();
	}
	
}
