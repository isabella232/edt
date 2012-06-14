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
package org.eclipse.edt.ide.debug.javascript.internal.server;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.internal.ui.actions.BreakpointUtils;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugContextResolver;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugMessages;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugTarget;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.debug.javascript.internal.utils.RUIDebugUtil;
import org.eclipse.edt.ide.rui.server.AbstractContext;
import org.eclipse.edt.ide.rui.server.EvServer;
import org.eclipse.edt.ide.rui.server.EvServer.Event;
import org.eclipse.edt.ide.rui.server.IContext2;
import org.eclipse.edt.javart.json.TokenMgrError;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class DebugContext extends AbstractContext implements IContext2
{
	private RUIDebugTarget debugTarget;
	private List<String> eventQueue;
	private final String launchConfigName;
	
	/**
	 * High priority events jump to the front - even ahead of other high priority events. Normal priority events are added to the end of the list.
	 */
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_HIGH = 1;
	
	public DebugContext( String url, Integer key, RUIDebugTarget debugTarget, String launchConfigName )
	{
		super( url, key, new DebugContentProvider() );
		this.debugTarget = debugTarget;
		this.eventQueue = new ArrayList<String>();
		this.launchConfigName = launchConfigName;
	}
	
	public RUIDebugTarget getDebugTarget()
	{
		return debugTarget;
	}
	
	public void clear()
	{
		debugTarget = null;
		eventQueue = null;
		contentProvider = null;
	}
	
	public String getLaunchConfigName()
	{
		return launchConfigName;
	}
	
	public void addEvent( String event, int priority )
	{
		if ( eventQueue != null )
		{
			synchronized ( eventQueue )
			{
				switch ( priority )
				{
					case PRIORITY_HIGH:
						eventQueue.add( 0, event );
						break;
					case PRIORITY_NORMAL:
					default:
						eventQueue.add( event );
						break;
				}
			}
		}
	}
	
	private String getNextEvent()
	{
		if ( eventQueue != null )
		{
			synchronized ( eventQueue )
			{
				if ( eventQueue.size() == 0 )
				{
					return null;
				}
				return (String)eventQueue.remove( 0 );
			}
		}
		return null;
	}
	
	public String waitForEvent()
	{
		long timeOut = 5000;
		String eventToSend = getNextEvent();
		while ( eventToSend == null && timeOut > 0 )
		{
			timeOut -= 100;
			try
			{
				Thread.sleep( 100 );
			}
			catch ( Exception e )
			{
			}
			eventToSend = getNextEvent();
		}
		
		// Either we found an event or time's up.
		return eventToSend;
	}
	
	public void addBreakpoint( String file, String line, String enabled )
	{
		addEvent( "egl.addBreakpoint(\"" + file + "\", " + line + ", " + enabled + ")", PRIORITY_NORMAL ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	public void changeBreakpoint( String file, String oldline, String enabled )
	{
		addEvent( "egl.changeBreakpoint(\"" + file + "\", " + oldline + ", " + enabled + ")", PRIORITY_NORMAL ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	public void removeBreakpoint( String file, String line )
	{
		addEvent( "egl.removeBreakpoint(\"" + file + "\", " + line + ")", PRIORITY_NORMAL ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public void breakpointManagerChanged( String enabled )
	{
		addEvent( "egl.breakpointManagerChanged(" + enabled + ")", PRIORITY_NORMAL ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void disconnectDebugger()
	{
		addEvent( "egl.disconnectDebugger()", PRIORITY_HIGH ); //$NON-NLS-1$
	}
	
	@Override
	public void handleEvent( Event event )
	{
		String url = event.url;
		try
		{
			if ( url.indexOf( "___getevent" ) >= 0 ) //$NON-NLS-1$
			{
				getDebugEvent( event );
			}
			else if ( url.indexOf( "___atLine" ) != -1 ) //$NON-NLS-1$
			{
				atLine( event );
			}
			else if ( url.indexOf( "___getUserDebugRequest" ) != -1 ) //$NON-NLS-1$
			{
				getUserDebugRequest( event.ps );
			}
			else if ( url.indexOf( "___debugStack" ) != -1 ) //$NON-NLS-1$
			{
				String stackStr = (String)event.arguments.get( "stack" ); //$NON-NLS-1$
				if ( stackStr == null )
				{
					stackStr = event.xmlRequest.getContent().substring( "stack=".length() ); //$NON-NLS-1$
					stackStr = stackStr.replaceAll( "&nbsp;", " " ); //$NON-NLS-1$ //$NON-NLS-2$
					int indexOfAmp = stackStr.indexOf( '&' );
					if ( indexOfAmp != -1 )
					{
						stackStr = stackStr.substring( 0, indexOfAmp );
					}
				}
				debugStack( event.ps, stackStr );
			}
			else if ( url.indexOf( "___debugResume" ) != -1 ) //$NON-NLS-1$
			{
				debugResume( event.ps, event.arguments );
			}
			else if ( url.indexOf( "___varValue" ) != -1 ) //$NON-NLS-1$
			{
				// The variable value is always sent as content
				varValue( event.ps, event.xmlRequest.getContentArguments() );
			}
			else if ( url.indexOf( "___varVariables" ) != -1 ) //$NON-NLS-1$
			{
				// The variable value is always sent as content
				varVariables( event.ps, (String)event.xmlRequest.getContentArguments().get( "variables" ) ); //$NON-NLS-1$
			}
			else if ( url.indexOf( "___varSetValue" ) != -1 ) //$NON-NLS-1$
			{
				// The variable value is always sent as content
				varSetValue( event.ps, event.xmlRequest.getContentArguments() );
			}
			else if ( url.indexOf( "___debugTerminate" ) != -1 ) //$NON-NLS-1$
			{
				String msg = debugTerminate();
				event.ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
				try
				{
					event.ps.write( msg.getBytes( "UTF-8" ) ); //$NON-NLS-1$
				}
				catch ( UnsupportedEncodingException uee )
				{
					event.ps.write( msg.getBytes() );
				}
			}
			else if ( url.indexOf( "___windowClosed" ) != -1 ) //$NON-NLS-1$
			{
				browserWindowClosed();
			}
			else if ( url.indexOf( "___getExistingBreakpoints" ) != -1 ) //$NON-NLS-1$
			{
				sendBreakpoints( event.ps );
			}
			else if ( url.indexOf( "___localeSettings" ) != -1 ) //$NON-NLS-1$
			{
				setDebugSessionLocale( event.ps, event.arguments );
			}
			else if ( url.indexOf( "___getBreakpointManagerState" ) != -1 ) //$NON-NLS-1$
			{
				getBreakpointManagerState( event.ps );
			}
			else if ( url.indexOf( "__getversion" ) != -1 ) //$NON-NLS-1$
			{
				EvServer.getInstance().sendVersion( event.ps );
			}
			else if ( url.indexOf( "___traceEvents" ) != -1 ) //$NON-NLS-1$
			{
				event.ps.print( EvServer.getInstance().getGoodResponseHeader( "", EvServer.getInstance().getContentType( "" ), false ) ); //$NON-NLS-1$ //$NON-NLS-2$
				event.ps.print( "OK" ); //$NON-NLS-1$
				event.ps.flush();
			}
			else if ( url.indexOf( ".." ) == -1 ) //$NON-NLS-1$
			{ // no hacking this server, please
				EvServer.getInstance().loadFile( url, event.key, event.ps );
			}
			else
			{
				EvServer.getInstance().fail( event.ps );
			}
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
		}
		catch ( TokenMgrError ex )
		{
			ex.printStackTrace();
		}
		finally
		{
			if ( url.indexOf( "___getevent" ) == -1 && url.indexOf( "___atLine" ) == -1 ) //$NON-NLS-1$ //$NON-NLS-2$
			{
				event.ps.close();
				try
				{
					event.socket.close();
				}
				catch ( IOException ex )
				{
				}
			}
		}
	}
	
	private void getDebugEvent( final Event event )
	{
		// Run in a new thread so we don't block other browser requests while waiting for an event.
		new Thread() {
			public void run()
			{
				String eventToSend = waitForEvent();
				event.ps.print( EvServer.getInstance().getGoodResponseHeader( "", EvServer.getInstance().getContentType( "" ), false ) ); //$NON-NLS-1$ //$NON-NLS-2$
				event.ps.print( eventToSend == null
						? "" //$NON-NLS-1$
						: eventToSend );
				event.ps.close();
				try
				{
					event.socket.close();
				}
				catch ( IOException ex )
				{
				}
			}
		}.start();
	}
	
	private void setDebugSessionLocale( PrintStream ps, Map args )
	{
		if ( debugTarget != null )
		{
			debugTarget.setLocaleInfo( args );
		}
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * The browser occasionally polls for any user requests. This way requests like terminate and suspend don't have to wait for the function stack to
	 * complete before the getEvent response can be invoked.
	 */
	private void getUserDebugRequest( PrintStream ps )
	{
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
		if ( debugTarget != null )
		{
			if ( debugTarget.isTerminating() )
			{
				ps.print( "terminate" ); //$NON-NLS-1$
			}
			else if ( debugTarget.isSuspending() )
			{
				ps.print( "suspend" ); //$NON-NLS-1$
			}
			else
			{
				ps.print( "" ); //$NON-NLS-1$
			}
		}
		else
		{
			ps.print( "" ); //$NON-NLS-1$
		}
	}
	
	private void sendBreakpoints( PrintStream ps ) throws CoreException
	{
		StringBuffer sb = new StringBuffer();
		if ( debugTarget != null )
		{
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager()
					.getBreakpoints( IEGLDebugCoreConstants.EGL_JAVA_MODEL_PRESENTATION_ID );
			for ( int i = 0; i < breakpoints.length; i++ )
			{
				IBreakpoint breakpoint = breakpoints[ i ];
				if ( debugTarget.supportsBreakpoint( breakpoint ) )
				{
					try
					{
						String relativePath = RUIDebugUtil.encodeValue( BreakpointUtils.getRelativeBreakpointPath( breakpoint ) );
						if ( relativePath != null )
						{
							sb.append( relativePath );
							sb.append( "," ); //$NON-NLS-1$
							sb.append( Integer.toString( breakpoint.getMarker().getAttribute( IMarker.LINE_NUMBER, -1 ) ) );
							sb.append( "," ); //$NON-NLS-1$
							sb.append( Boolean.toString( breakpoint.isEnabled() ) );
							if ( i != breakpoints.length - 1 )
							{
								sb.append( "," ); //$NON-NLS-1$
							}
						}
					}
					catch ( DebugException e )
					{
					}
					catch ( EGLModelException e )
					{
					}
				}
			}
		}
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
		ps.print( sb.toString() );
	}
	
	private void getBreakpointManagerState( PrintStream ps ) throws CoreException
	{
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
		ps.print( DebugPlugin.getDefault().getBreakpointManager().isEnabled() );
	}
	
	private void browserWindowClosed()
	{
		// Terminate any debug sessions.
		if ( debugTarget != null )
		{
			debugTarget.windowClosed();
		}
		debugTerminationCleanup();
	}
	
	/**
	 * @return a message to report to the user, or null.
	 */
	private String debugTerminate()
	{
		if ( debugTarget != null )
		{
			debugTarget.terminated();
			return RUIDebugMessages.rui_debug_terminated_msg;
		}
		else
		{
			return RUIDebugMessages.rui_debug_refreshed_msg;
		}
	}
	
	public void debugTerminationCleanup()
	{
		// Wait a couple seconds and check if the debug session has been
		// terminated. If not, issue a warning and kill it.
		if ( debugTarget != null )
		{
			// if already terminated (e.g. browser window closed) then don't spin off a new thread.
			if ( debugTarget.isTerminated() )
			{
				RUIDebugContextResolver.getInstance().removeContext( this );
			}
			else
			{
				new Thread() {
					public void run()
					{
						try
						{
							Thread.sleep( 2000 );
						}
						catch ( InterruptedException e )
						{
						}
						
						Display.getDefault().asyncExec( new Runnable() {
							public void run()
							{
								if ( !debugTarget.isTerminated() )
								{
									// TODO put the app's name in this message.
									MessageDialog.openInformation( Display.getDefault().getActiveShell(),
											RUIDebugMessages.DEBUG_REMOTECLIENTNOTRESPONDING_TITLE,
											RUIDebugMessages.DEBUG_REMOTECLIENTNOTRESPONDING_MSG );
									debugTerminate();
								}
								RUIDebugContextResolver.getInstance().removeContext( DebugContext.this );
							}
						} );
					}
				}.start();
			}
		}
	}
	
	private void debugStack( PrintStream ps, String stackStr )
	{
		if ( debugTarget != null )
		{
			debugTarget.parseStack( stackStr );
		}
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void debugResume( PrintStream ps, Map args )
	{
		if ( debugTarget != null )
		{
			debugTarget.resume( (String)args.get( "resumeReason" ) ); //$NON-NLS-1$
		}
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void atLine( final Event event )
	{
		// Run in a new thread so we don't block other browser requests while waiting
		// for an event.
		new Thread() {
			public void run()
			{
				if ( debugTarget == null )
				{
					event.ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
					event.ps.print( "disconnect" ); //$NON-NLS-1$
				}
				else
				{
					/*
					 * If we don't have a stack, ask the runtime for one before we tell the debug target to suspend. The runtime will invoke
					 * __debugStack and then __atLine again to get back here.
					 */
					if ( debugTarget.needsNewStack() )
					{
						event.ps.print( EvServer.getInstance().getGoodResponseHeader( "stack", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
						event.ps.print( "stack" ); //$NON-NLS-1$
					}
					else
					{
						String response = debugTarget.handleAtLine( event.arguments );
						
						// Send the command back to be processed in function response() in egl.atLine
						event.ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
						event.ps.print( response == null
								? "" //$NON-NLS-1$
								: response );
					}
				}
				
				event.ps.close();
				try
				{
					event.socket.close();
				}
				catch ( IOException ex )
				{
				}
			}
		}.start();
	}
	
	private void varValue( PrintStream ps, Map args )
	{
		if ( debugTarget != null )
		{
			debugTarget.varValue( args );
		}
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void varVariables( PrintStream ps, String value )
	{
		if ( debugTarget != null )
		{
			debugTarget.varVariables( value );
		}
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void varSetValue( PrintStream ps, Map args )
	{
		if ( debugTarget != null )
		{
			debugTarget.varSetValue( args );
		}
		ps.print( EvServer.getInstance().getGoodResponseHeader( "", "text/html", false ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public boolean useTestServer()
	{
		return true;
	}
}
