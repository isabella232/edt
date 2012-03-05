/*******************************************************************************
 * Copyright 漏 2012 IBM Corporation and others.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsSqlTemplateConstants;
import org.eclipse.edt.ide.ui.internal.services.wizards.NewServiceWizardMessages;
import org.eclipse.edt.ide.ui.internal.services.wizards.ServiceConfiguration;
import org.eclipse.edt.ide.ui.internal.wizards.AbstractDataAccessWizard;
import org.eclipse.edt.ide.ui.internal.wizards.DTOConfigPage;
import org.eclipse.edt.ide.ui.internal.wizards.EGLCodePreviewPage;

public class LibraryFromDBWizard extends AbstractDataAccessWizard {

	public LibraryFromDBWizard() {
		super();
		needConfigGenerator = true;
		steps = 5;
		this.dto2EGLContributor = "org.eclipse.edt.ide.ui.internal.library.conversion.sqldb.DTO2EGLLibraryContributor";
	}

	protected void initPages() {
		sqlDbPage = new DTOConfigPage(config);
		sqlDbPage.setDescription(NewLibraryWizardMessages.LibraryFromSqlDatabasePage_Description);
		
		summaryPage = new EGLCodePreviewPage(NewLibraryWizardMessages.NewLibrarySummaryPage_pageName, NewLibraryWizardMessages.NewLibrarySummaryPage_pageTitle,
				NewLibraryWizardMessages.NewLibrarySummaryPage_pageDescription);
		generatingProgressMonitorText = NewLibraryWizardMessages.GeneratingProgressMonitor_PromptionText;
	}


	public LibraryConfiguration getConfiguration() {
		return (LibraryConfiguration) super.getConfiguration();
	}

	@Override
	public void setupEGLSourceContext(EglSourceContext context, ConnectionInfo connection, IProgressMonitor monitor) {
		super.setupEGLSourceContext(context, connection, monitor);
		
		LibraryConfiguration libConf = getConfiguration();		
		context.getVariables().put(DataToolsSqlTemplateConstants.LIBRARY_NAME, libConf.getLibraryName());
	}
}