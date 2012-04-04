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
package org.eclipse.edt.ide.ui.internal.services.wizards;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsObjectsToEGLUtils;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsSqlTemplateConstants;
import org.eclipse.edt.ide.ui.internal.wizards.AbstractDataAccessWizard;
import org.eclipse.edt.ide.ui.internal.wizards.DTOConfigPage;
import org.eclipse.edt.ide.ui.internal.wizards.EGLCodePreviewPage;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;

public class ServiceFromDBWizard extends AbstractDataAccessWizard {

	public ServiceFromDBWizard() {
		super();
		steps = 5;
		needConfigGenerator = true;
		this.dto2EGLContributor = "org.eclipse.edt.ide.ui.internal.service.conversion.sqldb.DTO2EGLServiceContributor";
	}

	protected void initPages() {
		sqlDbPage = new DTOConfigPage(config);
		sqlDbPage.setDescription(NewServiceWizardMessages.ServiceFromSqlDatabasePage_Description);
		
		summaryPage = new EGLCodePreviewPage(NewServiceWizardMessages.NewServiceSummaryPage_pageName, NewServiceWizardMessages.NewServiceSummaryPage_pageTitle,
				NewWizardMessages.NewEGLFilesPreviewPageDescription);
		generatingProgressMonitorText = NewServiceWizardMessages.GeneratingProgressMonitor_PromptionText;
	}


	public ServiceConfiguration getConfiguration() {
		return (ServiceConfiguration) super.getConfiguration();
	}

	@Override
	public void setupEGLSourceContext(EglSourceContext context, ConnectionInfo connection, IProgressMonitor monitor) {
		super.setupEGLSourceContext(context, connection, monitor);
		
		ServiceConfiguration servConf = getConfiguration();	
		context.getVariables().put(DataToolsSqlTemplateConstants.SERVICE_NAME, servConf.getServiceName());
	}
	
	public String getMainEGLFile(){
		return DataToolsObjectsToEGLUtils.getEGLFilePath(this.javaPackageName, getConfiguration().getServiceName());
		
	}
}
