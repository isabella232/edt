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
package org.eclipse.edt.debug.ui.launching;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class EGLJavaLaunchUtils
{
	private EGLJavaLaunchUtils()
	{
		// No Instances
	}
	
	private static boolean imageRegistryInitialized;
	private static final Object initializeMutex = new Object();
	
	/**
	 * The image registry which holds <code>Image</code>s
	 */
	private static ImageRegistry imageRegistry;
	
	private static URL ICON_BASE_URL;
	static
	{
		ICON_BASE_URL = EDTDebugUIPlugin.getDefault().getBundle().getEntry( "icons/full/" ); //$NON-NLS-1$
	}
	
	public static Image getImage( String key )
	{
		if ( !imageRegistryInitialized )
		{
			initializeImageRegistry();
		}
		return getImageRegistry().get( key );
	}
	
	private static ImageRegistry initializeImageRegistry()
	{
		synchronized ( initializeMutex )
		{
			if ( !imageRegistryInitialized )
			{
				imageRegistry = new ImageRegistry( getStandardDisplay() );
				declareImages();
				imageRegistryInitialized = true;
			}
		}
		return imageRegistry;
	}
	
	private final static void declareRegistryImage( String key, String path )
	{
		ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
		try
		{
			desc = ImageDescriptor.createFromURL( makeIconFileURL( path ) );
		}
		catch ( MalformedURLException me )
		{
		}
		imageRegistry.put( key, desc );
	}
	
	private static URL makeIconFileURL( String iconPath ) throws MalformedURLException
	{
		if ( ICON_BASE_URL == null )
		{
			throw new MalformedURLException();
		}
		return new URL( ICON_BASE_URL, iconPath );
	}
	
	private static void declareImages()
	{
		declareRegistryImage( IEGLJavaLaunchConstants.IMG_LAUNCH_MAIN_TAB, "obj16/main_tab_obj.gif" ); //$NON-NLS-1$
	}
	
	public static ImageRegistry getImageRegistry()
	{
		if ( imageRegistry == null )
		{
			imageRegistry = new ImageRegistry();
		}
		return imageRegistry;
	}
	
	public static Display getStandardDisplay()
	{
		Display display = Display.getCurrent();
		if ( display == null )
		{
			display = Display.getDefault();
		}
		return display;
	}
	
	public static void addJavaAttributes( String projectName, ILaunchConfigurationWorkingCopy configuration )
	{
		if ( projectName != null && projectName.length() != 0 )
		{
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
			if ( project != null && project.isAccessible() )
			{
				try
				{
					if ( project.hasNature( JavaCore.NATURE_ID ) )
					{
						configuration.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName );
					}
				}
				catch ( CoreException e )
				{
				}
			}
		}
		configuration.setAttribute( IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.eclipse.edt.javart.ide.MainProgramLauncher" ); //$NON-NLS-1$
		configuration.setAttribute( IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN, false );
	}
}
