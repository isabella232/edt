/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.DeploymentResultMessageRequestor;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.services.generators.ServiceUriMappingGenerator;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Service;

public class GenerateServiceUriMappingFileOperation extends AbstractDeploymentOperation {
	
	public static final String BIND_XML_FILE_SUFFIX = "-uri.xml";
	
	private String targetProjectName;
	private DeploymentDesc ddModel;
	private DeploymentContext context;
	
	@Override
	public void preCheck(DeploymentContext context,
			IDeploymentResultsCollector resultsCollector,
			IProgressMonitor monitor) throws CoreException {
		if ( context.getStatus() != DeploymentContext.STATUS_SHOULD_RUN ) {
			DeploymentDesc desc = context.getDeploymentDesc();
			if ( desc.getRestservices() != null && desc.getRestservices().size() > 0 ) {
				context.setStatus( DeploymentContext.STATUS_SHOULD_RUN );
			}
		}
	}
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		ddModel = context.getDeploymentDesc();
		
		List restServices = ddModel.getRestservices();
		
		DeploymentResultMessageRequestor messageRequestor = new DeploymentResultMessageRequestor(resultsCollector);
		IEGLProject eglProject = EGLCore.create(context.getSourceProject());

		for ( int i = 0; i < restServices.size(); i ++ ) {
			Restservice restService = (Restservice)restServices.get( i );
			String partName = restService.getImplementation();
			try {
				Part service = context.findPart( partName );
				IPath  path = eglProject.findPart( service.getFullyQualifiedName() ).getEGLFile().getPath();
				IResource serviceFile = ResourcesPlugin.getWorkspace().getRoot().findMember( path );
				if ( serviceFile != null && serviceFile.findMaxProblemSeverity(null, false, 1) == IMarker.SEVERITY_ERROR ) {
					messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
							EGLMessage.EGL_DEPLOYMENT_FAILED,
							null,
							new String[] { service.getFullyQualifiedName() }));
					return;
				}
				if ( service instanceof Service ) {
					ServiceUriMappingGenerator generator = new ServiceUriMappingGenerator( context );
					generator.visit( (Service)service, restService );
				}
				messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
						EGLMessage.EGL_DEPLOYMENT_COMPLETE,
						null,
						new String[] { service.getFullyQualifiedName() }));
			} catch (Exception e) {
				messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentErrorMessage(
						EGLMessage.EGL_DEPLOYMENT_FAILED,
						null,
						new String[] { DeploymentUtilities.createExceptionMessage(e) }));
			}
		}
		
		
		
		
//		String javaSourceFolder = EclipseUtilities.getJavaSourceFolderName( context.getTargetProject() );

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
