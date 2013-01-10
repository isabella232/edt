/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.wizards;

import java.util.List;

import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.WebServicesConfiguration;

public class WebServicesWizard extends EGLPartWizard {

	private String fNewWSImpl;

	public WebServicesWizard(){
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_WSDL2EGL);
	}
	
	public boolean init(EGLDeploymentRoot root, List allEGLServicesList){
		getWebServicesConfiguration().init(root, allEGLServicesList);
		setWindowTitle(NewWizardMessages.WebServicesWizTitle);
		
		return !getWebServicesConfiguration().getServicesNeed2BeWS().isEmpty();
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if(configuration == null)
			configuration = new WebServicesConfiguration();
		return configuration;
	}
	
	private WebServicesConfiguration getWebServicesConfiguration(){
		return (WebServicesConfiguration)getConfiguration();
	}	
	
	public void addPages() {
		addPage(new WebServicesWizardPage(WebServicesWizardPage.WIZPAGENAME_WebServicesWizardPage));
	}
	
	public boolean performFinish() {
		fNewWSImpl = getWebServicesConfiguration().executeAddWebServicesOperation();
		return true;
	}
	
	public String getNewlyAddedWebService(){
		return fNewWSImpl;
	}
}
