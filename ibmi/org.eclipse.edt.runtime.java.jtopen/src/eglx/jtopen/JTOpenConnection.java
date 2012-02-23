/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package eglx.jtopen;

import org.eclipse.edt.javart.resources.egldd.Binding;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;

import eglx.lang.AnyException;
import eglx.lang.InvocationException;

public class JTOpenConnection extends IBMiConnection {

	AS400 as400;
	public JTOpenConnection() {
	}
	public JTOpenConnection(Binding binding) {
		this.binding = binding;
	}
	public JTOpenConnection(AS400 as400) {
		this.as400 = as400;
	}
	@Override
	public AS400 getAS400() throws AnyException{
		if(as400 == null){
			try {
				as400 = JTOpenConnections.getAS400ConnectionPool().getConnection(getSystem(), getUserid(), getPassword());
			} catch (ConnectionPoolException e) {
				InvocationException ex = new InvocationException();
				ex.setMessage(e.getClass().getName() + ":" + e.getLocalizedMessage());
				throw ex;
			}
		}
		return as400;
	}
	
	
}
