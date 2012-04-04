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

package org.eclipse.edt.ide.ui.internal.service.conversion.sqldb;

import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DTO2EglSource;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsObjectsToEGLUtils;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.EGLCodeTemplate;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;

public class DTOConfigurationTemplate extends org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DTOConfigurationTemplate {

	@Override
	public void generateCoreFileContents(EglSourceContext ctx) {

		Hashtable<String, String> variables = ctx.getVariables();

		IProgressMonitor monitor = (IProgressMonitor) ctx.get(DTO2EglSource.PROGRESS_MONITOR);
		if (monitor != null) {
			monitor.subTask(NewWizardMessages.FromSqlDatabasePage_Generating + variables.get(SERVICE_NAME));
		}

		String serviceHeader = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_service_serviceHeader, variables);
		String serviceMethods = variables.get(SERVICE_LIBRARY_METHODS);
		ctx.getSourceFileContentTable().put(DataToolsObjectsToEGLUtils.getEGLFilePath(variables.get(JAVA_PACKAGE_NAME), variables.get(SERVICE_NAME)),
				serviceHeader + serviceMethods + "\nend");
		if (monitor != null) {
			monitor.worked(3);
		}
	}
}
