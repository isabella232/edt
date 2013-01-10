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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class TypeNameImpl extends NameImpl implements TypeName {
	private static int Slot_type=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + NameImpl.totalSlots();
	}
	
	static {
		int offset = NameImpl.totalSlots();
		Slot_type += offset;
	}
	@Override
	public Type getType() {		
		if (slotGet(Slot_type) == null) {
			slotSet(Slot_type, resolveType());
		}
		return (Type)slotGet(Slot_type);
	}
	
	@Override
	public void setType(Type value) {
		slotSet(Slot_type, value);
	}

	@Override
	public NamedElement getNamedElement() {
		Type type = getType();
		if (type instanceof NamedElement) {
			return (NamedElement) type;
		}
		return null;
	}
	
	private Type resolveType() {
		return (Type)IRUtils.getEGLType(getFullyQualifiedName());
	}

	@Override
	public String getFullyQualifiedName() {
		return getId();
	}

	@Override
	public MemberAccess addQualifier(Expression expr) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
