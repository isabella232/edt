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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.edt.debug.core.IEGLStackFrame;
import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaThread;
import org.eclipse.edt.debug.core.java.SMAPLineInfo;
import org.eclipse.edt.debug.core.java.SMAPUtil;
import org.eclipse.edt.debug.core.java.filters.FilterStepType;
import org.eclipse.edt.debug.core.java.filters.ITypeFilter;
import org.eclipse.edt.debug.core.java.filters.TypeFilterUtil;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;

/**
 * Wraps an IJavaThread.
 */
public class EGLJavaThread extends EGLJavaDebugElement implements IEGLJavaThread
{
	/**
	 * Set this to true to enable debug output when steps are forced due to filtering.
	 */
	private static final boolean TRACE_FILTERS = false;
	
	private static final EGLJavaStackFrame[] EMPTY_FRAMES = {};
	
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
	 * The evaluation lock.
	 */
	private final Object evaluationLock;
	
	/**
	 * The most recent location from where the user issued a step request (i.e. an unfiltered frame)
	 */
	private IJavaStackFrame stepStartFrame;
	
	/**
	 * Constructor.
	 * 
	 * @param target The debug target.
	 * @param thread The Java thread.
	 */
	public EGLJavaThread( EGLJavaDebugTarget target, IJavaThread thread )
	{
		super( target );
		evaluationLock = new Object();
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
		setStepStart( null );
		javaThread.stepInto();
	}
	
	@Override
	public void stepOver() throws DebugException
	{
		setStepStart( null );
		javaThread.stepOver();
	}
	
	@Override
	public void stepReturn() throws DebugException
	{
		setStepStart( null );
		javaThread.stepReturn();
	}
	
