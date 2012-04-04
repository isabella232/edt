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
package org.eclipse.edt.debug.core.java;

/**
 * Represents a line of function information parsed from the SMAP.
 */
public class SMAPFunctionInfo
{
	/**
	 * The name of the function as defined in the EGL source.
	 */
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
	 * The original line from the SMAP. This will be null for internal function infos (not corresponding to SMAP entries)
	 */
	public final String smapEntry;
	
	/**
	 * Constructor.
	 * 
	 * @param eglName The name of the function as it's declared in the EGL source.
	 * @param signature The Java signature of the function.
	 * @param lineDeclared The line number where the function is declared.
	 * @param smapEntry The original line from the SMAP.
	 */
	public SMAPFunctionInfo( String eglName, String signature, int lineDeclared, String smapEntry )
	{
		this.eglName = eglName;
		this.signature = signature;
		this.lineDeclared = lineDeclared;
		this.smapEntry = smapEntry;
	}
	
	/**
	 * For debug purposes.
	 */
	public String toString()
	{
		return smapEntry == null
				? "<internal> " + eglName + " (line=" + lineDeclared + ") (signature=" + signature + ")" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				: smapEntry;
	}
}
