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
package org.eclipse.edt.ide.ui.internal.externaltype;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.wizards.EGLTemplateWizardPage;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class NewExternalTypeWizardPage extends EGLTemplateWizardPage {
	protected TableViewer templateViewer;
	protected Text descriptionText;

	public NewExternalTypeWizardPage(ISelection selection, String pageName) {
		super(pageName);

		setTitle(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_title);
		setDescription(NewExternalTypeWizardMessages.NewExternalTypeWizardPage_description);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_NEW_EXTERNALTYPE_WIZARD_PAGE);

		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);

		createContainerControls(container, 5);
		createPackageControls(container);
		createSeparator(container, 5);
		//createEGLFileControls(container);
		createEGLFileControls(container, NewExternalTypeWizardMessages.NewTypeWizardPageTypenameLabel);
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
		return "org.eclipse.edt.ide.ui.externaltypes";
	}
}
