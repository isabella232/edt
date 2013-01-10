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
package org.eclipse.edt.compiler.generationServer.parts;

public interface IElementInfo{
	
	/**
	 * The various Element types
	 * 
	 */
	
	int PROGRAM_PART = 1;
	int TABLE_PART = 2;
	int RECORD_PART = 3;
	int ITEM_PART = 4;
	int FORM_PART = 5; 
	int FORMGROUP_PART = 6;
	int FUNCTION_PART = 7; 
	int LIBRARY_PART = 8;
	int HANDLER_PART = 9;
	int SERVICE_PART = 10;
	int INTERFACE_PART = 11;
	int DELEGATE_PART = 12;
	int EXTERNALTYPE_PART = 13;
	int ENUMERATION_PART = 14;
	
	int EXTERNAL_DYNAMIC_PROGRAM = 30;
	
	int NESTED_FUNCTION = 31;
	
	int EXTERNAL_FUNCTION = 33;

	
	/**
	 * @return Returns the name of the part
	 * 
	 */
	String getName();
		
	/**
	 * 
	 * @return Returns the type of the part.
	 * 
	 * @see IPartInfo.PROGRAM_PART
	 * @see IPartInfo.TABLE_PART
	 * @see IPartInfo.RECORD_PART
	 * @see IPartInfo.ITEM_PART 
	 * @see IPartInfo.FORM_PART
	 * @see IPartInfo.FORMGROUP_PART
	 * @see IPartInfo.FUNCTION_PART
	 * @see IPartInfo.LIBRARY_PART
	 * @see IPartInfo.HANDLER_PART
	 * @see IPartInfo.SERVICE_PART
	 * @see IPartInfo.INTERFACE_PART
	 */
	int getType();
	
	/**
	 * @return Returns the source code that defines this part.
	 */
	String getSource();
	
	void accept(IElementInfoVisitor info);
	void visitChildren(IElementInfoVisitor info);
	


}
