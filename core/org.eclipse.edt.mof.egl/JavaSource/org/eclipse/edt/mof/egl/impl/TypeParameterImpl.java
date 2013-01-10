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

import java.util.List;

import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeParameter;


public class TypeParameterImpl extends NamedElementImpl implements TypeParameter {
	private static int Slot_bounds=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + NamedElementImpl.totalSlots();
	}
	
	static {
		int offset = NamedElementImpl.totalSlots();
		Slot_bounds += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Type> getBounds() {
		return (List<Type>)slotGet(Slot_bounds);
	}
	
}
