/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
