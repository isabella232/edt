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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class AbstractOpenWizardWorkbenchAction 
	extends AbstractOpenWizardAction 
	implements IWorkbenchWindowActionDelegate {
		
	//$NON-NLS-1$
	protected AbstractOpenWizardWorkbenchAction() {
	}
	public AbstractOpenWizardWorkbenchAction(
		IWorkbench workbench,
		String label,
		Class[] activatedOnTypes,
		boolean acceptEmptySelection) {
		super(workbench, label, null, acceptEmptySelection);
	}
	public AbstractOpenWizardWorkbenchAction(
		IWorkbench workbench,
		String label,
		boolean acceptEmptySelection) {
		super(workbench, label, null, acceptEmptySelection);
	}
	/**
	 * @see AbstractOpenWizardAction#dispose
	 */
	public void dispose() {
		// do nothing.
		setWorkbench(null);
	}
	/**
	 * @see AbstractOpenWizardAction#init
	 */
	public void init(IWorkbenchWindow window) {
		setWorkbench(window.getWorkbench());
	}
	/**
	 * @see IActionDelegate#run
	 */
	public void run(IAction action) {
		run();
	}
	/**
	 * @see IActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}

}
