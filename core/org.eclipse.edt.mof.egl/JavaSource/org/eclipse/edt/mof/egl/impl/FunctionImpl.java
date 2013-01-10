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
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class FunctionImpl extends FunctionMemberImpl implements Function {
	private static int Slot_returnField=0;
	private static int totalSlots = 1;
	
	private List<StructPart> superTypes;

	public static int totalSlots() {
		return totalSlots + FunctionMemberImpl.totalSlots();
	}
	
	static {
		int offset = FunctionMemberImpl.totalSlots();
		Slot_returnField += offset;
	}
	@Override
	public Field getReturnField() {
		return (Field)slotGet(Slot_returnField);
	}
	
	@Override
	public void setReturnField(Field value) {
		slotSet(Slot_returnField, value);
	}
	
	@Override
	public Type getReturnType() {
		if (getReturnField() != null) {
			return getReturnField().getType();
		}
		return getType();
	}

	@Override
	public List<StructPart> getSuperTypes() {
		if (superTypes == null) {
			superTypes = new ArrayList<StructPart>();
			//TODO really need an AnyFunction type
			superTypes.add((StructPart)IRUtils.getEGLType(MofConversion.Type_AnyDelegate));
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
	
	@Override
	public Function resolveFunction() {
		return this;
	}
}
