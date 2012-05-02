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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.SuperExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class SuperExpressionImpl extends ExpressionImpl implements SuperExpression {
	private static int Slot_superObject=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_superObject += offset;
	}
	@Override
	public Element getSuperObject() {
		return (Element)slotGet(Slot_superObject);
	}
	
	@Override
	public void setSuperObject(Element value) {
		slotSet(Slot_superObject, value);
	}

	@Override
	public Type getType() {
		if (getSuperObject() instanceof Classifier) {
			return (Classifier)getSuperObject();
		}
		else {
			return ((TypedElement)getSuperObject()).getType();
		}
	}
	
}
