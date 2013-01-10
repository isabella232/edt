/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import eglx.lang.*;

public class Delegate {
	private Object target;
	private Method method;
	private String signature;
	
	public static boolean ezeIsa( Object obj, String sig ) 
	{
		if ( obj instanceof EAny )
		{
			obj = ((EAny)obj).ezeUnbox();
		}
		return obj instanceof Delegate && ((Delegate)obj).signature.equals( sig );
	}
	
	public static Delegate ezeCast( Object obj, String sig ) throws TypeCastException 
	{
		if ( obj instanceof EAny )
		{
			obj = ((EAny)obj).ezeUnbox();
		}

		if ( ezeIsa( obj, sig ) )
		{
			return (Delegate)obj;
		}
		else
		{
			TypeCastException tcx = new TypeCastException();
			tcx.castToName = sig;
			tcx.actualTypeName = obj == null ? "null" : obj.getClass().getName();
			throw tcx.fillInMessage( Message.CONVERSION_ERROR, obj, tcx.actualTypeName, tcx.castToName );
		}
	}

	public Delegate(String methodName, String signature, Object target, Class... argTypes) {
		this.target = target;
		this.signature = signature;
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
	
	public String getSignature() {
		return signature;
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
