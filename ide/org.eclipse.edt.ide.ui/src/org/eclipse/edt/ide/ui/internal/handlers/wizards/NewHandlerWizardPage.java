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
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.edt.ide.ui.internal.wizards.EGLTemplateWizardPage;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class NewHandlerWizardPage extends EGLTemplateWizardPage {

	protected TableViewer templateViewer;
	protected Text descriptionText;
	
	public NewHandlerWizardPage(String pageName) {
		super(pageName);
		setTitle(NewHandlerWizardMessages.NewHandlerWizardPage_title);
		setDescription(NewHandlerWizardMessages.NewHandlerWizardPage_description);
	}
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

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
		return "org.eclipse.edt.ide.ui.handlers";
	}
	
	protected void handleSelectedTemplate() {
		getNextPage();
		super.handleSelectedTemplate();
	}
}