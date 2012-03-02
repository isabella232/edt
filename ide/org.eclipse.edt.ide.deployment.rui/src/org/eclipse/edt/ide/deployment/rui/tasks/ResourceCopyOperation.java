/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.ResourceDeploymentModel;
import org.eclipse.edt.ide.deployment.rui.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.rui.internal.util.WebUtilities;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;

public class ResourceCopyOperation {

	private static final String WEB_LIB_FOLDER  = "WEB-INF/lib/";
	private IProject targetProject;
	public ResourceCopyOperation(final IProject targetProject) {
		this.targetProject = targetProject;
	}
	
	/**
	 * Deploy any optionally selected artifacts 
	 * @throws EGLRIADeploymentException 
	 */
	public void copyModelResources( final ResourceDeploymentModel model, final IProgressMonitor parentMonitor, IDeploymentResultsCollector resultsCollector) {
		IProgressMonitor monitor = new SubProgressMonitor(parentMonitor, 70, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		monitor.setTaskName(Messages.J2EEDeploymentSolutionProgress_13);
		monitor.beginTask("", calculateFileNumber(model));
		try
		{
			IPath targetPath = WebUtilities.getWebModulePath(targetProject);
			createFolders(model.getJavascriptFolders(), targetPath, monitor, resultsCollector);
			copyFiles(model.getJavascriptFiles(), targetPath, monitor, resultsCollector);
			
			targetPath = WebUtilities.getWebModulePath(targetProject);
			targetPath = targetPath.append(WEB_LIB_FOLDER);
			createFolders(model.getJavaJarFolders(), targetPath, monitor, resultsCollector);
			copyFiles(model.getJavaJarFiles(), targetPath, monitor, resultsCollector);
			
			String javaSourceFolder = EclipseUtilities.getJavaSourceFolderName(targetProject);
			targetPath = targetProject.getFullPath().append(javaSourceFolder);
			createFolders(model.getJavaFolders(), targetPath, monitor, resultsCollector);
			copyFiles(model.getJavaFiles(), targetPath, monitor, resultsCollector);
		}finally{
			monitor.done();
		}
		
	}
	
	//To get the total file number for subProgressMonitor
	private int calculateFileNumber(ResourceDeploymentModel model){
		int totalNumber = 0;
		Map<IFolder, Set<IFile>> filesToCopy =new HashMap<IFolder, Set<IFile>>();
		filesToCopy.putAll(model.getJavaFiles());
		filesToCopy.putAll(model.getJavascriptFiles());
		filesToCopy.putAll(model.getJavaJarFiles());
		
		for( Iterator<Entry<IFolder, Set<IFile>>> itr1 = filesToCopy.entrySet().iterator(); itr1.hasNext(); ) {
			Entry<IFolder, Set<IFile>> entry = itr1.next();
			totalNumber += entry.getValue().size();
		}
		
		return totalNumber;
	}
	
	private void createFolders(final Map<IFolder, Set<IFolder>> folders2Create, final IPath targetPath, final IProgressMonitor monitor, final IDeploymentResultsCollector resultsCollector)
	{

		for( Iterator<Entry<IFolder, Set<IFolder>>> itr1 = folders2Create.entrySet().iterator(); itr1.hasNext() && !monitor.isCanceled(); ) {

			Entry<IFolder, Set<IFolder>> entry = itr1.next();
			for( Iterator<IFolder> itr2 = entry.getValue().iterator(); itr2.hasNext() && !monitor.isCanceled(); ) {
				IFolder folder = itr2.next();
				if( !folder.getProject().equals(targetProject) )
				{
					monitor.subTask(folder.getProjectRelativePath().toOSString());
					try
					{
						IPath relativeSourcePath = folder.getProjectRelativePath().removeFirstSegments(entry.getKey().getProjectRelativePath().segmentCount());
						IFolder targetFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(targetPath.append(relativeSourcePath));
						if( !targetFolder.exists() ){
							CoreUtility.createFolder( targetFolder, true, true, monitor );
						}
					}catch(Exception e)
					{
						resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, Messages.bind(Messages.J2EEDeploymentSolution_27, new String[]{folder.getProjectRelativePath().toOSString(), targetProject.getName()})));
					}
				}
			}
		}
		
	}
	private void copyFiles(final Map<IFolder, Set<IFile>> files2Copy, final IPath targetPath, final IProgressMonitor monitor, final IDeploymentResultsCollector resultsCollector)
	{
		for( Iterator<Entry<IFolder, Set<IFile>>> itr1 = files2Copy.entrySet().iterator(); itr1.hasNext() && !monitor.isCanceled(); ) {
			
			Entry<IFolder, Set<IFile>> entry = itr1.next();
			for( Iterator<IFile> itr2 = entry.getValue().iterator(); itr2.hasNext() && !monitor.isCanceled(); ) {
				IFile file = itr2.next();
				if( !file.getProject().equals(targetProject) ) {
					monitor.subTask(file.getProjectRelativePath().toOSString());
					InputStream is = null;
					try
					{
						IPath relativeSourcePath = file.getProjectRelativePath().removeFirstSegments(entry.getKey().getProjectRelativePath().segmentCount());
						IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetPath.append(relativeSourcePath));
						
						if( targetFile.exists() ) {
							if( file.getLocalTimeStamp() != targetFile.getLocalTimeStamp() ){
								if ( "js".equalsIgnoreCase( file.getFileExtension() ) ) {
									is = Utils.shrinkJavascript( file.getContents(), file.getName() );
								} else {
									is = file.getContents();
								}
								targetFile.setContents(is, true, false, monitor);
								targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
							}
						}
						else {
							if ( "js".equalsIgnoreCase( file.getFileExtension() ) ) {
								is = Utils.shrinkJavascript( file.getContents(), file.getName() );
							} else {
								is = file.getContents();
							}
							targetFile.create(is, true, monitor);
							targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
						}
						
						monitor.worked(1);
					} catch ( Exception e ) {
						resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, Messages.bind(Messages.J2EEDeploymentSolution_26, new String[]{file.getProjectRelativePath().toOSString(), targetProject.getName()})));
						resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, Messages.bind(Messages.J2EEDeploymentSolution_Exception, new String[]{DeploymentUtilities.createExceptionMessage(e)})));
					} finally {
						try {
							if ( is != null ) {
								is.close();
							}
						} catch ( IOException ioe ) {
							//do nothing
						}
					}
				}
			}
		}
		
	}

//	void updateFda7Jar(final IProgressMonitor monitor) 
//	{
//		try{
//			monitor.subTask(Messages.J2EEDeploymentSolutionProgress_14);
//			WebUtilities.addRuntimeJars(targetProject);
//		}
//		catch( OperationCanceledException oce){
//		}
//	}	


	
}
