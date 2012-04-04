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
package org.eclipse.edt.ide.ui.internal.preferences;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.OpenPartSelectionDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.preferences.ImportOrganizeConfigurationBlock.ImportOrderEntry;
import org.eclipse.edt.ide.ui.internal.util.BusyIndicatorRunnableContext;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ImportOrganizeInputDialog extends StatusDialog {

	private class ImportOrganizeInputAdapter implements IDialogFieldListener, IStringButtonAdapter {
		/**
		 * @see IDialogFieldListener#dialogFieldChanged(DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			doValidation();
		}			

		/**
		 * @see IStringButtonAdapter#changeControlPressed(DialogField)
		 */
		public void changeControlPressed(DialogField field) {
			doBrowseTypes();
		}
	}
	
	private StringButtonDialogField fNameDialogField;
	private List fExistingEntries;
		
	public ImportOrganizeInputDialog(Shell parent, List/*<ImportOrderEntry>*/ existingEntries) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		fExistingEntries= existingEntries;
		
		String label, title;
		title= UINlsStrings.ImportOrganizeInputDialog_title; 
		label= UINlsStrings.ImportOrganizeInputDialog_name_group_label; 

		setTitle(title);

		ImportOrganizeInputAdapter adapter= new ImportOrganizeInputAdapter();

		fNameDialogField= new StringButtonDialogField(adapter);
		fNameDialogField.setLabelText(label); //$NON-NLS-1$
		fNameDialogField.setButtonLabel(UINlsStrings.ImportOrganizeInputDialog_browse_types_label); 
		fNameDialogField.setDialogFieldListener(adapter);
		fNameDialogField.setText(""); //$NON-NLS-1$
		
	}
		
	public void setInitialSelection(ImportOrderEntry editedEntry) {
		Assert.isNotNull(editedEntry);
		if (editedEntry.name.length() == 0) {
			fNameDialogField.setText(""); //$NON-NLS-1$
		} else {
			fNameDialogField.setText(editedEntry.name);
		}
	}
	
	public ImportOrderEntry getResult() {
		String val= fNameDialogField.getText();
		if ("*".equals(val)) { //$NON-NLS-1$
			return new ImportOrderEntry(""); //$NON-NLS-1$
		} else {
			return new ImportOrderEntry(val);
		}
	}
		
	protected Control createDialogArea(Composite parent) {
		Composite composite= (Composite) super.createDialogArea(parent);
		initializeDialogUnits(parent);
		
		GridLayout layout= (GridLayout) composite.getLayout();
		layout.numColumns= 2;
		
		fNameDialogField.doFillIntoGrid(composite, 3);
		
		LayoutUtil.setHorizontalSpan(fNameDialogField.getLabelControl(null), 2);
		
		int fieldWidthHint= convertWidthInCharsToPixels(60);
		LayoutUtil.setWidthHint(fNameDialogField.getTextControl(null), fieldWidthHint);
		LayoutUtil.setHorizontalGrabbing(fNameDialogField.getTextControl(null));
		
		fNameDialogField.postSetFocusOnDialogField(parent.getDisplay());
		
		applyDialogFont(composite);		
		return composite;
	}

	final void doBrowsePackages() {
		System.out.println("Not supported yet, need to implement EGL Package search");
		MessageDialog.openError(getShell(), "Temp EGL title", "Not yet supported, we cannot search for all the EGL packages in a workspace yet");
	}
	
	private void doBrowseTypes() {		
		IRunnableContext context= new BusyIndicatorRunnableContext();
		IEGLSearchScope scope= SearchEngine.createWorkspaceScope();
		int style= IEGLSearchConstants.PART; 
		OpenPartSelectionDialog dialog= new OpenPartSelectionDialog(getShell(), context, style, scope);			
		dialog.setTitle(UINlsStrings.ImportOrganizeInputDialog_ChooseTypeDialog_title); 
		dialog.setMessage(UINlsStrings.ImportOrganizeInputDialog_ChooseTypeDialog_description);
		dialog.setFilter(fNameDialogField.getText());	//init the filter text
		if (dialog.open() == Window.OK) {
			IPart res= (IPart) dialog.getResult()[0];
			fNameDialogField.setText(res.getFullyQualifiedName('.'));
		}
	}	
	
	private void doValidation() {
		StatusInfo status= new StatusInfo();
		String newText= fNameDialogField.getText();
		if (newText.length() == 0) {
			status.setError(""); //$NON-NLS-1$
		} else {
			if (newText.equals("*")) { //$NON-NLS-1$
				if (doesExist("")) { //$NON-NLS-1$
					status.setError(UINlsStrings.ImportOrganizeInputDialog_error_entryExists); 
				}
			} else {
				IStatus val= EGLConventions.validateEGLTypeName(newText);
				if (val.matches(IStatus.ERROR)) {
					status.setError(UINlsStrings.ImportOrganizeInputDialog_error_invalidName); 
				} else {
					if (doesExist(newText)) {
						status.setError(UINlsStrings.ImportOrganizeInputDialog_error_entryExists); 
					}
				}
			}
		}
		updateStatus(status);
	}
	
	private boolean doesExist(String name) {
		for (int i= 0; i < fExistingEntries.size(); i++) {
			ImportOrderEntry entry= (ImportOrderEntry) fExistingEntries.get(i);
			if (name.equals(entry.name)) {
				return true;
			}
		}
		return false;
	}
	

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IUIHelpConstants.IMPORT_ORGANIZE_INPUT_DIALOG);
	}	
}
