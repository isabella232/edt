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
package org.eclipse.edt.ide.ui.internal.wizards;


public class WSDLInterfaceWizardPage extends WSDLWizardPage {
	public static final String WIZPAGENAME_WSDLInterfaceWizardPage = "WIZPAGENAME_WSDLInterfaceWizardPage"; //$NON-NLS-1$

	public WSDLInterfaceWizardPage(String pageName) {
        super(pageName);

		setTitle(NewWizardMessages.NewEGLWSDLInterfaceWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLWSDLInterfaceWizardPageDescription);   
    }
}
