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

import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Member;

public abstract class MemberImpl extends TypedElementImpl implements Member {
	private static int Slot_isStatic=0;
	private static int Slot_isAbstract=1;
	private static int Slot_accessKind=2;
	private static int Slot_container=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + TypedElementImpl.totalSlots();
	}
	
	static {
		int offset = TypedElementImpl.totalSlots();
		Slot_isStatic += offset;
		Slot_isAbstract += offset;
		Slot_accessKind += offset;
		Slot_container += offset;
	}
	@Override
	public Boolean isStatic() {
		return (Boolean)slotGet(Slot_isStatic);
	}
	
	@Override
	public void setIsStatic(Boolean value) {
		slotSet(Slot_isStatic, value);
	}
	
	@Override
	public Boolean isAbstract() {
		return (Boolean)slotGet(Slot_isAbstract);
	}
	
	@Override
	public void setIsAbstract(Boolean value) {
		slotSet(Slot_isAbstract, value);
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
	public Container getContainer() {
		return (Container)slotGet(Slot_container);
	}
	
	@Override
	public void setContainer(Container value) {
		slotSet(Slot_container, value);
	}
	
	@Override
	public Member resolveMember() {
		return this;
	}
	
}
