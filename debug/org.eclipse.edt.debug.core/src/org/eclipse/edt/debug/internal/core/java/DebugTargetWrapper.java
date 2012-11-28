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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventFilter;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.ui.IStartup;

/**
 * Adds an event filter at startup to handle wrapping Java processes. Ideally we'd be able to hook into the launching process so that we don't force
 * plug-in activation at startup.
 */
public class DebugTargetWrapper implements IStartup
{
	/**
	 * VM arg that can be set to 'true' to disable wrapping the Java process. Useful for those debugging the generated code.
	 */
	private static final String DISABLE_EGL = "-Ddisable.egl=true";
	
	@Override
	public void earlyStartup()
	{
		DebugTargetFilter filter = new DebugTargetFilter();
		DebugPlugin.getDefault().addDebugEventFilter( filter );
	}
	
	/**
	 * Looks for the creation of the IJavaDebugTarget for the launch. When found, the target is wrapped and we set our own target on the launch.
	 */
	private class DebugTargetFilter implements IDebugEventFilter
	{
		@Override
		public DebugEvent[] filterDebugEvents( DebugEvent[] events )
		{
			if ( events != null && events.length != 0 && events[ 0 ].getKind() == DebugEvent.CREATE )
			{
				Object src = events[ 0 ].getSource();
				if ( src instanceof IDebugTarget && !(src instanceof IEGLDebugTarget) )
				{
					IJavaDebugTarget javaTarget = (IJavaDebugTarget)((IDebugTarget)src).getAdapter( IJavaDebugTarget.class );
					if ( javaTarget != null )
					{
						ILaunch launch = javaTarget.getLaunch();
						if ( launch != null && ILaunchManager.DEBUG_MODE.equals( launch.getLaunchMode() ) )
						{
							String vmArgs = null;
							try
							{
								vmArgs = launch.getLaunchConfiguration().getAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String)null );
							}
							catch ( CoreException ce )
							{
							}
							
							if ( vmArgs == null || !vmArgs.contains( DISABLE_EGL ) )
							{
								EGLJavaDebugTarget edtTarget = new EGLJavaDebugTarget( (IJavaDebugTarget)javaTarget );
								launch.removeDebugTarget( javaTarget );
								launch.addDebugTarget( edtTarget );
								
								// Set up a source lookup director that is capable of finding .egl files from the EGL build path, if necessary.
								try
								{
									ISourceLocator origLocator = launch.getSourceLocator();
									if ( !(origLocator instanceof EGLJavaSourceLookupDirector) )
									{
										EGLJavaSourceLookupDirector director = new EGLJavaSourceLookupDirector( origLocator );
										director.initializeDefaults( launch.getLaunchConfiguration() );
										launch.setSourceLocator( director );
									}
								}
								catch ( CoreException e )
								{
								}
								
								edtTarget.handleDebugEvents( events );
							}
						}
					}
				}
			}
			return events;
		}
	}
}
