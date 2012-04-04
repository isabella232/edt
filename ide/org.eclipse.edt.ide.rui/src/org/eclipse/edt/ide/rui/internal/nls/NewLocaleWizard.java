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
package org.eclipse.edt.ide.rui.internal.nls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

public class NewLocaleWizard extends Wizard {
	
	List currentDescriptions = new ArrayList();
	List currentCodes = new ArrayList();
	HashMap currentUserLocaleRuntimeLocaleCombinations;
	Locale locale;

	/**
	 * 
	 */
	public NewLocaleWizard(Locale locale, List currentCodes, List currentDescriptions, HashMap combinations) {
		setWindowTitle(RUINlsStrings.NewLocaleDialog_Create_a_new_local_);
		this.currentCodes = currentCodes;
		this.currentDescriptions = currentDescriptions;
		this.locale = locale;
		this.currentUserLocaleRuntimeLocaleCombinations = combinations;
	}
	
	public void addPages() {
		WizardPage page = new NewLocaleWizardPage("New locale", RUINlsStrings.NewLocaleDialog_Create_a_new_local_,
													locale, currentCodes, currentDescriptions, currentUserLocaleRuntimeLocaleCombinations);
		addPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		return true;
	}

}
