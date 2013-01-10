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
package org.eclipse.edt.ide.ui.internal.dialogs;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.wizards.InterfaceListConfiguration;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class InterfaceSelectionDialog extends PartSelectionDialog {
	private static final int ADD_ID = IDialogConstants.CLIENT_ID + 1;
	protected ListDialogField fList;
	protected List fOldContent;
	private InterfaceListConfiguration fConfig;

	/**
	 * @param parent
	 * @param context
	 * @param elementKinds
	 * @param scope
	 */
	public InterfaceSelectionDialog(Shell parent, IRunnableContext context,
			ListDialogField list, int elemKind, String InterfaceSubType,
			InterfaceListConfiguration config, IEGLProject project) {
		
		super(parent, context, elemKind, InterfaceSubType, createSearchScope(project));
		fList = list;
		fOldContent = fList.getElements();
		fConfig = config;
		
		setStatusLineAboveButtons(true);
		setFilter("*"); //$NON-NLS-1$        
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ADD_ID, NewWizardMessages.EGLInterfaceSelectionDialogAddButtonLabel, true); //$NON-NLS-1$
		super.createButtonsForButtonBar(parent);
	}
	
	@Override
	protected void handleShellCloseEvent() {
		super.handleShellCloseEvent();
		reset2Original();
	}	
	
	@Override
	protected void cancelPressed() {
	    reset2Original();
		super.cancelPressed();
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == ADD_ID){
			addSelectedPart();
		}
		super.buttonPressed(buttonId);	
	}
	
	@Override
	protected void okPressed() {
		addSelectedPart();
		super.okPressed();
	}
	
	protected void addSelectedPart() {
		Object ref= getLowerSelectedElement();
		if (ref instanceof PartInfo) {
		    PartInfo partinfo = (PartInfo) ref;
			String qualifiedName= (partinfo).getFullyQualifiedName();
			
			String message;
			if(fList.getElements().contains(qualifiedName)) {
				message= NewWizardMessages.bind(NewWizardMessages.EGLInterfaceSelectionDialogInterfaceDuplicateaddedInfo, qualifiedName); //$NON-NLS-1$
				updateStatus(new StatusInfo(IStatus.WARNING, message));
			} else {
				fList.addElement(qualifiedName);
				IPart part = getPartFromPartInfo(partinfo);
				if(part != null)
				    fConfig.addInterface(qualifiedName, part);
				message= NewWizardMessages.bind(NewWizardMessages.EGLInterfaceSelectionDialogInterfaceaddedInfo, qualifiedName); //$NON-NLS-1$
				updateStatus(new StatusInfo(IStatus.INFO, message));
			}
		}
	}	
	    
	@Override
	protected void handleDefaultSelected() {
		if (validateCurrentSelection())
			buttonPressed(ADD_ID);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IUIHelpConstants.PARTS_INTERFACE_SELECTION_DIALOG);
	}

	private static IEGLSearchScope createSearchScope(IEGLProject project) {
		return SearchEngine.createEGLSearchScope(new IEGLProject[] { project });
	}
	
	//cancel all the selections along the way, reset everything back to the saved old value
	private void reset2Original() {
		fList.setElements(fOldContent);
	}
}
