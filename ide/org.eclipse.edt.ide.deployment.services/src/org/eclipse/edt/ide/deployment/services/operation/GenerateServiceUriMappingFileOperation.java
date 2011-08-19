/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.deployment.operation.IDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.services.generators.ServiceUriMappingGenerator;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.Service;

public class GenerateServiceUriMappingFileOperation  implements IDeploymentOperation {
	
	public static final String BIND_XML_FILE_SUFFIX = "-uri.xml";
	
	private String targetProjectName;
	private DeploymentDesc ddModel;
	private DeploymentContext context;
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		ddModel = context.getDeploymentDesc();
		
		List restServices = ddModel.getRestservices();
		
		for ( int i = 0; i < restServices.size(); i ++ ) {
			Restservice restService = (Restservice)restServices.get( i );
			String partName = restService.getImplementation();
			try {
				Part service = context.findPart( partName );
				if ( service instanceof Service ) {
					ServiceUriMappingGenerator generator = new ServiceUriMappingGenerator( context );
					generator.visit( (Service)service );
				}
			} catch (PartNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		String javaSourceFolder = EclipseUtilities.getJavaSourceFolderName( context.getTargetProject() );

//		try {
//			InputStream is = new ByteArrayInputStream(ddModel.toBindXML().getBytes("UTF-8"));
//		
//			IPath targetFilePath = new Path( "/" + context.getTargetProject().getName() + "/" + javaSourceFolder + "/" + ddModel.getName().toLowerCase() + BIND_XML_FILE_SUFFIX ); 
//			
//			IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetFilePath);
//	
//			if( targetFile.exists() ) {
//				targetFile.setContents(is, true, true, monitor);
//			} else {
//				targetFile.create(is, true, monitor);
//	//			targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
//			}
//	
//			is.close();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}
