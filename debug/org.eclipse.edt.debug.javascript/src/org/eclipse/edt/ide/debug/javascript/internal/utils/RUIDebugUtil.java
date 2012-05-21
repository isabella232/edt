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
package org.eclipse.edt.ide.debug.javascript.internal.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.breakpoints.EGLLineBreakpoint;
import org.eclipse.edt.debug.javascript.EDTJavaScriptDebugPlugin;
import org.eclipse.edt.debug.javascript.internal.launching.IRUIDebugConstants;
import org.eclipse.edt.debug.javascript.internal.model.IRUILaunchConfigurationConstants;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugMessages;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugTarget;
import org.eclipse.edt.debug.ui.launching.EGLLaunchingMessages;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.internal.model.SourcePartElementInfo;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.debug.javascript.internal.server.DebugContext;
import org.eclipse.edt.ide.rui.server.EvServer;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class RUIDebugUtil
{
	private RUIDebugUtil()
	{
		// No instances.
	}
	
	private static boolean imageRegistryInitialized;
	private static final Object initializeMutex = new Object();
	
	/**
	 * The image registry which holds <code>Image</code>s
	 */
	private static ImageRegistry imageRegistry;
	/**
	 * A table of all the <code>ImageDescriptor</code>s
	 */
//	private static HashMap imageDescriptors;
	
	private static URL ICON_BASE_URL = null;
	static
	{
		ICON_BASE_URL = EDTJavaScriptDebugPlugin.getDefault().getBundle().getEntry( "icons/full/" ); //$NON-NLS-1$
	}
	
	public static String getProgramNameFromFile( String filename )
	{
		int start;
		int end;
		
		int index = filename.lastIndexOf( '/' );
		if ( index != -1 )
		{
			start = index + 1;
		}
		else
		{
			start = 0;
		}
		
		if ( DebugUtil.isEGLFileName( filename ) )
		{
			end = filename.length() - 4;
		}
		else
		{
			end = filename.length();
		}
		
		return filename.substring( start, end );
	}
	
	public static void launchRUIHandlerInDebugMode( IFile eglFile ) throws CoreException
	{
		launchRUIHandler( eglFile == null
				? null
				: eglFile.getProject(), eglFile, ILaunchManager.DEBUG_MODE );
	}
	
	public static void launchRUIHandler( IProject project, IFile eglFile, String mode ) throws CoreException
	{
		if ( eglFile == null )
		{
			abort( RUIDebugMessages.rui_debug_utils_missing_program_file );
		}
		
		ILaunchConfiguration config = findLaunchConfiguration( project, eglFile );
		if ( config != null )
		{
			DebugUITools.launch( config, mode );
		}
	}
	
	/**
	 * Abort - throws a <code>CoreException</code>.
	 * 
	 * @param message The exception message.
	 */
	public static void abort( String message ) throws CoreException
	{
		Status status = new Status( IStatus.ERROR, EDTJavaScriptDebugPlugin.PLUGIN_ID, IStatus.ERROR, message, null );
		throw new CoreException( status );
	}
	
	public static ILaunchConfiguration findLaunchConfiguration( IProject project, IFile eglFile ) throws CoreException
	{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType( IRUIDebugConstants.ID_RUI_LAUNCH_TYPE );
		if ( configType == null )
		{
			abort( RUIDebugMessages.rui_debug_utils_config_type_not_found );
		}
		String fileName = getQualifiedFilename( eglFile );
		ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations( configType );
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>( configs.length );
		for ( int i = 0; i < configs.length; i++ )
		{
			ILaunchConfiguration config = configs[ i ];
			String configFile = config.getAttribute( IRUILaunchConfigurationConstants.ATTR_HANDLER_FILE, "" ); //$NON-NLS-1$
			
			if ( filesMatch( fileName, configFile ) )
			{
				String configProjectName = config.getAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, "" ); //$NON-NLS-1$
				String projectName = ""; //$NON-NLS-1$
				if ( project != null && project.getName() != null )
				{
					projectName = project.getName();
				}
				if ( projectName.equals( configProjectName ) )
				{
					candidateConfigs.add( config );
				}
			}
		}
		
		int candidateCount = candidateConfigs.size();
		if ( candidateCount < 1 )
		{
			return createConfiguration( project, eglFile );
		}
		else if ( candidateCount == 1 )
		{
			return (ILaunchConfiguration)candidateConfigs.get( 0 );
		}
		else
		{
			// Prompt the user to choose a config. A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching anything.
			ILaunchConfiguration config = chooseConfiguration( candidateConfigs );
			if ( config != null )
			{
				return config;
			}
		}
		
		return null;
	}
	
	private static boolean filesMatch( String fileName1, String fileName2 )
	{
		if ( fileName1.length() == 0 && fileName2.length() == 0 )
		{
			return true;
		}
		
		File file1 = new File( fileName1 );
		File file2 = new File( fileName2 );
		if ( file1.equals( file2 ) )
		{
			return true;
		}
		
		return false;
	}
	
	public static ILaunchConfiguration createConfiguration( IProject project, IFile eglFile ) throws CoreException
	{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType( IRUIDebugConstants.ID_RUI_LAUNCH_TYPE );
		if ( configType == null )
		{
			abort( RUIDebugMessages.rui_debug_utils_config_type_not_found );
		}
		
		String fileName = getQualifiedFilename( eglFile );
		String programName = getFirstRUIHandler( eglFile );
		if ( programName == null )
		{
			abort( RUIDebugMessages.rui_debug_utils_no_handler_found );
		}
		
		String configName = launchManager.generateLaunchConfigurationName( programName );
		ILaunchConfigurationWorkingCopy configCopy = configType.newInstance( null, configName );
		if ( configCopy == null )
		{
			abort( RUIDebugMessages.rui_debug_utils_create_config_failed );
		}
		
		String projectName = null;
		if ( project != null )
		{
			projectName = project.getName();
		}
		
		configCopy.setAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName );
		configCopy.setAttribute( IRUILaunchConfigurationConstants.ATTR_HANDLER_FILE, fileName );
		return configCopy.doSave();
	}
	
	protected static ILaunchConfiguration chooseConfiguration( List configList )
	{
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog( DebugUtil.getShell(), labelProvider );
		dialog.setElements( configList.toArray() );
		dialog.setTitle( EGLLaunchingMessages.launch_config_selection_dialog_title );
		dialog.setMessage( EGLLaunchingMessages.launch_config_selection_dialog_message );
		dialog.setMultipleSelection( false );
		int result = dialog.open();
		labelProvider.dispose();
		if ( result == Window.OK )
		{
			return (ILaunchConfiguration)dialog.getFirstResult();
		}
		return null;
	}
	
	public static String getQualifiedFilename( IFile eglFile ) throws CoreException
	{
		String pathName = eglFile.getFullPath().toString();
		// Chop off project from file name
		int index = pathName.indexOf( '/', 1 );
		
		String fileName = pathName.substring( index + 1 );
		return fileName;
	}
	
	public static String getFirstRUIHandler( IFile handlerFile )
	{
		return getFirstRUIHandler( EGLCore.createEGLFileFrom( handlerFile ) );
	}
	
	public static String getFirstRUIHandler( IEGLFile handlerFile )
	{
		try
		{
			IPart[] parts = handlerFile.getParts();
			SourcePart srcPart = null;
			for ( int i = 0; i < parts.length; i++ )
			{
				if ( parts[ i ] instanceof SourcePart )
				{
					srcPart = (SourcePart)parts[ i ];
					if ( srcPart.isHandler() )
					{
						SourcePartElementInfo partInfo = (SourcePartElementInfo)((SourcePart)srcPart).getElementInfo();
						if ( partInfo.getSubTypeName() == null )
						{
							return null;
						}
						String typeName = InternUtil.intern( new String( partInfo.getSubTypeName() ) );
						if ( typeName == InternUtil.intern( "RUIHandler" ) || typeName == InternUtil.intern( "RUIWidget" ) ) //$NON-NLS-1$ //$NON-NLS-2$
						{
							return srcPart.getFullyQualifiedName();
						}
					}
				}
			}
		}
		catch ( org.eclipse.core.runtime.CoreException e )
		{
		}
		
		return null;
	}
	
	public static DebugContext createContext( ILaunchConfiguration config, ILaunch launch ) throws CoreException
	{
		RUIDebugTarget target = new RUIDebugTarget( launch );
		
		String project = config.getAttribute( IRUILaunchConfigurationConstants.ATTR_HANDLER_FILE, "" ); //$NON-NLS-1$
		String file = config.getAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, "" ); //$NON-NLS-1$
		
		// The following attributes are present when relaunching a debug session (browser refresh).
		String url = config.getAttribute( IRUILaunchConfigurationConstants.ATTR_URL, (String)null );
		int key = config.getAttribute( IRUILaunchConfigurationConstants.ATTR_CONTEXT_KEY, -1 );
		
		if ( key == -1 )
		{
			key = EvServer.getInstance().generateContextKey();
		}
		
		if ( url == null )
		{
			url = getDebugURL( project, file );
			if ( url.indexOf( "?" ) == -1 ) //$NON-NLS-1$
			{
				url = url + "?contextKey=" + key; //$NON-NLS-1$
			}
			else
			{
				url = url + "&contextKey=" + key; //$NON-NLS-1$
			}
		}
		
		DebugContext context = new DebugContext( url, new Integer( key ), target, config.getName() );
		target.setContext( context );
		return context;
	}
	
	public static void relaunchContext( DebugContext context ) throws CoreException
	{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType( IRUIDebugConstants.ID_RUI_LAUNCH_TYPE );
		ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations( configType );
		
		String configName = context.getLaunchConfigName();
		int size = configs == null
				? 0
				: configs.length;
		ILaunchConfiguration config = null;
		for ( int i = 0; i < size; i++ )
		{
			if ( configName.equals( configs[ i ].getName() ) )
			{
				config = configs[ i ];
				break;
			}
		}
		
		if ( config != null )
		{
			// If we are restarting a session, then this should always resolve fine.
			ILaunchConfigurationWorkingCopy copy = config.getWorkingCopy();
			copy.setAttribute( IRUILaunchConfigurationConstants.ATTR_URL, context.getUrl() );
			copy.setAttribute( IRUILaunchConfigurationConstants.ATTR_CONTEXT_KEY, context.getKey().intValue() );
			copy.launch( ILaunchManager.DEBUG_MODE, null );
		}
	}
	
	public static String getDebugURL( String file, String project )
	{
		String strPortNumber = Integer.toString( EvServer.getInstance().getPortNumber() );
		
		IEGLFile eglFile = EGLCore.createEGLFileFrom( ResourcesPlugin.getWorkspace().getRoot().getProject( project ).getFile( new Path( file ) ) );
		if ( eglFile != null && eglFile.exists() )
		{
			try
			{
				IPackageDeclaration[] pkgs = eglFile.getPackageDeclarations();
				if ( pkgs != null && pkgs.length != 0 )
				{
					file = pkgs[ 0 ].getElementName().replaceAll( "\\.", "/" ) + '/' + eglFile.getElementName(); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					// Default package.
					file = eglFile.getElementName();
				}
			}
			catch ( EGLModelException eme )
			{
			}
			finally
			{
				try
				{
					eglFile.close();
				}
				catch ( EGLModelException eme )
				{
				}
			}
		}
		else
		{
			// Couldn't create file - assume there's an EGLSource dir?
			file = file.replace( "EGLSource/", "" ); //$NON-NLS-1$ //$NON-NLS-2$
			int lastSlash = file.lastIndexOf( '/' );
			if ( lastSlash != -1 )
			{
				file = file.substring( 0, lastSlash ) + '/' + file.substring( lastSlash ); //$NON-NLS-1$
			}
		}
		
		// Remove .egl
		file = file.substring( 0, file.length() - 4 );
		
		StringBuffer strb = new StringBuffer();
		strb.append( "http://localhost:" ); //$NON-NLS-1$
		strb.append( strPortNumber );
		strb.append( '/' );
		strb.append( project );
		strb.append( '/' );
		strb.append( file );
		strb.append( ".html" ); //$NON-NLS-1$
		return strb.toString();
	}
	
	public static Image getImage( String key )
	{
		if ( !isImageRegistryInitialized() )
		{
			initializeImageRegistry();
		}
		return getImageRegistry().get( key );
	}
	
	public static boolean isImageRegistryInitialized()
	{
		return imageRegistryInitialized;
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
	
	public static ImageRegistry initializeImageRegistry()
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
		declareRegistryImage( IRUIDebugConstants.RUI_ICON_VARIABLE, "obj16/variable_obj.gif" ); //$NON-NLS-1$
		declareRegistryImage( IRUIDebugConstants.RUI_ICON_DEBUG_EXC, "etool16/debug_exc.gif" ); //$NON-NLS-1$
	}
	
	public static ImageRegistry getImageRegistry()
	{
		if ( imageRegistry == null )
		{
			imageRegistry = new ImageRegistry();
		}
		return imageRegistry;
	}
	
	/**
	 * @return <code>value</code> encoded so that it can be passed to the client and parsed via JavaScript's decodeURIComponent().
	 */
	public static String encodeValue( String value )
	{
		try
		{
			// URLEncoder doesn't escape exactly how decodeURIComponent works.
			return URLEncoder.encode( value, "UTF-8" ).replaceAll( "\\+", "%20" ).replaceAll( "\\%21", "!" ).replaceAll( "\\%27", "'" ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
					.replaceAll( "\\%28", "(" ).replaceAll( "\\%29", ")" ).replaceAll( "\\%7E", "~" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		}
		catch ( UnsupportedEncodingException e )
		{
			// Shouldn't happen, but just in case...
			return value.replaceAll( " ", "%20" ).replaceAll( "\\%21", "!" ).replaceAll( "\\%27", "'" ).replaceAll( "\\%28", "(" ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
					.replaceAll( "\\%29", ")" ).replaceAll( "\\%7E", "~" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}
	
	/**
	 * @return <code>value</code> decoded from JavaScript's decodeURIComponent() format.
	 */
	public static String decodeValue( String value )
	{
		try
		{
			return URLDecoder.decode( value, "UTF-8" ).replaceAll( "\\%20", " " ).replaceAll( "\\!", "\\%21" ).replaceAll( "\\'", "\\%27" ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
					.replaceAll( "\\(", "\\%28" ).replaceAll( "\\)", "\\%29" ).replaceAll( "\\~", "\\%7E" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		}
		catch ( UnsupportedEncodingException e )
		{
			// Shouldn't happen, but just in case...
			return value.replaceAll( "\\%20", " " ).replaceAll( "\\!", "\\%21" ).replaceAll( "\\'", "\\%27" ).replaceAll( "\\(", "\\%28" ) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
					.replaceAll( "\\)", "\\%29" ).replaceAll( "\\~", "\\%7E" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}
	
	/**
	 * @return the path of the resource relative to the source directory, or null if it was not in a source directory.
	 */
	public static String getRelativeBreakpointPath( IResource resource )
	{
		if ( resource != null && resource.getType() == IResource.FILE )
		{
			// Send the path of the resource, relative to its source directory.
			IEGLElement element = EGLCore.create( (IFile)resource );
			if ( element != null )
			{
				// Find the source folder
				IEGLElement parent = element.getParent();
				while ( parent != null && !(parent instanceof IPackageFragmentRoot) )
				{
					parent = parent.getParent();
				}
				
				if ( parent instanceof IPackageFragmentRoot )
				{
					return resource.getProjectRelativePath().removeFirstSegments( parent.getResource().getProjectRelativePath().segmentCount() )
							.toString();
				}
			}
		}
		return null;
	}
	
	/**
	 * @return the IResource for the breakpoint.
	 */
	public static IResource getBreakpointResource( IBreakpoint bp )
	{
		IResource resource = null;
		try
		{
			if ( bp instanceof EGLLineBreakpoint && ((EGLLineBreakpoint)bp).isRunToLine() )
			{
				// RTL is created on the workspace root so that the marker doesn't appear in the editor.
				// It will have stored its resource path.
				String path = bp.getMarker().getAttribute( IEGLDebugCoreConstants.RUN_TO_LINE_PATH, null );
				if ( path != null )
				{
					resource = ResourcesPlugin.getWorkspace().getRoot().findMember( path );
				}
			}
		}
		catch ( CoreException e )
		{
		}
		
		if ( resource == null )
		{
			resource = bp.getMarker().getResource();
		}
		
		return resource;
	}
}
