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
package org.eclipse.edt.mof.egl;


public interface MoveStatement extends Statement {
	
	static final int MOVE_DEFAULT = 0;
	static final int MOVE_BYNAME = 1;
	static final int MOVE_BYPOSITION = 2;
	static final int MOVE_FOR = 3;
	static final int MOVE_FORALL = 4;
	static final int MOVE_WITHV60COMPAT = 5;
	
	Expression getSourceExpr();
	
	void setSourceExpr(Expression value);
	
	LHSExpr getTargetExpr();
	
	void setTargetExpr(LHSExpr value);
	
	Expression getModifierExpr();
	
	void setModifierExpr(Expression value);
	
	int getModifier();
	
	void setModifier(int value);
	
}
