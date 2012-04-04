/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;

public class EGLPartWizard extends EGLFileWizard {

	protected boolean checkForEmptyPackage() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		//if package name not specified, give warning message
		if (checkForEmptyPackage() && getConfiguration().getFPackage().length() == 0)
			return MessageDialog.openQuestion(getShell(), 
					NewWizardMessages.NewEGLPartWizardPackageNameTitle,
					NewWizardMessages.NewEGLPartWizardDefaultPackageWarning);
		else
			return true;
	}

	protected boolean canPagePathFinish(IWizardPage[] mypages) {
		if(mypages != null){
	        for (int i = 0; i < mypages.length; i++) {
	            if (!((IWizardPage) mypages[i]).isPageComplete())
	                return false;
	        }
		}        
	    return true;   
	}
}
