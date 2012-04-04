/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.nls;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;

public class EditLocaleWizard extends NewLocaleWizard {
	
	public EditLocaleWizard(Locale locale, List currentCodes, List currentDescriptions, HashMap combinations) {
		super(locale, currentCodes, currentDescriptions, combinations);
		setWindowTitle(RUINlsStrings.EditLocaleDialog_edit_locale_);
	}
	
	public void addPages() {
		WizardPage page = new EditLocaleWizardPage("Edit locale", RUINlsStrings.EditLocaleDialog_edit_locale_,
													locale, currentCodes, currentDescriptions, currentUserLocaleRuntimeLocaleCombinations);
		addPage(page);
	}

}
