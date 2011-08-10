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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaThread;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;

/**
 * Wraps an IJavaThread.
 */
public class EGLJavaThread extends EGLJavaDebugElement implements IEGLJavaThread
{
	private static final boolean FILTER_RUNTIMES = !System.getProperty( "edt.debug.filter.runtimes", "yes" ).equalsIgnoreCase( "false" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	private static final EGLJavaStackFrame[] EMPTY_FRAMES = new EGLJavaStackFrame[ 0 ];
	
	/**
	 * The underlying Java thread.
	 */
	private final IJavaThread javaThread;
	
	/**
	 * The EGL-wrapped stack frames.
	 */
	private EGLJavaStackFrame[] eglFrames;
	
	/**
	 * The previous Java frames.
	 */
	private IStackFrame[] previousJavaFrames;
	
	/**
	 * The current stack frames.
	 */
	private Map<IStackFrame, EGLJavaStackFrame> currentStackFrames;
	
	/**
	 * The previous stack frames.
	 */
	private Map<IStackFrame, EGLJavaStackFrame> previousStackFrames;
	
	/**
	 * Constructor.
	 * 
	 * @param target The debug target.
	 * @param thread The Java thread.
	 */
	public EGLJavaThread( IDebugTarget target, IJavaThread thread )
	{
		super( target );
		javaThread = thread;
		disposeStackFrames();
	}
	
	protected void disposeStackFrames()
	{
		if ( currentStackFrames != null && currentStackFrames != Collections.EMPTY_MAP )
		{
			previousStackFrames = currentStackFrames;
		}
		currentStackFrames = null;
		eglFrames = null;
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IThread.class || adapter == EGLJavaThread.class || adapter == IEGLThread.class || adapter == IEGLJavaThread.class )
		{
			return this;
		}
		if ( adapter == IStackFrame.class || adapter == EGLJavaStackFrame.class || adapter == IEGLStackFrame.class
				|| adapter == IEGLJavaStackFrame.class )
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
		refreshFrames();
		return eglFrames;
	}
	
