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

import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

public interface IEGLJavaStackFrame extends IEGLStackFrame, IEGLJavaDebugElement
{
	/**
	 * @return the EGL thread to which this frame belongs.
	 */
	public IEGLJavaThread getEGLThread();
	
	/**
	 * @return the underlying stack frame.
	 */
	public IJavaStackFrame getJavaStackFrame();
	
	/**
	 * Returns the variable information from the frame's SMAP. It should never be null. If there is no SMAP information, or the Java type
	 * was not a type that we recognize, then this will return an empty array.
	 * 
	 * @return the variable information
	 * @throws DebugException
	 */
	public SMAPVariableInfo[] getSMAPVariableInfos() throws DebugException;
	
	/**
	 * Sets the variable information.
	 * 
	 * @param infos The variable information.
	 */
	public void setSMAPVariableInfos( SMAPVariableInfo[] infos );
	
	/**
	 * Returns the function information from the frame's SMAP. It might be null if there was no SMAP information.
	 * 
	 * @return the function information, possibly null.
	 * @throws DebugException
	 */
	public SMAPFunctionInfo getSMAPFunctionInfo() throws DebugException;
	
	/**
	 * Sets the function information.
	 * 
	 * @param info The function information.
	 */
	public void setSMAPFunctionInfo( SMAPFunctionInfo info );
}
