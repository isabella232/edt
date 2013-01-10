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
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.osgi.util.NLS;

public class NewHandlerWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.ui.internal.handlers.wizards.NewHandlerWizardMessages"; //$NON-NLS-1$

	private NewHandlerWizardMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, NewHandlerWizardMessages.class);
	}

	public static String HandlerTemplatePage_title;
	public static String HandlerTemplatePage_description;
	public static String HandlerTemplatePage_templatesLabel;
	public static String HandlerTemplatePage_descriptionLabel;
	
	public static String NewHandlerWizard_title;
	
	public static String NewHandlerWizardPage_title;
	public static String NewHandlerWizardPage_description;
	
	public static String NewEGLRUIHandlerWizardPageTitle;
	public static String NewEGLRUIHandlerWizardPageDescription;
	public static String NewEGLRUIHandlerWizardPagePartlabel;
	public static String NewEGLRUIHandlerWizardPageTitlelabel;
	
	public static String NewHandlerSummaryPage_pageName;
	public static String NewHandlerSummaryPage_pageTitle;
	public static String NewHandlerSummaryPage_pageDescription;
	public static String NewHandlerSummaryPage_previewLabel;
	public static String NewHandlerSummaryPage_warningLabel;
	public static String NewHandlerSummaryPage_warningLabel2;
	
}
