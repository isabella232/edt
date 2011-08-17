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
package org.eclipse.edt.ide.ui.internal.wizards;

import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.wizards.EGLDDProtocolConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;

public class EGLDDProtocolWizard extends EGLPartWizard {

	private Protocol fNewProtocol;
	
	public EGLDDProtocolWizard(){
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_EXTERNALSERVICE);
	}
	
	public void init(EGLDeploymentRoot root){
		getEGLDDProtocolConfiguration().init(root);
		setWindowTitle(NewWizardMessages.EGLDDProtocolWizTitle);
	}
	
	public void init(EGLDeploymentRoot root, Java400Protocol protocol){
		getEGLDDProtocolConfiguration().init(root, protocol);
		setWindowTitle(NewWizardMessages.EGLDDProtocolWizTitle);
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if(configuration == null)
			configuration = new EGLDDProtocolConfiguration();		
		return configuration;		
	}
	
	private EGLDDProtocolConfiguration getEGLDDProtocolConfiguration(){
		return (EGLDDProtocolConfiguration)getConfiguration();		
	}
	
	public void addPages() {
		addPage(new EGLDDProtocolWizardPage(EGLDDProtocolWizardPage.WIZPAGENAME_EGLDDProtocolWizardPage));
	}
	
	public Protocol getNewProtocol(){
		return fNewProtocol;
	}
	
	public void setNewProtocol( Protocol protocol ){
		fNewProtocol = protocol;
	}
	
	public boolean performFinish() {
		executeFinishOperations();
		return true;
	}

	private void executeFinishOperations() {
		fNewProtocol = getEGLDDProtocolConfiguration().executeAddProtocol();		
	}

	
}
