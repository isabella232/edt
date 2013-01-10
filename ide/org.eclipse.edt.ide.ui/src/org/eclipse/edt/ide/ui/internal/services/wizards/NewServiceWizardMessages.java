/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.services.wizards;

import org.eclipse.osgi.util.NLS;

public class NewServiceWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.services.wizards.NewServiceWizardMessages"; //$NON-NLS-1$

	private NewServiceWizardMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, NewServiceWizardMessages.class);
	}

	
	public static String NewServiceSummaryPage_pageName;
	public static String NewServiceSummaryPage_pageTitle;
	public static String NewServiceSummaryPage_previewLabel;
	
	public static String ServiceFromSqlDatabasePage_Description;
	
	public static String GeneratingProgressMonitor_PromptionText;
	
}
