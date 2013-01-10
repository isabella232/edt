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

import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class ArrayLiteralImpl extends LiteralImpl implements ArrayLiteral, MofConversion {
	private static String Type_EGLAny_List = Type_EGLList+Type.TypeArgsStartDelimiter+Type_Any+Type.TypeArgsEndDelimiter;
	private static int Slot_entries=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + LiteralImpl.totalSlots();
	}
	
	static {
		int offset = LiteralImpl.totalSlots();
		Slot_entries += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getEntries() {
		return (List<Expression>)slotGet(Slot_entries);
	}
	
	@Override
	public Type getType() {
		String typeSignature = Type_EGLAny_List;
		if (getEntries().size() != 0) {
			boolean typesSame = false;
			Type elementType = getEntries().get(0).getType();
			int i = 0;
			for (Expression entry : getEntries()) {
				
				if ((entry instanceof Name) && (((Name)entry).getNamedElement() instanceof Function)) {
					elementType = IRUtils.getEGLType(Type_EGLAny);
					break;
				}
				if (i != 0) {
					typesSame = entry.getType().equals(elementType);
					if (!typesSame) {
						elementType = IRUtils.getCommonSupertype(elementType, entry.getType());
					}
				}
				i++;
			}
			typeSignature = Type_EGLList + Type.TypeArgsStartDelimiter + elementType.getTypeSignature() + Type.TypeArgsEndDelimiter;
		}
		return IRUtils.getEGLType(typeSignature);
	}
}
