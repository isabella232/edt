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
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class EGLDDWebsServicesFormPage extends EGLDDBaseFormPage {
	private EGLDDWebServicesBlock block;

	public EGLDDWebsServicesFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		block = new EGLDDWebServicesBlock(this);
	}
	
	public void setActive(boolean active) {
		super.setActive(active);
		if(active){
			block.formPageActive();
		}
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		form.setText(SOAMessages.WebServiceFormTitle);
		EGLDeploymentRoot eglDDRoot = getModelRoot();
		managedForm.setInput(eglDDRoot);	
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);			
		
		block.createContent(managedForm);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), getHelpID());		
	}

	public boolean selectReveal(Object object) {
		block.selectTableElement((ISelection)object);
		//fTableViewer.setSelection((ISelection)object, true);
		return super.selectReveal(object) ;
	}
	
	public void setFocus() {
		block.setFocus();
	}
	
	protected String getHelpID() {
		return IUIHelpConstants.EGLDD_EDITOR_WSPAGE;
	}
}
