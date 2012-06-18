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
package org.eclipse.edt.ide.rui.wizards;

import java.util.ArrayList;

import org.eclipse.edt.ide.rui.internal.wizards.RuiNewWizardMessages;
import org.eclipse.edt.ide.rui.wizards.pages.ProjectWizardRUILibraryPage;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateWizard;
import org.eclipse.edt.ide.widgetLibProvider.IWidgetLibProvider;
import org.eclipse.edt.ide.widgetLibProvider.WidgetLibProviderManager;

public class WebClientProjectTemplateWizard extends ProjectTemplateWizard {
	
	protected ProjectWizardRUILibraryPage libraryPage;
	
	public void addPages() {
		libraryPage = getLibraryPage();
		libraryPage.setTitle(RuiNewWizardMessages.WebClientProjectTemplateWizard_0);
		addPage(libraryPage);
		super.addPages();
	}
	
	protected ProjectWizardRUILibraryPage getLibraryPage(){
		return new ProjectWizardRUILibraryPage(RuiNewWizardMessages.RUILibraryPage, template.getWidgetLibraryContainer());
	}

	public boolean performFinish() {
		//if the wizard is not displayed, set the default widget library
		if(libraryPage == null){
			IWidgetLibProvider[] libProviders = WidgetLibProviderManager.getInstance().getProviders(template.getWidgetLibraryContainer());
			if (libProviders != null) {
				ArrayList<String> selectedWidgetLibraries = new ArrayList<String>();				
				for (int i = 0; i < libProviders.length; i++) {
					if(libProviders[i].isSelected()){
						selectedWidgetLibraries.add(libProviders[i].getId());
					}					
				}
				((NewEGLProjectWizard) getParentWizard()).getModel().setSelectedWidgetLibraries(selectedWidgetLibraries);
			}
		}
		return true;
	}
}
