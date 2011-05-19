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

import org.eclipse.edt.javart.JavartException;

public class DynamicAccessException extends AnyException
{
	private static final long serialVersionUID = 70L;
	
	public String memberName;
	public String typeName;
		
	public DynamicAccessException( ) throws JavartException
	{
		memberName = "";
		typeName = "";
	}
	
	public DynamicAccessException(Exception ex) {
		super(ex);
	}
	
	public DynamicAccessException(String message) {
		super(message);
	}
	
	public DynamicAccessException(String memberName, String typeName ) throws JavartException
	{
		this.memberName = memberName;
		this.typeName = typeName;
	}
	
	public Object clone() throws java.lang.CloneNotSupportedException
	{
		DynamicAccessException ezeClone = (DynamicAccessException)super.clone();
		ezeClone.memberName = memberName;
		ezeClone.typeName = typeName;
		return ezeClone;
	}
	
}
