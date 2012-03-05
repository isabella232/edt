/*******************************************************************************
 * Copyright 脗漏 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.libraries.wizards;

import org.eclipse.osgi.util.NLS;

public class NewLibraryWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.libraries.wizards.NewLibraryWizardMessages"; //$NON-NLS-1$

	private NewLibraryWizardMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, NewLibraryWizardMessages.class);
	}

	
	public static String NewLibrarySummaryPage_pageName;
	public static String NewLibrarySummaryPage_pageTitle;
	public static String NewLibrarySummaryPage_pageDescription;
	public static String NewLibrarySummaryPage_previewLabel;
	
	public static String LibraryFromSqlDatabasePage_Description;
	public static String GeneratingProgressMonitor_PromptionText;

}
