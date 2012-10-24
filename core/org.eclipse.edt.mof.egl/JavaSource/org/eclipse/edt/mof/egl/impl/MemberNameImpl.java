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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Type;

public class MemberNameImpl extends NameImpl implements MemberName {
	private static int Slot_member=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + NameImpl.totalSlots();
	}
	
	static {
		int offset = NameImpl.totalSlots();
		Slot_member += offset;
	}

	@Override
	public Type getType() {
		return getMember().getType();
	}
	
	@Override
	public Member getMember()  {
		return (Member)slotGet(Slot_member);
	}
	
	@Override
	public void setMember(Member value) {
		slotSet(Slot_member, value);
	}

	@Override
	public NamedElement getNamedElement() {
		return getMember();
	}

	@Override
	public boolean isNullable() {
		return getMember().isNullable();
	}

	@Override
	public MemberAccess addQualifier(Expression expr) {
		MemberAccess mbrAccess = IrFactory.INSTANCE.createMemberAccess();
		mbrAccess.setId(getId());
		mbrAccess.setQualifier(expr);
		return mbrAccess;
	}

	@Override
	public Field resolveField() {
		if (getMember() instanceof Field) {
			return (Field)getMember();
		}
		return null;
	}

	@Override
	public Member resolveMember() {
		return getMember();
	}

	@Override
	public Function resolveFunction() {
		if (getMember() instanceof Function) {
			return (Function)getMember();
		}
		return null;
	}
}
