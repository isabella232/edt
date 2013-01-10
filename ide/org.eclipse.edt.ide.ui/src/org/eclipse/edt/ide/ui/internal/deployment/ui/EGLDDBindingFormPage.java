/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.dialogs.EGLPartSelectionDialog;
import org.eclipse.edt.ide.ui.wizards.EGLFileConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class EGLDDBindingFormPage extends EGLDDBaseFormPage {
	private EGLDDBaseBlock block;	

	public EGLDDBindingFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		block = new EGLDDBindingBlock(this);
	}

	public void setActive(boolean active) {
		super.setActive(active);
		if(active){
			block.formPageActive();
			//fProtocolTableViewer.refresh();
		}
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		form.setText(SOAMessages.BindingFormPageTitle);
		EGLDeploymentRoot eglDDRoot = getModelRoot();
		managedForm.setInput(eglDDRoot);
		
		//create the controls						
		block.createContent(managedForm);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), getHelpID());
	}
	
	/**
	 * helper methods
	 * @param elemKind type of egl Part, i.e. IEGLSearchConstants.SERVICE
	 * @return
	 */
	public EGLPartSelectionDialog getEGLPartSelectionDialog(int elemKind, String title, String message, final String helpId)
	{
		Shell shell = getSite().getShell();
		EGLFileConfiguration fileConfig = new EGLFileConfiguration();
		
		IWorkbenchWindow workbenchwin = getSite().getWorkbenchWindow();
		
		EGLDeploymentDescriptorEditor eglddEditor = (EGLDeploymentDescriptorEditor)(getEditor());
		IProject project = eglddEditor.getProject();
		IEGLProject eglProj = EGLCore.create(project);
		IEGLSearchScope projSearchScope = SearchEngine.createEGLSearchScope(new IEGLElement[]{eglProj}, true);

        EGLPartSelectionDialog dialog = new EGLPartSelectionDialog(shell, workbenchwin, elemKind, "", null, projSearchScope, fileConfig){ //$NON-NLS-1$
        	        	public Control createDialogArea(Composite parent) {
        		Control control = super.createDialogArea(parent);
        		if(helpId != null)
        			PlatformUI.getWorkbench().getHelpSystem().setHelp(control, helpId);
        		return control;
        	}
        };
		dialog.setTitle(title);	    
		dialog.setMessage(message);
		return dialog;
	}
	
	public void refreshBlockTableViewer(){
		block.refreshTableViewer();
	}

	public boolean selectReveal(Object object) {
		block.selectTableElement((ISelection)object);
		return super.selectReveal(object) ;
	}
	
	protected String getHelpID() {
		return IUIHelpConstants.EGLDD_EDITOR_BINDINGPAGE;
	}
}
