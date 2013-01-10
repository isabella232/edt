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
package org.eclipse.edt.debug.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.edt.debug.core.java.filters.TypeFilterUtil;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.edt.ide.core.EDTRuntimeContainerEntry;
import org.eclipse.equinox.frameworkadmin.BundleInfo;
import org.eclipse.equinox.simpleconfigurator.manipulator.SimpleConfiguratorManipulator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Plug-in class for the EDT debug model plug-in.
 */
public class EDTDebugCorePlugin extends Plugin implements BundleActivator
{
	/**
	 * Name of this plug-in.
	 */
	public static final String PLUGIN_ID = "org.eclipse.edt.debug.core"; //$NON-NLS-1$
	
	/**
	 * The shared instance.
	 */
	private static EDTDebugCorePlugin plugin;
	
	/**
	 * The absolute path to the transformer jar.
	 */
	private String transformerPath;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
	 */
	public void start( BundleContext bundleContext ) throws Exception
	{
		super.start( bundleContext );
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop( BundleContext bundleContext ) throws Exception
	{
		super.stop( bundleContext );
		
		TypeFilterUtil.INSTANCE.dispose();
		VariableUtil.dispose();
		PreferenceUtil.savePreferences();
		
		plugin = null;
	}
	
	public static EDTDebugCorePlugin getDefault()
	{
		return plugin;
	}
	
	@SuppressWarnings("unchecked")
	public String getTransformerPath()
	{
		if ( transformerPath == null )
		{
			BundleContext context = getBundle().getBundleContext();
			if ( context != null )
			{
				ServiceReference ref = context.getServiceReference( SimpleConfiguratorManipulator.class.getName() );
				if ( ref != null )
				{
					SimpleConfiguratorManipulator manipulator = (SimpleConfiguratorManipulator)context.getService( ref );
					if ( manipulator != null )
					{
						try
						{
							BundleInfo mainBundle = EDTRuntimeContainerEntry.findBestBundle( PLUGIN_ID,
									manipulator.loadConfiguration( context, null ), null );
							if ( mainBundle != null )
							{
								URL bundleURL = FileLocator.toFileURL( URIUtil.toURL( mainBundle.getLocation() ) );
								String path = bundleURL.getPath();
								path = URLDecoder.decode( path, "UTF-8" ); //$NON-NLS-1$
								
								File file = new File( path );
								if ( file.isDirectory() )
								{
									// Must be in development mode. Use its transformer.jar.
									file = new File( file, "transformer.jar" ); //$NON-NLS-1$
								}
								transformerPath = file.getAbsolutePath();
							}
						}
						catch ( IOException ioe )
						{
						}
					}
				}
			}
			
			if ( transformerPath == null )
			{
				transformerPath = ""; //$NON-NLS-1$
			}
		}
		return transformerPath;
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
			log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, t.getMessage(), t.getCause() == null
					? t
					: t.getCause() ) );
		}
		else
		{
			log( new Status( IStatus.ERROR, PLUGIN_ID, DebugPlugin.ERROR, "Internal Error", t ) ); //$NON-NLS-1$
		}
	}
}
