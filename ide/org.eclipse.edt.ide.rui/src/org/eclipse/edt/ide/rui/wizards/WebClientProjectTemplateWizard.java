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
package org.eclipse.edt.ide.rui.wizards;

import org.eclipse.edt.ide.rui.wizards.pages.ProjectWizardRUILibraryPage;
import org.eclipse.edt.ide.rui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.project.templates.ProjectTemplateWizard;

public class WebClientProjectTemplateWizard extends ProjectTemplateWizard {
	
	protected ProjectWizardRUILibraryPage libraryPage;
	
	public void addPages() {
		libraryPage = new ProjectWizardRUILibraryPage(NewWizardMessages.RUILibraryPage);
		libraryPage.setTitle(NewWizardMessages.WebClientProjectTemplateWizard_0);
		addPage(libraryPage);
		super.addPages();
	}

	public boolean performFinish() {
		return true;
	}
}
