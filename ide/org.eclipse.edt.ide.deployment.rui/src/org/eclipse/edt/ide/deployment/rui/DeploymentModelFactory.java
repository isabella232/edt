/*******************************************************************************
 * Copyright © 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.RUIApplication;
import org.eclipse.edt.ide.deployment.core.model.RUIHandler;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.deployment.core.model.WebBinding;
import org.eclipse.edt.ide.deployment.core.model.Webservice;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;

public class DeploymentModelFactory {

	private IFile ddFile;
	
	public DeploymentModel createDeploymentModel(IFile ddFile, IProgressMonitor pm) throws Exception{
		this.ddFile = ddFile;
		DeploymentModel model = null;
		if (this.ddFile != null && this.ddFile.exists()) {
			model = processDeploymentDesc(this.ddFile, pm);
		}
		return model;
	}
	
	private DeploymentModel processDeploymentDesc(IFile file, IProgressMonitor pm) throws Exception {

		pm.subTask(Messages.process_deployment_descriptor);

		DeploymentDesc desc = DeploymentDesc.createDeploymentDescriptor("", file.getContents());

		DeploymentModel model = new DeploymentModel(this.ddFile.getProject(), ddFile.getName(), desc );
		
		processRUISolutions(desc, model, pm);
		processWebServices(desc, model, pm);
		processRESTServices(desc, model, pm);
		processWebBindings(desc, model, pm);
		processResourceOmissions(desc, model, pm);
		
		return model;
	}
	
	private void processResourceOmissions(DeploymentDesc desc, DeploymentModel model, IProgressMonitor pm) throws Exception{
		
		pm.subTask(Messages.process_protocols);
		for( Iterator<String> itr = desc.getResourceOmissions().iterator(); itr.hasNext();)
		{
			model.addResourceOmission(itr.next());
		}
	}
	private void processWebBindings(DeploymentDesc desc, DeploymentModel model, IProgressMonitor pm) throws Exception{
		
		pm.subTask(Messages.process_protocols);

		model.setHasWebBindings(false);
		for( Iterator<WebBinding> itr = desc.getWebBindings().iterator(); itr.hasNext();)
		{
			if( itr.next().isEnableGeneration() )
			{
				model.setHasWebBindings(true);
				break;
			}
		}
	}
	
	private void processRESTServices(DeploymentDesc desc, DeploymentModel model, IProgressMonitor pm) throws Exception{
		
		pm.subTask(Messages.process_rest_services);

		Iterator restServicesIterator = desc.getRestservices().iterator();
		while (restServicesIterator.hasNext()) {
			Restservice service = (Restservice) restServicesIterator.next();
			if (service.isEnableGeneration()) {
				model.addServiceSolution(service, DeploymentUtilities.getDeploymentTargetId(desc.getDeploymentTarget(), null, model.getName()));
			}
		}	
	}
	
	private void processWebServices(DeploymentDesc desc, DeploymentModel model, IProgressMonitor pm) throws Exception{
		
		pm.subTask(Messages.process_web_services);

		Iterator webServicesIterator = desc.getWebservices().iterator();
		while (webServicesIterator.hasNext()) {
			Webservice service = (Webservice) webServicesIterator.next();
			if (service.isEnableGeneration()) {
				model.addServiceSolution(service, DeploymentUtilities.getDeploymentTargetId(desc.getDeploymentTarget(), null, model.getName()));
			}
		}	
	}
	
	private void processRUISolutions(DeploymentDesc desc, final DeploymentModel model, IProgressMonitor pm) throws Exception{
		
		pm.subTask(Messages.process_rich_ui_handlers);
		
		RUIApplication application = (RUIApplication) desc.getRUIApplication();
		if( application == null )
		{
			application = new RUIApplication("ezedefault", "true");
			/* TODO - EDT
			HandlerLocalesList localesList = new HandlerLocalesList();
			localesList.buildLocalesList();
			List locales = localesList.getLocales();
			List defaultLocales = new ArrayList();
			for( Iterator itr = locales.iterator(); itr.hasNext();)
			{
				Object locale = itr.next();
				if( locale instanceof DeployLocale &&
						((DeployLocale)locale).isDefault())
				{
					defaultLocales.add(locale);
				}
			}
			application.addParameter(new Parameter("locales", RUIDeployUtilities.getLocalesString(defaultLocales.toArray())));
			*/
		}
		if (application.deployAllHandlers()) {
			if( model.getSourceProject() != null )
			{
				/* TODO - EDT
				try
				{
					Collection ruiHandlerList = DeploymentUtilities.getAllRUIHandlersInProject( EGLCore.create(model.getSourceProject())).keySet();
		  		  	if( ruiHandlerList != null )
		  		  	{
						for( Iterator itr = ruiHandlerList.iterator(); itr.hasNext();)
						{
							String handleName = (String)itr.next();
							boolean isConfigured = false;
							//If a handler is already configured in EGLDD, used the configured information 
							//including HTML file name and dynamic loading handler names
							for(Iterator<RUIHandler> ite = application.getRUIHandlers().iterator();ite.hasNext();){
								RUIHandler configuredHandler = ite.next();
								if(configuredHandler.getImplementation().equals(handleName)){
									isConfigured = true;
									configuredHandler.setEnableGeneration(true);
									break;
								}
							}
							if(!isConfigured){
								application.addRUIHandler(new RUIHandler(handleName, "true"));
							}
						}
		  		  	} 
				}
				catch ( EGLModelException e )
				{
					IDeploymentResultsCollector resultsCollector = DeploymentResultsCollectorManager.getInstance().getCollector(RUIDeployUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName()), model.getName(), false, model.isCMDMode());
					resultsCollector.addMessage(e.getStatus());
				}
*/
			}
		}
		else
		{
			for (Iterator iterator = application.getRUIHandlers().iterator(); iterator.hasNext();) {
				RUIHandler handler = (RUIHandler) iterator.next();
				if(!handler.isEnableGeneration()){
					iterator.remove();
				}else{
					IEGLProject project = EGLCore.create(model.getSourceProject());
					if(project != null && project.exists()){
						IPart element = project.findPart(handler.getImplementation());
						if(element != null && element.exists()){
							//continue
						}else{
							iterator.remove();
						}
					}
				}
			}
		}
		model.setRUISolution(application, DeploymentUtilities.getDeploymentTargetId(desc.getDeploymentTarget(), null, model.getName()));
	}
	
	protected IFile getFile(String name) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(name));
		return file;
	}
}
