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
package org.eclipse.edt.debug.internal.core.java;

/**
 * Represents a line of function information parsed from the SMAP.
 */
public class SMAPFunctionInfo
{
	public final String eglName;
	
	/**
	 * The Java signature of the function.
	 */
	public final String signature;
	
	/**
	 * The line number where the function is declared.
	 */
	public final int lineDeclared;
	
	/**
	 * Constructor.
	 * 
	 * @param eglName The name of the function as it's declared in the EGL source.
	 * @param signature The Java signature of the function
	 * @param lineDeclared The line number where the function is declared.
	 */
	public SMAPFunctionInfo( String eglName, String signature, int lineDeclared )
	{
		this.eglName = eglName;
		this.signature = signature;
		this.lineDeclared = lineDeclared;
	}
	
	/**
	 * For debug purposes.
	 */
	public String toString()
	{
		return eglName + " (line=" + lineDeclared + ") (signature=" + signature + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
