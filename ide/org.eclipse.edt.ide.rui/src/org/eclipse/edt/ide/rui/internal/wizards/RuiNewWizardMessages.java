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
package org.eclipse.edt.ide.rui.internal.wizards;

import org.eclipse.osgi.util.NLS;

public class RuiNewWizardMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.rui.internal.wizards.NewWizardMessages"; //$NON-NLS-1$

	private RuiNewWizardMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, RuiNewWizardMessages.class);
	}	
	
	public static String RUILibraryPage;
	public static String MobileRUILibraryPage;
	public static String RUILibraryPageTitle;
	public static String RUILibraryPageDescription;
	public static String RUILibraryPage_widget_library;
	public static String RUILibraryPage_libname_label;
	public static String RUILibraryPage_version_label;
	public static String RUILibraryPage_provider_label;
	public static String RUILibraryPage_details_label;
	public static String RUILibraryPage_version_tooltip;

	public static String ImportTask;
	public static String ImportTask_Unzip;
	public static String WebClientProjectTemplateWizard_0;
	public static String WebClientWithServicesProjectTemplateWizard_0;
	public static String MobileWebClientProjectTemplateWizard_0;
	public static String MobileWebClientWithServicesProjectTemplateWizard_0;
}
