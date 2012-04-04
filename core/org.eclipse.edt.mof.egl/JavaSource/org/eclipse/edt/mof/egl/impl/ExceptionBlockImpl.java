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

import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Parameter;

public class ExceptionBlockImpl extends StatementBlockImpl implements ExceptionBlock {
	private static int Slot_exception=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + StatementBlockImpl.totalSlots();
	}
	
	static {
		int offset = StatementBlockImpl.totalSlots();
		Slot_exception += offset;
	}
	@Override
	public Parameter getException() {
		return (Parameter)slotGet(Slot_exception);
	}
	
	@Override
	public void setException(Parameter value) {
		slotSet(Slot_exception, value);
	}
	
}
