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
package org.eclipse.edt.ide.ui.internal.wizards;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.wizards.BindingEGLConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class ServiceBindingWizard extends EGLFileWizard {
	public ServiceBindingWizard(){
		super();
		setDefaultPageImageDescriptor(PluginImages.DESC_WIZBAN_EXTERNALSERVICE);
		setDialogSettings(EDTUIPlugin.getDefault().getDialogSettings());
	}
	
	@Override
	public void addPages() {
		addPage(new ServiceBindingWizardPage(ServiceBindingWizardPage.WIZPAGENAME_EGLServiceBindingWizardPage));
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getConfiguration().init(workbench, selection);
		setWindowTitle(NewWizardMessages.EGLServiceBindingWizTitle);
	}
	
	@Override
	public EGLPackageConfiguration getConfiguration() {
		if(configuration == null)
			configuration = new BindingEGLConfiguration();
		return configuration;
	}
	
	@Override
	public boolean performFinish() {
		getBindingEGLConfiguration().executeAddBinding(null);
		return true;
	}
	
	private BindingEGLConfiguration getBindingEGLConfiguration(){
		return (BindingEGLConfiguration)getConfiguration();
	}
}