	protected synchronized void refreshFrames() throws DebugException
	{
		if ( eglFrames == null )
		{
			IStackFrame[] javaFrames = javaThread.getStackFrames();
			if ( javaFrames.length > 0 )
			{
				int size = 0;
				EGLJavaStackFrame[] newEGLFrames = new EGLJavaStackFrame[ javaFrames.length ];
				currentStackFrames = new HashMap<IStackFrame, EGLJavaStackFrame>( javaFrames.length );
				for ( int i = 0; i < javaFrames.length; i++ )
				{
					// Filtering rules:
					// 1. don't filter anything if the system property is set
					// 2. don't filter the top frame
					// 3. filter the initial main method (if there is one - there won't be if running on a server)
					if ( !FILTER_RUNTIMES
							|| (i == 0 || (!filterFrameType( (IJavaStackFrame)javaFrames[ i ] ) && (i + 1 < javaFrames.length || !isMainMethod( (IJavaStackFrame)javaFrames[ i ] )))) )
					{
						EGLJavaStackFrame frame = previousStackFrames == null
								? null
								: previousStackFrames.get( javaFrames[ i ] );
						if ( frame == null )
						{
							frame = new EGLJavaStackFrame( (IJavaStackFrame)javaFrames[ i ], this );
						}
						else
						{
							frame.bind( (IJavaStackFrame)javaFrames[ i ] );
						}
						currentStackFrames.put( javaFrames[ i ], frame );
						newEGLFrames[ size++ ] = frame;
					}
				}
				
				if ( size == newEGLFrames.length )
				{
					eglFrames = newEGLFrames;
				}
				else
				{
					eglFrames = new EGLJavaStackFrame[ size ];
					System.arraycopy( newEGLFrames, 0, eglFrames, 0, size );
				}
			}
			else
			{
				currentStackFrames = Collections.EMPTY_MAP;
				eglFrames = EMPTY_FRAMES;
			}
			previousJavaFrames = javaFrames;
		}
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
		
		List<DebugEvent> savedEvents = new ArrayList<DebugEvent>();
		Object src = events[ 0 ].getSource();
		if ( src == javaThread )
		{
			for ( int i = 0; i < events.length; i++ )
			{
				switch ( events[ i ].getKind() )
				{
					case DebugEvent.CREATE:
						fireCreationEvent();
						break;
					case DebugEvent.TERMINATE:
						EGLJavaDebugTarget target = getEGLJavaDebugTarget();
						if ( target != null )
						{
							target.removeThread( javaThread );
						}
						fireTerminateEvent();
						break;
					case DebugEvent.RESUME:
						if ( FILTER_RUNTIMES && events[ i ].getDetail() == DebugEvent.STEP_INTO )
						{
							IStackFrame topJavaFrame = previousJavaFrames == null || previousJavaFrames.length == 0
									? null
									: previousJavaFrames[ 0 ];
							EGLJavaStackFrame topEGLFrame = eglFrames == null || eglFrames.length == 0
									? null
									: eglFrames[ 0 ];
							
							if ( topEGLFrame != null && topEGLFrame.getJavaStackFrame() == topJavaFrame )
							{
								try
								{
									topEGLFrame.setLineBeforeStepInto( topEGLFrame.getLineNumber() );
								}
								catch ( DebugException e )
								{
									topEGLFrame.setLineBeforeStepInto( -1 );
								}
							}
						}
						savedEvents.add( events[ i ] );
						break;
					case DebugEvent.SUSPEND:
						if ( FILTER_RUNTIMES && events[ i ].getDetail() == DebugEvent.STEP_END )
						{
							try
							{
								IJavaStackFrame topJavaFrame = (IJavaStackFrame)javaThread.getTopStackFrame();
								if ( topJavaFrame != null )
								{
									if ( shouldStepInto( topJavaFrame ) )
									{
										topJavaFrame.stepInto();
										break;
									}
									else if ( shouldStepReturn( topJavaFrame ) )
									{
										topJavaFrame.stepReturn();
										break;
									}
									else if ( javaThread.getFrameCount() == 1 && isMainMethod( topJavaFrame ) )
									{
										// Don't step into the initial main frame - there's no EGL source for it, users will be confused.
										// Note: stepReturn isn't supported on a bottom frame in JDT so we must use resume.
										topJavaFrame.resume();
										break;
									}
									else
									{
										// If we forced a return after a step into, we might be at the same line as before.
										// If we're at the same line, do another step into so the user isn't confused.
										EGLJavaStackFrame topEGLFrame = (EGLJavaStackFrame)getTopStackFrame();
										if ( topEGLFrame != null && topEGLFrame.getJavaStackFrame() == topJavaFrame
												&& topEGLFrame.getLineBeforeStepInto() != -1
												&& topEGLFrame.getLineBeforeStepInto() == topEGLFrame.getLineNumber() )
										{
											stepInto();
											break;
										}
									}
								}
							}
							catch ( DebugException de )
							{
								// We tried, we failed, but we carry on...
							}
						}
						disposeStackFrames();
						savedEvents.add( events[ i ] );
						break;
					default:
						savedEvents.add( events[ i ] );
						break;
				}
			}
		}
		
		if ( savedEvents.size() > 0 )
		{
			super.handleDebugEvents( savedEvents.toArray( new DebugEvent[ savedEvents.size() ] ) );
		}
	}
	
	private boolean filterFrameType( IJavaStackFrame frame ) throws DebugException
	{
		return shouldStepInto( frame ) || shouldStepReturn( frame );
	}
	
	private boolean shouldStepReturn( IJavaStackFrame frame ) throws DebugException
	{
		// TODO make this dynamic/extensible. For now just include JRE packages.
		String type = frame.getDeclaringTypeName();
		return type.startsWith( "java." ) || type.startsWith( "javax." ) || type.startsWith( "com.ibm." ) || type.startsWith( "com.sun." ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				|| type.startsWith( "sun." ) || type.startsWith( "org.apache." ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private boolean shouldStepInto( IJavaStackFrame frame ) throws DebugException
	{
		// TODO make this dynamic/extensible. For now just include EDT runtime packages.
		String type = frame.getDeclaringTypeName();
		return type.startsWith( "org.eclipse.edt." ) || type.startsWith( "egl." ) || type.startsWith( "eglx." ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	private boolean isMainMethod( IJavaStackFrame frame ) throws DebugException
	{
		return frame.isStatic() && "main".equals( frame.getName() ) && "([Ljava/lang/String;)V".equals( frame.getSignature() ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public IJavaThread getJavaThread()
	{
		return javaThread;
	}
	
	@Override
	public Object getJavaDebugElement()
	{
		return getJavaThread();
	}
}
