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
package org.eclipse.edt.mof.eglx.persistence.sql.impl;

import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlForEachStatement;


public class SqlForEachStatementImpl extends SqlIOStatementImpl implements SqlForEachStatement {
	private static int Slot_declarationExpression=0;
	private static int Slot_dataSource=1; 
	private static int Slot_body=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_declarationExpression += offset;
		Slot_dataSource += offset;
		Slot_body += offset;
	}
	
	@Override
	public Statement getBody() {
		return (Statement)slotGet(Slot_body);
	}
	
	@Override
	public void setBody(Statement value) {
		slotSet(Slot_body, value);
	}

	@Override
	public DeclarationExpression getDeclarationExpression() {
		return (DeclarationExpression)slotGet(Slot_declarationExpression);
	}

	@Override
	public void setDeclarationExpression(DeclarationExpression value) {
		slotSet(Slot_declarationExpression, value);
	}
	
	@Override
	public Expression getDataSource() {
		return (Expression)slotGet(Slot_dataSource);
	}
	
	@Override
	public void setDataSource(Expression value) {
		slotSet(Slot_dataSource, value);
	}
	
}
