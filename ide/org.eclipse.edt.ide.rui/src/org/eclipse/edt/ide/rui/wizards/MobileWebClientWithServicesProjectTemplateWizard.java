/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.rui.internal.wizards.RuiNewWizardMessages;
import org.eclipse.edt.ide.rui.wizards.pages.ProjectWizardRUILibraryPage;

public class MobileWebClientWithServicesProjectTemplateWizard extends
		WebClientWithServicesProjectTemplateWizard {
	@Override
	public void addPages() {
		super.addPages();
		
		libraryPage.setTitle(RuiNewWizardMessages.MobileWebClientWithServicesProjectTemplateWizard_0);
	}
	
	protected ProjectWizardRUILibraryPage getLibraryPage(){
		return new ProjectWizardRUILibraryPage(RuiNewWizardMessages.MobileRUILibraryPage, template.getWidgetLibraryContainer());
	}

}
