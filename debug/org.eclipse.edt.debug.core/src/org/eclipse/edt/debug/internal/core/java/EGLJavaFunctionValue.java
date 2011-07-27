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

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.IEGLVariable;

/**
 * A value that contains local variables.
 */
public class EGLJavaFunctionValue extends EGLJavaDebugElement implements IValue
{
	/**
	 * The EGL-wrapped stack frame.
	 */
	private final EGLJavaStackFrame frame;
	
	/**
	 * The EGL-wrapped variables.
	 */
	private EGLJavaVariable[] eglVariables;
	
	/**
	 * The previous Java variables that were wrapped.
	 */
	private IVariable[] previousJavaVariables;
	
	/**
	 * Constructor.
	 * 
	 * @param frame The EGL-wrapped stack frame.
	 */
	public EGLJavaFunctionValue( EGLJavaStackFrame frame )
	{
		super( frame.getDebugTarget() );
		this.frame = frame;
	}
	
	@Override
	public IVariable[] getVariables() throws DebugException
	{
		if ( frame.getSMAP().length() == 0 )
		{
			// Couldn't get the variable info from the SMAP...just return the Java variables.
			return frame.getJavaStackFrame().getLocalVariables();
		}
		
		boolean recompute = true;
		IVariable[] javaVariables = frame.getJavaStackFrame().getLocalVariables();
		if ( previousJavaVariables != null )
		{
			if ( javaVariables.length == previousJavaVariables.length )
			{
				recompute = false;
				for ( int i = 0; i < javaVariables.length; i++ )
				{
					if ( javaVariables[ i ] != previousJavaVariables[ i ] )
					{
						recompute = true;
						break;
					}
				}
			}
		}
		
		if ( recompute )
		{
			List<EGLJavaVariable> newEGLVariables = SMAPUtil.filterAndWrapVariables( javaVariables, eglVariables,
					frame.getSMAPVariableInfos(), frame, frame.getDebugTarget(), false );
			previousJavaVariables = javaVariables;
			eglVariables = newEGLVariables.toArray( new EGLJavaVariable[ newEGLVariables.size() ] );
		}
		
		return eglVariables;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IVariable.class || adapter == EGLJavaVariable.class || adapter == IEGLVariable.class )
		{
			return this;
		}
		return super.getAdapter( adapter );
	}
	
	@Override
	public String getReferenceTypeName() throws DebugException
	{
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public String getValueString() throws DebugException
	{
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public boolean isAllocated() throws DebugException
	{
		return true;
	}
	
	@Override
	public boolean hasVariables() throws DebugException
	{
		return getVariables().length > 0;
	}
	
	@Override
	public Object getJavaElement()
	{
		return frame;
	}
}
