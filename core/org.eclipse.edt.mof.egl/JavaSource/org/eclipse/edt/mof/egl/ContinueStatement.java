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


public interface ContinueStatement extends LabelStatement {
	int CONTINUE_FOR = 1;
	int CONTINUE_FOREACH = 2;
	int CONTINUE_OPENUI = 3;
	int CONTINUE_WHILE = 4;
	
	Integer getContinueType();
	
	void setContinueType(Integer value);
	
	
	public Boolean isContinueFor();
	
	public Boolean isContinueForeach();
	
	public Boolean isContinueWhile();
	
	public Boolean isContinueOpenUI();
}
