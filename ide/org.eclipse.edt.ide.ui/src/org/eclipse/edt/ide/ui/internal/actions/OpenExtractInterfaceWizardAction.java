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

import org.eclipse.edt.ide.core.internal.model.SourcePart;
import org.eclipse.edt.ide.core.model.Signature;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDWebServicesBlock;
import org.eclipse.edt.ide.ui.internal.wizards.ExtractInterfaceFrExternalTypeWizard;
import org.eclipse.edt.ide.ui.internal.wizards.ExtractInterfaceWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.Wizard;

public class OpenExtractInterfaceWizardAction extends OpenEGLServiceBindingWizardAction {
	private boolean isService = false;
	
    @Override
    protected Wizard createWizard() {
    	if(isService)
    		return new ExtractInterfaceWizard();
    	else
    		return new ExtractInterfaceFrExternalTypeWizard();
    }

	protected boolean fndType(SourcePart part, IAction action) {
		isService = false;
		if (part.isService()) {
			isService = true;
			action.setEnabled(true);
			return true;
		} else if (part.isExternalType()) {
			// need to check the subType, need to be hostProgram External Type
			String partSubTypeString = Signature.toString(part.getSubTypeSignature());
			if (EGLDDWebServicesBlock.HOSTPGM.equalsIgnoreCase(partSubTypeString)) {
				action.setEnabled(true);
				return true;
			}
		}

		return false;
	}
}