	public void setStepStart( IJavaStackFrame frame )
	{
		if ( frame == null )
		{
			frame = eglFrames == null || eglFrames.length == 0
					? null
					: eglFrames[ 0 ].getJavaStackFrame();
		}
		
		stepStartFrame = frame;
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
				
				int indexOfTopEGLFrame = -1;
				for ( int i = 0; i < javaFrames.length; i++ )
				{
					if ( SMAPUtil.isEGLStratum( (IJavaStackFrame)javaFrames[ i ], getEGLJavaDebugTarget() ) )
					{
						indexOfTopEGLFrame = i;
						break;
					}
				}
				
				boolean doNotFilterRuntimes = !getEGLJavaDebugTarget().filterRuntimes() || indexOfTopEGLFrame == -1;
				for ( int i = 0; i < javaFrames.length; i++ )
				{
					// Filtering rules:
					// 1. don't filter anything if the preference is disabled (see doNotFilterRuntimes flag)
					// 2. don't filter if there are no frames with the EGL stratum (see doNotFilterRuntimes flag)
					// 3. don't filter the top frame
					// 4. filter the initial main method (if there is one - there won't be if running on a server)
					// 5. if we're disabling filtering due to a breakpoint in the runtime, don't filter frames above the topmost frame w/EGL stratum
					if ( doNotFilterRuntimes
							|| i == 0
							|| indexOfTopEGLFrame >= i
							|| (!filterFromStack( (IJavaStackFrame)javaFrames[ i ] ) && (i + 1 < javaFrames.length || !isMainMethod( (IJavaStackFrame)javaFrames[ i ] ))) )
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
				currentStackFrames = Collections.emptyMap();
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
			// While looping over the events, it's possible we have both a STEP_END and a BREAKPOINT details. If there's
			// a BREAKPOINT suspend event then we do not want to do any filtering. Check it first since it usually (always?) is listed second.
			boolean bpHit = false;
			
			for ( int i = 0; i < events.length; i++ )
			{
				if ( events[ i ].getDetail() == DebugEvent.BREAKPOINT && events[ i ].getKind() == DebugEvent.SUSPEND )
				{
					bpHit = true;
					break;
				}
			}
			
			for ( int i = 0; i < events.length; i++ )
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
					case DebugEvent.RESUME:
						if ( getEGLJavaDebugTarget().filterRuntimes() && events[ i ].getDetail() == DebugEvent.STEP_INTO )
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
						if ( !bpHit && getEGLJavaDebugTarget().filterRuntimes() && events[ i ].getDetail() == DebugEvent.STEP_END )
						{
							try
							{
								IJavaStackFrame topJavaFrame = (IJavaStackFrame)javaThread.getTopStackFrame();
								if ( topJavaFrame != null )
								{
									int frameCount = javaThread.getFrameCount();
									if ( frameCount == 1 && isMainMethod( topJavaFrame ) )
									{
										// Don't step into the initial main frame - there's no EGL source for it, users will be confused.
										// Note: stepReturn isn't supported on a bottom frame in JDT so we must use resume.
										topJavaFrame.resume();
										break;
									}
									else
									{
										// Filter strategy: If the type is EGL and its line number is -1, force a step into. If it's not EGL then
										// check if it should be filtered. If it should be filtered and the original location where the user issued
										// the (unfiltered) step request is still in the stack, filter back to that point and then stop. If the
										// original location is no longer in the stack (user explicitly stepped out of that frame) then: if the
										// location is not filtered, we stop at it; if it is filtered and there are no other EGL stratum frames then
										// we will perform a resume instead of a step; otherwise if there is a valid EGL stratum we step to it.
										// This is not ideal but this avoids cases where we infinitely run steps due to some loop like an event loop
										// that sits around forever; the infinite stepping causes flickering and poor performance.
										
										FilterStepType filterType = FilterStepType.NO_STEP;
										if ( SMAPUtil.isEGLStratum( topJavaFrame, getEGLJavaDebugTarget() ) )
										{
											// Can be some internal method like ezeSetEmpty.
											if ( getLineNumber( topJavaFrame ) == -1 )
											{
												filterType = FilterStepType.STEP_INTO;
											}
										}
										else if ( stepStartFrame != topJavaFrame )
										{
											filterType = filter( topJavaFrame );
										}
										
										if ( filterType == FilterStepType.STEP_INTO || filterType == FilterStepType.STEP_RETURN )
										{
											boolean forceResume = true;
											IStackFrame[] frames = javaThread.getStackFrames();
											for ( int j = 0; j < frames.length; j++ )
											{
												// If we find the starting step frame OR we have a valid EGL stratum, perform a step.
												if ( frames[ j ] == stepStartFrame
														|| (SMAPUtil.isEGLStratum( (IJavaStackFrame)frames[ j ], getEGLJavaDebugTarget() ) && getLineNumber( (IJavaStackFrame)frames[ j ] ) != -1) )
												{
													forceResume = false;
													break;
												}
											}
											
											if ( forceResume )
											{
												if ( TRACE_FILTERS )
												{
													System.out
															.println( "EDT DEBUG: Forcing resume for frame " + topJavaFrame.getReferenceType().getName() //$NON-NLS-1$
																	+ "." + topJavaFrame.getName() ); //$NON-NLS-1$
												}
												topJavaFrame.resume();
											}
											else if ( filterType == FilterStepType.STEP_INTO )
											{
												if ( TRACE_FILTERS )
												{
													System.out.println( "EDT DEBUG: Forcing stepInto for frame " //$NON-NLS-1$
															+ topJavaFrame.getReferenceType().getName() + "." + topJavaFrame.getName() ); //$NON-NLS-1$
												}
												topJavaFrame.stepInto();
											}
											else if ( topJavaFrame.canStepReturn() )
											{
												if ( TRACE_FILTERS )
												{
													System.out.println( "EDT DEBUG: Forcing stepReturn for frame " //$NON-NLS-1$
															+ topJavaFrame.getReferenceType().getName() + "." + topJavaFrame.getName() ); //$NON-NLS-1$
												}
												topJavaFrame.stepReturn();
											}
											else
											{
												// Resume when we can't step return (e.g. bottom frame, or above obsolete frame)
												if ( TRACE_FILTERS )
												{
													System.out.println( "EDT DEBUG: Forcing resume (stepReturn not supported) for frame " //$NON-NLS-1$
															+ topJavaFrame.getReferenceType().getName() + "." + topJavaFrame.getName() ); //$NON-NLS-1$
												}
												topJavaFrame.resume();
											}
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
												if ( TRACE_FILTERS )
												{
													System.out.println( "EDT DEBUG: Forcing stepInto for EGL frame " //$NON-NLS-1$
															+ topEGLFrame.getJavaStackFrame().getReferenceType().getName() + "." //$NON-NLS-1$
															+ topEGLFrame.getJavaStackFrame().getName() );
												}
												stepInto();
												break;
											}
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
	
	private boolean filterFromStack( IJavaStackFrame frame ) throws DebugException
	{
		if ( SMAPUtil.isEGLStratum( frame, getEGLJavaDebugTarget() ) )
		{
			return getLineNumber( frame ) == -1;
		}
		
		return filter( frame ) != FilterStepType.NO_STEP;
	}
	
	private int getLineNumber( IJavaStackFrame frame ) throws DebugException
	{
		if ( !getEGLJavaDebugTarget().supportsSourceDebugExtension() )
		{
			// Try our own JSR-45 support.
			String smap = SMAPUtil.getSMAP( getEGLJavaDebugTarget(), frame.getReferenceType().getName() );
			if ( SMAPUtil.isEGLStratum( smap ) )
			{
				SMAPLineInfo lineInfo = SMAPUtil.getSMAPLineInfo( smap, getEGLJavaDebugTarget().getSMAPLineCache() );
				if ( lineInfo != null )
				{
					return lineInfo.getEGLLine( frame.getLineNumber() );
				}
			}
		}
		return frame.getLineNumber();
	}
	
	private FilterStepType filter( IJavaStackFrame frame ) throws DebugException
	{
		EGLJavaDebugTarget target = getEGLJavaDebugTarget();
		for ( ITypeFilter filter : TypeFilterUtil.INSTANCE.getActiveFilters() )
		{
			if ( filter.filter( frame, target ) )
			{
				return filter.getCategory().getStepType( frame );
			}
		}
		
		return FilterStepType.NO_STEP;
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
	
	@Override
	public Object getEvaluationLock()
	{
		return evaluationLock;
	}
}
