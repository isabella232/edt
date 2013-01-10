/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.action;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.newvariable.EvNewEGLVariableWizardDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.wizards.newvariable.NewEGLVariableWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class NewEGLVariableAction extends Action {
	private EvEditor evEditor;
	 
	public NewEGLVariableAction(EvEditor evEditor){
		this.setText(Messages.NL_PDV_Context_Menu_New_EGL_Variable_Action);
		this.evEditor = evEditor;
	}
	
	public void run(){
		NewEGLVariableWizard newEGLVariableWizard = new NewEGLVariableWizard(evEditor);
		Shell shell = Display.getCurrent().getActiveShell();
		EvNewEGLVariableWizardDialog evNewEGLVariableWizardDialog = new EvNewEGLVariableWizardDialog(shell, newEGLVariableWizard);
		evNewEGLVariableWizardDialog.setPageSize(700,350);
		evNewEGLVariableWizardDialog.open();
	}
}
