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
package eglx.services;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.runtime.java.egl.lang.AnyException;



public class ServiceBindingException extends AnyException 
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	public ServiceBindingException() { super(); }
	
	public ServiceBindingException( String id )
	{
		super(id);
	}

	public ServiceBindingException( String id, String message )
	{
		super(id, message);
	}
	
	public ServiceBindingException( Throwable ex )
	{
		super(ex);
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
