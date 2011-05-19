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


public class JavaObjectException extends AnyException
{
	private static final long serialVersionUID = 70L;
	
	public String exceptionType;

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public JavaObjectException( ) 
		throws JavartException
	{
		ezeInitialize( );
	}
	
	public JavaObjectException(String id, String message) {
		super(id, message);
	}
	
	public JavaObjectException(Throwable ex) {
		super(ex);
	}
		
	/**
	 * Returns a clone of this object.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		JavaObjectException theClone = (JavaObjectException)super.clone();
		
		theClone.exceptionType = exceptionType;
		
		return theClone;
	}
}
