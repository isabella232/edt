/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.edt.debug.core.IEGLVariable;
import org.eclipse.edt.debug.core.breakpoints.EGLBreakpoint;
import org.eclipse.edt.ide.debug.javascript.internal.server.DebugContext;
import org.eclipse.edt.ide.debug.javascript.internal.utils.RUIDebugUtil;
import org.eclipse.edt.ide.rui.server.EvServer;
import org.eclipse.osgi.util.NLS;

public class RUIDebugTarget extends RUIDebugElement implements IEGLDebugTarget, IBreakpointManagerListener, ISuspendResume
{
	
	public final static String VAR_VAR = "var_"; //$NON-NLS-1$
	public final static String VAR_VALUE = VAR_VAR + "Value"; //$NON-NLS-1$
	public final static String VAR_SET_VALUE = VAR_VAR + "SetValue"; //$NON-NLS-1$
	public final static String VAR_VARIABLES = VAR_VAR + "Variables"; //$NON-NLS-1$
	
	private static final IStackFrame[] EMPTY_FRAMES = {};
	
	private ILaunch fLaunch;
	private DebugContext context;
	private RUIThread fThread;
	private IThread[] fThreads;
	private boolean fTerminated;
	private boolean fTerminating;
	private boolean fSuspended;
	private boolean fSuspending;
	private List fStackFrames;
	private List fOldStackFrames;
	private String debugCmd;
	private boolean steppingIn;
	private LocaleInfo localeInfo;
	
	private IVariableQueryCompletion completion;
	private Object completionSynchObj = new Object();
	
