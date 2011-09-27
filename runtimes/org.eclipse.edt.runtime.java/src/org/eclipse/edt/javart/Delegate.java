/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.edt.javart.messages.Message;

import eglx.lang.AnyException;
import eglx.lang.DynamicAccessException;
import eglx.lang.InvocationException;

public class Delegate {
	Object target;
	Method method;

	public Delegate(String methodName, Object target, Class... argTypes) {
		this.target = target;
		try {
			this.method = target.getClass().getMethod(methodName, argTypes);
		}
		catch (Exception ex) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = methodName;
			dax.initCause( ex );
			throw dax.fillInMessage( Message.EXCEPTION_IN_DELEGATE_GET, methodName, ex );
		}
	}

	public Object invoke(Object... args) throws AnyException {
		try {
			return method.invoke(target, args);
		}
		catch (Throwable problem) {
			if ( problem instanceof InvocationTargetException )
			{
				problem = ((InvocationTargetException)problem).getTargetException();
				if ( problem instanceof AnyException )
				{
					throw (AnyException)problem;
				}
			}
			InvocationException ix = new InvocationException();
			ix.name = method.getName();
			ix.initCause( problem );
			throw ix.fillInMessage( Message.EXCEPTION_IN_DELEGATE_INVOKE, ix.name, problem );
		}
	}
}
