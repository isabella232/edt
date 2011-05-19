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
	 * @param signature The Java signature of the function
	 * @param lineDeclared The line number where the function is declared.
	 */
	public SMAPFunctionInfo( String signature, int lineDeclared )
	{
		this.signature = signature;
		this.lineDeclared = lineDeclared;
	}
	
	/**
	 * For debug purposes.
	 */
	public String toString()
	{
		return signature + " (line=" + lineDeclared + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
