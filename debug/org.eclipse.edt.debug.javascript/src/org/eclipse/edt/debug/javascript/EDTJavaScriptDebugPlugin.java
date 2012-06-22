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
package org.eclipse.edt.debug.javascript;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugContextResolver;
import org.eclipse.edt.ide.rui.server.EvServer;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EDTJavaScriptDebugPlugin extends AbstractUIPlugin
{
	/**
	 * Name of this plug-in.
	 */
	public static final String PLUGIN_ID = "org.eclipse.edt.debug.javascript"; //$NON-NLS-1$
	
	/**
	 * The shared instance.
	 */
	private static EDTJavaScriptDebugPlugin plugin;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start( BundleContext bundleContext ) throws Exception
	{
		super.start( bundleContext );
		plugin = this;
		
		EvServer.getInstance().addContextResolver( RUIDebugContextResolver.getInstance() );
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop( BundleContext bundleContext ) throws Exception
	{
		EvServer.getInstance().removeContextResolver( RUIDebugContextResolver.getInstance() );
		plugin = null;
		super.stop( bundleContext );
	}
	
	public static EDTJavaScriptDebugPlugin getDefault()
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
}
