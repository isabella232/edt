/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class DedicatedBindingDetailPage extends WebBindingDetailPage {

	private Binding fDedicatedBinding;
	
	public DedicatedBindingDetailPage(){
		super();
		nColumnSpan = 2;
	}
	
	protected Composite createDetailSection(Composite parent,
			FormToolkit toolkit, int sectionStyle, int columnSpan) {
		return createSection(parent, toolkit, SOAMessages.DedicatedBindingDetailSecTitle, 
				SOAMessages.DedicatedBindingDetailSecDescp, sectionStyle, columnSpan);
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		Label note = toolkit.createLabel(parent, NewWizardMessages.LabelDedicatedBindingNote, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan;
		gd.verticalIndent = 20;
		gd.widthHint = 1; // without a width hint it doesn't wrap.
		note.setLayoutData(gd);
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if(ssel.size() == 1) {
			fDedicatedBinding = (Binding)ssel.getFirstElement();
		}
		else {
			fDedicatedBinding = null;
		}
		update();
	}
	
	protected void update(){
		fNameText.setText(fDedicatedBinding.getName() == null ? "" : fDedicatedBinding.getName()); //$NON-NLS-1$
	}
	
	protected void HandleNameChanged() {
		fDedicatedBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
}
