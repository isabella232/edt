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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventFilter;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaThread;

/**
 * Wraps an IJavaDebugTarget.
 */
public class EGLJavaDebugTarget extends EGLJavaDebugElement implements IEGLDebugTarget, IDebugEventFilter
{
	/**
	 * The underlying Java debug target.
	 */
	private final IJavaDebugTarget javaTarget;
	
	/**
	 * A map of Java threads to EGL threads.
	 */
	private final Map<IJavaThread, EGLJavaThread> threads;
	
	/**
	 * The EGL-wrapped threads.
	 */
	private final List<EGLJavaThread> eglThreads;
	
	public EGLJavaDebugTarget( IJavaDebugTarget target )
	{
		super( null );
		javaTarget = target;
		threads = new HashMap<IJavaThread, EGLJavaThread>();
		eglThreads = new ArrayList<EGLJavaThread>();
		
		// Add the initial threads, which are created before the target.
		try
		{
			IThread[] threads = javaTarget.getThreads();
			for ( int i = 0; i < threads.length; i++ )
			{
				if ( threads[ i ] instanceof IJavaThread )
				{
					getThread( (IJavaThread)threads[ i ] );
				}
			}
		}
		catch ( DebugException e )
		{
			EDTDebugCorePlugin.log( e );
		}
		
		DebugPlugin.getDefault().addDebugEventFilter( this );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget()
	{
		return this;
	}
	
	@Override
	public ILaunch getLaunch()
	{
		return javaTarget.getLaunch();
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IDebugTarget.class || adapter == EGLJavaDebugTarget.class || adapter == IEGLDebugTarget.class )
		{
			return this;
		}
		return super.getAdapter( adapter );
	}
	
	@Override
	public boolean canTerminate()
	{
		return javaTarget.canTerminate();
	}
	
	@Override
	public boolean isTerminated()
	{
		return javaTarget.isTerminated();
	}
	
	@Override
	public void terminate() throws DebugException
	{
		javaTarget.terminate();
	}
	
	@Override
	public boolean canResume()
	{
		return javaTarget.canResume();
	}
	
	@Override
	public boolean canSuspend()
	{
		return javaTarget.canSuspend();
	}
	
	@Override
	public boolean isSuspended()
	{
		return javaTarget.isSuspended();
	}
	
	@Override
	public void resume() throws DebugException
	{
		javaTarget.resume();
	}
	
	@Override
	public void suspend() throws DebugException
	{
		javaTarget.suspend();
	}
	
	@Override
	public void breakpointAdded( IBreakpoint breakpoint )
	{
		javaTarget.breakpointAdded( breakpoint );
	}
	
	@Override
	public void breakpointRemoved( IBreakpoint breakpoint, IMarkerDelta delta )
	{
		javaTarget.breakpointRemoved( breakpoint, delta );
	}
	
	@Override
	public void breakpointChanged( IBreakpoint breakpoint, IMarkerDelta delta )
	{
		javaTarget.breakpointChanged( breakpoint, delta );
	}
	
	@Override
	public boolean canDisconnect()
	{
		return javaTarget.canDisconnect();
	}
	
	@Override
	public void disconnect() throws DebugException
	{
		javaTarget.disconnect();
	}
	
	@Override
	public boolean isDisconnected()
	{
		return javaTarget.isDisconnected();
	}
	
	@Override
	public boolean supportsStorageRetrieval()
	{
		return javaTarget.supportsStorageRetrieval();
	}
	
	@Override
	public IMemoryBlock getMemoryBlock( long startAddress, long length ) throws DebugException
	{
		return javaTarget.getMemoryBlock( startAddress, length );
	}
	
	@Override
	public IProcess getProcess()
	{
		return javaTarget.getProcess();
	}
	
	@Override
	public IThread[] getThreads() throws DebugException
	{
		return eglThreads.toArray( new EGLJavaThread[ eglThreads.size() ] );
	}
	
