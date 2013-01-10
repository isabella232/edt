/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.property.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.ui.internal.wizards.IStatusChangeListener;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;



/**
 * A dialog to configure the source attachment of a library (class folder, archives
 * and variable entries).
 *
 */
public class SourceAttachmentDialog extends StatusDialog {
	
	private SourceAttachmentBlock fSourceAttachmentBlock;

	/**
	 * Creates an instance of the SourceAttachmentDialog. After
	 * <code>open</code>, the edited paths can be accessed from
	 * the classpath entry returned by <code>getResult</code>
	 * @param parent Parent shell for the dialog
	 * @param entry The entry to edit.
	 */
	public SourceAttachmentDialog(Shell parent, IEGLPathEntry entry) {
		super(parent);

		IStatusChangeListener listener= new IStatusChangeListener() {
			public void statusChanged(IStatus status) {
				updateStatus(status);
			}
		};		
		fSourceAttachmentBlock= new SourceAttachmentBlock(listener, entry);			
	
		setTitle(NewWizardMessages.SourceAttachmentDialog_title); 
	}
	
	/*
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 * @since 3.4
	 */
	protected boolean isResizable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJavaHelpContextIds.SOURCE_ATTACHMENT_DIALOG);
	}		
			
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite= (Composite) super.createDialogArea(parent);
			
		Control inner= createSourceAttachmentControls(composite);
		inner.setLayoutData(new GridData(GridData.FILL_BOTH));			
		applyDialogFont(composite);		
		return composite;
	}

	/**
	 * Creates the controls for the source attachment configuration.
	 * 
	 * @param composite the parent composite
	 * @return the control 
	 */	
	protected Control createSourceAttachmentControls(Composite composite) {
		return fSourceAttachmentBlock.createControl(composite);
	}
	
	/**
	 * Returns the configured class path entry.
	 * 
	 * @return the configured class path entry 
	 */
	public IEGLPathEntry getResult() {
		return fSourceAttachmentBlock.getNewEntry();
	}
}	
