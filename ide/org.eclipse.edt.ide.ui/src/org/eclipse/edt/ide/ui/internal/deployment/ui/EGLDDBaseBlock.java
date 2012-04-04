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

import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public abstract class EGLDDBaseBlock extends MasterDetailsBlock {
	protected FormPage fPage;
	private EGLDeploymentRoot fEGLDeploymentRoot;
	protected TableViewer fTableViewer;

	protected EGLDeploymentRoot getEGLDeploymentRootInput()
	{
		if(fEGLDeploymentRoot == null)
		{
			FormEditor formEditor = fPage.getEditor();
			if(formEditor instanceof EGLDeploymentDescriptorEditor)		
				fEGLDeploymentRoot = ((EGLDeploymentDescriptorEditor)formEditor).getModelRoot();
		}		
		return fEGLDeploymentRoot;		
	}

	public void refreshTableViewer() {
		fTableViewer.refresh();				
	}

	/**
	 * the form page calls this method when it become active
	 *	so you can set some control state on the block when the page it is on become active
	 * i.e. select the 1st element if nothing is selected 
	 */
	public void formPageActive() {
		refreshTableViewer();
		EGLDDBaseFormPage.selectFristElementInTable(fTableViewer);
	}	

	public void selectTableElement(ISelection selection){
		fTableViewer.setSelection(selection, true);
	}	
}
