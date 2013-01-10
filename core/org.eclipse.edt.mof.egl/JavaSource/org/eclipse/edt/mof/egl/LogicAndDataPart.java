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

import java.util.List;

public interface LogicAndDataPart extends StructPart {
	String INIT_FUNCTION_NAME = "<init>";
	
	List<Field> getFields();
	
	List<Function> getFunctions();
	
	List<Part> getUsedParts();
	
	
	public void addInitializerStatements(StatementBlock block);
	
	public Field getField(String name);
	
	public List<Function> getFunctions(String name);
	
	public Function getFunction(String name);
	
}
