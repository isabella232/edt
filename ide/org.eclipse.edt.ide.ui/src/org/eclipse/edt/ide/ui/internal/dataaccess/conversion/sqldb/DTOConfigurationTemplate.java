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

package org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.wizards.DTOConfiguration;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;

public abstract class DTOConfigurationTemplate extends DataToolsSqlTemplate {

	public void genDTOConfiguration(DTOConfiguration config, EglSourceContext ctx, IProgressMonitor monitor) {

		List tables = config.getSelectedTables();
		for (Object object : tables) {
			if (monitor.isCanceled()) {
				break;
			} else {
				org.eclipse.datatools.modelbase.sql.tables.Table table = (org.eclipse.datatools.modelbase.sql.tables.Table) object;
				monitor.subTask(NewWizardMessages.FromSqlDatabasePage_Generating + table.getName());
				ctx.invoke(genTable, table, ctx);
				monitor.worked(1);
			}
		}
		generateSourceFileContents(ctx);
		monitor.worked(1);
	}

	public void genDTOConfiguration(DTOConfiguration config, EglSourceContext ctx) {

		List tables = config.getSelectedTables();

		for (Object object : tables) {
			org.eclipse.datatools.modelbase.sql.tables.Table table = (org.eclipse.datatools.modelbase.sql.tables.Table) object;
			ctx.invoke(genTable, table, ctx);
		}
		generateSourceFileContents(ctx);
	}

	public void genObject(Object object, EglSourceContext ctx) {
		if (object instanceof DTOConfiguration) {
			IProgressMonitor monitor = (IProgressMonitor) ctx.get(DTO2EglSource.PROGRESS_MONITOR);
			if (monitor == null) {
				genDTOConfiguration((DTOConfiguration) object, ctx);
			} else {
				genDTOConfiguration((DTOConfiguration) object, ctx, monitor);
			}

		}
	}

	public abstract void generateCoreFileContents(EglSourceContext ctx);

	public void generateSourceFileContents(EglSourceContext ctx) {
		Hashtable<String, String> variables = ctx.getVariables();
		IProgressMonitor monitor = (IProgressMonitor) ctx.get(DTO2EglSource.PROGRESS_MONITOR);
		
		generateCoreFileContents(ctx);
		String javaJsPackage = variables.get(JAVA_JS_PACKAGE_NAME);
		if (monitor != null) {
			monitor.subTask(NewWizardMessages.FromSqlDatabasePage_Generating + "ControlStructures");
		}

		ctx.getSourceFileContentTable().put(
				DataToolsObjectsToEGLUtils.getEGLFilePath(javaJsPackage, "ControlStructures"),
				DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_ControlStructures, variables));
		
		if (monitor != null) {
			monitor.worked(1);
			monitor.subTask(NewWizardMessages.FromSqlDatabasePage_Generating + "ConditionHandlingLib");
		}
		ctx.getSourceFileContentTable().put(
				DataToolsObjectsToEGLUtils.getEGLFilePath(javaJsPackage, "ConditionHandlingLib"),
				DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_conditionHandlingLib, variables));
		if (monitor != null) {
			monitor.worked(1);
		}

	}
}
