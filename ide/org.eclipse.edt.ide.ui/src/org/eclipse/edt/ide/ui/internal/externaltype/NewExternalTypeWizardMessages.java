/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype;

import org.eclipse.osgi.util.NLS;

public class NewExternalTypeWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.externaltype.NewExternalTypeWizardMessages"; //$NON-NLS-1$

	private NewExternalTypeWizardMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, NewExternalTypeWizardMessages.class);
	}
	
	public static String NewExternalTypeWizard_title;
	
	public static String NewExternalTypeWizardPage_title;
	public static String NewExternalTypeWizardPage_description;
}
