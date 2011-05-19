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
 * Represents a line of variable information parsed from the SMAP. This can be used to map the Java variables to EGL
 * variables, filter those that weren't defined in the EGL source, display the EGL type instead of the Java type, etc.
 */
public class SMAPVariableInfo
{
	/**
	 * The name of the variable as defined in the EGL source.
	 */
	public final String eglName;
	
	/**
	 * The name of the variable in the generated Java code. This can differ from {@link SMAPVariableInfo#eglName}, due
	 * to aliasing.
	 */
	public final String javaName;
	
	/**
	 * The EGL type.
	 */
	public final String type;
	
	/**
	 * The line number in which the variable is declared.
	 */
	public final int lineDeclared;
	
	/**
	 * The signature of the Java method for local variables. Null for global variables.
	 */
	public final String javaMethodSignature;
	
	/**
	 * Constructor for global variables (no Java method signature).
	 * 
	 * @param eglName The variable's name in the EGL source.
	 * @param javaName The variable's name in the Java source.
	 * @param type The variable's type.
	 * @param lineDeclared The line number in which the variable is declared.
	 */
	public SMAPVariableInfo( String eglName, String javaName, String type, int lineDeclared )
	{
		this( eglName, javaName, type, lineDeclared, null );
	}
	
	/**
	 * Constructor.
	 * 
	 * @param eglName The variable's name in the EGL source.
	 * @param javaName The variable's name in the Java source.
	 * @param type The variable's type.
	 * @param lineDeclared The line number in which the variable is declared.
	 * @param javaMethodSignature The signature of the Java method for local variables, or null for global variables.
	 */
	public SMAPVariableInfo( String eglName, String javaName, String type, int lineDeclared, String javaMethodSignature )
	{
		this.eglName = eglName;
		this.javaName = javaName;
		this.type = type;
		this.lineDeclared = lineDeclared;
		
		if ( javaMethodSignature != null && javaMethodSignature.trim().length() == 0 )
		{
			javaMethodSignature = null;
		}
		this.javaMethodSignature = javaMethodSignature;
	}
	
	/**
	 * For debug purposes.
	 */
	public String toString()
	{
		return eglName + " (java=" + javaName + ") (type=" + type + ") (line=" + lineDeclared + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