	@Override
	public boolean hasThreads() throws DebugException
	{
		return getThreads().length != 0;
	}
	
	@Override
	public String getName() throws DebugException
	{
		return javaTarget.getName();
	}
	
	@Override
	public boolean supportsBreakpoint( IBreakpoint breakpoint )
	{
		return javaTarget.supportsBreakpoint( breakpoint );
	}
	
	protected EGLJavaThread getThread( IJavaThread javaThread )
	{
		EGLJavaThread eglThread = threads.get( javaThread );
		if ( eglThread == null )
		{
			eglThread = new EGLJavaThread( this, javaThread );
			threads.put( javaThread, eglThread );
			eglThreads.add( eglThread );
		}
		return eglThread;
	}
	
	EGLJavaThread removeThread( IJavaThread javaThread )
	{
		EGLJavaThread eglThread = threads.remove( javaThread );
		if ( eglThread != null )
		{
			eglThreads.remove( eglThread );
			return eglThread;
		}
		return null;
	}
	
	@Override
	public DebugEvent[] filterDebugEvents( DebugEvent[] events )
	{
		if ( events == null || events.length < 1 )
		{
			return events;
		}
		
		Object src = events[ 0 ].getSource();
		if ( !(src instanceof IDebugElement) )
		{
			return events;
		}
		
		// We only care about events for the Java target we're wrapping.
		if ( ((IDebugElement)src).getDebugTarget() != javaTarget )
		{
			return events;
		}
		
		List<DebugEvent> unfiltered = new ArrayList<DebugEvent>( events.length );
		Map<Object, List<DebugEvent>> groupedEvents = groupBySource( events );
		for ( Iterator<Entry<Object, List<DebugEvent>>> it = groupedEvents.entrySet().iterator(); it.hasNext(); )
		{
			Entry<Object, List<DebugEvent>> entry = it.next();
			src = entry.getKey();
			List<DebugEvent> srcEvents = entry.getValue();
			
			if ( src instanceof IDebugTarget )
			{
				handleDebugEvents( srcEvents.toArray( new DebugEvent[ srcEvents.size() ] ) );
			}
			else if ( src instanceof IThread )
			{
				IJavaThread javaThread = (IJavaThread)((IThread)src).getAdapter( IJavaThread.class );
				if ( javaThread != null )
				{
					EGLJavaThread eglThread = getThread( javaThread );
					eglThread.handleDebugEvents( srcEvents.toArray( new DebugEvent[ srcEvents.size() ] ) );
				}
				else
				{
					unfiltered.addAll( srcEvents );
				}
			}
			else if ( src instanceof IStackFrame )
			{
				IJavaThread javaThread = (IJavaThread)((IStackFrame)src).getThread().getAdapter( IJavaThread.class );
				if ( javaThread != null )
				{
					EGLJavaThread eglThread = getThread( javaThread );
					eglThread.handleDebugEvents( srcEvents.toArray( new DebugEvent[ srcEvents.size() ] ) );
				}
				else
				{
					unfiltered.addAll( srcEvents );
				}
			}
			else
			{
				unfiltered.addAll( srcEvents );
			}
		}
		return unfiltered.toArray( new DebugEvent[ unfiltered.size() ] );
	}
	
	@Override
	public void handleDebugEvents( DebugEvent[] events )
	{
		if ( events == null || events.length == 0 )
		{
			return;
		}
		
		if ( events[ 0 ].getSource() == javaTarget )
		{
			if ( events[ 0 ].getKind() == DebugEvent.TERMINATE )
			{
				DebugPlugin.getDefault().removeDebugEventFilter( this );
			}
			super.handleDebugEvents( events );
		}
	}
	
	/**
	 * @return the underlying debug target.
	 */
	public IJavaDebugTarget getJavaDebugTarget()
	{
		return javaTarget;
	}
	
	/**
	 * @return the underlying Java element.
	 */
	@Override
	public Object getJavaElement()
	{
		return getJavaDebugTarget();
	}
}
