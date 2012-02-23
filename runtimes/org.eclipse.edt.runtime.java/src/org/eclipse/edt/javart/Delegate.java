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
			this.method = target.getClass().getDeclaredMethod(methodName, argTypes);
		}
		catch (Exception ex) {
			DynamicAccessException dax = new DynamicAccessException();
			dax.key = methodName;
			dax.initCause( ex );
			throw dax.fillInMessage( Message.EXCEPTION_IN_DELEGATE_GET, methodName, ex );
		}
	}

	public Object invoke(Object... args) throws AnyException {
		boolean accessibleSet = true;
		try {
			accessibleSet = method.isAccessible();
			if (!accessibleSet) {
				method.setAccessible(true);
				Object ret = method.invoke(target, args);
				method.setAccessible(false);
				return ret;
			} else
				return method.invoke(target, args);
		}
		catch (Throwable problem) {
			if (!accessibleSet)
				method.setAccessible(false);
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

	public Object getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}
	
	/**
	 * Added for an easy way to nicely display this in the debugger.
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append(target.getClass().getCanonicalName());
		buf.append('.');
		buf.append(method.getName());
		return buf.toString();
	}
}
