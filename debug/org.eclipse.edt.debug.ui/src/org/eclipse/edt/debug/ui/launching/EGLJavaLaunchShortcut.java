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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class EGLJavaLaunchShortcut extends AbstractEGLLaunchShortcut
{
	@Override
	public void doLaunch( IFile eglFile, String mode )
	{
		try
		{
			ILaunchConfiguration config = findLaunchConfiguration( eglFile.getProject(), eglFile );
			if ( config != null )
			{
				DebugUITools.launch( config, mode );
			}
		}
		catch ( CoreException e )
		{
			MessageDialog.openError( DebugUtil.getShell(), EGLLaunchingMessages.launch_error_dialog_title, e.getMessage() );
		}
	}
	
	protected ILaunchConfiguration findLaunchConfiguration( IProject project, IFile eglFile ) throws CoreException
	{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType( IEGLJavaLaunchConstants.CONFIG_TYPE_MAIN_PROGRAM );
		if ( configType == null )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_type_not_found );
		}
		String fileName = getQualifiedFilename( eglFile );
		ILaunchConfiguration[] configs = launchManager.getLaunchConfigurations( configType );
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>( configs.length );
		for ( int i = 0; i < configs.length; i++ )
		{
			ILaunchConfiguration config = configs[ i ];
			if ( config.getAttribute( IEGLJavaLaunchConstants.ATTR_PROGRAM_FILE, "" ).equals( fileName ) ) //$NON-NLS-1$
			{
				String configProjectName = config.getAttribute( IEGLJavaLaunchConstants.ATTR_PROJECT_NAME, "" ); //$NON-NLS-1$
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
	
	protected void abort( String message ) throws CoreException
	{
		Status status = new Status( IStatus.ERROR, EDTDebugUIPlugin.PLUGIN_ID, IStatus.ERROR, message, null );
		throw new CoreException( status );
	}
	
	protected String getQualifiedFilename( IFile eglFile ) throws CoreException
	{
		String pathName = eglFile.getFullPath().toString();
		// Chop off project from file name
		int index = pathName.indexOf( '/', 1 );
		
		return pathName.substring( index + 1 );
	}
	
	protected ILaunchConfiguration createConfiguration( IProject project, IFile eglFile ) throws CoreException
	{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType( IEGLJavaLaunchConstants.CONFIG_TYPE_MAIN_PROGRAM );
		if ( configType == null )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_type_not_found );
		}
		
		String fileName = getQualifiedFilename( eglFile );
		String programName = getFirstProgram( (IEGLFile)EGLCore.create( eglFile ) );
		if ( programName == null )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_no_program_found );
		}
		
		String configName = launchManager.generateLaunchConfigurationName( programName );
		ILaunchConfigurationWorkingCopy configCopy = configType.newInstance( null, configName );
		if ( configCopy == null )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_create_config_failed );
		}
		
		String projectName = null;
		if ( project != null )
		{
			projectName = project.getName();
		}
		
		configCopy.setAttribute( IEGLJavaLaunchConstants.ATTR_PROJECT_NAME, projectName );
		configCopy.setAttribute( IEGLJavaLaunchConstants.ATTR_PROGRAM_FILE, fileName );
		EGLJavaLaunchUtils.addJavaAttributes( projectName, configCopy );
		
		return configCopy.doSave();
	}
	
	protected ILaunchConfiguration chooseConfiguration( List configList )
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
	
	protected String getFirstProgram( IEGLFile programFile )
	{
		if ( programFile != null )
		{
			try
			{
				IPart[] parts = programFile.getParts();
				SourcePart srcPart = null;
				for ( int i = 0; i < parts.length; i++ )
				{
					if ( parts[ i ] instanceof SourcePart )
					{
						srcPart = (SourcePart)parts[ i ];
						if ( srcPart.isProgram() )
						{
							return srcPart.getFullyQualifiedName();
						}
					}
				}
			}
			catch ( CoreException e )
			{
				EDTDebugUIPlugin.log( e );
			}
		}
		
		return null;
	}
}
