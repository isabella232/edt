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

import egl.lang.AnyException;
import egl.lang.EglAny;

public class TypeConstraints {

	private Class<? extends EglAny> clazz;
	private Object[] constraints;
	
	public TypeConstraints(Class<? extends EglAny> clazz, Object...constraints) {
		this.clazz = clazz;
		this.constraints = constraints.length == 0 ? null : constraints;
	}
	
	public Object constrainValue(Object value) throws AnyException {
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
					method = org.eclipse.edt.runtime.java.egl.lang.EglAny.class.getMethod("ezeCast", Object.class, Class.class);
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
	
	public EglAny box(Object value) {
		Method method = null;
		try {
			method = clazz.getMethod("ezeBox", Object.class, Object[].class);
		}
		catch (NoSuchMethodException ex) {
			try {
				method = clazz.getMethod("ezeBox", Object.class);
			} catch (NoSuchMethodException e) {
				try { 
					method = org.eclipse.edt.runtime.java.egl.lang.EglAny.class.getMethod("ezeBox", Object.class);
				}
				catch (Exception ex1) {
					// Will not get here
					return null;
				}
			} 
		}
		try {
			EglAny newValue = constraints == null
				? (EglAny)method.invoke(clazz, value)
				: (EglAny)method.invoke(clazz, value, constraints);
			return newValue;
		}
		catch (Exception ex) {
			// Do nothing to the value
			return null;
		}

	}
}
