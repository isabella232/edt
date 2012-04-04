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
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.StatementBlock;

public class FieldImpl extends MemberImpl implements Field {
	private static int Slot_initializerStatements=0;
	private static int Slot_hasSetValuesBlock=1;
	private static int Slot_isImplicit=2;
	private static int Slot_isSystemField=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + MemberImpl.totalSlots();
	}
	
	static {
		int offset = MemberImpl.totalSlots();
		Slot_initializerStatements += offset;
		Slot_hasSetValuesBlock += offset;
		Slot_isImplicit += offset;
		Slot_isSystemField += offset;
	}
	@Override
	public StatementBlock getInitializerStatements() {
		return (StatementBlock)slotGet(Slot_initializerStatements);
	}
	
	@Override
	public void setInitializerStatements(StatementBlock value) {
		slotSet(Slot_initializerStatements, value);
	}
	
	@Override
	public Boolean hasSetValuesBlock() {
		return (Boolean)slotGet(Slot_hasSetValuesBlock);
	}
	
	@Override
	public void setHasSetValuesBlock(Boolean value) {
		slotSet(Slot_hasSetValuesBlock, value);
	}
	
	@Override
	public Boolean isImplicit() {
		return (Boolean)slotGet(Slot_isImplicit);
	}
	
	@Override
	public void setIsImplicit(Boolean value) {
		slotSet(Slot_isImplicit, value);
	}
	
	@Override
	public Boolean isSystemField() {
		return (Boolean)slotGet(Slot_isSystemField);
	}
	
	@Override
	public void setIsSystemField(Boolean value) {
		slotSet(Slot_isSystemField, value);
	}
	
	
	@Override
	public Container getDeclarer() {
		return getContainer();
	}
}
