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
package org.eclipse.edt.mof.egl.impl;

import java.util.List;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.WhenClause;
import org.eclipse.edt.mof.impl.EModelElementImpl;


public class WhenClauseImpl extends ElementImpl implements WhenClause {
	private static int Slot_matchExpressions=0;
	private static int Slot_statements=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + EModelElementImpl.totalSlots();
	}
	
	static {
		int offset = EModelElementImpl.totalSlots();
		Slot_matchExpressions += offset;
		Slot_statements += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getMatchExpressions() {
		return (List<Expression>)slotGet(Slot_matchExpressions);
	}
	
	@Override
	public StatementBlock getStatements() {
		return (StatementBlock)slotGet(Slot_statements);
	}
	
	@Override
	public void setStatements(StatementBlock value) {
		slotSet(Slot_statements, value);
	}
	
}
