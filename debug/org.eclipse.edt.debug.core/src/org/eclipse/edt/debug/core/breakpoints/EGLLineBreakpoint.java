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
package org.eclipse.edt.debug.core.breakpoints;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;

public class EGLLineBreakpoint extends EGLBreakpoint implements ILineBreakpoint
{
	public EGLLineBreakpoint()
	{
		super();
	}
	
	public EGLLineBreakpoint( IResource resource, String typeName, int lineNumber, int charStart, int charEnd, boolean register, boolean runToLine )
			throws DebugException
	{
		this( resource, typeName, lineNumber, charStart, charEnd, register, runToLine, new HashMap() );
	}
	
	public EGLLineBreakpoint( final IResource resource, final String typeName, final int lineNumber, final int charStart, final int charEnd,
			final boolean register, final boolean runToLine, final Map attributes ) throws DebugException
	{
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run( IProgressMonitor monitor ) throws CoreException
			{
				setMarker( resource.createMarker( IEGLDebugCoreConstants.EGL_LINE_BREAKPOINT_MARKER_ID ) );
				
				attributes.put( IBreakpoint.ID, IEGLDebugCoreConstants.EGL_JAVA_MODEL_PRESENTATION_ID );
				attributes.put( IEGLDebugCoreConstants.BREAKPOINT_TYPE_NAME, typeName );
				attributes.put( IBreakpoint.ENABLED, Boolean.TRUE );
				attributes.put( IMarker.LINE_NUMBER, Integer.valueOf( lineNumber ) );
				attributes.put( IMarker.CHAR_START, Integer.valueOf( charStart ) );
				attributes.put( IMarker.CHAR_END, Integer.valueOf( charEnd ) );
				attributes.put( IEGLDebugCoreConstants.RUN_TO_LINE, Boolean.valueOf( runToLine ) );
				attributes.put( IBreakpoint.PERSISTED, Boolean.valueOf( !runToLine ) );
				
				ensureMarker().setAttributes( attributes );
				
				if ( register )
				{
					DebugPlugin.getDefault().getBreakpointManager().addBreakpoint( EGLLineBreakpoint.this );
				}
				else
				{
					setRegistered( false );
				}
			}
		};
		run( getMarkerRule( resource ), runnable );
	}
	
	@Override
	public int getLineNumber() throws CoreException
	{
		return ensureMarker().getAttribute( IMarker.LINE_NUMBER, -1 );
	}
	
	@Override
	public int getCharStart() throws CoreException
	{
		return ensureMarker().getAttribute( IMarker.CHAR_START, -1 );
	}
	
	@Override
	public int getCharEnd() throws CoreException
	{
		return ensureMarker().getAttribute( IMarker.CHAR_END, -1 );
	}
	
	public boolean isRunToLine() throws CoreException
	{
		return ensureMarker().getAttribute( IEGLDebugCoreConstants.RUN_TO_LINE, false );
	}
	
	public void setRunToLine( boolean isRunToLine ) throws CoreException
	{
		ensureMarker().setAttribute( IEGLDebugCoreConstants.RUN_TO_LINE, isRunToLine );
	}
}
