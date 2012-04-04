/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugElement;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;

/**
 * Super class of all EGL Java Debug framework classes.
 */
public abstract class EGLJavaDebugElement extends DebugElement implements IEGLJavaDebugElement
{
	public EGLJavaDebugElement( IDebugTarget target )
	{
		super( target );
	}
	
	@Override
	public String getModelIdentifier()
	{
		return IEGLDebugCoreConstants.EGL_JAVA_MODEL_PRESENTATION_ID;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == EGLJavaDebugElement.class || adapter == IDebugElement.class || adapter == IEGLJavaDebugElement.class )
		{
			return this;
		}
		if ( adapter == IDebugTarget.class || adapter == ITerminate.class )
		{
			return getDebugTarget();
		}
		if ( adapter == IEGLDebugTarget.class || adapter == EGLJavaDebugTarget.class || adapter == IEGLJavaDebugTarget.class )
		{
			return getEGLJavaDebugTarget();
		}
		if ( adapter == IJavaDebugTarget.class )
		{
			IDebugTarget target = getDebugTarget();
			if ( target instanceof IJavaDebugTarget )
			{
				return target;
			}
			if ( target instanceof IEGLJavaDebugTarget )
			{
				return ((IEGLJavaDebugTarget)target).getJavaDebugTarget();
			}
		}
		
		// Try not to break other adapters when it's something that doesn't have an EGL-specific adapter.
		if ( shouldCheckJavaElementAdapter() )
		{
			Object adapted = ((IAdaptable)getJavaDebugElement()).getAdapter( adapter );
			if ( adapted != null )
			{
				return adapted;
			}
		}
		
		return super.getAdapter( adapter );
	}
	
	/**
	 * @return true if we should attempt to obtain an adapter from the underlying Java object.
	 */
	protected boolean shouldCheckJavaElementAdapter()
	{
		return false;
	}
	
	/**
	 * @return The EGL debug target, or null if this thread is within a non-EGL debug target.
	 */
	public EGLJavaDebugTarget getEGLJavaDebugTarget()
	{
		return (EGLJavaDebugTarget)getDebugTarget();
	}
	
	/**
	 * Default is to just refire the events with ourself as the source.
	 * 
	 * @param events The events.
	 */
	public void handleDebugEvents( DebugEvent[] events )
	{
		if ( events != null && events.length != 0 )
		{
			DebugEvent[] copy = new DebugEvent[ events.length ];
			for ( int i = 0; i < events.length; i++ )
			{
				copy[ i ] = new DebugEvent( this, events[ i ].getKind(), events[ i ].getDetail() );
				copy[ i ].setData( events[ i ].getData() );
			}
			DebugPlugin.getDefault().fireDebugEventSet( copy );
		}
	}
	
	/**
	 * Groups the array of events by source.
	 * 
	 * @param events The events
	 * @return a mapping of sources to their events
	 */
	protected HashMap<Object, List<DebugEvent>> groupBySource( DebugEvent[] events )
	{
		HashMap<Object, List<DebugEvent>> grouped = new HashMap<Object, List<DebugEvent>>();
		
		for ( int i = 0; i < events.length; i++ )
		{
			List<DebugEvent> list = grouped.get( events[ i ].getSource() );
			if ( list == null )
			{
				list = new ArrayList<DebugEvent>();
				grouped.put( events[ i ].getSource(), list );
			}
			list.add( events[ i ] );
		}
		
		return grouped;
	}
}
