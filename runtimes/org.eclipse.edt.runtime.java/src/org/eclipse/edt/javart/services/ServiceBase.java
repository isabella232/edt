/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.resources.ExecutableBase;

import eglx.lang.AnyException;



public abstract class ServiceBase extends ExecutableBase {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public ServiceBase() throws AnyException {
	}
//	public ServiceParameter[] getServiceParameters(String functionName){
//		throw ServiceUtilities.buildServiceInvocationException(_runUnit(), Message.SOA_E_FUNCTION_NOT_FOUND, new String[]{functionName, this.getClass().getName()}, null, ServiceKind.EGL);
//	}
}
