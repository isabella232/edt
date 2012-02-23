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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
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
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.edt.debug.core.PreferenceUtil;
import org.eclipse.edt.debug.core.breakpoints.EGLLineBreakpoint;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.IEGLJavaThread;
import org.eclipse.edt.debug.core.java.filters.ITypeFilter;
import org.eclipse.edt.debug.core.java.filters.TypeFilterUtil;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.ui.BreakpointUtils;

/**
 * Wraps an IJavaDebugTarget.
 */
@SuppressWarnings("restriction")
public class EGLJavaDebugTarget extends EGLJavaDebugElement implements IEGLJavaDebugTarget, IDebugEventFilter, IBreakpointManagerListener,
		IPreferenceChangeListener
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
	private final List<IEGLJavaThread> eglThreads;
	
	/**
	 * The breakpoints registered with this target.
	 */
	private final Map<EGLLineBreakpoint, IJavaBreakpoint> breakpoints;
	
	/**
	 * A flag indicating if we should filter out certain Java class types that EGL programmers won't want to step through.
	 */
	private boolean filterRuntimes;
	
	/**
	 * Cache of class names to contents of *.eglsmap files. Used when the class itself didn't contain SMAP data.
	 */
	private Map<String, String> smapFileCache;
	
	public EGLJavaDebugTarget( IJavaDebugTarget target )
	{
		super( null );
		javaTarget = target;
		threads = new HashMap<IJavaThread, EGLJavaThread>();
		eglThreads = new ArrayList<IEGLJavaThread>();
		breakpoints = new HashMap<EGLLineBreakpoint, IJavaBreakpoint>();
		smapFileCache = new HashMap<String, String>(); // enable reading *.eglsmap off the disk for the Java runtime.
		initFilters();
		
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
		
		PreferenceUtil.addPreferenceChangeListener( this );
		DebugPlugin.getDefault().addDebugEventFilter( this );
		initializeBreakpoints();
	}
	
	/**
	 * Initialize the filtering settings.
	 */
	protected void initFilters()
	{
		filterRuntimes = PreferenceUtil.getBoolean( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED, true );
		
		if ( filterRuntimes )
		{
			for ( ITypeFilter filter : TypeFilterUtil.INSTANCE.getActiveFilters() )
			{
				filter.initialize( this );
			}
		}
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
	
	@Override
	public ILaunch getLaunch()
	{
		return javaTarget.getLaunch();
	}
	
	@Override
	public Object getAdapter( Class adapter )
	{
		if ( adapter == IDebugTarget.class || adapter == EGLJavaDebugTarget.class || adapter == IEGLDebugTarget.class
				|| adapter == IEGLJavaDebugTarget.class )
		{
			return this;
		}
		return super.getAdapter( adapter );
	}
	
	public Map<String, String> getSMAPFileCache()
	{
		return smapFileCache;
	}
	
	public boolean filterRuntimes()
	{
		return filterRuntimes;
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
	
	/**
	 * Adds the initial EGL breakpoints.
	 */
	private void initializeBreakpoints()
	{
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		manager.addBreakpointListener( this );
		manager.addBreakpointManagerListener( this );
		IBreakpoint[] bps = manager.getBreakpoints( IEGLDebugCoreConstants.EGL_JAVA_MODEL_PRESENTATION_ID );
		
		for ( IBreakpoint bp : bps )
		{
			if ( bp instanceof EGLLineBreakpoint )
			{
				breakpointAdded( bp );
			}
		}
	}
	
	@Override
	public boolean supportsBreakpoint( IBreakpoint breakpoint )
	{
		return javaTarget.supportsBreakpoint( breakpoint ) || breakpoint instanceof EGLLineBreakpoint;
	}
	
	@Override
	public void breakpointAdded( IBreakpoint breakpoint )
	{
		if ( breakpoint instanceof EGLLineBreakpoint )
		{
			EGLLineBreakpoint eglBP = (EGLLineBreakpoint)breakpoint;
			if ( !breakpoints.containsKey( eglBP ) )
			{
				try
				{
					IJavaBreakpoint javaBP = createStratumBreakpoint( eglBP );
					if ( javaBP != null )
					{
						javaBP.setEnabled( eglBP.isEnabled() );
						breakpoints.put( eglBP, javaBP );
						
						// Add to the java target so long as either the bp manager is enabled, or the egl bp isn't registered with the manager.
						if ( DebugPlugin.getDefault().getBreakpointManager().isEnabled() || !eglBP.isRegistered() )
						{
							javaTarget.breakpointAdded( javaBP );
						}
					}
				}
				catch ( CoreException e )
				{
					EDTDebugCorePlugin.log( e );
				}
			}
		}
		else if ( notifyJavaTarget( breakpoint ) )
		{
			javaTarget.breakpointAdded( breakpoint );
		}
	}
	
	@Override
	public void breakpointRemoved( IBreakpoint breakpoint, IMarkerDelta delta )
	{
		if ( breakpoint instanceof EGLLineBreakpoint )
		{
			IJavaBreakpoint javaBP = breakpoints.remove( breakpoint );
			if ( javaBP != null )
			{
				javaTarget.breakpointRemoved( javaBP, delta );
				try
				{
					javaBP.delete();
				}
				catch ( CoreException e )
				{
					EDTDebugCorePlugin.log( e );
				}
			}
			
			// If this isn't a registered breakpoint, delete it when it's removed.
			try
			{
				if ( !breakpoint.isRegistered() )
				{
					breakpoint.delete();
				}
			}
			catch ( CoreException e )
			{
				EDTDebugCorePlugin.log( e );
			}
		}
		else if ( notifyJavaTarget( breakpoint ) )
		{
			javaTarget.breakpointRemoved( breakpoint, delta );
		}
	}
	
	@Override
	public void breakpointChanged( IBreakpoint breakpoint, IMarkerDelta delta )
	{
		if ( breakpoint instanceof EGLLineBreakpoint )
		{
			EGLLineBreakpoint eglBP = (EGLLineBreakpoint)breakpoint;
			IJavaBreakpoint javaBP = breakpoints.get( eglBP );
			if ( javaBP != null )
			{
				IMarker marker = eglBP.getMarker();
				
				// If the line number has changed then we need to recreate the java bp (there's no setLineNumber()).
				if ( lineNumberChanged( marker, delta ) )
				{
					try
					{
						javaBP.delete();
						javaBP = createStratumBreakpoint( eglBP );
						if ( javaBP != null )
						{
							javaBP.setEnabled( breakpoint.isEnabled() );
							breakpoints.put( eglBP, javaBP );
							javaTarget.breakpointAdded( javaBP );
						}
						else
						{
							breakpoints.remove( eglBP );
						}
					}
					catch ( CoreException e )
					{
						EDTDebugCorePlugin.log( e );
					}
				}
				else if ( enablementChanged( marker, delta ) )
				{
					try
					{
						javaBP.setEnabled( eglBP.isEnabled() );
					}
					catch ( CoreException e )
					{
						EDTDebugCorePlugin.log( e );
					}
				}
			}
		}
		else if ( notifyJavaTarget( breakpoint ) )
		{
			javaTarget.breakpointChanged( breakpoint, delta );
		}
	}
	
	private boolean notifyJavaTarget( IBreakpoint bp )
	{
		try
		{
			return !bp.isRegistered() || (bp instanceof IJavaLineBreakpoint && BreakpointUtils.isRunToLineBreakpoint( (IJavaLineBreakpoint)bp ));
		}
		catch ( CoreException e )
		{
			return false;
		}
	}
	
	@Override
	public void breakpointManagerEnablementChanged( boolean enabled )
	{
		for ( Map.Entry<EGLLineBreakpoint, IJavaBreakpoint> entry : breakpoints.entrySet() )
		{
			if ( enabled )
			{
				javaTarget.breakpointAdded( entry.getValue() );
			}
			else
			{
				// When the manager is disabled, only remove the breakpoints that are registered with it.
				try
				{
					if ( entry.getKey().isRegistered() )
					{
						javaTarget.breakpointRemoved( entry.getValue(), null );
					}
				}
				catch ( CoreException e )
				{
					EDTDebugCorePlugin.log( e );
				}
			}
		}
	}
	
	private boolean lineNumberChanged( IMarker marker, IMarkerDelta delta )
	{
		if ( delta != null )
		{
			return delta.getAttribute( IMarker.LINE_NUMBER, -1 ) != marker.getAttribute( IMarker.LINE_NUMBER, -1 );
		}
		
		// Must assume it changed.
		return true;
	}
	
	private boolean enablementChanged( IMarker marker, IMarkerDelta delta )
	{
		if ( delta != null )
		{
			return delta.getAttribute( IBreakpoint.ENABLED, true ) != marker.getAttribute( IBreakpoint.ENABLED, true );
		}
		
		// Must assume it changed.
		return true;
	}
	
	private IJavaBreakpoint createStratumBreakpoint( EGLLineBreakpoint bp ) throws CoreException
	{
		IMarker marker = bp.getMarker();
		if ( marker != null && marker.exists() )
		{
			IResource resource = null;
			if ( bp.isRunToLine() )
			{
				// RTL is created on the workspace root so that the marker doesn't appear in the editor.
				// It will have stored its resource path.
				String path = marker.getAttribute( IEGLDebugCoreConstants.RUN_TO_LINE_PATH, null );
				if ( path != null )
				{
					resource = ResourcesPlugin.getWorkspace().getRoot().findMember( path );
				}
			}
			else
			{
				resource = marker.getResource();
			}
			
			if ( resource == null )
			{
				return null;
			}
			
			String qualifiedName = getGeneratedClassName( resource );
			if ( qualifiedName != null )
			{
				int hitcount = 0;
				Map attributes = null;
				if ( bp.isRunToLine() )
				{
					hitcount = 1;
					attributes = new HashMap( 2 );
					BreakpointUtils.addRunToLineAttributes( attributes );
				}
				
				return JDIDebugModel.createStratumBreakpoint( ResourcesPlugin.getWorkspace().getRoot(), IEGLDebugCoreConstants.EGL_STRATUM,
						resource.getName(), null, qualifiedName, bp.getLineNumber(), bp.getCharStart(), bp.getCharEnd(), hitcount, false, attributes );
			}
		}
		return null;
	}
	
	/**
	 * @return the fully-qualified class name of the generated file.
	 */
	private String getGeneratedClassName( IResource resource )
	{
		// TODO remove this dependency on the Java generator. need some other way to get the generated name for a resource.
		// Different Java generators will use different naming conventions
		IEGLFile eglFile = (IEGLFile)EGLCore.create( resource );
		if ( eglFile != null && eglFile.exists() )
		{
			try
			{
				StringBuilder buf = new StringBuilder( 50 );
				IPackageDeclaration[] pkg = eglFile.getPackageDeclarations();
				if ( pkg != null && pkg.length > 0 )
				{
					buf.append( JavaAliaser.packageNameAlias( pkg[ 0 ].getElementName() ) );
					buf.append( '.' );
				}
				
				String name = eglFile.getElementName();
				int idx = name.lastIndexOf( '.' );
				if ( idx != -1 )
				{
					name = name.substring( 0, idx );
				}
				
				buf.append( JavaAliaser.getAlias( name ) );
				return buf.toString();
			}
			catch ( EGLModelException e )
			{
			}
		}
		return null;
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
		synchronized ( eglThreads )
		{
			return eglThreads.toArray( new EGLJavaThread[ eglThreads.size() ] );
		}
	}
	
	@Override
	public boolean hasThreads() throws DebugException
	{
		return eglThreads.size() > 0;
	}
	
	@Override
	public String getName() throws DebugException
	{
		return javaTarget.getName();
	}
	
	protected EGLJavaThread getThread( IJavaThread javaThread )
	{
		EGLJavaThread eglThread;
		synchronized ( threads )
		{
			eglThread = threads.get( javaThread );
		}
		if ( eglThread == null )
		{
			eglThread = new EGLJavaThread( this, javaThread );
			synchronized ( threads )
			{
				threads.put( javaThread, eglThread );
			}
			synchronized ( eglThreads )
			{
				eglThreads.add( eglThread );
			}
		}
		return eglThread;
	}
	
	EGLJavaThread removeThread( IJavaThread javaThread )
	{
		EGLJavaThread eglThread;
		synchronized ( threads )
		{
			eglThread = threads.remove( javaThread );
		}
		
		if ( eglThread != null )
		{
			synchronized ( eglThreads )
			{
				eglThreads.remove( eglThread );
			}
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
				cleanup();
			}
			super.handleDebugEvents( events );
		}
	}
	
	private void cleanup()
	{
		DebugPlugin plugin = DebugPlugin.getDefault();
		plugin.getBreakpointManager().removeBreakpointListener( this );
		plugin.getBreakpointManager().removeBreakpointManagerListener( this );
		plugin.removeDebugEventFilter( this );
		PreferenceUtil.removePreferenceChangeListener( this );
		
		// Delete all the stratum breakpoints we created.
		for ( IJavaBreakpoint bp : breakpoints.values() )
		{
			try
			{
				bp.delete();
			}
			catch ( CoreException e )
			{
			}
		}
		
		// Make sure any non-persisted EGL breakpoints are also deleted.
		for ( EGLLineBreakpoint bp : breakpoints.keySet() )
		{
			try
			{
				if ( !bp.isPersisted() )
				{
					bp.delete();
				}
			}
			catch ( CoreException e )
			{
			}
		}
		breakpoints.clear();
		
		// Let the type filters clean up anything cached for this target. Do this for all filters in case some were disabled during debugging
		// and therefore no longer part of the active filter list.
		for ( ITypeFilter filter : TypeFilterUtil.INSTANCE.getAllFilters() )
		{
			filter.dispose( this );
		}
		
		if ( smapFileCache != null )
		{
			smapFileCache.clear();
		}
		
		synchronized ( threads )
		{
			threads.clear();
		}
		synchronized ( eglThreads )
		{
			eglThreads.clear();
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
	 * @return the underlying Java debug element.
	 */
	@Override
	public Object getJavaDebugElement()
	{
		return getJavaDebugTarget();
	}
	
	@Override
	public void preferenceChange( PreferenceChangeEvent event )
	{
		boolean refreshFrames = false;
		String key = event.getKey();
		if ( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTERS_ENABLED.equals( key ) )
		{
			initFilters();
			refreshFrames = true;
		}
		else if ( IEGLDebugCoreConstants.PREFERENCE_TYPE_FILTER_ENABLEMENT.equals( key ) )
		{
			refreshFrames = true;
		}
		
		if ( refreshFrames )
		{
			refreshAllFrames();
		}
	}
	
	/**
	 * Tells debug platform that the content of each EGLJavaThread has changed.
	 */
	public void refreshAllFrames()
	{
		synchronized ( eglThreads )
		{
			for ( IEGLJavaThread thread : eglThreads )
			{
				try
				{
					if ( thread.hasStackFrames() && thread instanceof EGLJavaThread )
					{
						((EGLJavaThread)thread).disposeStackFrames();
						fireEvent( new DebugEvent( thread, DebugEvent.CHANGE, DebugEvent.CONTENT ) );
					}
				}
				catch ( DebugException de )
				{
				}
			}
		}
	}
	
	@Override
	public boolean supportsStepFilters()
	{
		return javaTarget.supportsStepFilters();
	}
	
	@Override
	public boolean isStepFiltersEnabled()
	{
		return javaTarget.isStepFiltersEnabled();
	}
	
	@Override
	public void setStepFiltersEnabled( boolean enabled )
	{
		javaTarget.setStepFiltersEnabled( enabled );
	}
}
