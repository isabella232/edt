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
package org.eclipse.edt.ide.ui.internal.actions;

import java.util.Iterator;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;

public abstract class AbstractOpenWizardAction extends Action {
	
	//$NON-NLS-1$
	public static final String WIZARD_TITLE = "AbstractOpenWizardAction.title"; //$NON-NLS-1$
	//$NON-NLS-1$
	private IWorkbench fWorkbench;
	private Class[] fActivatedOnTypes;
	private boolean fAcceptEmptySelection;
	protected AbstractOpenWizardAction() {
	}
	public AbstractOpenWizardAction(
		IWorkbench workbench,
		String label,
		Class[] activatedOnTypes,
		boolean acceptEmptySelection) {
		super(label);
		fWorkbench = workbench;
		fActivatedOnTypes = activatedOnTypes;
		fAcceptEmptySelection = acceptEmptySelection;
	}
	public AbstractOpenWizardAction(
		IWorkbench workbench,
		String label,
		boolean acceptEmptySelection) {
		this(workbench, label, null, acceptEmptySelection);
	}
	public boolean canActionBeAdded() {
		ISelection selection = getCurrentSelection();
		if (selection == null || selection.isEmpty()) {
			return fAcceptEmptySelection;
		}
		if (fActivatedOnTypes != null) {
			if (selection instanceof IStructuredSelection) {
				return isEnabled(((IStructuredSelection) selection).iterator());
			}
			return false;
		}
		return true;
	}
	/**
	 * Create the specific Wizard
	 * (to be implemented by a subclass)
	 */
	abstract protected Wizard createWizard();
	protected IStructuredSelection getCurrentSelection() {
		IWorkbenchWindow window = EDTUIPlugin.getActiveWorkbenchWindow();
		if (window != null) {
			ISelection selection = window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection) selection;
			}

		}
		return null;
	}
	protected IWorkbench getWorkbench() {
		return fWorkbench;
	}
	private boolean isEnabled(Iterator iter) {
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (!isOfAcceptedType(obj) || !shouldAcceptElement(obj)) {
				return false;
			}
		}
		return true;
	}
	private boolean isOfAcceptedType(Object obj) {
		for (int i = 0; i < fActivatedOnTypes.length; i++) {
			if (fActivatedOnTypes[i].isInstance(obj)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * The user has invoked this action.
	 */
	public void run() {
		Wizard wizard = getWizard();
		openWizard(wizard);
	}
	/**
     * @param wizard
     */
    protected void openWizard(Wizard wizard) {
        WizardDialog dialog =
			new WizardDialog(EDTUIPlugin.getActiveWorkbenchShell(), wizard);
		dialog.create();
		dialog.open();
    }
    
    /**
     * @return
     */
    protected Wizard getWizard() {
        Wizard wizard = createWizard();
		if (wizard instanceof IWorkbenchWizard) {
			IStructuredSelection sel = null;
			if (getCurrentSelection() instanceof IStructuredSelection)
				sel = (IStructuredSelection) getCurrentSelection();
			else
				sel = new StructuredSelection();
			((IWorkbenchWizard) wizard).init(EDTUIPlugin.getDefault().getWorkbench(), sel);
		}
        return wizard;
    }
    
    protected void setWorkbench(IWorkbench workbench) {
		fWorkbench = workbench;
	}
	/**
	 * can be overridden to add more checks
	 * obj is guaranteed to be instance of one of the accepted types
	 */
	protected boolean shouldAcceptElement(Object obj) {
		return true;
	}
}
