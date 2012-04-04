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
package org.eclipse.edt.mof.impl;

import java.util.List;

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EMetadataObject;
import org.eclipse.edt.mof.EModelElement;
import org.eclipse.edt.mof.utils.EList;


public abstract class EModelElementImpl extends EObjectImpl implements EModelElement {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_metadata = 0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EObjectImpl.totalSlots();
	}
	
	static { 
		int offset = EObjectImpl.totalSlots();
		Slot_metadata += offset;
	}
	
	@Override
	// Go in reverse order to pick up last instance
	public EMetadataObject getMetadata(String typeName) {
		List<EMetadataObject> anns = getMetadataList();
		for (int i = anns.size()-1; i >= 0; i--) {
			if (anns.get(i).getEClass().getName().equalsIgnoreCase(typeName)) {
				return anns.get(i);
			}
		}
		return null;
	}
	@Override
	// Go in reverse order to pick up last instance
	public EMetadataObject getMetadata(EClass annType) {
		List<EMetadataObject> anns = getMetadataList();
		for (int i = anns.size()-1; i >= 0; i--) {
			if (anns.get(i).getEClass() == annType) {
				return anns.get(i);
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EMetadataObject> getMetadataList() {
		if (slotGet(Slot_metadata) == null) {
			slotSet(Slot_metadata, new EList<EMetadataObject>());
		}
		return (List<EMetadataObject>)slotGet(Slot_metadata);
	}
}
