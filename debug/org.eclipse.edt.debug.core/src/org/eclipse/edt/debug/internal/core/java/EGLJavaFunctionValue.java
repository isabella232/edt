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
import org.eclipse.edt.debug.core.IEGLValue;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaValue;

/**
 * A value that contains local variables.
 */
public class EGLJavaFunctionValue extends EGLJavaDebugElement implements IEGLJavaValue
{
	/**
	 * The EGL-wrapped stack frame.
	 */
	private final EGLJavaStackFrame frame;
	
	/**
	 * The variable to which this value belongs.
	 */
	protected final IEGLJavaVariable parentVariable;
	
	/**
	 * The children variables.
	 */
	protected IVariable[] children;
	
	/**
	 * Constructor.
	 * 
	 * @param frame The EGL-wrapped stack frame.
	 */
	public EGLJavaFunctionValue( EGLJavaStackFrame frame, EGLJavaFunctionVariable parentVariable )
	{
		super( frame.getDebugTarget() );
		this.frame = frame;
		this.parentVariable = parentVariable;
	}
	
	@Override
	public synchronized IVariable[] getVariables() throws DebugException
	{
		if ( children != null )
		{
			return children;
		}
		
		if ( frame.getSMAP().length() == 0 )
		{
			// Couldn't get the variable info from the SMAP...just return the local Java variables.
			children = frame.getJavaStackFrame().getLocalVariables();
		}
		else
		{
			List<IEGLJavaVariable> newEGLVariables = VariableUtil.filterAndWrapVariables( frame.getJavaStackFrame().getLocalVariables(),
					parentVariable.getEGLStackFrame(), false, this );
			children = newEGLVariables.toArray( new EGLJavaVariable[ newEGLVariables.size() ] );
		}
		
		return children;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IValue.class || adapter == IEGLValue.class || adapter == IEGLJavaValue.class || adapter == EGLJavaFunctionValue.class )
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
	
	/**
	 * @return the underlying Java debug element.
	 */
	@Override
	public Object getJavaDebugElement()
	{
		return frame;
	}
	
	public String computeDetail()
	{
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public IJavaValue getJavaValue()
	{
		return null;
	}
	
	@Override
	public IEGLJavaVariable getParentVariable()
	{
		return parentVariable;
	}
	
	@Override
	public SMAPVariableInfo[] getSMAPVariableInfos() throws DebugException
	{
		return frame.getSMAPVariableInfos();
	}
}
