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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.DataType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.TypeKind;


public class DataTypeImpl extends ClassifierImpl implements DataType {

	private static int Slot_typeKind=0;
	private static int Slot_functions=1;
	private static int Slot_operations=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + ClassifierImpl.totalSlots();
	}
	
	static {
		int offset = ClassifierImpl.totalSlots();
		Slot_typeKind += offset;
		Slot_functions += offset;
		Slot_operations += offset;
	}
	@Override
	public TypeKind getTypeKind() {
		return (TypeKind)slotGet(Slot_typeKind);
	}
	
	@Override
	public void setTypeKind(TypeKind value) {
		slotSet(Slot_typeKind, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Function> getFunctions() {
		return (List<Function>)slotGet(Slot_functions);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Operation> getOperations() {
		return (List<Operation>)slotGet(Slot_operations);
	}

	@Override
	public void addMember(Member mbr) {
		if (mbr instanceof Operation) {
			getOperations().add((Operation)mbr);
		}
		else {
			getFunctions().add((Function)mbr);
		}
		mbr.setContainer(this);
	}

	@Override
	public List<Member> getMembers() {
		List<Member> members = new ArrayList<Member>();
		members.addAll(getOperations());
		members.addAll(getFunctions());
		return null;
	}

	@Override
	public List<Member> getAllMembers() {
		return getMembers();
	}
	
}
