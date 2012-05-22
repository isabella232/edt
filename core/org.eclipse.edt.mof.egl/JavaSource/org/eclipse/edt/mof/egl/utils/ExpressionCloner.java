/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.impl.EObjectImpl;
import org.eclipse.edt.mof.impl.Slot;
import org.eclipse.edt.mof.utils.EList;

public class ExpressionCloner {

	private Map<Object, Object> alreadyCloned = new HashMap<Object, Object>();
	
	public static Expression clone(Expression expr) {
		return new ExpressionCloner().primClone(expr);
	}
	
	private Expression primClone(Expression expr) {
		if (expr == null) {
			return null;
		}
		
		if (!(expr instanceof EObjectImpl)) {
			return expr;
		}
		
		EObjectImpl newExpr = (EObjectImpl)expr.getEClass().newInstance();
		Slot[] slots = ((EObjectImpl)expr).getSlots();
		for (int i = 0; i < slots.length && i < newExpr.getSlots().length; i++) {
			if(slots[i] != null) {
				if (newExpr.getSlots()[i] == null) {
					newExpr.getSlots()[i] = new Slot();
				}
				
				Object value = slots[i].get();
				
				Object newValue = cloneIfNeeded(value);		
				newExpr.getSlots()[i].set(newValue);
			}
		}
		return (Expression)newExpr;
		
	}
	
	private Object cloneIfNeeded(Object value) {
		if (value == null) {
			return null;
		}
		
		Object newValue = alreadyCloned.get(value);
		
		if (newValue == null) {
			if (value instanceof Expression) {
				newValue = primClone((Expression)value);
			}
			else if (value instanceof EList) {
				newValue = cloneList((EList)value);
			}
			else {
				newValue = value;
			}

			alreadyCloned.put(value, newValue);
		}
		return newValue;
	}
	
	private EList cloneList(EList list) {
		EList newList = new EList();
		for (Object obj : list) {
			newList.add(cloneIfNeeded(obj));
		}
		return newList;
	}
}
