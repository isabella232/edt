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
package org.eclipse.edt.mof.impl;

import org.eclipse.edt.mof.EFunction;
import org.eclipse.edt.mof.EParameter;

public class EParameterImpl extends EFieldImpl implements EParameter {
	private static final long serialVersionUID = 1L;
	
	private static int Slot_function = 0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EFieldImpl.totalSlots();
	}
	
	static { 
		int offset = EFieldImpl.totalSlots();
		Slot_function += offset;
	}
		

	@Override
	public EFunction getFunction() {
		return (EFunction)slotGet(Slot_function);
	}

	@Override
	public void setFunction(EFunction mbr) {
		slotSet(Slot_function, mbr);
	}


}
