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

import java.lang.reflect.Method;

import egl.lang.InvocationException;
import egl.lang.AnyException;

public class Delegate {
	Object target;
	Method method;
	
	public Delegate(String methodName, Object target, Class...argTypes) {
		this.target = target;
		try {
			this.method = target.getClass().getMethod(methodName, argTypes);
		}
		catch(Exception ex) {
			throw new RuntimeException(ex); //TODO this should be one of our exceptions...something for an internal error
		}
	}
	
	public Object invoke(Object...args) throws AnyException {
		try {
			return method.invoke(target, args);
		} catch (Exception e) {
			InvocationException ix = new InvocationException();
			ix.name = method.getName();
			throw ix;
		} 
	}
}
