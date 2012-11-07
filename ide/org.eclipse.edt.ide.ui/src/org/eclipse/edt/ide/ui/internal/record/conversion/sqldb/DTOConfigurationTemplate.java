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

package org.eclipse.edt.ide.ui.internal.record.conversion.sqldb;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DTO2EglSource;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsObjectsToEGLUtils;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsSqlTemplate;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.EGLCodeTemplate;
import org.eclipse.edt.ide.ui.internal.wizards.DTOConfiguration;

public class DTOConfigurationTemplate extends DataToolsSqlTemplate {

	public void genDTOConfiguration(DTOConfiguration config, EglSourceContext ctx, IProgressMonitor monitor) {

		List tables = config.getSelectedTables();

		if (!tables.isEmpty()) {
			String s = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_sql_recordHeader, ctx.getVariables());
			ctx.appendVariableValue(RECORD_FILE_CONTENT, s, "");

		}
		
		for (Object object : tables) {
			if (monitor.isCanceled()) {
				break;
			} else {
				org.eclipse.datatools.modelbase.sql.tables.Table table = (org.eclipse.datatools.modelbase.sql.tables.Table) object;
				monitor.subTask(table.getName());
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

	public void generateSourceFileContents(EglSourceContext ctx) {
		Hashtable<String, String> variables = ctx.getVariables();
		String basePackage = variables.get(BASE_PACKAGE_NAME);
		String recordFileName = variables.get(RECORD_FILE_NAME);
		String recordFileContent = variables.get(RECORD_FILE_CONTENT);
		ctx.getSourceFileContentTable().put(
				DataToolsObjectsToEGLUtils.getEGLFilePath(basePackage, recordFileName),
				recordFileContent);
		
	}
}
