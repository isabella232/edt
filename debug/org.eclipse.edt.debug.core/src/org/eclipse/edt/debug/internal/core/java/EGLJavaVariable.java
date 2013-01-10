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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapter;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapterExtension;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLVariable;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPUtil;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Wraps an IJavaVariable.
 */
public class EGLJavaVariable extends EGLJavaDebugElement implements IEGLJavaVariable
{
	/**
	 * The underlying Java variable.
	 */
	protected IJavaVariable javaVariable;
	
	/**
	 * The EGL-wrapped value of the variable.
	 */
	protected IEGLJavaValue value;
	
	/**
	 * The variable information retrieved from the SMAP.
	 */
	protected SMAPVariableInfo variableInfo;
	
	/**
	 * The stack frame.
	 */
	protected IEGLJavaStackFrame frame;
	
	/**
	 * This variable's parent.
	 */
	protected IEGLJavaValue parent;
	
	/**
	 * Constructor.
	 * 
	 * @param target The debug target.
	 * @param javaVariable The underlying Java variable.
	 * @param variableInfo The variable information retreived from the SMAP.
	 * @param frame The stack frame owning this variable.
	 * @param parent The parent of this variable; this should be null if the variable is toplevel.
	 */
	public EGLJavaVariable( IDebugTarget target, IJavaVariable javaVariable, SMAPVariableInfo variableInfo, IEGLJavaStackFrame frame,
			IEGLJavaValue parent )
	{
		super( target );
		initialize( frame, variableInfo, javaVariable, parent );
	}
	
	@Override
	public void initialize( IEGLJavaVariable newVariable, IEGLJavaValue newParent )
	{
		initialize( newVariable.getEGLStackFrame(), newVariable.getVariableInfo(), newVariable.getJavaVariable(), newParent );
	}
	
	private void initialize( IEGLJavaStackFrame frame, SMAPVariableInfo varInfo, IJavaVariable javaVar, IEGLJavaValue parent )
	{
		this.frame = frame;
		this.variableInfo = varInfo;
		this.javaVariable = javaVar;
		this.parent = parent;
		this.value = null;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IVariable.class || adapter == EGLJavaVariable.class || adapter == IEGLVariable.class || adapter == IEGLJavaVariable.class )
		{
			return this;
		}
		if ( adapter == IJavaVariable.class )
		{
			return javaVariable;
		}
		if ( adapter == IStackFrame.class || adapter == IEGLStackFrame.class || adapter == IEGLJavaStackFrame.class
				|| adapter == EGLJavaStackFrame.class )
		{
			return frame;
		}
		if ( adapter == IJavaStackFrame.class )
		{
			return frame.getJavaStackFrame();
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
	
	@Override
	public void setValue( String expression ) throws DebugException
	{
	}
	
	@Override
	public void setValue( IValue value ) throws DebugException
	{
	}
	
	@Override
	public boolean supportsValueModification()
	{
		return false;
	}
	
	@Override
	public boolean verifyValue( String expression ) throws DebugException
	{
		return false;
	}
	
	@Override
	public boolean verifyValue( IValue value ) throws DebugException
	{
		return false;
	}
	
	@Override
	public IValue getValue() throws DebugException
	{
		if ( value == null )
		{
			value = createEGLValue( (IJavaValue)javaVariable.getValue() );
		}
		return value;
	}
	
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new EGLJavaValue( getDebugTarget(), (IJavaValue)javaValue, this );
	}
	
	@Override
	public String getName() throws DebugException
	{
		return variableInfo.eglName;
	}
	
	@Override
	public String getReferenceTypeName() throws DebugException
	{
		return variableInfo.type;
	}
	
	@Override
	public boolean hasValueChanged() throws DebugException
	{
		return javaVariable.hasValueChanged();
	}
	
	@Override
	public IJavaVariable getJavaVariable()
	{
		return javaVariable;
	}
	
	@Override
	public Object getJavaDebugElement()
	{
		return getJavaVariable();
	}
	
	@Override
	public IEGLJavaStackFrame getEGLStackFrame()
	{
		return frame;
	}
	
	@Override
	public SMAPVariableInfo getVariableInfo()
	{
		return variableInfo;
	}
	
	@Override
	public boolean isLocal()
	{
		if ( parent != null )
		{
			if ( parent.getParentVariable() != null )
			{
				parent.getParentVariable().isLocal();
			}
		}
		return false;
	}
	
	@Override
	public IEGLJavaValue getParentValue()
	{
		return parent;
	}
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		if ( SMAPUtil.isEGLStratum( javaVariable ) )
		{
			return false;
		}
		
		try
		{
			getValue();
			if ( value instanceof EGLJavaValue )
			{
				return ((EGLJavaValue)value).shouldCheckJavaElementAdapter();
			}
		}
		catch ( DebugException e )
		{
		}
		return true;
	}
}
