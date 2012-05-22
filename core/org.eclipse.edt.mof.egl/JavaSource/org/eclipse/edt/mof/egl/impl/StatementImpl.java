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

import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Statement;

public abstract class StatementImpl extends ElementImpl implements Statement {
	private static int Slot_container=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ElementImpl.totalSlots();
	}
	
	static {
		int offset = ElementImpl.totalSlots();
		Slot_container += offset;
	}
	@Override
	public Container getContainer() {
		return (Container)slotGet(Slot_container);
	}
	
	@Override
	public void setContainer(Container value) {
		slotSet(Slot_container, value);
	}
	
}
