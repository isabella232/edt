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

import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.utils.EList;


public abstract class LogicAndDataPartImpl extends StructPartImpl implements LogicAndDataPart {

	private static int Slot_fields=0;
	private static int Slot_usedParts=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + StructPartImpl.totalSlots();
	}
	
	static {
		int offset = StructPartImpl.totalSlots();
		Slot_fields += offset;
		Slot_usedParts += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Field> getFields() {
		return (List<Field>)slotGet(Slot_fields);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Part> getUsedParts() {
		return (List<Part>)slotGet(Slot_usedParts);
	}
	
	
	@Override
	public void addInitializerStatements(StatementBlock block) {
		// TODO: What should this method do?
	}
	
	@Override
	public Field getField(String name) {
		for (Field field : getFields()) {
			if (field.getName().equalsIgnoreCase(name)) {
				return field;
			}
		}
		return null;
	}
		
	@Override
	public void addMember(Member mbr) {
		if (mbr instanceof Field) {
			getFields().add((Field)mbr);
		}
		else {
			super.addMember(mbr);
		}
		mbr.setContainer(this);
	}

	@Override
	public List<Member> getMembers() {
		List<Member> mbrs = new ArrayList<Member>();
		mbrs.addAll(getFields());
		mbrs.addAll(getStructuredFields());
		mbrs.addAll(getFunctions());
		return mbrs;
	}
	
	@Override
	public boolean isNativeType() {
		return false;
	}

}
