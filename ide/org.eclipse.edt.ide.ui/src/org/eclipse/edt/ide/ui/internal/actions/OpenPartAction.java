/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.OpenPartSelectionDialog;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenPartAction extends Action implements IWorkbenchWindowActionDelegate {
	
	/**
	 * constructor
	 */
	public OpenPartAction() {
		super();
		setText(UINlsStrings.OpenPartLabel); 
		setDescription(UINlsStrings.OpenPartDescription); 
		setToolTipText(UINlsStrings.OpenPartTooltip); 
		setImageDescriptor(PluginImages.DESC_TOOL_OPENPART);
	}

	/**
	 * Run the open part action
	 */
	public void run() {		
		IPart type = openPartSelectionDialog();
		if (type != null)
			openInEditor(type);
	}
	
	private boolean openInEditor(IPart type) {
		boolean beep = false;
		try {
			IEditorPart part= EditorUtility.openInEditor(type, true);
			//EditorUtility.revealInEditor(part, type);
		} catch (CoreException x) {
			beep = true;
			EGLLogger.log(this, UINlsStrings.OpenPartErrorMessage);
		}
		return beep;
	}

	private IPart openPartSelectionDialog() {
		//multiple parts found, ask user for which one to open
		IPart type = null;
		Shell parent= EDTUIPlugin.getActiveWorkbenchShell();
		OpenPartSelectionDialog dialog= new OpenPartSelectionDialog(parent, new ProgressMonitorDialog(parent), 
			IEGLSearchConstants.PART, SearchEngine.createWorkspaceScope());
		
		dialog.setMatchEmptyString(true);	
		dialog.setTitle(UINlsStrings.OpenPartDialogTitle); 
		dialog.setMessage(UINlsStrings.OpenPartDialogMessage); 
		int result= dialog.open();
		if (result == IDialogConstants.OK_ID) {
			Object[] types = dialog.getResult();
			if (types != null && types.length > 0)
				type = (IPart) types[0];
		}
		return type;
	}
	
//	---- IWorkbenchWindowActionDelegate ------------------------------------------------

	public void run(IAction action) {
		run();
	}
	
	public void dispose() {
		// do nothing.
	}
	
	public void init(IWorkbenchWindow window) {
		// do nothing.
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
}
