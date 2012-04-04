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

import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.TryStatement;


public class TryStatementImpl extends StatementImpl implements TryStatement {
	private static int Slot_tryBlock=0;
	private static int Slot_exceptionBlocks=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_tryBlock += offset;
		Slot_exceptionBlocks += offset;
	}
	@Override
	public StatementBlock getTryBlock() {
		return (StatementBlock)slotGet(Slot_tryBlock);
	}
	
	@Override
	public void setTryBlock(StatementBlock value) {
		slotSet(Slot_tryBlock, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ExceptionBlock> getExceptionBlocks() {
		return (List<ExceptionBlock>)slotGet(Slot_exceptionBlocks);
	}
	
}
