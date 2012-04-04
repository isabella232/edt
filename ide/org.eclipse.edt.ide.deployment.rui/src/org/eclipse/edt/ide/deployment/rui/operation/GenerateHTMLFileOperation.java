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

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.deployment.core.IDeploymentConstants;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.DeploymentTarget;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.model.RUIDeploymentModel;
import org.eclipse.edt.ide.deployment.rui.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.rui.tasks.J2EERUIDeploymentOperation;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.javart.resources.egldd.Parameter;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;

public class GenerateHTMLFileOperation extends AbstractDeploymentOperation {

	private String targetProjectName;
	private DeploymentDesc model;
	private DeploymentContext context;
	
	@Override
	public void preCheck(DeploymentContext context,
			IDeploymentResultsCollector resultsCollector,
			IProgressMonitor monitor) throws CoreException {
		if ( context.getStatus() != DeploymentContext.STATUS_SHOULD_RUN ) {
			DeploymentDesc desc = context.getDeploymentDesc();
			if ( desc.getRUIApplication() !=  null && desc.getRUIApplication().getRUIHandlers() != null && desc.getRUIApplication().getRUIHandlers().size() > 0 ) {
				context.setStatus( DeploymentContext.STATUS_SHOULD_RUN );
			}
		}
	}
	
	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		model = context.getDeploymentDesc();
		targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
		IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);

		if( !EclipseUtilities.isWebProject(targetProject))
		{
			resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, Messages.bind(Messages.J2EEDeploymentSolution_95, new String[] {targetProjectName})));
			return;
		}
		if( !monitor.isCanceled() ){
			removeJavaScriptValidation(targetProject, monitor);
			deployRUISolutions(monitor, resultsCollector);
		}
	}

	private void removeJavaScriptValidation(final IProject project, final IProgressMonitor monitor)
	{
		try {
			// check if the project has the JavaScript (wst.jsdt.web) facet installed
	        IFacetedProject facetedProject = ProjectFacetsManager.create(project);
	        IProjectFacet javaScriptFacet = ProjectFacetsManager.getProjectFacet("wst.jsdt.web"); //$NON-NLS-1$
	        if (facetedProject.hasProjectFacet(javaScriptFacet))
	        {
	            // the JavaScript facet is already installed, clear the JavaScript include path to disable validation
	            setJavaScriptIncludePath(project, monitor);
	        }
		}
		catch ( CoreException ce) {
		}
	}

	private void setJavaScriptIncludePath(IProject project, IProgressMonitor monitor)
    {
        try
        {
            IJavaScriptProject javaScriptProject = JavaScriptCore.create(project);
            javaScriptProject.setRawIncludepath(new IIncludePathEntry[0], monitor);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
	private void deployRUISolutions(IProgressMonitor monitor, final IDeploymentResultsCollector resultsCollector) {
		try{
			DeploymentTarget target = model.getDeploymentTarget();
			String contextRoot = "";
			if( target != null )
			{
				for (Iterator parameterIterator = target.getParameters().iterator(); parameterIterator.hasNext();) {
					Parameter parameter = (Parameter) parameterIterator.next();
					if(IDeploymentConstants.PARAMETER_CONTEXT_ROOT.equals(parameter.getName())){
						contextRoot = parameter.getValue();
					}
				}
			}
			RUIDeploymentModel ruiModel = new RUIDeploymentModel(model.getRUIApplication(), context.getSourceProject(), targetProjectName, DeploymentUtilities.getAllEglddsName(context), contextRoot, resultsCollector);
			
			if(ruiModel.getHandlerLocales().size() > 0){
				if(ruiModel.getSourceRUIHandlers().size() > 0){
					J2EERUIDeploymentOperation deployOp = new J2EERUIDeploymentOperation(ruiModel);
					deployOp.execute(monitor, resultsCollector);
				}else{
					// a RUI Model might have no handlers if none of them validated correctly.  
					// We already issued error messages for the validation failures, so don't do anything else
				}
			}else{
				resultsCollector.addMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.J2EEDeploymentSolution_68));
			}
		}catch(CoreException e) {
			resultsCollector.addMessage(e.getStatus());
		}catch( Throwable t ) {
			t.printStackTrace();
			resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, t.toString()));
			Utils.buildStackTraceMessages(resultsCollector, t);
		}
	}
}
