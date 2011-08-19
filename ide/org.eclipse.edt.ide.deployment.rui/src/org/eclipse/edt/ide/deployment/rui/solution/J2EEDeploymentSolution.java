/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.solution;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.deployment.core.IDeploymentConstants;
import org.eclipse.edt.ide.deployment.core.model.DeploymentTarget;
import org.eclipse.edt.ide.deployment.core.model.Parameter;
import org.eclipse.edt.ide.deployment.core.model.RUIApplication;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.DeploymentModel;
import org.eclipse.edt.ide.deployment.rui.DeploymentModel.DeploymentSolution;
import org.eclipse.edt.ide.deployment.rui.internal.model.RUIDeploymentModel;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.rui.tasks.J2EERUIDeploymentOperation;
import org.eclipse.edt.ide.deployment.solution.AbstractDeploymentSolution;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;

/**
 * 
 * @version 1.0
 *
 */
public class J2EEDeploymentSolution extends AbstractDeploymentSolution {

	private DeploymentModel model;
	private String targetProjectName;
	
	public void setModel(DeploymentModel model) {
		this.model = model;		
	}

	public void execute(IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException {
//		targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
//		IDeploymentResultsCollector resultsCollector = DeploymentResultsCollectorManager.getInstance().getCollector(targetProjectName, model.getName(), false, model.isCMDMode());
////		targetProjectName = RUIDeployUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
//		
///*		TODO - EDT
//		BuildDescriptor bd = null;
//		IFile bdFile = null;
//		if( model.getDeploymentTarget().getTargetType() == DeploymentTarget.TARGET_BUILD_DESCRIPTOR )
//		{
//			IGenerationMessageRequestor messageRequestor = new DeploymentResultMessageRequestor(resultsCollector);
//			bdFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(((DeploymentBuildDescriptor)model.getDeploymentTarget()).getFileName()));
//			bd = new JavaGenerationOperation().createBuildDescriptor(bdFile, ((DeploymentBuildDescriptor)model.getDeploymentTarget()).getName(), messageRequestor);
//		}
//*/
//		// TODO - EDT just for now
//		BuildDescriptor bd = new BuildDescriptor();
//		IFile bdFile = null;
//		// End
//		executeWeb(bd, bdFile, resultsCollector, monitor);
		super.execute(resultsCollector, monitor);
	}
	
	/* TODO - EDT	
	private void executeWeb(BuildDescriptor bd, IFile bdFile, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) {
		if( model.hasParts() )
		{
			IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);
			if( targetProject != null && targetProject.exists() && targetProject.isOpen() )
			{

				if( !EclipseUtilities.isWebProject(targetProject))
				{
					resultsCollector.addMessage(RUIDeployUtilities.createStatus(IStatus.ERROR, Messages.bind(Messages.J2EEDeploymentSolution_95, new String[] {targetProjectName})));
					return;
				}

				if( !monitor.isCanceled() ){
					removeJavaScriptValidation(targetProject, monitor);
					deployRUISolutions(monitor, resultsCollector);
				}
				monitor.subTask("");
				TargetDeploymentModel targetDeploymentModel = new TargetDeploymentModel(model);
				ResourceCopyOperation resourceCopyOperation = new ResourceCopyOperation(targetProject);
				if( !monitor.isCanceled() ){
					resourceCopyOperation.updateFda7Jar(monitor);
				}
				if( !monitor.isCanceled() ){
					resourceCopyOperation.copyModelResources(new ResourceDeploymentModel(targetDeploymentModel.getSourceProject(), model.getResourceOmissions(), model.isSupportDynamicLoading()), 
																						monitor, resultsCollector);
				}
				if( !monitor.isCanceled() ){
					deployDD(targetDeploymentModel, bd, bdFile, monitor, resultsCollector);
				}
				monitor.subTask("");
				if( !monitor.isCanceled() ){
					monitor.setTaskName(Messages.J2EEDeploymentSolution_67);
					WebXMLManager.instance.updateModel(targetProject);
				}
				if( monitor.isCanceled() ){
					resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, Messages.J2EEDeploymentSolution_Canceled));
				}
			}
			else
			{
				resultsCollector.addMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR, Messages.bind(Messages.J2EEDeploymentSolution_32, targetProjectName)));
			}
		}
		else
		{
			resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.WARNING, Messages.bind(Messages.deployment_no_parts_found, model.getName())));
		}
	}
	*/

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
	
	/* TODO - EDT
	private void deployDD(TargetDeploymentModel targetDeploymentModel, BuildDescriptor bd, IFile bdFile, IProgressMonitor monitor, final IDeploymentResultsCollector resultsCollector)
	{
		if( model.getServiceSolutions().size() > 0 || model.getRUISolution() != null )
		{
			String targetProjectName = null;
			try{
				targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), bd != null ? bd.getGenProject() : null , model.getName());
				IProject targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);
				new J2EEJavaEglddDeploymentOperation(targetDeploymentModel, targetProject).execute(monitor , resultsCollector);
			}
			catch( Throwable t )
			{
				resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, t.toString()));
				Utils.buildStackTraceMessages(resultsCollector, t);
			}
		}
	}

	private void deployRUISolutions(IProgressMonitor monitor, final IDeploymentResultsCollector resultsCollector) {
		try{
			DeploymentSolution solution = model.getRUISolution();
			
			if( solution != null )
			{
				// The target was checked for existence in the execute method above
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
					RUIDeploymentModel ruiModel = new RUIDeploymentModel((RUIApplication)solution.getSolution(), model.getSourceProject(), targetProjectName, "", contextRoot,resultsCollector);
					
//					if(ruiModel.getHandlerLocales().size() > 0){
						if(ruiModel.getSourceRUIHandlers().size() > 0){
							J2EERUIDeploymentOperation deployOp = new J2EERUIDeploymentOperation(ruiModel);
							deployOp.execute(monitor, resultsCollector);
						}else{
							// a RUI Model might have no handlers if none of them validated correctly.  
							// We already issued error messages for the validation failures, so don't do anything else
						}
//					}else{
//						resultsCollector.addMessage(RUIDeployUtilities.createDeployMessage(IStatus.ERROR, Messages.J2EEDeploymentSolution_68));
//					}
				}catch(CoreException e){
					resultsCollector.addMessage(e.getStatus());
				}
			}
		}
		catch( Throwable t )
		{
			resultsCollector.addMessage(DeploymentUtilities.createStatus(IStatus.ERROR, t.toString()));
			Utils.buildStackTraceMessages(resultsCollector, t);
		}
	}
	*/

}
