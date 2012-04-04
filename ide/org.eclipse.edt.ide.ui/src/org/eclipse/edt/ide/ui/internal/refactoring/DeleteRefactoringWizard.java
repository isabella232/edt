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
package org.eclipse.edt.ide.ui.internal.refactoring;

import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class DeleteRefactoringWizard extends RefactoringWizard {
	Object[] elements;

	String pageTitle = "";

	String elementMessage = "";

	public DeleteRefactoringWizard(final DeleteRefactoring refactoring, final Object[] elements) {
		super(refactoring, DIALOG_BASED_USER_INTERFACE | YES_NO_BUTTON_STYLE | NO_PREVIEW_PAGE | NO_BACK_BUTTON_ON_STATUS_DIALOG);
		this.elements = elements;
	}

	// interface methods of RefactoringWizard
	// ///////////////////////////////////////

	protected void addUserInputPages() {
		setStrings();
		setDefaultPageTitle(pageTitle);
		addPage(new DeleteInputPage(elementMessage));
	}

	private void setStrings() {
		String elementType = "";
		if (elements[0] instanceof IEGLElement) {
			switch (((IEGLElement) elements[0]).getElementType()) {
			case IEGLElement.EGL_FILE:
				elementType = UINlsStrings.EGLDeleteDialogElementTypeEGLFile;
				break;
			case IEGLElement.PACKAGE_FRAGMENT:
				elementType = UINlsStrings.EGLDeleteDialogElementTypeEGLPackage;
				break;
			case IEGLElement.PACKAGE_FRAGMENT_ROOT:
				elementType = UINlsStrings.EGLDeleteDialogElementTypeEGLSourceFolder;
				break;
			default:
				elementType = UINlsStrings.EGLDeleteDialogElementTypeResource;
			}

			if (elements.length == 1)
				elementMessage = NewWizardMessages.bind(UINlsStrings.EGLDeleteDialogMessageSingular, ((IEGLElement) elements[0])
						.getElementName()); //$NON-NLS-1$
			else
				elementMessage = NewWizardMessages.bind(UINlsStrings.EGLDeleteDialogMessagePlural, String.valueOf(elements.length)); //$NON-NLS-1$
		} else
			elementType = UINlsStrings.EGLDeleteDialogElementTypeResource;

		pageTitle = NewWizardMessages.bind(UINlsStrings.EGLDeleteDialogAction, elementType);

	}

	public boolean needsProgressMonitor() {
		return super.needsProgressMonitor();
	}

	private static class DeleteInputPage extends UserInputWizardPage {
		
		String labelText;

		public DeleteInputPage(String labelText) {
			
			super("EGL Delete");
			
			this.labelText = labelText;
		}

		public void createControl(final Composite parent) {
			Composite composite = createRootComposite(parent);
			setControl(composite);

			createLabel(composite);
		}

		private Composite createRootComposite(final Composite parent) {
			Composite result = new Composite(parent, SWT.NONE);
			GridLayout gridLayout = new GridLayout(2, false);
			gridLayout.marginWidth = 10;
			gridLayout.marginHeight = 10;
			result.setLayout(gridLayout);
			initializeDialogUnits(result);
			Dialog.applyDialogFont(result);
			return result;
		}

		private void createLabel(final Composite composite) {
			Image image= Display.getCurrent().getSystemImage(SWT.ICON_QUESTION);
			Label label= new Label(composite, SWT.NULL);
			image.setBackground(label.getBackground());
			label.setImage(image);
			label.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_BEGINNING));

			
			Label lblNewName = new Label(composite, SWT.NONE);
			lblNewName.setText(labelText);
		}

	}

}
