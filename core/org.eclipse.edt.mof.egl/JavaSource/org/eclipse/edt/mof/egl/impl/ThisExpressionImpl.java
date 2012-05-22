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

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class ThisExpressionImpl extends ExpressionImpl implements ThisExpression {
	private static int Slot_thisObject=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_thisObject += offset;
	}
	@Override
	public Element getThisObject() {
		return (Element)slotGet(Slot_thisObject);
	}
	
	@Override
	public void setThisObject(Element value) {
		slotSet(Slot_thisObject, value);
	}

	@Override
	public Type getType() {
		if (getThisObject() instanceof Classifier) {
			return (Classifier)getThisObject();
		}
		else {
			return ((TypedElement)getThisObject()).getType();
		}
	}
	
}
