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
package org.eclipse.edt.ide.deployment.rui.operation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.gen.deployment.javascript.Constants;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.ide.rui.utils.DeployFileLocator;
import org.eclipse.edt.ide.rui.utils.EGLResource;
import org.eclipse.edt.ide.rui.utils.FileLocator;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;

public class CopyJSRuntimeResourcesOperation extends AbstractDeploymentOperation {

	private static final String RUNTIME_FOLDER = "runtime/";
	
	private String targetProjectName;
	private DeploymentDesc model;
	private DeploymentContext context;
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		if ( context.getDeploymentDesc().getRUIApplication() == null || context.getDeploymentDesc().getRUIApplication().getRUIHandlers().size() == 0 ) {
			return;
		}
		
		this.context = context;
		model = context.getDeploymentDesc();
		targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
		IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);
		
		IFolder projectRootFolder = Utils.getContextDirectory(targetProject);
		
		DeployFileLocator jsFileLocator = new DeployFileLocator( context.getSourceProject() );
		
		copyFiles(Constants.RUI_RUNTIME_JAVASCRIPT_FILES, projectRootFolder, jsFileLocator, monitor);
		copyFiles(FileLocator.RUI_RUNTIME_PROPERTIES_FILES, projectRootFolder, jsFileLocator, monitor);
	}

	private void copyFiles(List sourceFiles, IFolder projectRootFolder,
			DeployFileLocator jsFileLocator, IProgressMonitor monitor) throws CoreException {
		for ( int i = 0; i < sourceFiles.size(); i ++ ) {
			String fileName = (String)sourceFiles.get( i );
			EGLResource resource = jsFileLocator.findResource( fileName );
			if ( resource == null ) {
				continue;
			}
			IPath path = new Path( RUNTIME_FOLDER + fileName );
			IPath targetFilePath = projectRootFolder.getFullPath().append( path );
			CoreUtility.createFolder( ResourcesPlugin.getWorkspace().getRoot().getFolder( targetFilePath.removeLastSegments( 1 ) ), true, true, monitor );
			InputStream is = null;
			try {
				IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetFilePath);
				is = resource.getInputStream();
				
				boolean isJS = targetFile.getFileExtension().equalsIgnoreCase( "js" ) ;
				if( targetFile.exists() ) {
					if( resource.getLocalTimeStamp() != targetFile.getLocalTimeStamp() ){
						if ( isJS ) {
							is = Utils.shrinkJavascript( is,  resource.getFullName() );
						}
						targetFile.setContents(is, true, false, monitor);
					}
				} else {
					if ( isJS ) {
						is = Utils.shrinkJavascript( is,  resource.getFullName() );
					}
					targetFile.create(is, true, monitor);
				}
				targetFile.setLocalTimeStamp(resource.getLocalTimeStamp());
			} catch (IOException e) {
			} finally {
				try {
					if ( is != null ) {
						is.close();
					}
				} catch ( IOException ioe ) {
					// do nothing
				}
						
			}
		}
	}
}
