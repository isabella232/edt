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


public interface TransferStatement extends Statement {
	public final int TARGET_TYPE_PROGRAM = 1;
	public final int TARGET_TYPE_TRANSACTION = 2;
	
	Expression getInvocationTarget();
	
	void setInvocationTarget(Expression value);
	
	Expression getPassingRecord();
	
	void setPassingRecord(Expression value);
	
	Integer getTargetType();
	
	void setTargetType(Integer value);
	
	String getLinkageKey(); 
	Boolean isExternal();

}
