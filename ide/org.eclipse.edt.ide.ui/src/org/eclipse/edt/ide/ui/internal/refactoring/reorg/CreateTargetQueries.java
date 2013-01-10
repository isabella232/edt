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
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.util.PixelConverter;
import org.eclipse.edt.ide.ui.internal.wizards.EGLFileWizard;
import org.eclipse.edt.ide.ui.internal.wizards.EGLPackageWizard;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWizard;

public class CreateTargetQueries implements ICreateTargetQueries {

	private final Wizard fWizard;
	private final Shell fShell;

	public CreateTargetQueries(Wizard wizard) {
		fWizard= wizard;
		fShell= null;
	}
	
	public CreateTargetQueries(Shell shell) {
		fShell = shell;
		fWizard= null;
	}

	private Shell getShell() {
		Assert.isTrue(fWizard == null || fShell == null);
		if (fWizard != null)
			return fWizard.getContainer().getShell();
		else if (fShell != null)
			return fShell;
		else
			return EDTUIPlugin.getActiveWorkbenchShell();
	}
	
	public ICreateTargetQuery createNewPackageQuery() {
		return new ICreateTargetQuery() {
			public Object getCreatedTarget(Object selection) {
				EGLPackageWizard packageCreationWizard= new EGLPackageWizard();
				openNewElementWizard(packageCreationWizard, getShell(), selection);
				return packageCreationWizard.getNewPackageFragment();
			}
			
			public String getNewButtonLabel() {
				return UINlsStrings.ReorgMoveWizard_newPackage;
			}
		};
	}
	
	public ICreateTargetQuery createNewEGLFileQuery() {
		return new ICreateTargetQuery() {
			public Object getCreatedTarget(Object selection) {
				EGLFileWizard fileCreationWizard= new EGLFileWizard(false);
				openNewElementWizard(fileCreationWizard, getShell(), selection);
				IFile fileHandle = ((EGLFileConfiguration) fileCreationWizard.getConfiguration()).getFileHandle();
				return fileHandle == null ? null : EGLCore.create(fileHandle);
			}
			
			public String getNewButtonLabel() {
				return UINlsStrings.ReorgMoveWizard_newEGLFile;
			}
		};
	}
	
	private IWizardPage[] openNewElementWizard(IWorkbenchWizard wizard, Shell shell, Object selection) {
		wizard.init(EDTUIPlugin.getDefault().getWorkbench(), new StructuredSelection(selection));
		
		WizardDialog dialog= new WizardDialog(shell, wizard);
		PixelConverter converter= new PixelConverter(shell);

		dialog.setMinimumPageSize(converter.convertWidthInCharsToPixels(70), converter.convertHeightInCharsToPixels(20));
		dialog.create();
		dialog.open();
		IWizardPage[] pages= wizard.getPages();
		return pages;
	}
}
