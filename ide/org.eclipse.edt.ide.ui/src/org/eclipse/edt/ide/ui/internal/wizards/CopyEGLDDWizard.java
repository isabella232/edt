/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.wizards.CopyEGLDDConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;

public class CopyEGLDDWizard extends EGLPartWizard {

	public CopyEGLDDWizard(){
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_COPYEGLDD);
	}
	
	public void init(EGLDeploymentRoot root, IFile currEGLDDFile, IProject project){
		getCopyEGLDDConfiguration().init(root, currEGLDDFile, project);
		setWindowTitle(NewWizardMessages.CopyEGLDDWizTitle);
	}
	
	public EGLPackageConfiguration getConfiguration() {
		if(configuration == null)
			configuration = new CopyEGLDDConfiguration();
		return configuration;
	}
	
	private CopyEGLDDConfiguration getCopyEGLDDConfiguration(){
		return (CopyEGLDDConfiguration)getConfiguration();
	}
	
	public void addPages() {
		addPage(new CopyEGLDDWizardPage(CopyEGLDDWizardPage.WIZPAGENAME_CopyEGLDDWizardPage));
	}
	
	public boolean performFinish() {
		getCopyEGLDDConfiguration().executeCopy();
		return true;
	}
}
