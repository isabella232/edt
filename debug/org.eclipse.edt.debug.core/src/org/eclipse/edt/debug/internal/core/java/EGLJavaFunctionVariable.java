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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;

/**
 * Variable whose value contains the local variables. There is no corresponding Java variable.
 */
public class EGLJavaFunctionVariable extends EGLJavaVariable
{
	/**
	 * The EGL-wrapped stack frame.
	 */
	private final EGLJavaStackFrame frame;
	
	/**
	 * The EGL-wrapped value for this variable.
	 */
	private final EGLJavaFunctionValue value;
	
	/**
	 * Constructor.
	 * 
	 * @param frame The EGL-wrapped stack frame.
	 * @throws DebugException
	 */
	public EGLJavaFunctionVariable( EGLJavaStackFrame frame ) throws DebugException
	{
		super( frame.getDebugTarget(), null, new SMAPVariableInfo( frame.getName(), frame.getName(), "", -1 ) ); //$NON-NLS-1$
		this.frame = frame;
		value = new EGLJavaFunctionValue( frame );
	}
	
	@Override
	public boolean hasValueChanged() throws DebugException
	{
		return false;
	}
	
	@Override
	public IValue getValue() throws DebugException
	{
		return value;
	}
	
	@Override
	public Object getJavaElement()
	{
		return frame.getJavaStackFrame();
	}
}
