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

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.wizards.ServiceBindingWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

public class OpenEGLServiceBindingWizardAction extends AbstractOpenWizardWorkbenchAction {
	public OpenEGLServiceBindingWizardAction() {
        super();
    }

    /**
     * @param workbench
     * @param label
     * @param activatedOnTypes
     * @param acceptEmptySelection
     */
    public OpenEGLServiceBindingWizardAction(IWorkbench workbench, String label,
            Class[] activatedOnTypes, boolean acceptEmptySelection) {
        super(workbench, label, activatedOnTypes, acceptEmptySelection);
    }

    /**
     * @param workbench
     * @param label
     * @param acceptEmptySelection
     */
    public OpenEGLServiceBindingWizardAction(IWorkbench workbench, String label,
            boolean acceptEmptySelection) {
        super(workbench, label, acceptEmptySelection);
    }

	protected Wizard createWizard() {
		return new ServiceBindingWizard();
	}

    @Override
    public void run(IAction action) {
        if(EDTUIPlugin.getActiveWorkbenchWindow().getWorkbench().saveAllEditors(true))
            super.run(action);
    }
    
	public void selectionChanged(IAction action, ISelection selection) {
	    action.setEnabled(false);
	    //Only enable the menu when it is EGLService file	 
	    IEGLFile eglpartFile = null;	    
	    if(selection instanceof IStructuredSelection) {
			Object selectedElement= ((IStructuredSelection)selection).getFirstElement();
			if(selectedElement instanceof IFile) {
			    IEGLElement eglElem = EGLCore.create((IFile)selectedElement);	
			    if(eglElem instanceof IEGLFile)
			        eglpartFile = (IEGLFile)eglElem;
			} else if(selectedElement instanceof IEGLFile) {
			    eglpartFile = (IEGLFile)selectedElement;
			}
			
			if(eglpartFile != null) {				
				shouldEnableMenu(action, eglpartFile);				
			}
	    }	
	}

	protected void shouldEnableMenu(IAction action, IEGLFile eglpartFile) {
		try {
			if(eglpartFile.exists()) {
		       IPart[] allParts = eglpartFile.getAllParts();
		       boolean bFndServicePart=false;
		        for(int i=0; i<allParts.length && !bFndServicePart; i++) {
		        	if(allParts[i] instanceof SourcePart) {
		        		bFndServicePart = fndType((SourcePart)allParts[i], action);
		        	}
		        }					
			}
		} catch (EGLModelException e) {
			e.printStackTrace();
		}
	}
    
	protected boolean fndType(SourcePart part, IAction action) {
	    if(part.isService()) {
	        action.setEnabled(true);
	        return true;
	    }
	    return false;
	}
}
