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

import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class AsExpressionImpl extends TypeExpressionImpl implements AsExpression {
	private static int Slot_conversionOperation=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + TypeExpressionImpl.totalSlots();
	}
	
	static {
		int offset = TypeExpressionImpl.totalSlots();
		Slot_conversionOperation += offset;
	}

	@Override
	public Operation getConversionOperation() {
		if (slotGet(Slot_conversionOperation) == null) {
			setConversionOperation(IRUtils.getConversionOperation(getObjectExpr(), getType()));
		}
		return (Operation)slotGet(Slot_conversionOperation);
	}

	@Override
	public void setConversionOperation(Operation op) {
		slotSet(Slot_conversionOperation, op);
	}
	
	@Override
	public Type getType() {
		return getEType();
	}

	@Override
	public boolean isNullable() {
		if (getConversionOperation() != null) {
			return getConversionOperation().isNullable();
		}
		return super.isNullable();
	}

	
}
