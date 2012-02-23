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
package org.eclipse.edt.debug.core.java;

import org.eclipse.edt.debug.core.IEGLVariable;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Represents an EGL variable in the Java-based debugger. EGL variables wrap the original Java variable.
 */
public interface IEGLJavaVariable extends IEGLVariable, IEGLJavaDebugElement
{
	/**
	 * @return the underlying variable.
	 */
	public IJavaVariable getJavaVariable();
	
	/**
	 * @return the EGL frame to which this variable belongs.
	 */
	public IEGLJavaStackFrame getEGLStackFrame();
	
	/**
	 * @return the variable information from the SMAP.
	 */
	public SMAPVariableInfo getVariableInfo();
	
	/**
	 * @return true if this is a local variable.
	 */
	public boolean isLocal();
	
	/**
	 * Initializes the variable from newVariable, resetting its value if necessary.
	 * 
	 * @param newVariable The new variable.
	 * @param newParent The new parent value, possibly null.
	 */
	public void initialize( IEGLJavaVariable newVariable, IEGLJavaValue newParent );
	
	/**
	 * @return the variable's parent value, or null if it has no parent.
	 */
	public IEGLJavaValue getParentValue();
}
