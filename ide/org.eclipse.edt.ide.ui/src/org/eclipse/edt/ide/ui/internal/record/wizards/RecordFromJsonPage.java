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
package org.eclipse.edt.ide.ui.internal.record.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.edt.javart.json.JsonParser;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;

public class RecordFromJsonPage extends AbstractRecordFromStringInputPage {
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RecordFromJsonPage(ISelection selection) {
		super(selection);
		setTitle(NewRecordWizardMessages.RecordFromJsonPage_title);
		setDescription(NewRecordWizardMessages.RecordFromJsonPage_description);
	}

	protected String[] getValidInputFileExtensions() {
		return new String[] { "*.json", "*" };//$NON-NLS-1$
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IUIHelpConstants.EGL_NEW_RECORD_FROM_JSON_PAGE);
	}

	protected void validatePage() {
		super.validatePage();

		if (isPageComplete()) {
			if (createFromStringButton.getSelection() && stringText.getText().trim().length() > 0) {
				try {
					JsonParser.parseValue(stringText.getText());
				} catch (Throwable ex) {
					error(ex.getMessage());
				} 
			}
		}
	}
}
