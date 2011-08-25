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

import org.eclipse.datatools.connectivity.sqm.core.definition.DatabaseDefinition;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class DataToolsSqlColumnTemplate extends DataToolsSqlTemplate {
	
	public void genColumn(org.eclipse.datatools.modelbase.sql.tables.Column column, EglSourceContext ctx, TabbedWriter out){
		EGLSQLStructureItem item = new EGLSQLStructureItem();
		
		DatabaseDefinition def = (DatabaseDefinition)ctx.get(DataToolsObjectsToEglSource.DATA_DEFINITION_OBJECT);
		
		EGLSQLRetrieveUtility.getInstance().populateStructureItem(def, column, item);
		
		out.print(item.getName() + " " + item.getPrimitiveType());
		if (item.isNullable()) {
			out.print("?");
		}
		
		boolean needsExtraAnnotations = column.isPartOfPrimaryKey() || !item.getColumnName().equals(item.getName());
		
		if (needsExtraAnnotations) {
			out.print("{");
		}
		if (column.isPartOfPrimaryKey()) {
			out.print(" @id ");		
		}		
		if (!item.getColumnName().equals(item.getName())) {
			if (column.isPartOfPrimaryKey()) {
				out.print(",");
			}
			out.print("@column { name = \"" + item.getColumnName() + "\" }");
		}
		if (needsExtraAnnotations) {
			out.print("}");
		}
		out.println(";");
	}
	
	
}
