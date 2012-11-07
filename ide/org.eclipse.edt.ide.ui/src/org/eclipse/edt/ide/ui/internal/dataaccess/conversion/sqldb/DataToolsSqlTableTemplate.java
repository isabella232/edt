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


package org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb;

import java.util.Hashtable;

import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;

public class DataToolsSqlTableTemplate extends org.eclipse.edt.ide.ui.internal.record.conversion.sqldb.DataToolsSqlTableTemplate{
	private final String genEntityRecordColumnMethodName = "genEntityRecordColumn";
	private final String genSearchRecordColumnMethodName = "genSearchRecordColumn";
	
	public void genTable(Table table, EglSourceContext ctx){

		Hashtable<String, String> variables = ctx.getVariables();
		DataToolsObjectsToEGLUtils.cleanTableVariable(variables);
		variables.put(ENTITY_NAME, this.getEntityRecordName(table));
		variables.put(ENTITY_RECORD_NAME, this.getEntityRecordName(table));
		

		boolean isTableQualified = (Boolean)ctx.get(DTO2EglSource.TABLE_NAME_QUALIFIED);
		if(isTableQualified) {
			variables.put(SCHEMA_NAME, table.getSchema().getName());
			variables.put(SCHEMA_PREFIX, table.getSchema().getName() + ".");
		} else {
			variables.put(SCHEMA_NAME, "");
			variables.put(SCHEMA_PREFIX, "");
		}

		genRecordEGLFile(table, ctx);
		updateCRUDMethod(ctx);
	}
	
	public void updateCRUDMethod(EglSourceContext ctx){
		Hashtable<String, String> variables = ctx.getVariables();
		
		String addMethod = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_addMethods, variables);
		ctx.appendVariableValue(SERVICE_LIBRARY_METHODS, addMethod, "\n");
		
		if(ctx.getVariables().get(SEARCH_METHOD_PARAM) != null){
			
			String getMethod = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_getmethod, variables);
			ctx.appendVariableValue(SERVICE_LIBRARY_METHODS, getMethod, "\n");
			
			String udpateMethod = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_updatemethod, variables);
			ctx.appendVariableValue(SERVICE_LIBRARY_METHODS, udpateMethod, "\n");


			String deleteMethod = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_deleteMethod, variables);
			ctx.appendVariableValue(SERVICE_LIBRARY_METHODS, deleteMethod, "\n");

			String existMethod = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_existMethod, variables);
			ctx.appendVariableValue(SERVICE_LIBRARY_METHODS, existMethod, "\n");
			
		}

		String isValidMethod = DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_isValidmethod, variables);
		ctx.appendVariableValue(SERVICE_LIBRARY_METHODS, isValidMethod, "\n");
		
	}
	
	
	public void genRecordEGLFile(Table table, EglSourceContext ctx){

		Hashtable<String, String> variable = ctx.getVariables();
		String fileName = getEntityRecordName(table);
		String packageName = variable.get(JAVA_JS_PACKAGE_NAME);
		String filePath = DataToolsObjectsToEGLUtils.getEGLFilePath(packageName, fileName);
		
		StringBuilder recordsDef = new StringBuilder();
		recordsDef.append(DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_sql_recordHeader, ctx.getVariables()));
		
		recordsDef.append(getEntityRecordHeader(table, ctx));	

		Object[] columns = table.getColumns().toArray();
		for (Object column : columns) {
			ctx.invoke(genEntityRecordColumnMethodName, (Object)column, ctx, recordsDef);	
		}
		recordsDef.append(getRecordFooter());
		
		recordsDef.append(getSearchRecordHeader(table, ctx));	
		for (Object column : columns) {
			ctx.invoke(genSearchRecordColumnMethodName, (Object)column, ctx, recordsDef);	
		}
		recordsDef.append(getRecordFooter());
		
		recordsDef.append(DataToolsObjectsToEGLUtils.getReplacedString(EGLCodeTemplate.mdd_statusRecord, variable));
		ctx.getSourceFileContentTable().put(filePath, recordsDef.toString());
	}
	
	
	protected String getSearchRecordName(Table table){
		return getEntityRecordName(table) + "Search";
	}
	
	protected String getSearchRecordHeader(Table table, EglSourceContext ctx){
		return "record " + getSearchRecordName(table);
	}

	public void genObject(Table object, EglSourceContext ctx){
		if (object instanceof Table) {
			genTable((Table)object, ctx);
		}
	}
	
	
	
}
