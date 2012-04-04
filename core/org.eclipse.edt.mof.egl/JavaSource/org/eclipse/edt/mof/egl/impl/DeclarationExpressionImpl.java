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

import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Type;


public class DeclarationExpressionImpl extends ExpressionImpl implements DeclarationExpression {
	private static int Slot_fields=0;
	private static int Slot_initializers=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_fields += offset;
		Slot_initializers += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Field> getFields() {
		return (List<Field>)slotGet(Slot_fields);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getInitializers() {
		return (List<Expression>)slotGet(Slot_initializers);
	}

	@Override
	public Type getType() {
		return getFields().get(0).getType();
	}
	
}
