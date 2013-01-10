/*******************************************************************************
 * Copyright © 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenEGLSearchPageAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow fWindow;
			
	@Override
	public void dispose() {
		fWindow = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	@Override
	public void run(IAction action) {
		if(fWindow == null || fWindow.getActivePage() == null) {
			beep();
			EDTUIPlugin.logErrorMessage("Could not open the search dialog - for some reason the window handle was null"); //$NON-NLS-1$
			return;
		}
		NewSearchUI.openSearchDialog(fWindow, EGLSearchPage.EXTENSION_POINT_ID);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	protected void beep() {
		Shell shell= EDTUIPlugin.getActiveWorkbenchShell();
		if (shell != null && shell.getDisplay() != null)
			shell.getDisplay().beep();
	}	
}
