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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unchecked")
public class RuntimeLoader {
	private static final String runtimeClassKey = "org.eclipse.edt.runtime.java.Runtime";

	public static Runtime load() {
		String typeSignature = getProperty(runtimeClassKey);
		
		try
		{
			Class runtimeClass = Class.forName( typeSignature );
			Constructor cons = runtimeClass.getDeclaredConstructor();
			return (Runtime)cons.newInstance();
		}
		catch ( Throwable ex )
		{
			if ( ex instanceof InvocationTargetException )
			{
				// An exception was thrown by the constructor.  The
				// exception is wrapped by the InvocationTargetException.
				ex = ((InvocationTargetException)ex).getTargetException();
			}

			throw new java.lang.RuntimeException( "Create runtime failed: " + ex.getLocalizedMessage() );
		}
	}
	
	private static String getProperty(String key) {
		// TODO do real property lookup in System.getProperty()
		if (key.equals(runtimeClassKey)) {
			return "org.eclipse.edt.javart.JSERuntime";
		}
		return null;
	}
}
