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
import org.eclipse.edt.ide.internal.sql.util.EGLSQLRetrieveUtility;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLStructureItem;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.compiler.core.IEGLConstants;

public class DataToolsSqlColumnTemplate extends DataToolsSqlTemplate {
	
	public void genColumn(org.eclipse.datatools.modelbase.sql.tables.Column column, EglSourceContext ctx, TabbedWriter out){
		EGLSQLStructureItem item = new EGLSQLStructureItem();
		
		DatabaseDefinition def = (DatabaseDefinition)ctx.get(DataToolsObjectsToEglSource.DATA_DEFINITION_OBJECT);
		
		EGLSQLRetrieveUtility.getInstance().populateStructureItem(def, column, item);
		StringBuilder builder = new StringBuilder();
		
		builder.append("    ");
		builder.append(item.getName());
		builder.append(" ");
		builder.append(item.getPrimitiveType());
		if(item.getPrimitiveType().equals(IEGLConstants.KEYWORD_CHAR)
				|| item.getPrimitiveType().equals(IEGLConstants.KEYWORD_MBCHAR) 
				|| item.getPrimitiveType().equals(IEGLConstants.KEYWORD_UNICODE) 
				|| item.getPrimitiveType().equals(SQLConstants.LIMITED_STRING) ) {
			builder.append(SQLConstants.LPAREN);
			builder.append(item.getLength());
			builder.append(SQLConstants.RPAREN);
		}
		
		boolean needsExtraAnnotations = column.isPartOfPrimaryKey() || !item.getColumnName().equals(item.getName());
		
		if (needsExtraAnnotations) {
			builder.append("{");
		}
		if (column.isPartOfPrimaryKey()) {
			builder.append(" @id ");
		}		
		if (!item.getColumnName().equals(item.getName())) {
			if (column.isPartOfPrimaryKey()) {
				builder.append(SQLConstants.COMMA_AND_SPACE);
			}
			builder.append("@column { name =");
			builder.append(SQLConstants.DOUBLE_QUOTE);
			builder.append(item.getColumnName());
			builder.append(SQLConstants.DOUBLE_QUOTE);
			builder.append("}");
		}
		if (item.isNullable()) {
			builder.append(SQLConstants.COMMA_AND_SPACE);
			builder.append(IEGLConstants.PROPERTY_ISSQLNULLABLE);
			builder.append("="); //$NON-NLS-1$		
			builder.append(IEGLConstants.KEYWORD_YES);
		}
		
		if (item.isSQLVar()) {
			builder.append(SQLConstants.COMMA_AND_SPACE);
			builder.append(IEGLConstants.PROPERTY_SQLVARIABLELEN);
			builder.append("="); //$NON-NLS-1$		
			builder.append(IEGLConstants.KEYWORD_YES);
		}
		
		if (needsExtraAnnotations) {
			builder.append("}");
		}
		
		builder.append(";");
		out.println(builder.toString());
	}
	
	
}
