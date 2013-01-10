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

public interface StructPart extends Part, SubType, Container {
	List<StructPart> getSuperTypes();
	
	boolean isSubtypeOf(StructPart part);
	
	List<Interface> getInterfaces();
	
	List<StructuredField> getStructuredFields();
	
	List<Constructor> getConstructors();
	
	List<Function> getFunctions();
	
	List<Operation> getOperations();
	
	List<StructuredField> getStructuredFields(String name);
	
	List<Function> getFunctions(String name);
	
	List<Operation> getOperations(String name);
	
	Function getFunction(String name);
	
	void collectMembers(List<Member> members, List<StructPart> alreadySeen);
	
	StatementBlock getInitializerStatements();
	
	void setInitializerStatements(StatementBlock value);


}
