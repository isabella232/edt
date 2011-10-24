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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class DataToolsSqlTableTemplate extends DataToolsSqlTemplate {

	public void genTable(org.eclipse.datatools.modelbase.sql.tables.Table table, EglSourceContext ctx, TabbedWriter out){
		boolean isTableQualified = (Boolean)ctx.get(DataToolsObjectsToEglSource.TABLE_NAME_QUALIFIED);
		
		out.print("record " + table.getName() + " type Entity ");
		if(isTableQualified) {
			out.println("{ @table { name=\"" + table.getSchema().getName() + "." +table.getName() + "\" } }");		
		} else {
			out.println("{ @table { name=\"" + table.getName() + "\" } }");		
		}
		
		Object[] columns = table.getColumns().toArray();
		for (Object column : columns) {
			ctx.invoke(genColumn, (Object)column, ctx, out);	
		}		
		
		out.println("end");
		out.println("");
	}
	
	public void genObject(org.eclipse.datatools.modelbase.sql.tables.Table object, EglSourceContext ctx, TabbedWriter out){
		if (object instanceof Table) {
			genTable((Table)object, ctx, out);
		}
	}
	
	
}
