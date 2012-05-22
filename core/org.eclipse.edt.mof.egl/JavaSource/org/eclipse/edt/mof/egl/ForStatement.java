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


public interface ForStatement extends LoopStatement {
	DeclarationExpression getDeclarationExpression();
	
	void setDeclarationExpression(DeclarationExpression value);
	
	Expression getDeltaExpression();
	
	void setDeltaExpression(Expression value);
	
	Expression getCounterVariable();
	
	void setCounterVariable(Expression value);
	
	Expression getFromExpression();
	
	void setFromExpression(Expression value);
	
	Expression getToExpression();
	
	void setToExpression(Expression value);
	
	Boolean isIncrement();
	
	void setIsIncrement(Boolean value);
	
	String getLabel();
	
	void setLabel(String value);
	
}
