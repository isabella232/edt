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
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeExpression;

public abstract class TypeExpressionImpl extends ExpressionImpl implements TypeExpression {
	private static int Slot_objectExpr=0;
	private static int Slot_typeSignature=1;
	private static int Slot_eType=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_objectExpr += offset;
		Slot_typeSignature += offset;
		Slot_eType += offset;
	}
	@Override
	public Expression getObjectExpr() {
		return (Expression)slotGet(Slot_objectExpr);
	}
	
	@Override
	public void setObjectExpr(Expression value) {
		slotSet(Slot_objectExpr, value);
	}
	
	@Override
	public String getTypeSignature() {
		return (String)slotGet(Slot_typeSignature);
	}
	
	@Override
	public void setTypeSignature(String value) {
		slotSet(Slot_typeSignature, value);
	}
	
	@Override
	public Type getEType() {
		return (Type)slotGet(Slot_eType);
	}
	
	@Override
	public void setEType(Type value) {
		slotSet(Slot_eType, value);
	}
	
}
