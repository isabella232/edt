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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.internal.web.WebXML;
import org.eclipse.edt.ide.deployment.internal.web.WebXMLManager;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.operation.IDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.IConstants;
import org.eclipse.edt.ide.deployment.rui.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;

public class UpdateWebXMLOperation extends AbstractDeploymentOperation {

	private String targetProjectName;
	private DeploymentDesc model;
	private DeploymentContext context;
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		
		if ( context.getDeploymentDesc().getRUIApplication() == null || context.getDeploymentDesc().getRUIApplication().getRUIHandlers().size() == 0 ) {
			return;
		}
		
		/**
		 * configure the __proxy
		 */
		monitor.subTask(Messages.J2EEDeploymentOperation_1);

		this.context = context;
		model = context.getDeploymentDesc();
		targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
		IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);
		
		deployProxy( targetProject );
		
		WebXMLManager.instance.updateModel( targetProject );

		monitor.worked(1);
	}
	
	private void deployProxy(IProject project) {
		/**
		 * register the servlet
		 */
		String servletName = IConstants.RUI_PROXY_SERVLET_NAME;
		String servletClassName = IConstants.RUI_PROXY_SERVLET; 
		String mapping = IConstants.RUI_PROXY_MAPPING; 
		Map<String, String> parameterList = new HashMap<String, String>();
		boolean isWebsphere = false; //ServiceUtilities.isWebsphereRuntime(ServiceUtilities.getRuntime(project)); TODO - EDT
		if( isWebsphere )
		{
			parameterList.put("isOnWebSphere","true");
		}
	
		//Add the compression filter into web.xml
		String filterName = IConstants.RUI_COMPRESS_FILTER_NAME;
		String filterClassName = IConstants.RUI_COMPRESS_FILTER_CLASSNAME;

		WebXML util = WebXMLManager.instance.getWebXMLUtil(project);
		if( util != null )
		{
			util.addServlet(servletName, servletClassName, mapping, parameterList);
			util.addFilter(filterName, filterClassName, IConstants.urlMappingList, IConstants.initParameterList);
		}

	}
}
