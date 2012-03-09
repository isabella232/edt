/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.record.conversion.sqldb;

import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DTO2EglSource;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsSqlTemplate;

public class DataToolsSqlTableTemplate extends DataToolsSqlTemplate {

	public void genTable(org.eclipse.datatools.modelbase.sql.tables.Table table, EglSourceContext ctx){
		ctx.appendVariableValue(RECORD_FILE_CONTENT, getEntityRecordHeader(table, ctx), "");
		
		Object[] columns = table.getColumns().toArray();
		for (Object column : columns) {
			ctx.invoke(genColumn, (Object)column, ctx);	
		}
		ctx.appendVariableValue(RECORD_FILE_CONTENT, getRecordFooter(), "");
	}
	
	public void genObject(org.eclipse.datatools.modelbase.sql.tables.Table object, EglSourceContext ctx){
		if (object instanceof Table) {
			genTable((Table)object, ctx);
		}
	}
	
	public String getEntityRecordName(Table table){
		String name = getValidName(table).toLowerCase();
		char[] arr = name.toCharArray();
		for(int i = 0; i < arr.length; i++){
			if(Character.isLetter(arr[i])){
				arr[i] = Character.toUpperCase(arr[i]);
				break;
			}
		}
		return String.valueOf(arr);
	}
	
	public String getValidName(Table table){
		String aliasName = getAliasName(table.getName());
		return (aliasName == null)?table.getName():aliasName;
	}
	
	public String getEntityRecordHeader(org.eclipse.datatools.modelbase.sql.tables.Table table, EglSourceContext ctx){
		
		StringBuffer s = new StringBuffer("record " + getEntityRecordName(table) + " type Entity ");
		boolean isTableQualified = (Boolean)ctx.get(DTO2EglSource.TABLE_NAME_QUALIFIED);
		
		if(isTableQualified) {
			s.append("{ @table { name=\"" + table.getSchema().getName() + "." +table.getName() + "\" } }");		
		} else {
			s.append("{ @table { name=\"" + table.getName() + "\" } }");		
		}
		return s.toString();
	}
	
	public String getRecordFooter(){
		return "\nend\n";
	}
	
}
