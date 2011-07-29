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
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.IEGLValue;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPUtil;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.jdt.debug.core.IJavaValue;

/**
 * Wraps an IJavaValue.
 */
public class EGLJavaValue extends EGLJavaDebugElement implements IEGLJavaValue
{
	/**
	 * The underlying Java value.
	 */
	protected final IJavaValue javaValue;
	
	/**
	 * The variable to which this value belongs.
	 */
	protected final IEGLJavaVariable parentVariable;
	
	/**
	 * The EGL-wrapped variables.
	 */
	protected IEGLJavaVariable[] eglVariables;
	
	/**
	 * The previous Java variables that were wrapped.
	 */
	protected IVariable[] previousJavaVariables;
	
	/**
	 * The SMAP data from the source debug extension of the class file, if applicable.
	 */
	private String smap;
	
	/**
	 * The variable information retrieved from the SMAP.
	 */
	private SMAPVariableInfo[] smapVariableInfos;
	
	/**
	 * Constructor.
	 * 
	 * @param target The debug target.
	 * @param value The underlying Java value.
	 * @param parent The variable to which this value belongs.
	 */
	public EGLJavaValue( IDebugTarget target, IJavaValue value, IEGLJavaVariable parent )
	{
		super( target );
		this.javaValue = value;
		this.parentVariable = parent;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IValue.class || adapter == EGLJavaValue.class || adapter == IEGLValue.class || adapter == IEGLJavaValue.class )
		{
			return this;
		}
		if ( adapter == IJavaValue.class )
		{
			return javaValue;
		}
		return super.getAdapter( adapter );
	}
	
	@Override
	public String getReferenceTypeName() throws DebugException
	{
		return parentVariable.getReferenceTypeName();
	}
	
	@Override
	public String getValueString() throws DebugException
	{
		return javaValue.getValueString();
	}
	
	@Override
	public boolean isAllocated() throws DebugException
	{
		return javaValue.isAllocated();
	}
	
	@Override
	public IVariable[] getVariables() throws DebugException
	{
		if ( getSMAP().length() == 0 )
		{
			// Couldn't get the variable info from the SMAP...just return the Java variables.
			return javaValue.getVariables();
		}
		
		boolean recompute = true;
		IVariable[] javaVariables = javaValue.getVariables();
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
			List<IEGLJavaVariable> newEGLVariables = VariableUtil.filterAndWrapVariables( javaVariables, eglVariables, getSMAPVariableInfos(),
					parentVariable.getEGLStackFrame(), false, this );
			previousJavaVariables = javaVariables;
			eglVariables = newEGLVariables.toArray( new EGLJavaVariable[ newEGLVariables.size() ] );
		}
		
		return eglVariables;
	}
	
	@Override
	public boolean hasVariables() throws DebugException
	{
		return javaValue.hasVariables() && getVariables().length != 0;
	}
	
	@Override
	public Object getJavaDebugElement()
	{
		return javaValue;
	}
	
	@Override
	public IJavaValue getJavaValue()
	{
		return javaValue;
	}
	
	/**
	 * Returns the SMAP information for the value. It will never be null. If there is no SMAP information, or the Java type was not a type that we
	 * recognize, then this will return blank.
	 * 
	 * @return the SMAP information.
	 * @throws DebugException
	 */
	public String getSMAP() throws DebugException
	{
		if ( smap == null )
		{
			smap = SMAPUtil.getSMAP( javaValue.getJavaType() );
		}
		return smap;
	}
	
	/**
	 * Returns the variable information from the SMAP for the value. It will never be null. If there is no SMAP information, or the Java type was not
	 * a type that we recognize, then this will return empty.
	 * 
	 * @return the variable information
	 * @throws DebugException
	 */
	public SMAPVariableInfo[] getSMAPVariableInfos() throws DebugException
	{
		if ( smapVariableInfos == null )
		{
			// We only care about the variables, which is the first element
			smapVariableInfos = (SMAPVariableInfo[])SMAPUtil.parseVariables( getSMAP(), null );
		}
		return smapVariableInfos;
	}
	
	@Override
	public String computeDetail()
	{
		return null;
	}
	
	@Override
	public IEGLJavaVariable getParentVariable()
	{
		return parentVariable;
	}
}
