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

import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DanglingReferenceException;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.NoSuchMemberError;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;

public class MemberAccessImpl extends NameImpl implements MemberAccess {
	private static int Slot_member=0;
	private static int Slot_qualifier=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + NameImpl.totalSlots();
	}
	
	static {
		int offset = NameImpl.totalSlots();
		Slot_member += offset;
		Slot_qualifier += offset;
	}
	@Override
	public Expression getQualifier() {
		return (Expression)slotGet(Slot_qualifier);
	}
	
	@Override
	public void setQualifier(Expression value) {
		slotSet(Slot_qualifier, value);
	}
	
	@Override
	public void setTopLevelQualifier(Expression value) {
		if (getQualifier() instanceof MemberAccess) {
			((MemberAccess)getQualifier()).setTopLevelQualifier(value);
		}
		else {
			setQualifier(value);
		}
	}

	@Override
	public MemberAccess addQualifier(Expression expr) {
		
		MemberAccess newMA = IrFactory.INSTANCE.createMemberAccess();
		newMA.setId(getId());
		newMA.setQualifier(getQualifier());
		newMA.getAnnotations().addAll(getAnnotations());
		
		if (getQualifier() instanceof LHSExpr) {
			newMA.setQualifier(((LHSExpr)getQualifier()).addQualifier(expr));
		}		
		else if (getQualifier() instanceof InvocationExpression) {
			newMA.setQualifier(((InvocationExpression)getQualifier()).addQualifier(expr));
		}
		else if (getQualifier() instanceof ThisExpression) {
			newMA.setQualifier(expr);
		}
		return newMA;
	}

	@Override
	public Member getMember() {
		if (slotGet(Slot_member) == null) {
			try {
				resolveMember();
			} catch (NoSuchMemberError e) {
				throw new RuntimeException(e);
			} catch (DanglingReferenceException e1) {
				throw new RuntimeException(e1);
			}
		}
		return (Member)slotGet(Slot_member);
	}
	
	@Override
	public void setMember(Member mbr) {
		slotSet(Slot_member, mbr);
	}
	
	@Override
	public Type getType() {
		Type type = getMember().getType();
		if (type instanceof GenericType && type.getClassifier() == null) {
			type = ((GenericType)type).resolveTypeParameter(getQualifier().getType());
		}
		return type;
	}
	
	@Override
	public NamedElement getNamedElement() {
		return getMember();
	}
	
	@Override
	public boolean isNullable() {
		return getMember().isNullable();
	}

	private void resolveMember() throws DanglingReferenceException, NoSuchMemberError {
		Container container = (Container)getQualifier().getType();
		if (container == null) {
			// We have a DanglingReference
			throw new DanglingReferenceException(getId());
		}
		Member result = null;
		for (Member mbr : container.getAllMembers()) {
			if (mbr.getId().equalsIgnoreCase(getId())) { 
				result = mbr;
				break;
			}
		}
		if (result == null)
			throw new NoSuchMemberError("Member not found: " + getId());
		
		setMember(result);
	}


	
}
