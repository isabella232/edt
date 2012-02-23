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
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaType;
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
	 * The children variables.
	 */
	protected IVariable[] children;
	
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
	public synchronized IVariable[] getVariables() throws DebugException
	{
		if ( children != null )
		{
			return children;
		}
		
		if ( getSMAP().length() == 0 )
		{
			// Couldn't get the variable info from the SMAP...just return the Java variables.
			children = javaValue.getVariables();
		}
		else
		{
			List<IEGLJavaVariable> newEGLVariables = VariableUtil.filterAndWrapVariables( javaValue.getVariables(),
					parentVariable.getEGLStackFrame(), false, this );
			children = newEGLVariables.toArray( new EGLJavaVariable[ newEGLVariables.size() ] );
		}
		
		return children;
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
			smap = SMAPUtil.getSMAP( javaValue.getJavaType(), getEGLJavaDebugTarget().getSMAPFileCache() );
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
			
			// Also acquire variable infos from super types so we can display inherited fields.
			IJavaType type = javaValue.getJavaType();
			while ( type instanceof IJavaClassType )
			{
				IJavaClassType superType = ((IJavaClassType)type).getSuperclass();
				if ( superType != null )
				{
					String superSMAP = SMAPUtil.getSMAP( superType, getEGLJavaDebugTarget().getSMAPFileCache() );
					if ( superSMAP.length() != 0 )
					{
						SMAPVariableInfo[] superInfos = SMAPUtil.parseVariables( superSMAP, null );
						if ( superInfos.length != 0 )
						{
							if ( smapVariableInfos.length == 0 )
							{
								smapVariableInfos = superInfos;
							}
							else
							{
								SMAPVariableInfo[] newInfos = new SMAPVariableInfo[ smapVariableInfos.length + superInfos.length ];
								System.arraycopy( smapVariableInfos, 0, newInfos, 0, smapVariableInfos.length );
								System.arraycopy( superInfos, 0, newInfos, smapVariableInfos.length, superInfos.length );
								smapVariableInfos = newInfos;
							}
						}
					}
				}
				type = superType;
			}
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
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		try
		{
			return !SMAPUtil.isEGLStratum( getSMAP() );
		}
		catch ( DebugException e )
		{
			return true;
		}
	}
}
