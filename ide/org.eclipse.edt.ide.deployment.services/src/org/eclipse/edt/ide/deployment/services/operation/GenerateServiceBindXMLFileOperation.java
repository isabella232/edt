/*******************************************************************************
 * Copyright � 2011 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.DeploymentResultMessageRequestor;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.services.generators.DeploymentDescUtil;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;

public class GenerateServiceBindXMLFileOperation extends AbstractDeploymentOperation {
	
	public static final String BIND_XML_FILE_SUFFIX = "-bnd.xml";
	
	private String targetProjectName;
	private DeploymentDesc ddModel;
	private DeploymentContext context;
	
	@Override
	public void preCheck(DeploymentContext context,
			IDeploymentResultsCollector resultsCollector,
			IProgressMonitor monitor) throws CoreException {
		if ( context.getStatus() != DeploymentContext.STATUS_SHOULD_RUN ) {
			DeploymentDesc desc = context.getDeploymentDesc();
			if ( desc.getRestBindings() != null && desc.getRestBindings().size() > 0 ) {
				context.setStatus( DeploymentContext.STATUS_SHOULD_RUN );
			}
		}
	}
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		ddModel = context.getDeploymentDesc();
		
		DeploymentResultMessageRequestor messageRequestor = new DeploymentResultMessageRequestor(resultsCollector);

		String javaSourceFolder = EclipseUtilities.getJavaSourceFolderName( context.getTargetProject() );

		try {
			InputStream is = new ByteArrayInputStream(DeploymentDescUtil.convertToBindXML(ddModel, context.getTargetProject()).getBytes("UTF-8"));
		
			IPath targetFilePath = new Path( "/" + context.getTargetProject().getName() + "/" + javaSourceFolder + "/" + ddModel.getName().toLowerCase() + BIND_XML_FILE_SUFFIX ); 
			
			IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetFilePath);
	
			if( targetFile.exists() ) {
				targetFile.setContents(is, true, true, monitor);
			} else {
				targetFile.create(is, true, monitor);
	//			targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
			}
	
			is.close();
			
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
					EGLMessage.EGL_DEPLOYMENT_DEPLOYED_BIND_FILE,
					null,
					new String[] { targetFile.getProjectRelativePath().toPortableString() }));
		} catch (Exception e) {
			messageRequestor.addMessage(DeploymentUtilities.createEGLDeploymentInformationalMessage(
					EGLMessage.EGL_DEPLOYMENT_EXCEPTION,
					null,
					new String[] { DeploymentUtilities.createExceptionMessage(e) }));
		}

	}
	
}