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
package org.eclipse.edt.ide.ui.internal.record.wizards.sqldb;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsSqlTemplateConstants;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizard;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.edt.ide.ui.internal.record.RecordConfiguration;
import org.eclipse.edt.ide.ui.internal.wizards.AbstractDataAccessWizard;
import org.eclipse.edt.ide.ui.internal.wizards.DTOConfigPage;
import org.eclipse.edt.ide.ui.internal.wizards.EGLCodePreviewPage;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class RecordFromSqlDatabaseWizard extends AbstractDataAccessWizard {

	public RecordFromSqlDatabaseWizard() {
		super();
		steps = 0;
		needConfigGenerator = false;
		this.dto2EGLContributor = "org.eclipse.edt.ide.ui.internal.record.conversion.sqldb.DTO2EGLRecordContributor";
	}

	protected void initPages() {
		sqlDbPage = new DTOConfigPage(config);
		sqlDbPage.setDescription(NewRecordWizardMessages.RecordFromSqlDatabasePage_Description);

		summaryPage = new EGLCodePreviewPage(NewRecordWizardMessages.NewRecordSummaryPage_pageName, NewRecordWizardMessages.NewRecordSummaryPage_pageTitle,
				NewRecordWizardMessages.NewRecordSummaryPage_pageDescription);
		generatingProgressMonitorText = NewRecordWizardMessages.GeneratingProgressMonitor_PromptionText;
	}

	public RecordConfiguration getConfiguration() {
		RecordConfiguration configuration = (RecordConfiguration) ((NewRecordWizard) getParentWizard()).getConfiguration();
		return configuration;
	}
	
	protected IRunnableWithProgress createFinishOperation() {
		return new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				Set<String> fileNames = sourceFileContents.keySet();
				for (String fileName : fileNames) {
					((NewRecordWizard) getParentWizard()).setContentObj(sourceFileContents.get(fileName));
					return;
				}
			}
		};

	}
	
	@Override
	public void setupEGLSourceContext(EglSourceContext context, ConnectionInfo connection, IProgressMonitor monitor) {
		super.setupEGLSourceContext(context, connection, monitor);
		
		RecordConfiguration conf = getConfiguration();		
		context.getVariables().put(DataToolsSqlTemplateConstants.RECORD_FILE_NAME, conf.getFileName());
	}
}
