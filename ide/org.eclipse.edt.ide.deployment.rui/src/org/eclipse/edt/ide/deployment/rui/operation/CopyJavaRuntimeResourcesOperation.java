/*******************************************************************************
 * Copyright Â© 2011 IBM Corporation and others.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.operation.IDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;

public class CopyJavaRuntimeResourcesOperation extends AbstractDeploymentOperation {

	private static final String WEBLIB_FOLDER = "WEB-INF/lib/";
	private static final String JAVARUNTIME_NAME = "org.eclipse.edt.runtime.java.jar";
	
	private String targetProjectName;
	private DeploymentDesc model;
	private DeploymentContext context;
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		model = context.getDeploymentDesc();
		IFolder projectRootFolder = Utils.getContextDirectory( context.getTargetProject() );
		
		String javaRuntimeLoc = Platform.getBundle("org.eclipse.edt.runtime.java").getLocation();
		if ( !javaRuntimeLoc.endsWith( ".jar" ) ) {
			return;
		}
		int index = javaRuntimeLoc.indexOf( ":/" );
		javaRuntimeLoc = javaRuntimeLoc.substring( index + 2 );
		IPath path = new Path( WEBLIB_FOLDER + JAVARUNTIME_NAME );
		IPath targetFilePath = projectRootFolder.getFullPath().append( path );
		CoreUtility.createFolder( ResourcesPlugin.getWorkspace().getRoot().getFolder( targetFilePath.removeLastSegments( 1 ) ), true, true, monitor );
		try {

			FileInputStream fis = new FileInputStream( new File( javaRuntimeLoc ) ) ;
			
			IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetFilePath);
			if( targetFile.exists() ) {
				targetFile.setContents(fis, true, true, monitor);
			} else {
				targetFile.create(fis, true, monitor);
//					targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
			}
			fis.close();
//				DeploymentUtilities.copyFile(resource.getInputStream(), path.toFile().getPath() );
		} catch (IOException e) {
		}
	}
}
