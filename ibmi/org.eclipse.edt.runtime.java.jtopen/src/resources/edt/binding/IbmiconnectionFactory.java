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
package resources.edt.binding;

import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.Parameter;

import eglx.jtopen.JTOpenConnection;
import eglx.jtopen.JTOpenConnections;



public class IbmiconnectionFactory extends BindingFactory{

	@Override
	public Object createResource(Binding binding) throws Exception{
		Parameter system = binding.getParameter("system");
		Parameter userID = binding.getParameter("userId");
		Parameter password = binding.getParameter("password");
		Parameter library = binding.getParameter("library");
		return new JTOpenConnection(JTOpenConnections.getAS400ConnectionPool().getConnection((system == null ? null : system.getValue()), 
						(userID == null ? null : userID.getValue()), 
						(password == null ? null : password.getValue())), 
						(library == null ? null : library.getValue()));
	}

}
