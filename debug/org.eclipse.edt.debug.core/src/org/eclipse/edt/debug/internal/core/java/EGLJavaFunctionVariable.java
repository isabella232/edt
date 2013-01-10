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
package org.eclipse.edt.debug.internal.core.java;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;

/**
 * Variable whose value contains the local variables. There is no corresponding Java variable.
 */
public class EGLJavaFunctionVariable extends EGLJavaVariable
{
	/**
	 * Constructor.
	 * 
	 * @param frame The EGL-wrapped stack frame.
	 * @throws DebugException
	 */
	public EGLJavaFunctionVariable( EGLJavaStackFrame frame ) throws DebugException
	{
		super( frame.getDebugTarget(), null, new SMAPVariableInfo( frame.getSMAPFunctionInfo() == null
				? frame.getName()
				: frame.getSMAPFunctionInfo().eglName, frame.getName(), "", -1, frame.getSMAPFunctionInfo() == null //$NON-NLS-1$
				? null
				: frame.getSMAPFunctionInfo().smapEntry ), frame, null );
	}
	
	@Override
	public boolean hasValueChanged() throws DebugException
	{
		return false;
	}
	
	@Override
	public IValue getValue() throws DebugException
	{
		if ( value == null )
		{
			value = new EGLJavaFunctionValue( (EGLJavaStackFrame)frame, this );
		}
		return value;
	}
	
	@Override
	public Object getJavaDebugElement()
	{
		return frame.getJavaStackFrame();
	}
	
	@Override
	public boolean isLocal()
	{
		return true;
	}
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		return false;
	}
}
