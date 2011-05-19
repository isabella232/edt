/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2000, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
package org.eclipse.edt.ide.ui.internal.actions;

import org.eclipse.edt.ide.ui.internal.wizards.EGLFileWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

public class NewSourceFileAction extends AbstractOpenWizardWorkbenchAction {

	public NewSourceFileAction() {
	}
	public NewSourceFileAction(IWorkbench workbench, String label, Class[] acceptedTypes) {
		super(workbench, label, acceptedTypes, false);
	}
	protected Wizard createWizard() {  
		return new EGLFileWizard();
	}
	protected boolean shouldAcceptElement(Object obj) { 
		return true;
	}
}
