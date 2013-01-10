/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.operation;

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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.javart.resources.egldd.Binding;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;

public class CopyJDBCJarOperation extends AbstractDeploymentOperation {
	
	private static final String WEBLIB_FOLDER = "WEB-INF/lib/";
	public static final String SEPERATOR = ";";
	
	private String targetProjectName;
	private DeploymentDesc ddModel;
	private DeploymentContext context;
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		ddModel = context.getDeploymentDesc();
		IFolder projectRootFolder = Utils.getContextDirectory( context.getTargetProject() );
		
		try {
			IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();
			for ( Binding binding: ddModel.getBindings() ) {
				if ( binding instanceof SQLDatabaseBinding ) {
					SQLDatabaseBinding sqlBinding = (SQLDatabaseBinding)binding;
					String jarList = sqlBinding.getJarList();
					if ( jarList == null )
						continue;
					
					String[] jars = jarList.split( SEPERATOR );
					for ( String jarPath : jars ) {
						File file = new File( jarPath );
						if ( !file.exists() ) {
							continue;
						}
			
						FileInputStream fis = new FileInputStream( file ) ;

						IPath path = new Path( WEBLIB_FOLDER + file.getName() );
						IPath targetFilePath = projectRootFolder.getFullPath().append( path );
						CoreUtility.createFolder( ResourcesPlugin.getWorkspace().getRoot().getFolder( targetFilePath.removeLastSegments( 1 ) ), true, true, monitor );
						
						IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetFilePath);
						if( targetFile.exists() ) {
							targetFile.setContents(fis, true, true, monitor);
						} else {
							targetFile.create(fis, true, monitor);
//							targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
						}
						fis.close();
//						DeploymentUtilities.copyFile(resource.getInputStream(), path.toFile().getPath() );
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
