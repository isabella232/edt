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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.edt.debug.core.IEGLDebugElement;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.edt.debug.javascript.EDTJavaScriptDebugPlugin;
import org.eclipse.edt.debug.javascript.internal.launching.IRUIDebugConstants;

public class RUIDebugElement extends PlatformObject implements IDebugElement, IEGLDebugElement
{
	protected RUIDebugTarget fTarget;
	
	/**
	 * Constructs a new debug element contained in the given debug target.
	 * 
	 * @param target debug target (RUI VM)
	 */
	public RUIDebugElement( RUIDebugTarget target )
	{
		fTarget = target;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	@Override
	public String getModelIdentifier()
	{
		return IRUIDebugConstants.ID_RUI_DEBUG_MODEL;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	@Override
	public IDebugTarget getDebugTarget()
	{
		return fTarget;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	@Override
	public ILaunch getLaunch()
	{
		return getDebugTarget().getLaunch();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IDebugElement.class || adapter == RUIDebugElement.class )
		{
			return this;
		}
		
		if ( adapter == IDebugTarget.class || adapter == RUIDebugTarget.class || adapter == IEGLDebugTarget.class )
		{
			return getDebugTarget();
		}
		
		if ( adapter == ILaunch.class )
		{
			return getDebugTarget().getLaunch();
		}
		return super.getAdapter( adapter );
	}
	
	protected void abort( String message, Throwable e ) throws DebugException
	{
		throw new DebugException( new Status( IStatus.ERROR, EDTJavaScriptDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, message, e ) );
	}
	
	/**
	 * Fires a debug event
	 * 
	 * @param event the event to be fired
	 */
	protected void fireEvent( DebugEvent event )
	{
		DebugPlugin.getDefault().fireDebugEventSet( new DebugEvent[] { event } );
	}
	
	/**
	 * Fires a <code>CREATE</code> event for this element.
	 */
	protected void fireCreationEvent()
	{
		fireEvent( new DebugEvent( this, DebugEvent.CREATE ) );
	}
	
	/**
	 * Fires a <code>RESUME</code> event for this element with the given detail.
	 * 
	 * @param detail event detail code
	 */
	public void fireResumeEvent( int detail )
	{
		fireEvent( new DebugEvent( this, DebugEvent.RESUME, detail ) );
	}
	
	/**
	 * Fires a <code>SUSPEND</code> event for this element with the given detail.
	 * 
	 * @param detail event detail code
	 */
	public void fireSuspendEvent( int detail )
	{
		fireEvent( new DebugEvent( this, DebugEvent.SUSPEND, detail ) );
	}
	
	/**
	 * Fires a <code>TERMINATE</code> event for this element.
	 */
	protected void fireTerminateEvent()
	{
		fireEvent( new DebugEvent( this, DebugEvent.TERMINATE ) );
	}
}
