/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;

import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.EDTUIPlugin;

public class EglarPackageWizard extends Wizard implements IExportWizard {
	private static String DIALOG_SETTINGS_KEY= "EglarPackageWizard"; //$NON-NLS-1$

	private boolean fHasNewDialogSettings;

	private boolean fInitializeFromEglarPackage;

	private EglarPackageData fEglarPackage;

	private EglarPackageWizardPage fEglarPackageWizardPage;

	private EglarOptionsPage fEglarOptionsWizardPage;
	
	private IStructuredSelection fSelection;
	
	public EglarPackageWizard() {
		IDialogSettings workbenchSettings= EDTUIPlugin.getDefault().getDialogSettings();
		IDialogSettings section= workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		if (section == null) {
			fHasNewDialogSettings= true;
		} else {
			fHasNewDialogSettings= false;
			setDialogSettings(section);
		}
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page == fEglarPackageWizardPage) {
			return fEglarOptionsWizardPage;
		} else if(page == fEglarOptionsWizardPage) {
			return null;
		}
		return super.getNextPage(page);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.fEglarPackageWizardPage = new EglarPackageWizardPage(fEglarPackage, fSelection);
		addPage(fEglarPackageWizardPage);
		fEglarOptionsWizardPage = new EglarOptionsPage(fEglarPackage);
		addPage(fEglarOptionsWizardPage);
		super.addPages();
	}


	public void init(IWorkbench workbench, IStructuredSelection selection) {
		fSelection= getValidSelection();
		fEglarPackage= new EglarPackageData();
		fEglarPackage.setExportEGLSrcFiles(false);	//do not export egl source file
		setInitializeFromEglarPackage(false);
		setWindowTitle(EglarPackagerMessages.EglarPackageWizard_windowTitle);
		setNeedsProgressMonitor(true);
		
	}
	
	void setInitializeFromEglarPackage(boolean state) {
		fInitializeFromEglarPackage= state;
	}
	
	protected boolean executeExportOperation(IEglarExportRunnable op) {
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException ex) {
			if (ex.getTargetException() != null) {
				ExceptionHandler.handle(ex, getShell(), EglarPackagerMessages.EglarPackageWizard_EglarExportError_title, EglarPackagerMessages.EglarPackageWizard_EglarExportError_message);
				return false;
			}
		}
		IStatus status= op.getStatus();
		if (!status.isOK()) {
			ErrorDialog.openError(getShell(), EglarPackagerMessages.EglarPackageWizard_EglarExport_title, null, status);
			return !(status.matches(IStatus.ERROR));
		}
		return true;
	}
	
	protected IStructuredSelection getValidSelection() {
		ISelection currentSelection= EDTUIPlugin.getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (currentSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection= (IStructuredSelection) currentSelection;
			List<IEGLProject> selectedElements= getProjectsOfSelection(structuredSelection); 
			return new StructuredSelection(selectedElements);
		} else
			return StructuredSelection.EMPTY;
	}
	
	private ArrayList<IEGLProject> getProjectsOfSelection(IStructuredSelection structuredSelection) {
		Iterator<?> iter= structuredSelection.iterator();
		ArrayList<IEGLProject> selectedProjects = new ArrayList<IEGLProject>();
		while (iter.hasNext()) {
			Object selectedElement= iter.next();
			IProject temp = null;
			if(selectedElement instanceof IProject) {
				temp = (IProject) selectedElement;
			} else if(selectedElement instanceof IResource) {
				temp = ((IResource)selectedElement).getProject();
			} else if(selectedElement instanceof IEGLElement) {
				temp = ((IEGLElement)selectedElement).getEGLProject().getProject();
			} else if(selectedElement instanceof IPackageFragmentRoot) {
				temp = ((IPackageFragmentRoot)selectedElement).getJavaProject().getProject();
			}
			if(temp != null && checkIfEGLProjectAndNotBin(temp)) {
				selectedProjects.add(EGLCore.create(temp));
			}
		}
		return selectedProjects;
	}
	
	private boolean checkIfEGLProjectAndNotBin(IProject project) {
		try {
			IEGLProject ieglProject = EGLCore.create(project);
			if (project.hasNature(EGLCore.NATURE_ID) && (!ieglProject.isBinary()))
				return true;
		} catch (CoreException ex) {
		}
		return false;
	}
	
	boolean isInitializingFromEglarPackage() {
		return fInitializeFromEglarPackage;
	}

	public boolean performFinish() {
		fEglarPackage.setElements(fEglarPackageWizardPage.getSelectedElementsWithoutContainedChildren());

		if (!executeExportOperation(fEglarPackage.createEglarExportRunnable(getShell())))
			return false;

		// Save the dialog settings
		if (fHasNewDialogSettings) {
			IDialogSettings workbenchSettings= EDTUIPlugin.getDefault().getDialogSettings();
			IDialogSettings section= workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
			section= workbenchSettings.addNewSection(DIALOG_SETTINGS_KEY);
			setDialogSettings(section);
		}
		IWizardPage[] pages= getPages();
		for (int i= 0; i < getPageCount(); i++) {
			IWizardPage page= pages[i];
			if (page instanceof IEglarPackageWizardPage)
				((IEglarPackageWizardPage) page).finish();
		}
		return true;
	}
}
