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
package org.eclipse.edt.ide.rui.visualeditor.internal.wizards.newvariable;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.wizard.Wizard;


public class NewEGLVariableWizard extends Wizard {
	private EvEditor evEditor;
	
	private NewEGLVariableWizardPage newEGLVariableWizardPage;
	
	public NewEGLVariableWizard(EvEditor evEditor){
		setWindowTitle(Messages.NL_NEVW_Title);
		setDefaultPageImageDescriptor(Activator.getImageDescriptor(EvConstants.ICON_NEW_EGL_VARIABLE_WIZARD));
		this.evEditor = evEditor;
		newEGLVariableWizardPage = new NewEGLVariableWizardPage(evEditor);
	}
	
	public void addPages() {
		addPage(newEGLVariableWizardPage);
	}

	@Override
	public boolean performFinish() {
		evEditor.doSourceOperationFieldCreate(newEGLVariableWizardPage.getFieldTypePackage(), newEGLVariableWizardPage.getFieldName(), newEGLVariableWizardPage.getFieldType(), newEGLVariableWizardPage.getTemplate());
		return true;
	}	
}
