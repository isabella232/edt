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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapter;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapterExtension;
import org.eclipse.edt.debug.core.IEGLValue;
import org.eclipse.edt.debug.core.IEGLVariable;

public class RUIVariable extends RUIDebugElement implements IEGLVariable
{
	
	private String fName;
	private String fJSName; // the generated variable name. can differ from fName.
	private String fGetterName; // name of the get method, if the field had this property specified.
	private String fSetterName; // name of the set method, if the field had this property specified.
	private RUIStackFrame fFrame;
	private String fIndex;
	private RUIValue fValue;
	private String fType;
	private String fQualifiedName;
	private String fOldValueString;
	private boolean fHasChildren;
	private RUIVariable fParentVariable;
	
	/**
	 * Constructs a variable.
	 * 
	 * @param frame Owning stack frame.
	 * @param name The name.
	 * @param qualifiedName The qualified name (parent.child).
	 * @param index The index.
	 * @param type The type.
	 * @param hasChildren Whether this has child variables.
	 */
	public RUIVariable( RUIStackFrame frame, RUIVariable parent, String name, String jsName, String getterName, String setterName,
			String qualifiedName, String index, String type, boolean hasChildren )
	{
		super( (RUIDebugTarget)frame.getDebugTarget() );
		initialize( frame, parent, name, jsName, getterName.trim(), setterName.trim(), qualifiedName, index, type, hasChildren );
	}
	
	public void initialize( RUIStackFrame frame, RUIVariable parent, RUIVariable other )
	{
		fOldValueString = getCurrentValueString();
		initialize( frame, parent, other.fName, other.fJSName, other.fGetterName, other.fSetterName, other.fQualifiedName, other.fIndex, other.fType,
				other.fHasChildren );
	}
	
	private void initialize( RUIStackFrame frame, RUIVariable parent, String name, String jsName, String getterName, String setterName,
			String qualifiedName, String index, String type, boolean hasChildren )
	{
		fFrame = frame;
		fName = name;
		fJSName = jsName == null
				? "" //$NON-NLS-1$
				: jsName.trim();
		fGetterName = getterName;
		fSetterName = setterName;
		fQualifiedName = qualifiedName;
		fIndex = index;
		fType = type;
		fHasChildren = hasChildren;
		fValue = null;
		fParentVariable = parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	@Override
	public IValue getValue()
	{
		synchronized ( getDebugTarget() )
		{
			if ( getCurrValue().getValueString() == null )
			{
				return ((RUIDebugTarget)getDebugTarget()).getVariableValue( this );
			}
			return getCurrValue();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	@Override
	public String getName()
	{
		return fName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName()
	{
		return fType;
	}
	
	public RUIVariable getParent()
	{
		return fParentVariable;
	}
	
	public String getJSName()
	{
		return fJSName;
	}
	
	public String getGetterName()
	{
		return fGetterName;
	}
	
	public String getSetterName()
	{
		return fSetterName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	@Override
	public boolean hasValueChanged()
	{
		return ((RUIValue)getValue()).hasChanged();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	@Override
	public void setValue( String expression ) throws DebugException
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	@Override
	public void setValue( IValue value ) throws DebugException
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	@Override
	public boolean supportsValueModification()
	{
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	@Override
	public boolean verifyValue( String expression ) throws DebugException
	{
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	@Override
	public boolean verifyValue( IValue value ) throws DebugException
	{
		return true;
	}
	
	/**
	 * Returns the stack frame owning this variable.
	 * 
	 * @return the stack frame owning this variable
	 */
	public RUIStackFrame getStackFrame()
	{
		return fFrame;
	}
	
	public String getIndex()
	{
		return fIndex;
	}
	
	public IEGLVariable[] getVariables()
	{
		synchronized ( getDebugTarget() )
		{
			return ((RUIDebugTarget)getDebugTarget()).getVariables( this );
		}
	}
	
	public RUIValue getCurrValue()
	{
		if ( fValue == null )
		{
			fValue = new RUIValue( (RUIDebugTarget)getDebugTarget(), this, fHasChildren, fOldValueString );
		}
		return fValue;
	}
	
	public void setType( String type )
	{
		fType = type;
	}
	
	public String getLabel( boolean qualified )
	{
		String label = null;
		StringBuilder buf = new StringBuilder( 20 );
		try
		{
			if ( qualified )
			{
				String type = getReferenceTypeName();
				if ( type != null && (type = type.trim()).length() != 0 )
				{
					buf.append( type );
					buf.append( " " ); //$NON-NLS-1$
				}
			}
			buf.append( getName() );
			
			String value = getValue().getValueString();
			if ( value != null )
			{
				buf.append( " = " ); //$NON-NLS-1$
				buf.append( value );
			}
			label = buf.toString();
		}
		catch ( DebugException e )
		{
		}
		return label;
	}
	
	/**
	 * @return the current value string without creating the value if it's unset.
	 */
	protected String getCurrentValueString()
	{
		if ( fValue != null )
		{
			return fValue.getValueString();
		}
		return null;
	}
	
	public String getQualifiedName()
	{
		return fQualifiedName;
	}
	
	public void setQualifiedName( String qualifiedName )
	{
		fQualifiedName = qualifiedName;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == RUIVariable.class || adapter == IVariable.class || adapter == IEGLVariable.class )
		{
			return this;
		}
		if ( adapter == IWatchExpressionFactoryAdapter.class )
		{
			// This is to prevent the 'Watch' context menu item from appearing on variables.
			return new IWatchExpressionFactoryAdapterExtension() {
				@Override
				public String createWatchExpression( IVariable variable ) throws CoreException
				{
					return variable.getName();
				}
				
				@Override
				public boolean canCreateWatchExpression( IVariable variable )
				{
					return false;
				}
			};
		}
		return super.getAdapter( adapter );
	}
	
	public IEGLValue getEGLValue() throws DebugException
	{
		return (IEGLValue)getValue();
	}
	
	public boolean isNull()
	{
		try
		{
			return "null".equals( getValue().getValueString() ); //$NON-NLS-1$
		}
		catch ( DebugException e )
		{
			return true;
		}
	}
	
	public String getWatchExpressionString()
	{
		return ""; //$NON-NLS-1$
	}
}
