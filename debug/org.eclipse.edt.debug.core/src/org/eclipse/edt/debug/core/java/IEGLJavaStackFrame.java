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

import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * Represents an EGL stack frame in the Java-based debugger. EGL stack frames wrap the original Java stack frame.
 */
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
	 * @return true if this frame is an EGL stratum.
	 */
	public boolean isEGLStratum() throws DebugException;
	
	/**
	 * Returns the variable information from the frame's SMAP. It should never be null. If there is no SMAP information, or the Java type was not a
	 * type that we recognize, then this will return an empty array.
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
	
	/**
	 * Returns the corresponding EGL variable given the new variable. Implementations may choose to just return the new variable that was passed in,
	 * but this allows a frame to reuse an existing variable.
	 * 
	 * @param newVariable The new EGL variable.
	 * @param parent The parent value, possibly null.
	 * @return the corresponding EGL variable.
	 * @throws DebugException
	 */
	public IEGLJavaVariable getCorrespondingVariable( IEGLJavaVariable newVariable, IEGLJavaValue parent ) throws DebugException;
	
	/**
	 * Returns the path of the frame's source file, relative to the source directory.
	 * 
	 * @return the path of the frame's source file, relative to the source directory.
	 * @throws DebugException
	 */
	public String getSourcePath() throws DebugException;
}
