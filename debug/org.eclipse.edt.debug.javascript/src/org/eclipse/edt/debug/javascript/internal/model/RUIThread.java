/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.edt.debug.core.breakpoints.EGLBreakpoint;
import org.eclipse.osgi.util.NLS;

public class RUIThread extends RUIDebugElement implements IEGLThread
{
	/**
	 * Breakpoints this thread is suspended at or <code>null</code> if none.
	 */
	private IBreakpoint[] fBreakpoints;
	
	/**
	 * Whether this thread is stepping
	 */
	private boolean fStepping = false;
	
	/**
	 * Constructs a new thread for the given target
	 * 
	 * @param target VM
	 */
	public RUIThread( RUIDebugTarget target )
	{
		super( target );
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == RUIThread.class || adapter == IThread.class || adapter == IEGLThread.class )
		{
			return this;
		}
		if ( adapter == RUIStackFrame.class || adapter == IStackFrame.class || adapter == IEGLStackFrame.class )
		{
			return getTopStackFrame();
		}
		return super.getAdapter( adapter );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	@Override
	public IStackFrame[] getStackFrames()
	{
		if ( isSuspended() )
		{
			return ((RUIDebugTarget)getDebugTarget()).getStackFrames();
		}
		else
		{
			return new IStackFrame[ 0 ];
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	@Override
	public boolean hasStackFrames() throws DebugException
	{
		return !isTerminated() && isSuspended();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	@Override
	public int getPriority() throws DebugException
	{
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	@Override
	public IStackFrame getTopStackFrame()
	{
		IStackFrame[] frames = getStackFrames();
		if ( frames.length > 0 )
		{
			return frames[ 0 ];
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	@Override
	public String getName() throws DebugException
	{
		String threadName = RUIDebugMessages.rui_thread_thread_name;
		String label = null;
		if ( isTerminated() )
		{
			label = NLS.bind( RUIDebugMessages.rui_thread_label_terminated, threadName );
		}
		else if ( isStepping() )
		{
			label = NLS.bind( RUIDebugMessages.rui_thread_label_stepping, threadName );
		}
		else if ( isSuspended() )
		{
			IBreakpoint breakpoint = getCurrentUserBreakpoint();
			if ( breakpoint == null )
			{
				label = NLS.bind( RUIDebugMessages.rui_thread_label_suspended, threadName );
			}
			else
			{
				IMarker marker = breakpoint.getMarker();
				int line = marker.getAttribute( IMarker.LINE_NUMBER, -1 );
				String[] args = new String[ 3 ];
				args[ 0 ] = threadName;
				args[ 1 ] = Integer.toString( line );
				args[ 2 ] = marker.getResource().getName();
				label = NLS.bind( RUIDebugMessages.rui_thread_label_suspendedAtBreakpoint, args );
			}
		}
		else
		{
			label = NLS.bind( RUIDebugMessages.rui_thread_label_running, threadName );
		}
		return label;
	}
	
	/**
	 * Get the current user breakpoint.
	 * 
	 * @return An EGLBreakpoint.
	 */
	protected EGLBreakpoint getCurrentUserBreakpoint()
	{
		if ( fBreakpoints != null && fBreakpoints.length != 0 )
		{
			return (EGLBreakpoint)fBreakpoints[ 0 ];
		}
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	@Override
	public IBreakpoint[] getBreakpoints()
	{
		if ( fBreakpoints == null )
		{
			return new IBreakpoint[ 0 ];
		}
		return fBreakpoints;
	}
	
	/**
	 * Sets the breakpoints this thread is suspended at, or <code>null</code> if none.
	 * 
	 * @param breakpoints the breakpoints this thread is suspended at, or <code>null</code> if none
	 */
	public void setBreakpoints( IBreakpoint[] breakpoints )
	{
		fBreakpoints = breakpoints;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	@Override
	public boolean canResume()
	{
		return getDebugTarget().canResume();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	@Override
	public boolean canSuspend()
	{
		return getDebugTarget().canSuspend();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	@Override
	public boolean isSuspended()
	{
		return getDebugTarget().isSuspended();
	}
	
	/*
	 * @Override (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	@Override
	public void resume() throws DebugException
	{
		fBreakpoints = null;
		getDebugTarget().resume();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	@Override
	public void suspend() throws DebugException
	{
		getDebugTarget().suspend();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	@Override
	public boolean canStepInto()
	{
		return isSuspended() && !((RUIDebugTarget)getDebugTarget()).isTerminating();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	@Override
	public boolean canStepOver()
	{
		return isSuspended() && !((RUIDebugTarget)getDebugTarget()).isTerminating();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	@Override
	public boolean canStepReturn()
	{
		return isSuspended() && !((RUIDebugTarget)getDebugTarget()).isTerminating();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	@Override
	public boolean isStepping()
	{
		return fStepping;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	@Override
	public void stepInto() throws DebugException
	{
		fBreakpoints = null;
		((RUIDebugTarget)getDebugTarget()).stepIn();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	@Override
	public void stepOver() throws DebugException
	{
		stepOver( (RUIStackFrame)getTopStackFrame() );
	}
	
	protected void stepOver( RUIStackFrame frame )
	{
		fBreakpoints = null;
		((RUIDebugTarget)getDebugTarget()).stepOver( frame );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	@Override
	public void stepReturn() throws DebugException
	{
		stepReturn( (RUIStackFrame)getTopStackFrame() );
	}
	
	protected void stepReturn( RUIStackFrame frame )
	{
		fBreakpoints = null;
		((RUIDebugTarget)getDebugTarget()).stepOut( frame );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	@Override
	public boolean canTerminate()
	{
		return getDebugTarget().canTerminate();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	@Override
	public boolean isTerminated()
	{
		return getDebugTarget().isTerminated();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	@Override
	public void terminate() throws DebugException
	{
		getDebugTarget().terminate();
	}
	
	/**
	 * Sets whether this thread is stepping
	 * 
	 * @param stepping whether stepping
	 */
	protected void setStepping( boolean stepping )
	{
		fStepping = stepping;
	}
}
