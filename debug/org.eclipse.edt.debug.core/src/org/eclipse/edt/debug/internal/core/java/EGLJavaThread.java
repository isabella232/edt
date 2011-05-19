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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;

/**
 * Wraps an IJavaThread.
 */
public class EGLJavaThread extends EGLJavaDebugElement implements IEGLThread
{
	/**
	 * The underlying Java thread.
	 */
	private final IJavaThread javaThread;
	
	/**
	 * The EGL-wrapped stack frames.
	 */
	private EGLJavaStackFrame[] eglFrames;
	
	/**
	 * The previous Java stack frames that were wrapped.
	 */
	private IStackFrame[] previousJavaFrames;
	
	public EGLJavaThread( IDebugTarget target, IJavaThread thread )
	{
		super( target );
		javaThread = thread;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IThread.class || adapter == EGLJavaThread.class || adapter == IEGLThread.class )
		{
			return this;
		}
		if ( adapter == IStackFrame.class || adapter == EGLJavaStackFrame.class || adapter == IEGLStackFrame.class )
		{
			try
			{
				return getTopStackFrame();
			}
			catch ( DebugException e )
			{
			}
		}
		if ( adapter == IJavaThread.class )
		{
			return javaThread;
		}
		
		return super.getAdapter( adapter );
	}
	
	@Override
	public boolean canResume()
	{
		return javaThread.canResume();
	}
	
	@Override
	public boolean canSuspend()
	{
		return javaThread.canSuspend();
	}
	
	@Override
	public boolean isSuspended()
	{
		return javaThread.isSuspended();
	}
	
	@Override
	public void resume() throws DebugException
	{
		javaThread.resume();
	}
	
	@Override
	public void suspend() throws DebugException
	{
		javaThread.suspend();
	}
	
	@Override
	public boolean canStepInto()
	{
		return javaThread.canStepInto();
	}
	
	@Override
	public boolean canStepOver()
	{
		return javaThread.canStepOver();
	}
	
	@Override
	public boolean canStepReturn()
	{
		return javaThread.canStepReturn();
	}
	
	@Override
	public boolean isStepping()
	{
		return javaThread.isStepping();
	}
	
	@Override
	public void stepInto() throws DebugException
	{
		javaThread.stepInto();
	}
	
	@Override
	public void stepOver() throws DebugException
	{
		javaThread.stepOver();
	}
	
	@Override
	public void stepReturn() throws DebugException
	{
		javaThread.stepReturn();
	}
	
	@Override
	public boolean canTerminate()
	{
		return javaThread.canTerminate();
	}
	
	@Override
	public boolean isTerminated()
	{
		return javaThread.isTerminated();
	}
	
	@Override
	public void terminate() throws DebugException
	{
		javaThread.terminate();
	}
	
	@Override
	public IStackFrame[] getStackFrames() throws DebugException
	{
		boolean recompute = true;
		IStackFrame[] javaFrames = javaThread.getStackFrames();
		if ( previousJavaFrames != null )
		{
			if ( javaFrames.length == previousJavaFrames.length )
			{
				recompute = false;
				for ( int i = 0; i < javaFrames.length; i++ )
				{
					if ( javaFrames[ i ] != previousJavaFrames[ i ] )
					{
						recompute = true;
						break;
					}
				}
			}
		}
		
		if ( recompute )
		{
			EGLJavaStackFrame[] newEGLFrames = new EGLJavaStackFrame[ javaFrames.length ];
			for ( int i = 0; i < javaFrames.length; i++ )
			{
				if ( eglFrames != null )
				{
					for ( int j = 0; j < eglFrames.length; j++ )
					{
						if ( eglFrames[ j ].getJavaStackFrame() == javaFrames[ i ] )
						{
							// Reuse this frame.
							newEGLFrames[ i ] = eglFrames[ j ];
							break;
						}
					}
				}
				
				if ( newEGLFrames[ i ] == null )
				{
					newEGLFrames[ i ] = new EGLJavaStackFrame( (IJavaStackFrame)javaFrames[ i ], this );
				}
			}
			previousJavaFrames = javaFrames;
			eglFrames = newEGLFrames;
		}
		
		return eglFrames;
	}
	
	@Override
	public boolean hasStackFrames() throws DebugException
	{
		return javaThread.hasStackFrames();
	}
	
	@Override
	public int getPriority() throws DebugException
	{
		return javaThread.getPriority();
	}
	
	@Override
	public IStackFrame getTopStackFrame() throws DebugException
	{
		IStackFrame[] frames = getStackFrames();
		if ( frames.length > 0 )
		{
			return frames[ 0 ];
		}
		return null;
	}
	
	@Override
	public String getName() throws DebugException
	{
		return javaThread.getName();
	}
	
	@Override
	public IBreakpoint[] getBreakpoints()
	{
		return javaThread.getBreakpoints();
	}
	
	@Override
	public void handleDebugEvents( DebugEvent[] events )
	{
		if ( events == null || events.length == 0 )
		{
			return;
		}
		
		Object src = events[ 0 ].getSource();
		if ( src == javaThread )
		{
			for ( int i = 0; i < events.length; i++ )
			{
				if ( src instanceof IThread )
				{
					switch ( events[ i ].getKind() )
					{
						case DebugEvent.CREATE:
							fireCreationEvent();
							break;
						case DebugEvent.TERMINATE:
							getEGLJavaDebugTarget().removeThread( javaThread );
							fireTerminateEvent();
							break;
						default:
							super.handleDebugEvents( events );
							break;
					}
				}
			}
		}
	}
	
	/**
	 * @return the underlying thread.
	 */
	public IJavaThread getJavaThread()
	{
		return javaThread;
	}
	
	/**
	 * @return the underlying Java element.
	 */
	@Override
	public Object getJavaElement()
	{
		return getJavaThread();
	}
}
