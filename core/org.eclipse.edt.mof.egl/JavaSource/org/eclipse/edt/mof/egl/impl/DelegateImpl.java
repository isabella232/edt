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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class DelegateImpl extends PartImpl implements Delegate, MofConversion {
	private static int Slot_parameters=0;
	private static int Slot_isNullable=1;
	private static int Slot_returnType=2;
	private static int totalSlots = 3;
	
	private List<StructPart> superTypes;
	
	public static int totalSlots() {
		return totalSlots + PartImpl.totalSlots();
	}
	
	static {
		int offset = PartImpl.totalSlots();
		Slot_parameters += offset;
		Slot_isNullable += offset;
		Slot_returnType += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<FunctionParameter> getParameters() {
		return (List<FunctionParameter>)slotGet(Slot_parameters);
	}
	
	@Override
	public Boolean isNullable() {
		return (Boolean)slotGet(Slot_isNullable);
	}
	
	@Override
	public void setIsNullable(Boolean value) {
		slotSet(Slot_isNullable, value);
	}
	
	@Override
	public Type getReturnType() {
		return (Type)slotGet(Slot_returnType);
	}
	
	@Override
	public void setReturnType(Type value) {
		slotSet(Slot_returnType, value);
	}
	

	@Override
	public void addMember(Member mbr) {
		if (mbr instanceof FunctionParameter) {
			getParameters().add((FunctionParameter)mbr);
		}	
		mbr.setContainer(this);
		mbr.setAccessKind(AccessKind.ACC_PRIVATE);
	}

	@Override
	public List<Member> getMembers() {
		List<Member> list = new ArrayList<Member>();
		list.addAll(getParameters());
		return list;
	}
	
	@Override
	public List<Member> getAllMembers() {
		return getMembers();
	}

	@Override
	public List<StructPart> getSuperTypes() {
		if (superTypes == null) {
			superTypes = new ArrayList<StructPart>();
			superTypes.add((StructPart)IRUtils.getEGLType(Type_AnyDelegate));
		}
		return superTypes;
	}
	
	@Override
	public boolean isSubtypeOf(StructPart part) {
		if (!getSuperTypes().isEmpty()) {
			for (StructPart superType : getSuperTypes()) {
				if (superType.equals(part)) {
					return true;
				}
			}
			for (StructPart superType : getSuperTypes()) {
				if (superType.isSubtypeOf(part)) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
}
