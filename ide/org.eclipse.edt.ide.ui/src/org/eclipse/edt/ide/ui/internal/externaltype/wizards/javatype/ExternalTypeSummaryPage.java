/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype.wizards.javatype;

import org.eclipse.edt.ide.ui.internal.externaltype.NewExternalTypeWizardMessages;
import org.eclipse.edt.ide.ui.internal.record.NewRecordSummaryPage;
import org.eclipse.jface.viewers.ISelection;

public class ExternalTypeSummaryPage extends NewRecordSummaryPage {
	public ExternalTypeSummaryPage(ISelection selection) {
		super(selection);
		setTitle(NewExternalTypeWizardMessages.ExternalTypeSummaryPage_pageTitle);
		setDescription(NewExternalTypeWizardMessages.ExternalTypeSummaryPage_pageDescription);
	}
	
	protected static String getPageName() {
		return NewExternalTypeWizardMessages.ExternalTypeSummaryPage_pageName;
	}
}