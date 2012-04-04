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
package org.eclipse.edt.ide.ui.internal.refactoring.rename;

/**
 * Wizard page for renaming a part
 */
class RenamePartWizardInputPage extends RenameInputWizardPage {

	public RenamePartWizardInputPage(String description, String contextHelpId, boolean isLastUserPage, String initialValue) {
		super(description, contextHelpId, isLastUserPage, initialValue);
	}

	public void dispose() {
		super.dispose();
	}

	/*
	 * Override - we don't want to initialize the next page (may needlessly
	 * trigger change creation if similar elements page is skipped, which is not
	 * indicated by fIsLastUserInputPage in parent).
	 */
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	protected boolean performFinish() {
		boolean returner= super.performFinish();
		// check if we got deferred to the error page
		if (!returner && getContainer().getCurrentPage() != null)
			getContainer().getCurrentPage().setPreviousPage(this);
		return returner;
	}
}
