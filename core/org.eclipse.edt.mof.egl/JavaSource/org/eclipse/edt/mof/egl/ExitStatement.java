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
package org.eclipse.edt.mof.egl;


public interface ExitStatement extends LabelStatement {
	int EXIT_CASE = 1;
	int EXIT_IF = 2;
	int EXIT_FOR = 3;
	int EXIT_WHILE = 4;
	int EXIT_FOREACH = 5;
	int EXIT_OPENUI = 6;
	int EXIT_PROGRAM = 7;
	int EXIT_RUNUNIT = 9;
	int EXIT_STACK = 9;
	
	Integer getExitStatementType();
	
	void setExitStatementType(Integer value);
	
	Expression getReturnExpr();
	
	void setReturnExpr(Expression value);
	
}
