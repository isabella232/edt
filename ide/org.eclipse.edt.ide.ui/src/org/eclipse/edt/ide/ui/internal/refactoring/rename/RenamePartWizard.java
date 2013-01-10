/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;

/**
 * The type renaming wizard.
 */
public class RenamePartWizard extends RenameRefactoringWizard {

	public RenamePartWizard(Refactoring refactoring) {
		this(refactoring, UINlsStrings.RenamePartWizard_defaultPageTitle, UINlsStrings.RenamePartWizardInputPage_description,
				IUIHelpConstants.EGL_RENAME_DIALOG);
	}

	public RenamePartWizard(Refactoring refactoring, String defaultPageTitle, String inputPageDescription, String pageContextHelpId) {
		super(refactoring, defaultPageTitle, inputPageDescription, pageContextHelpId);
	}

	/*
	 * non java-doc
	 * 
	 * @see RefactoringWizard#addUserInputPages
	 */
	protected void addUserInputPages() {
		super.addUserInputPages();
	}

	public RenamePartProcessor getRenameTypeProcessor() {
		RefactoringProcessor proc= ((RenameRefactoring) getRefactoring()).getProcessor();
		if (proc instanceof RenamePartProcessor)
			return (RenamePartProcessor) proc;
		else if (proc instanceof RenameEGLFileProcessor) {
			RenameEGLFileProcessor rcu= (RenameEGLFileProcessor) proc;
			return rcu.getRenamePartProcessor();
		}
		Assert.isTrue(false); // Should never get here
		return null;
	}

	protected boolean isRenameType() {
		return true;
	}

	protected RenameInputWizardPage createInputPage(String message, String initialSetting) {
		return new RenamePartWizardInputPage(message, IUIHelpConstants.EGL_RENAME_DIALOG, true, initialSetting) {

			protected RefactoringStatus validateTextField(String text) {
				return validateNewName(text);
			}
		};
	}
}
