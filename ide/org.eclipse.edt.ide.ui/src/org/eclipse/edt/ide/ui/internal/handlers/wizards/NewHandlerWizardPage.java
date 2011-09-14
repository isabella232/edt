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

import org.eclipse.edt.ide.ui.internal.wizards.EGLPartWizardPage;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class NewHandlerWizardPage extends EGLPartWizardPage {

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
		// TODO #jiyong# Change help
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_NEW_RECORD_WIZARD_PAGE);

		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);

		createContainerControls(container, 5);
		createPackageControls(container);
		createSeparator(container, 5);
		createEGLFileControls(container);

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

}
