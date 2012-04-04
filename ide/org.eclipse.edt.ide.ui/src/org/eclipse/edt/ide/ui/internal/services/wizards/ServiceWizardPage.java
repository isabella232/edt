/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.services.wizards;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
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

public class ServiceWizardPage extends EGLTemplateWizardPage {
	protected Text descriptionText;

	public ServiceWizardPage(ISelection selection, String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLServiceWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLServiceWizardPageDescription);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_SERVICE_DEFINITION);

		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);

		createContainerControls(container, 5);
		createPackageControls(container);
		createSeparator(container, 5);
		createEGLFileControls(container);
		createTemplateArea(container,5);

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
		// / updateStatus(null);
	}
			
	protected String getTemplateID() {
		return "org.eclipse.edt.ide.ui.services";
	}

	@Override
	protected void handleSelectedTemplate() {
		super.handleSelectedTemplate();
		Object o = ((IStructuredSelection) templateViewer.getSelection()).getFirstElement();
		if (o instanceof ITemplate) {
			ITemplate template = (ITemplate) o;
			((ServiceConfiguration)((ServiceWizard) getWizard()).getConfiguration()).setTemplateTypeByTemplateID(template.getId());
		}
	}

	
}
