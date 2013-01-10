/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Plug-in class for the EDT debug UI plug-in.
 */
public class EDTDebugUIPlugin extends AbstractUIPlugin
{
	/**
	 * Name of this plug-in.
	 */
	public static final String PLUGIN_ID = "org.eclipse.edt.debug.ui"; //$NON-NLS-1$
	
	/**
	 * The shared instance.
	 */
	private static EDTDebugUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public EDTDebugUIPlugin()
	{
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	public void start( BundleContext context ) throws Exception
	{
		super.start( context );
		plugin = this;
		EGLDebugContextListener.getInstance(); // Register the context listener
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop( BundleContext context ) throws Exception
	{
		plugin = null;
		super.stop( context );
	}
	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static EDTDebugUIPlugin getDefault()
	{
		return plugin;
	}
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status status to log
	 */
	public static void log( IStatus status )
	{
		getDefault().getLog().log( status );
	}
	
	/**
	 * Logs an internal error with the specified throwable
	 * 
	 * @param t the exception to be logged
	 */
	public static void log( Throwable t )
	{
		if ( t instanceof CoreException )
		{
			log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, t.getMessage(), t.getCause() ) );
		}
		else
		{
			log( new Status( IStatus.ERROR, PLUGIN_ID, DebugPlugin.ERROR, "Internal Error", t ) ); //$NON-NLS-1$
		}
	}
	
	/**
	 * Returns the currently active workbench window shell or <code>null</code> if none.
	 * 
	 * @return the currently active workbench window shell or <code>null</code>
	 */
	public static Shell getShell()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if ( window == null )
		{
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if ( windows.length > 0 )
			{
				return windows[ 0 ].getShell();
			}
		}
		else
		{
			return window.getShell();
		}
		return null;
	}
}
