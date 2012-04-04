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

import java.util.List;

import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.ObjectExpression;
import org.eclipse.edt.mof.egl.ObjectExpressionEntry;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class ObjectExpressionImpl extends ExpressionImpl implements ObjectExpression, MofConversion {
	private static int Slot_entries=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_entries += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ObjectExpressionEntry> getEntries() {
		return (List<ObjectExpressionEntry>)slotGet(Slot_entries);
	}
	
	@Override
	public Type getType() {
		return IRUtils.getEGLType(Type_EGLAny);
	}
}
