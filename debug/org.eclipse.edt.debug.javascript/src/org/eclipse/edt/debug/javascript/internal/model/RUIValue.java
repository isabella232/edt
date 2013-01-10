/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.IEGLValue;
import org.eclipse.edt.debug.core.IEGLVariable;

public class RUIValue extends RUIDebugElement implements IEGLValue
{
	private static final IEGLVariable[] EMPTY_VARIABLES = {};
	
	private String fValueString;
	private String fValueStringOld;
	private RUIVariable fVariable;
	private IEGLVariable[] fVariables;
	private boolean fValueChanged;
	private boolean fHasChildren;
	
	public RUIValue( RUIDebugTarget target, RUIVariable variable, boolean hasChildren, String oldValue )
	{
		super( target );
		fVariable = variable;
		fValueStringOld = oldValue;
		fHasChildren = hasChildren;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName()
	{
		return fVariable.getReferenceTypeName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	@Override
	public String getValueString()
	{
		return fValueString;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	@Override
	public boolean isAllocated() throws DebugException
	{
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException
	{
		if ( fVariables == null )
		{
			buildVariables();
		}
		return fVariables;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException
	{
		return fHasChildren;
	}
	
	private void buildVariables()
	{
		fVariables = fVariable.getVariables();
		if ( fVariables == null )
		{
			fVariables = EMPTY_VARIABLES;
		}
		else
		{
			RUIStackFrame frame = fVariable.getStackFrame();
			IEGLVariable[] newVars = new IEGLVariable[ fVariables.length ];
			for ( int i = 0; i < fVariables.length; i++ )
			{
				newVars[ i ] = frame.getCorrespondingVariable( (RUIVariable)fVariables[ i ], fVariable );
			}
			fVariables = newVars;
		}
	}
	
	public boolean hasChanged()
	{
		return fValueChanged;
	}
	
	public void clearValue()
	{
		fValueStringOld = fValueString;
		fValueString = null;
	}
	
	public void setValue( String value )
	{
		if ( fValueStringOld != null )
		{
			if ( value != null )
			{
				if ( !fValueStringOld.equals( value ) )
				{
					fValueChanged = true;
				}
			}
			else
			{
				fValueChanged = true;
			}
		}
		fValueString = value;
	}
	
	public IEGLVariable getParentVariable()
	{
		return fVariable;
	}
}