	public RUIDebugTarget( ILaunch launch )
	{
		super( null );
		
		fLaunch = launch;
		initialize();
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == RUIDebugTarget.class || adapter == IEGLDebugTarget.class || adapter == IDebugTarget.class )
		{
			return this;
		}
		return super.getAdapter( adapter );
	}
	
	private void initialize()
	{
		fThread = new RUIThread( this );
		fThreads = new IThread[] { fThread };
		
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener( this );
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointManagerListener( this );
		
		fireCreationEvent();
	}
	
	public void setContext( DebugContext context )
	{
		this.context = context;
	}
	
	@Override
	public String getName() throws DebugException
	{
		return NLS.bind( RUIDebugMessages.rui_debug_target_label,
				new Object[] { getClass().getName(), "localhost", Integer.valueOf( EvServer.getInstance().getPortNumber() ) } ); //$NON-NLS-1$
	}
	
	@Override
	public IProcess getProcess()
	{
		return null;
	}
	
	@Override
	public ILaunch getLaunch()
	{
		return fLaunch;
	}
	
	RUIThread getCurrentThread()
	{
		return (RUIThread)fThreads[ 0 ];
	}
	
	@Override
	public IThread[] getThreads() throws DebugException
	{
		return fThreads;
	}
	
	@Override
	public boolean hasThreads() throws DebugException
	{
		return true;
	}
	
	@Override
	public boolean supportsBreakpoint( IBreakpoint breakpoint )
	{
		return breakpoint instanceof EGLBreakpoint;
	}
	
	@Override
	public boolean canTerminate()
	{
		return !isTerminated() && !isTerminating();
	}
	
	@Override
	public boolean isTerminated()
	{
		return fTerminated;
	}
	
	public boolean isTerminating()
	{
		return fTerminating;
	}
	
	@Override
	public void terminate() throws DebugException
	{
		fTerminating = true;
		if ( isSuspended() )
		{
			if ( !setDebugCommand( "disconnect" ) ) //$NON-NLS-1$
			{
				context.disconnectDebugger();
			}
		}
		else if ( context != null )
		{
			context.disconnectDebugger();
		}
		
		context.debugTerminationCleanup();
	}
	
	public void terminated()
	{
		fSuspending = false;
		fSuspended = false;
		fTerminating = false;
		fTerminated = true;
		steppingIn = false;
		try
		{
			fireTerminateEvent();
			fireEvent( new DebugEvent( this, DebugEvent.CHANGE ) );
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener( this );
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpointManagerListener( this );
		}
		catch ( Exception e )
		{
			// Can happen when workbench is closed before this target is terminated.
		}
	}
	
	public void windowClosed()
	{
		// If suspended at a breakpoint, let the atLine AJAX request be completed so that
		// the browser doesn't manually terminate it, flashing an error message in the
		// window.
		if ( !fTerminating && isSuspended() && debugCmd == null )
		{
			// do not call setDebugCmd - browser closing/refresh trumps everything else.
			debugCmd = "silentTerminate"; //$NON-NLS-1$
			
			int numWaits = 0;
			while ( debugCmd != null && numWaits < 20 )
			{
				try
				{
					Thread.sleep( 1 );
				}
				catch ( Exception e )
				{
				}
				numWaits++;
			}
		}
		terminated();
	}
	
	@Override
	public boolean canResume()
	{
		return !isTerminated() && !isTerminating() && isSuspended();
	}
	
	@Override
	public boolean canSuspend()
	{
		return !isSuspending() && !isSuspended() && !isTerminated();
	}
	
	@Override
	public boolean isSuspended()
	{
		return !isTerminated() && fSuspended;
	}
	
	public boolean isSuspending()
	{
		return !isTerminated() && !isTerminating() && fSuspending;
	}
	
	@Override
	public void breakpointAdded( IBreakpoint breakpoint )
	{
		if ( supportsBreakpoint( breakpoint ) && context != null )
		{
			IResource resource = RUIDebugUtil.getBreakpointResource( breakpoint );
			if ( resource != null )
			{
				String file = RUIDebugUtil.encodeValue( RUIDebugUtil.getRelativeBreakpointPath( resource ) );
				String line = Integer.toString( breakpoint.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 ) );
				String enabled = Boolean.toString( breakpoint.getMarker().getAttribute( IBreakpoint.ENABLED, true ) );
				
				if ( isSuspended() )
				{
					if ( !setDebugCommand( "addBreakpoint " + file + "," + line + "," + enabled ) ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					{
						context.addBreakpoint( file, line, enabled );
					}
				}
				else
				{
					context.addBreakpoint( file, line, enabled );
				}
			}
		}
	}
	
	@Override
	public void breakpointRemoved( IBreakpoint breakpoint, IMarkerDelta delta )
	{
		if ( supportsBreakpoint( breakpoint ) && context != null )
		{
			IResource resource = RUIDebugUtil.getBreakpointResource( breakpoint );
			if ( resource != null )
			{
				String file = RUIDebugUtil.encodeValue( RUIDebugUtil.getRelativeBreakpointPath( resource ) );
				String line = Integer.toString( breakpoint.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 ) );
				
				if ( isSuspended() )
				{
					if ( !setDebugCommand( "removeBreakpoint " + file + "," + line ) ) //$NON-NLS-1$ //$NON-NLS-2$
					{
						context.removeBreakpoint( file, line );
					}
				}
				else
				{
					context.removeBreakpoint( file, line );
				}
			}
		}
	}
	
	@Override
	public void breakpointChanged( IBreakpoint breakpoint, IMarkerDelta delta )
	{
		if ( supportsBreakpoint( breakpoint ) && context != null )
		{
			IMarker marker = breakpoint.getMarker();
			boolean currentEnable = marker.getAttribute( IBreakpoint.ENABLED, true );
			
			boolean oldEnable;
			int oldLine;
			if ( delta != null )
			{
				oldEnable = delta.getAttribute( IBreakpoint.ENABLED, true );
				oldLine = delta.getAttribute( IMarker.LINE_NUMBER, -1 );
			}
			else
			{
				// If there is no delta then need to assume that enablement state changed.
				oldEnable = !currentEnable;
				oldLine = marker.getAttribute( IMarker.LINE_NUMBER, -1 );
			}
			
			// We don't report changes to line numbers because RUI doesn't support hotswapping. The current generated
			// code being run will be using the old line numbers.
			if ( currentEnable != oldEnable )
			{
				IResource resource = RUIDebugUtil.getBreakpointResource( breakpoint );
				if ( resource != null )
				{
					String file = RUIDebugUtil.encodeValue( RUIDebugUtil.getRelativeBreakpointPath( resource ) );
					String line = Integer.toString( oldLine );
					String enabled = Boolean.toString( currentEnable );
					
					if ( isSuspended() )
					{
						if ( !setDebugCommand( "changeBreakpoint " + file + "," + line + "," + enabled ) ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						{
							context.changeBreakpoint( file, line, enabled );
						}
					}
					else
					{
						context.changeBreakpoint( file, line, enabled );
					}
				}
			}
		}
	}
	
	/**
	 * Called when Eclipse breakpoints as a whole are enabled or disabled.
	 */
	@Override
	public void breakpointManagerEnablementChanged( boolean enabled )
	{
		if ( context != null )
		{
			String state = Boolean.toString( enabled );
			if ( isSuspended() )
			{
				if ( !setDebugCommand( "breakpointEnablement " + state ) ) //$NON-NLS-1$
				{
					context.breakpointManagerChanged( state );
				}
			}
			else
			{
				context.breakpointManagerChanged( state );
			}
		}
	}
	
	/**
	 * Returns whether breakpoints have been enabled / disabled globally by Eclipse
	 * 
	 * @return true if breakpoint manager is enabled, false otherwise.
	 */
	protected boolean breakpointsEnabled()
	{
		return DebugPlugin.getDefault().getBreakpointManager().isEnabled();
	}
	
	@Override
	public boolean canDisconnect()
	{
		return false;
	}
	
	@Override
	public void disconnect() throws DebugException
	{
	}
	
	@Override
	public boolean isDisconnected()
	{
		return false;
	}
	
	@Override
	public IMemoryBlock getMemoryBlock( long startAddress, long length ) throws DebugException
	{
		return null;
	}
	
	@Override
	public boolean supportsStorageRetrieval()
	{
		return false;
	}
	
	public IStackFrame[] getStackFrames()
	{
		if ( !isSuspended() || fStackFrames == null || fStackFrames.size() == 0 )
		{
			return EMPTY_FRAMES;
		}
		return (IStackFrame[])fStackFrames.toArray( new IStackFrame[ fStackFrames.size() ] );
	}
	
	public void stepOver( RUIStackFrame frame )
	{
		steppingIn = false;
		setDebugCommand( "stepOver " + frame.getId() ); //$NON-NLS-1$
	}
	
	public void stepIn()
	{
		steppingIn = true;
		setDebugCommand( "stepIn" ); //$NON-NLS-1$
	}
	
	public void stepOut( RUIStackFrame frame )
	{
		steppingIn = false;
		setDebugCommand( "stepOut " + frame.getId() ); //$NON-NLS-1$
	}
	
	/**
	 * @return true if the command was set, false otherwise (meaning we're no longer suspended).
	 */
	private synchronized boolean setDebugCommand( String str )
	{
		while ( debugCmd != null )
		{
			// Don't clobber other commands, like step-over!
			try
			{
				if ( !isSuspended() )
				{
					// While the command was waiting, a prior command caused it to no longer be suspended.
					// We return false so that the terminate button will know the command wasn't set, so it must
					// use its alternate method of termination.
					return false;
				}
				Thread.sleep( 10 );
			}
			catch ( Exception e )
			{
			}
		}
		debugCmd = str;
		return true;
	}
	
	public IValue getVariableValue( final RUIVariable variable )
	{
		if ( !isSuspended() )
		{
			variable.getCurrValue().setValue( "" ); //$NON-NLS-1$
			return variable.getCurrValue();
		}
		
		synchronized ( completionSynchObj )
		{
			
			final String[] result = new String[ 2 ];
			completion = new IVariableQueryCompletion() {
				@Override
				public void completed( Object value )
				{
					if ( value instanceof String[] )
					{
						result[ 0 ] = ((String[])value)[ 0 ];
						result[ 1 ] = ((String[])value)[ 1 ];
					}
				}
				
				@Override
				public RUIVariable getVariable()
				{
					return variable;
				}
			};
			
			setDebugCommand( VAR_VALUE + " " + variable.getStackFrame().getId() + " " + variable.getIndex() ); //$NON-NLS-1$ //$NON-NLS-2$
			
			int timeLeft = 4000; // wait up to 4 seconds for a response.
			while ( result[ 0 ] == null )
			{
				try
				{
					if ( timeLeft == 0 )
					{
						// If we sit here waiting forever, we break the variables view.
						completion.completed( new String[] { "", "" } ); //$NON-NLS-1$ //$NON-NLS-2$
						debugCmd = null; // reset so new requests can be made.
					}
					else
					{
						Thread.sleep( 10 );
						timeLeft -= 10;
					}
				}
				catch ( Exception e )
				{
					result[ 0 ] = ""; //$NON-NLS-1$
				}
			}
			completion = null;
			variable.getCurrValue().setValue( result[ 0 ] );
			if ( result[ 1 ] != null && result[ 1 ].trim().length() != 0 )
			{
				variable.setType( result[ 1 ].trim() );
			}
		}
		
		return variable.getCurrValue();
	}
	
	public boolean setVariableValue( final RUIVariable variable, final String LHS, final String RHS, final String getter, final String setter )
	{
		if ( isSuspended() )
		{
			synchronized ( completionSynchObj )
			{
				final String[] result = new String[ 1 ];
				completion = new IVariableQueryCompletion() {
					@Override
					public void completed( Object value )
					{
						if ( value instanceof String )
						{
							result[ 0 ] = (String)value;
						}
					}
					
					@Override
					public RUIVariable getVariable()
					{
						return variable;
					}
				};
				
				setDebugCommand( VAR_SET_VALUE + " " + variable.getStackFrame().getId() + " " + variable.getIndex() + " " + LHS + " " + RHS + " " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						+ getter + " " + setter ); //$NON-NLS-1$
				
				int timeLeft = 4000; // wait up to 4 seconds for a response.
				while ( result[ 0 ] == null )
				{
					try
					{
						if ( timeLeft == 0 )
						{
							// If we sit here waiting forever, we break the variables view.
							completion.completed( "0" ); //$NON-NLS-1$
							debugCmd = null; // reset so new requests can be made.
						}
						else
						{
							Thread.sleep( 10 );
							timeLeft -= 10;
						}
					}
					catch ( Exception e )
					{
						result[ 0 ] = ""; //$NON-NLS-1$
					}
				}
				completion = null;
				if ( result[ 0 ] != null && result[ 0 ].length() != 0 && result[ 0 ].charAt( 0 ) == '1' )
				{
					// 1 means variable was successfully updated.
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void suspend() throws DebugException
	{
		if ( isSuspended() || context == null )
		{
			return;
		}
		fSuspending = true;
		fireEvent( new DebugEvent( this, DebugEvent.CHANGE ) );
	}
	
	public void suspend( String reason )
	{
		fSuspending = false;
		fSuspended = true;
		int detail = getSuspendDetail( reason );
		if ( detail != DebugEvent.STEP_END )
		{
			steppingIn = false;
		}
		fThread.fireSuspendEvent( detail );
		fireEvent( new DebugEvent( getCurrentThread(), DebugEvent.CHANGE ) );
		fireEvent( new DebugEvent( this, DebugEvent.CHANGE ) );
	}
	
	public IEGLVariable[] getVariables( final RUIVariable variable )
	{
		if ( !isSuspended() )
		{
			return new IEGLVariable[ 0 ];
		}
		
		final Object[] result;
		synchronized ( completionSynchObj )
		{
			
			result = new Object[ 1 ];
			completion = new IVariableQueryCompletion() {
				@Override
				public void completed( Object value )
				{
					if ( value instanceof IEGLVariable[] )
					{
						result[ 0 ] = value;
					}
				}
				
				@Override
				public RUIVariable getVariable()
				{
					return variable;
				}
			};
			
			setDebugCommand( VAR_VARIABLES + " " + variable.getStackFrame().getId() + " " + variable.getIndex() ); //$NON-NLS-1$ //$NON-NLS-2$
			
			int timeLeft = 4000; // wait up to 4 seconds for a response.
			while ( result[ 0 ] == null )
			{
				try
				{
					if ( timeLeft == 0 )
					{
						// If we sit here waiting forever, we break the variables view.
						completion.completed( new RUIVariable[ 0 ] );
						debugCmd = null; // reset so new requests can be made.
					}
					else
					{
						Thread.sleep( 10 );
						timeLeft -= 10;
					}
				}
				catch ( Exception e )
				{
					result[ 0 ] = new RUIVariable[ 0 ];
				}
			}
			completion = null;
		}
		return (IEGLVariable[])result[ 0 ];
	}
	
	private int getSuspendDetail( String reason )
	{
		if ( "breakpoint".equals( reason ) ) //$NON-NLS-1$
		{
			return DebugEvent.BREAKPOINT;
		}
		if ( "step".equals( reason ) ) //$NON-NLS-1$
		{
			return DebugEvent.STEP_END;
		}
		return 0;
	}
	
	@Override
	public void resume() throws DebugException
	{
		steppingIn = false;
		setDebugCommand( "resume" ); //$NON-NLS-1$
	}
	
	public void resume( String reason )
	{
		fSuspended = false;
		int detail = getResumeDetail( reason );
		if ( detail != DebugEvent.STEP_INTO )
		{
			steppingIn = false;
		}
		fThread.fireResumeEvent( detail );
		fireEvent( new DebugEvent( this, DebugEvent.CHANGE ) );
	}
	
	private int getResumeDetail( String reason )
	{
		if ( "clientRequest".equals( reason ) ) //$NON-NLS-1$
		{
			return DebugEvent.CLIENT_REQUEST;
		}
		if ( "stepOver".equals( reason ) ) //$NON-NLS-1$
		{
			return DebugEvent.STEP_OVER;
		}
		if ( "stepIn".equals( reason ) ) //$NON-NLS-1$
		{
			return DebugEvent.STEP_INTO;
		}
		if ( "stepOut".equals( reason ) ) //$NON-NLS-1$
		{
			return DebugEvent.STEP_RETURN;
		}
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	@Override
	public IDebugTarget getDebugTarget()
	{
		return this;
	}
	
	public void parseStack( String stackStr )
	{
		fStackFrames = new ArrayList();
		StringTokenizer stackStrings = new StringTokenizer( stackStr, "|" ); //$NON-NLS-1$
		while ( stackStrings.hasMoreTokens() )
		{
			String nextStack = stackStrings.nextToken();
			StringTokenizer stackProperties = new StringTokenizer( nextStack, "," ); //$NON-NLS-1$
			
			String nextFile = RUIDebugUtil.decodeValue( stackProperties.nextToken() );
			String nextFunctionName = stackProperties.nextToken();
			int nextLineNum = Integer.parseInt( stackProperties.nextToken() );
			int nextFrameId = Integer.parseInt( stackProperties.nextToken() );
			
			RUIStackFrame newFrame = new RUIStackFrame( getCurrentThread(), nextFrameId, RUIDebugUtil.getProgramNameFromFile( nextFile ), nextFile,
					nextFunctionName, nextLineNum );
			
			// process the variable info
			boolean first = true;
			while ( stackProperties.hasMoreTokens() )
			{
				String varName = stackProperties.nextToken();
				String jsName = stackProperties.nextToken();
				String getterName = stackProperties.nextToken();
				String setterName = stackProperties.nextToken();
				String varIndex = stackProperties.nextToken();
				String varType = getTypeFromTokenizer( stackProperties );
				boolean kids = Integer.parseInt( stackProperties.nextToken() ) != 0;
				RUIVariable var = new RUIVariable( newFrame, null, varName, jsName, getterName, setterName, first
						? "this" : varName, varIndex, varType, kids ); //$NON-NLS-1$
				newFrame.addVariable( var );
				first = false;
			}
			
			if ( (nextLineNum != -1 || fStackFrames.size() != 0) && (nextLineNum != -1 || !"<<undefined>>".equals( nextFile )) ) //$NON-NLS-1$
			{
				// There can be 1 or more "init" frames with no real line number. Skip them.
				fStackFrames.add( 0, newFrame );
			}
		}
		
		// Check if we can re-use any to save the variables.
		if ( fOldStackFrames != null && fOldStackFrames.size() != 0 )
		{
			for ( int i = 0, size = fStackFrames.size(); i < size; i++ )
			{
				RUIStackFrame newFrame = (RUIStackFrame)fStackFrames.get( i );
				RUIStackFrame oldFrame = findMatchingOldStackFrame( newFrame, size - i - 1 );
				if ( oldFrame != null )
				{
					oldFrame.initialize( newFrame );
					fStackFrames.set( i, oldFrame );
				}
			}
		}
	}
	
	/**
	 * Checks if we have an old stack frame at a certain depth (from the bottom of the call stack) that was previously used for the given stack frame
	 * 
	 * @param depth The distance from the bottom of the call stack, 0 indicates the bottom call stack entry
	 */
	private RUIStackFrame findMatchingOldStackFrame( RUIStackFrame newFrame, int depth )
	{
		if ( fOldStackFrames == null || fOldStackFrames.size() == 0 )
		{
			return null;
		}
		
		// Determine the array index that is depth entries from the end of the array
		int index = fOldStackFrames.size() - depth - 1;
		if ( index < 0 )
		{
			return null;
		}
		RUIStackFrame oldFrame = (RUIStackFrame)fOldStackFrames.get( index );
		if ( oldFrame.getProgramName().equals( newFrame.getProgramName() ) && oldFrame.getFunctionName().equals( newFrame.getFunctionName() ) )
		{
			return oldFrame;
		}
		return null;
	}
	
	/**
	 * Parses the variable type from a tokenizer. Some types, such as "num(1,0)", will be broken into two tokens when the delimiter is a comma.
	 * 
	 * @param tok The tokenizer.
	 * @return the variable type.
	 */
	private String getTypeFromTokenizer( StringTokenizer tok )
	{
		String type = tok.nextToken();
		if ( type.indexOf( '(' ) != -1 && type.indexOf( ')' ) == -1 )
		{
			type += "," + tok.nextToken(); //$NON-NLS-1$
		}
		return type;
	}
	
	public String handleAtLine( Map args )
	{
		// Tell the debug target to suspend. This updates the UI
		String reason = (String)args.get( "suspendReason" ); //$NON-NLS-1$
		if ( reason != null )
		{
			if ( "breakpoint".equals( reason ) ) //$NON-NLS-1$
			{
				// Look for the breakpoint that we hit.
				IBreakpoint bp = findBreakpointFromArgs( args );
				if ( bp != null )
				{
					getCurrentThread().setBreakpoints( new IBreakpoint[] { bp } );
				}
			}
			suspend( reason );
		}
		
		// Sleep until there is a command to report. It will be "resume" or "step" etc...
		while ( isSuspended() && debugCmd == null )
		{
			try
			{
				Thread.sleep( 10 );
			}
			catch ( Exception e )
			{
			}
		}
		
		String temp = debugCmd;
		debugCmd = null;
		
		if ( temp == null || !temp.startsWith( RUIDebugTarget.VAR_VAR ) )
		{
			if ( fStackFrames != null && fStackFrames.size() != 0 )
			{
				fOldStackFrames = fStackFrames;
			}
			fStackFrames = null;
		}
		return temp;
	}
	
	private IBreakpoint findBreakpointFromArgs( final Map args )
	{
		int line;
		String file = (String)args.get( "file" ); //$NON-NLS-1$
		
		try
		{
			line = Integer.parseInt( (String)args.get( "line" ) ); //$NON-NLS-1$
		}
		catch ( NumberFormatException nfe )
		{
			line = -1;
		}
		
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
				.getBreakpoints( IEGLDebugCoreConstants.EGL_JAVA_MODEL_PRESENTATION_ID );
		for ( int i = 0; i < breakpoints.length; i++ )
		{
			IBreakpoint breakpoint = breakpoints[ i ];
			if ( supportsBreakpoint( breakpoint ) )
			{
				try
				{
					if ( breakpoint.isEnabled() && line == breakpoint.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 )
							&& RUIDebugUtil.getRelativeBreakpointPath( RUIDebugUtil.getBreakpointResource( breakpoint ) ).equals( file ) )
					{
						return breakpoint;
					}
				}
				catch ( CoreException ce )
				{
				}
			}
		}
		
		return null;
	}
	
	public boolean needsNewStack()
	{
		return fStackFrames == null || fStackFrames.size() == 0;
	}
	
	public void varValue( Map args )
	{
		if ( completion != null )
		{
			String value = (String)args.get( "value" ); //$NON-NLS-1$
			String type = (String)args.get( "type" ); //$NON-NLS-1$
			
			value = (value == null
					? "" //$NON-NLS-1$
					: value);
			type = (type == null
					? "" //$NON-NLS-1$
					: type);
			
			completion.completed( new String[] { value, type } );
		}
	}
	
	public void varSetValue( Map args )
	{
		if ( completion != null )
		{
			completion.completed( args.get( "success" ) ); //$NON-NLS-1$
		}
	}
	
	public void varVariables( String value )
	{
		if ( completion != null )
		{
			List list = new ArrayList();
			
			if ( value != null )
			{
				StringTokenizer varTokenizer = new StringTokenizer( value, "," ); //$NON-NLS-1$
				
				RUIVariable parent = completion.getVariable();
				RUIStackFrame frame = parent.getStackFrame();
				while ( varTokenizer.hasMoreTokens() )
				{
					String varName = varTokenizer.nextToken();
					String jsName = varTokenizer.nextToken();
					String getterName = varTokenizer.nextToken();
					String setterName = varTokenizer.nextToken();
					String varIndex = varTokenizer.nextToken();
					String varType = getTypeFromTokenizer( varTokenizer );
					boolean kids = Integer.parseInt( varTokenizer.nextToken() ) != 0;
					RUIVariable var = new RUIVariable( frame, parent, varName, jsName, getterName, setterName, parent.getQualifiedName() + "." //$NON-NLS-1$
							+ varName, varIndex, varType, kids );
					list.add( var );
				}
			}
			
			completion.completed( (IEGLVariable[])list.toArray( new IEGLVariable[ list.size() ] ) );
		}
	}
	
	public void setVarValue()
	{
		if ( completion != null )
		{
			completion.completed( null );
		}
	}
	
	public boolean isStepInto()
	{
		return steppingIn;
	}
	
	public LocaleInfo getLocaleInfo()
	{
		return this.localeInfo;
	}
	
	public void setLocaleInfo( Map args )
	{
		if ( this.localeInfo == null )
		{
			this.localeInfo = new LocaleInfo();
		}
		String val = (String)args.get( "decimalSymbol" ); //$NON-NLS-1$
		if ( val != null )
		{
			this.localeInfo.decimalSymbol = val;
		}
		val = (String)args.get( "currencySymbol" ); //$NON-NLS-1$
		if ( val != null )
		{
			this.localeInfo.currencySymbol = val;
		}
	}
	
	public class LocaleInfo
	{
		public String decimalSymbol;
		public String currencySymbol;
	}
}
