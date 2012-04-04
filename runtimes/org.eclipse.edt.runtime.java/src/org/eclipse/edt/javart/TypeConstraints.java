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
package org.eclipse.edt.javart;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import eglx.lang.AnyException;
import eglx.lang.EAny;

public class TypeConstraints {

	private Class<? extends EAny> clazz;
	private Object[] constraints;
	
	public TypeConstraints(Class<? extends EAny> clazz, Object...constraints) {
		this.clazz = clazz;
		this.constraints = constraints.length == 0 ? null : constraints;
	}
	
	/**
	 * Attempts to convert the value to the type.
	 * 
	 * @param value  the value to be converted.
	 * @return the result of the conversion.
	 * @throws RuntimeException  it may or may not be an AnyException.
	 */
	public Object constrainValue(Object value) throws RuntimeException {
		Method method = null;
		boolean useDefault = false;
		try {
			method = clazz.getMethod("ezeCast", Object.class, Object[].class);
		}
		catch (NoSuchMethodException ex) {
			try {
				method = clazz.getMethod("ezeCast", Object.class);
			} catch (Exception e) {
				try { 
					method = org.eclipse.edt.runtime.java.eglx.lang.EAny.class.getMethod("ezeCast", Object.class, Class.class);
					useDefault = true;
				}
				catch (Exception ex1) {
					// Will not get here
					return null;
				}
			} 
		}
		try {
			Object newValue = useDefault 
				? method.invoke(value, clazz)
				: constraints == null
					? method.invoke(clazz, value)
					: method.invoke(clazz, value, constraints);
			return newValue;
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof AnyException) 
				throw (AnyException)ex.getTargetException();
			else
				throw new RuntimeException(ex.getTargetException());
		}
		catch(Exception ex1) {
			throw new RuntimeException(ex1);
		}
	}
	
	public EAny box(Object value) {
		Method method = null;
		try {
			method = clazz.getMethod("ezeBox", Object.class, Object[].class);
		}
		catch (NoSuchMethodException ex) {
			try {
				method = clazz.getMethod("ezeBox", Object.class);
			} catch (NoSuchMethodException e) {
				try { 
					method = org.eclipse.edt.runtime.java.eglx.lang.EAny.class.getMethod("ezeBox", Object.class);
				}
				catch (Exception ex1) {
					// Will not get here
					return null;
				}
			} 
		}
		try {
			EAny newValue = constraints == null
				? (EAny)method.invoke(clazz, value)
				: (EAny)method.invoke(clazz, value, constraints);
			return newValue;
		}
		catch (Exception ex) {
			// Do nothing to the value
			return null;
		}

	}
}
