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

import org.eclipse.edt.mof.egl.CaseStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.WhenClause;


public class CaseStatementImpl extends StatementImpl implements CaseStatement {
	private static int Slot_criterion=0;
	private static int Slot_whenClauses=1;
	private static int Slot_defaultStatements=2;
	private static int Slot_label=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_criterion += offset;
		Slot_whenClauses += offset;
		Slot_defaultStatements += offset;
		Slot_label += offset;
	}
	@Override
	public Expression getCriterion() {
		return (Expression)slotGet(Slot_criterion);
	}
	
	@Override
	public void setCriterion(Expression value) {
		slotSet(Slot_criterion, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<WhenClause> getWhenClauses() {
		return (List<WhenClause>)slotGet(Slot_whenClauses);
	}
	
	@Override
	public StatementBlock getDefaultStatements() {
		return (StatementBlock)slotGet(Slot_defaultStatements);
	}
	
	@Override
	public void setDefaultStatements(StatementBlock value) {
		slotSet(Slot_defaultStatements, value);
	}
	
	@Override
	public String getLabel() {
		return (String)slotGet(Slot_label);
	}
	
	@Override
	public void setLabel(String value) {
		slotSet(Slot_label, value);
	}
	
}
