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
	public static String ExternalTypeFromJavaTypePage_Title;
	public static String ExternalTypeFromJavaTypePage_Description;
	
	public static String ExternalTypeFromJavaPage_SelectedClass_label;
	public static String NewExternalTypeWizardPage_selectedclass_button;
	public static String NewExternalTypeWizardPage_SelectedClassDialog_title;
	public static String NewExternalTypeWizardPage_SelectedClassDialog_message;
	public static String NewExternalTypeWizardPage_SelectAll_message;
	public static String NewExternalTypeWizardPage_DeSelectAll_message;
	
	public static String NewExternalTypeWizardPage_GeneratedAllSuperTypes_message;
	public static String NewExternalTypeWizardPage_GeneratedAllReferencedTypes_message;
	
	public static String ExternalTypeSummaryPage_pageName;
	public static String ExternalTypeSummaryPage_pageTitle;
	public static String ExternalTypeSummaryPage_pageDescription;
	
	public static String ExternalTypeFromJavaPage_Validation_NoMember;
	public static String ExternalTypeFromJavaPage_Validation_EGLBuiltinType;
}
