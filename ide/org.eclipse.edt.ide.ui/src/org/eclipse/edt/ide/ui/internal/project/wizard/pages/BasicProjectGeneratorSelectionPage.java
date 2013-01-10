/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.wizard.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.ui.internal.project.wizards.BasicProjectTemplateWizard;
import org.eclipse.edt.ide.ui.internal.project.wizards.NewEGLProjectWizard;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.preferences.CompilerPropertyAndPreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class BasicProjectGeneratorSelectionPage extends ProjectWizardPage {

	private InnerCompilerGeneratorPage innerPage;
	// Currently selected generator names
	private List<String> selectedGenerators = new ArrayList<String>();
	
	public BasicProjectGeneratorSelectionPage(String pageName,
			BasicProjectTemplateWizard basicProjectTemplateWizard) {
		super(pageName);
		this.setWizard(basicProjectTemplateWizard);
		setTitle(NewWizardMessages.GeneratorSelectionPageTitle);
		setDescription(NewWizardMessages.GeneratorSelectionPageDescription);
		innerPage = new InnerCompilerGeneratorPage(this);
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayoutData( new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		parent.setLayout(layout);
		createPageControl(innerPage, parent);
	}

	@Override
	public void setProjectName(String projectName) {
		((NewEGLProjectWizard)((BasicProjectTemplateWizard)getWizard()).getParentWizard()).getModel().setProjectName(projectName);
	}
	
	private void createPageControl(InnerCompilerGeneratorPage page, Composite parent) {
//		page.createControl(parent);
		page.createContents(parent);
	}
	
	public List<String> getSelectedGenerators() {
		return innerPage.getSelectedGenerators();
	}

	public boolean performOK() {
		return innerPage.performOk();
	}

	public boolean isPageComplete() {
		return super.isPageComplete() && innerPage.isValidPage();
	}
}
