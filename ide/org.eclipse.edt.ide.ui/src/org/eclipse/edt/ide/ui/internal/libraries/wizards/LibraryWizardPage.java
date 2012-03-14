/*******************************************************************************
 * Copyright 漏 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.libraries.wizards;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizard;
import org.eclipse.edt.ide.ui.internal.wizards.EGLTemplateWizardPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.templates.ITemplate;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class LibraryWizardPage extends EGLTemplateWizardPage {
	protected Text descriptionText;
	private StatusInfo fLibraryStatus;
	

	public LibraryWizardPage(ISelection selection, String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLLibraryWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLLibraryWizardPageDescription);

		fLibraryStatus = new StatusInfo();
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.EGL_LIBRARY_DEFINITION);
		
		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);

		createContainerControls(container, 5);
		createPackageControls(container);
		createSeparator(container, 5);
		createEGLFileControls(container);
		createTemplateArea(container, 5);

		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged() {
	}
	
	protected void handleEGLFileDialogFieldChanged() {
		super.handleEGLFileDialogFieldChanged();
		LibraryConfiguration config = ((LibraryConfiguration)((EGLPartWizard) getWizard()).getConfiguration());
		String libName = config.getFileName();
		config.setLibraryName(libName);
	}

	protected String getTemplateID() {
		return "org.eclipse.edt.ide.ui.libraries";
	}

	protected void handleSelectedTemplate() {
		super.handleSelectedTemplate();
		if (templateViewer != null) {
			Object o = ((IStructuredSelection) templateViewer.getSelection())
					.getFirstElement();
			if (o instanceof ITemplate) {
				ITemplate template = (ITemplate) o;
				LibraryConfiguration config = ((LibraryConfiguration)((EGLPartWizard) getWizard()).getConfiguration());
				config.setCodeTemplateId(template.getCodeTemplateId());
				config.setLibraryTypeByTemplateID(template.getId());
			}
		}
	}
}
