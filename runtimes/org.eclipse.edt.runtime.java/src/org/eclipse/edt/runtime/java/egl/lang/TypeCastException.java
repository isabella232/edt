/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.egl.lang;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.messages.Message;

public class TypeCastException extends AnyException
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	public String castToName;
	public String actualTypeName;
		
	public TypeCastException( )
		throws JavartException
	{
		castToName = "";
		actualTypeName = "";
	}
	
	public TypeCastException(Exception ex) {
		super(ex);
	}

	public TypeCastException(String castName, String typeName ) {
		this.setMessageID(Message.CONVERSION_ERROR);
		this.castToName = castName;
		this.actualTypeName = typeName;
	}
	 
	public Object clone() throws java.lang.CloneNotSupportedException
	{
		TypeCastException ezeClone = (TypeCastException)super.clone();
		ezeClone.castToName = castToName;
		ezeClone.actualTypeName = actualTypeName;
		return ezeClone;
	}
	
}
