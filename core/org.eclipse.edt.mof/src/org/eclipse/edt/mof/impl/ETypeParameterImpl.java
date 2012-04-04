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

import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.ETypeParameter;


@SuppressWarnings("unchecked")
public class ETypeParameterImpl extends ENamedElementImpl implements ETypeParameter {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_bounds = 0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ENamedElementImpl.totalSlots();
	}
	
	static { 
		int offset = ENamedElementImpl.totalSlots();
		Slot_bounds += offset;
	}

	@Override
	public List<EType> getBounds() {
		return (List<EType>)slotGet(Slot_bounds);
	}

}
