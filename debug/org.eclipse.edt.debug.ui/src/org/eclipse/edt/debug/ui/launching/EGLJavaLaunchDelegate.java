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
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.edt.ide.deployment.services.internal.testserver.DDUtil;
import org.eclipse.edt.ide.deployment.services.internal.testserver.DeploymentDescriptorFinder;
import org.eclipse.edt.ide.testserver.ClasspathUtil;
import org.eclipse.edt.ide.testserver.TestServerIDEConnector;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.dialogs.ListDialog;

public class EGLJavaLaunchDelegate extends JavaLaunchDelegate
{
	@Override
	public void launch( ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor ) throws CoreException
	{
		String projectName = configuration.getAttribute( IEGLJavaLaunchConstants.ATTR_PROJECT_NAME, (String)null );
		if ( projectName == null || projectName.trim().length() < 1 )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_no_project_specified );
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectName );
		if ( project == null || !project.exists() )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_invalid_project );
		}
		
		String programFileName = configuration.getAttribute( IEGLJavaLaunchConstants.ATTR_PROGRAM_FILE, "" ).trim(); //$NON-NLS-1$
		if ( programFileName.length() < 1 )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_no_program_file_specified );
		}
		
		IFile file = project.getFile( programFileName );
		if ( file == null || !file.exists() )
		{
			abort( EGLLaunchingMessages.egl_java_main_launch_configuration_invalid_program_file );
		}
		
		final List<IFile> files = EGLLaunchableTester.getMainJavaOutputFiles( file );
		if ( files == null || files.size() == 0 )
		{
			abort( NLS.bind( EGLLaunchingMessages.java_launch_no_files, file.getName() ) );
		}
		
		IFile javaFile = null;
		if ( files.size() > 1 )
		{
			final ListDialog dialog = new ListDialog( DebugUtil.getShell() );
			dialog.setTitle( EGLLaunchingMessages.java_launch_file_selection_title );
			dialog.setMessage( EGLLaunchingMessages.java_launch_file_selection_msg );
			dialog.setContentProvider( new IStructuredContentProvider() {
				@Override
				public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
				{
				}
				
				@Override
				public void dispose()
				{
				}
				
				@Override
				public Object[] getElements( Object inputElement )
				{
					return files.toArray();
				}
			} );
			dialog.setLabelProvider( new LabelProvider() {
				public String getText( Object element )
				{
					if ( element instanceof IFile )
					{
						return ((IFile)element).getFullPath().toString();
					}
					return super.getText( element );
				}
			} );
			dialog.setInput( files );
			
			final IFile[] selectedFile = new IFile[ 1 ];
			EGLJavaLaunchUtils.getStandardDisplay().syncExec( new Runnable() {
				public void run()
				{
					if ( dialog.open() == Window.OK )
					{
						Object[] result = dialog.getResult();
						if ( result != null && result.length > 0 && result[ 0 ] instanceof IFile )
						{
							selectedFile[ 0 ] = (IFile)result[ 0 ];
						}
					}
				}
			} );
			javaFile = selectedFile[ 0 ];
		}
		else
		{
			javaFile = files.get( 0 );
		}
		
		if ( javaFile != null && javaFile.exists() )
		{
			ILaunchConfigurationWorkingCopy copy = configuration.getWorkingCopy();
			
			String className = null;
			IJavaElement javaElement = JavaCore.create( javaFile );
			if ( javaElement instanceof ITypeRoot )
			{
				IType type = ((ITypeRoot)javaElement).findPrimaryType();
				if ( type != null )
				{
					className = type.getFullyQualifiedName();
				}
			}
			
			if ( className != null )
			{
				// set VM args required by the main program launcher class
				String vmArgs = copy.getAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "" ); //$NON-NLS-1$
				vmArgs += "-Degl.main.class.name=\"" + className + "\" -Degl.ide.port=\"" + TestServerIDEConnector.getInstance().getPortNumber() //$NON-NLS-1$ //$NON-NLS-2$
						+ "\" -Degl.dd.list=\"" //$NON-NLS-1$
						+ DeploymentDescriptorFinder.toArgumentString( DeploymentDescriptorFinder.findDeploymentDescriptors( project ) )
						+ "\" -Degl.default.dd=\"" + DeploymentDescriptorFinder.getDefaultDDName( project ) + "\""; //$NON-NLS-1$ //$NON-NLS-2$
				copy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs );
				
				// Set the Java project, which might be different than the egl project.
				copy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, javaFile.getProject().getName() );
				
				// Add the JDBC jars to the classpath.
				@SuppressWarnings("unchecked")
				List<String> classpath = copy.getAttribute( IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, new ArrayList<String>() );
				
				// The main project.
				classpath.add( ClasspathUtil.getWorkspaceProjectClasspathEntry( javaFile.getProject().getName() ) );
				
				// For each project on the EGL path, add it to the classpath if it's a Java project and not already on the Java path of the launching
				// project.
				ClasspathUtil.addEGLPathToJavaPathIfNecessary( JavaCore.create( javaFile.getProject() ), project, new HashSet<IProject>(), classpath );
				
				// Finally, add any JDBC jars that might be required.
				DDUtil.addJDBCJars( project, new HashSet<IProject>(), new HashSet<IResource>(), classpath );
				
				copy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath );
				copy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false );
				
				super.launch( copy, mode, launch, monitor );
			}
			else
			{
				abort( NLS.bind( EGLLaunchingMessages.egl_java_main_launch_configuration_missing_java_type, javaFile.getFullPath().toString() ) );
			}
		}
	}
	
	protected void abort( String message ) throws CoreException
	{
		Status status = new Status( IStatus.ERROR, EDTDebugUIPlugin.PLUGIN_ID, IStatus.ERROR, message, null );
		throw new CoreException( status );
	}
}
