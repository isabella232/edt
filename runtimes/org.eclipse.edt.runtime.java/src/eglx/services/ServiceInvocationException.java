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

public class ServiceInvocationException extends AnyException 
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	public String detail1;
	public String detail2;
	public String detail3;
	public ServiceKind serviceKind;

	public ServiceInvocationException() { super(); }
	
	public ServiceInvocationException( String id )
	{
		super(id);
	}

	public ServiceInvocationException( String id, String message )
	{
		super(id, message);
	}
	
	public ServiceInvocationException( Throwable ex )
	{
		super(ex);
	}

	
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public String toString() 
	{
		String msg = getLocalizedMessage();
		if ( msg == null )
		{
			return ezeTypeSignature();
		}
		else
		{
			String typeSig = ezeTypeSignature();
			return new StringBuilder( typeSig + 1 + msg.length() ).append( typeSig).append(' ')
				.append( msg )
				.append(" detail1:").append(detail1 == null ? "null" : detail1)
				.append(" detail2:").append(detail2 == null ? "null" : detail2)
				.append(" detail3:").append(detail3 == null ? "null" : detail3).toString();
		}
	}

	public void setDetail1(String detail1) {
		this.detail1 = detail1;
	}
	
	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}
	
	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}
	
	public void setServiceKind(ServiceKind serviceKind) {
		this.serviceKind = serviceKind;
	}
	
	public String getDetail1() {
		return detail1;
	}
	public String getDetail2() {
		return detail2;
	}
	public String getDetail3() {
		return detail3;
	}
	
	public ServiceKind getServiceKind() {
		return serviceKind;
	}
}
