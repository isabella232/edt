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

import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;

public class FunctionParameterImpl extends ParameterImpl implements FunctionParameter {
	private static int Slot_parameterKind=0;
	private static int Slot_isDefinedSqlNullable=1;
	private static int Slot_isField=2;
	private static int Slot_isConst=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + ParameterImpl.totalSlots();
	}
	
	static {
		int offset = ParameterImpl.totalSlots();
		Slot_parameterKind += offset;
		Slot_isDefinedSqlNullable += offset;
		Slot_isField += offset;
		Slot_isConst += offset;
	}
	@Override
	public ParameterKind getParameterKind() {
		return (ParameterKind)slotGet(Slot_parameterKind);
	}
	
	@Override
	public void setParameterKind(ParameterKind value) {
		slotSet(Slot_parameterKind, value);
	}
	
	@Override
	public Boolean isDefinedSqlNullable() {
		return (Boolean)slotGet(Slot_isDefinedSqlNullable);
	}
	
	@Override
	public void setIsDefinedSqlNullable(Boolean value) {
		slotSet(Slot_isDefinedSqlNullable, value);
	}
	
	@Override
	public Boolean isField() {
		return (Boolean)slotGet(Slot_isField);
	}
	
	@Override
	public void setIsField(Boolean value) {
		slotSet(Slot_isField, value);
	}

	@Override
	public Boolean isConst() {
		return (Boolean)slotGet(Slot_isConst);
	}
	
	@Override
	public void setIsConst(Boolean value) {
		slotSet(Slot_isConst, value);
	}
	
	@Override
	public boolean isGenericTypeParameter() {
		return getType().getClassifier() == null;
	}
	
}
