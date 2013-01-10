/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.SuperExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class SuperExpressionImpl extends ExpressionImpl implements SuperExpression {
	private static int Slot_superObject=0;
	private static int totalSlots = 1;
	
	private Type parentType;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_superObject += offset;
	}
	@Override
	public Element getThisObject() {
		return (Element)slotGet(Slot_superObject);
	}
	
	@Override
	public void setThisObject(Element value) {
		slotSet(Slot_superObject, value);
	}

	@Override
	public Type getType() {
		if (parentType == null) {
			// SuperExpression is just like ThisExpression, except we return the parent.
			Type thisType;
			if (getThisObject() instanceof Classifier) {
				thisType = (Classifier)getThisObject();
			}
			else {
				thisType = ((TypedElement)getThisObject()).getType();
			}
			
			if (thisType instanceof StructPart) {
				// When we add inheritance we'll need to modify this to create some intermediary type on the fly that
				// has the multiple super types as its parents, to support multiple inheritance.
				List<StructPart> supers = ((StructPart)thisType).getSuperTypes();
				if (supers != null && supers.size() > 0) {
					parentType = supers.get(0);
				}
			}
			
			if (parentType == null) {
				// Shouldn't get here. Would have had a validation error if trying to use super with no super type.
				parentType = thisType;
			}
		}
		return parentType;
	}
	
}
