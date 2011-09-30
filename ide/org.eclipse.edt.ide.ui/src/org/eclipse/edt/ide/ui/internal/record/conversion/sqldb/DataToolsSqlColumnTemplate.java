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

import java.util.Locale;

import org.eclipse.datatools.connectivity.sqm.core.definition.DatabaseDefinition;
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.edt.compiler.core.EGLKeywordHandler;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLRetrieveUtility;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLStructureItem;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.record.conversion.xmlschema.WSDLUtil;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class DataToolsSqlColumnTemplate extends DataToolsSqlTemplate {
	
	public void genColumn(Column column, EglSourceContext ctx, TabbedWriter out){
		EGLSQLStructureItem item = new EGLSQLStructureItem();
		String colNameAlias = null;
		
		DatabaseDefinition def = (DatabaseDefinition)ctx.get(DataToolsObjectsToEglSource.DATA_DEFINITION_OBJECT);
		
		EGLSQLRetrieveUtility.getInstance().populateStructureItem(def, column, item);
		StringBuilder builder = new StringBuilder();
		
		builder.append("    ");
		
		colNameAlias = getAliasName(item.getName().trim(),ctx);
		if( colNameAlias != null) {
			builder.append(colNameAlias);
		} else {
			builder.append(item.getName());
		}
		builder.append(" ");
		builder.append(item.getPrimitiveType());
		if(item.getPrimitiveType().equals(IEGLConstants.KEYWORD_CHAR)
				|| item.getPrimitiveType().equals(IEGLConstants.KEYWORD_MBCHAR) 
				|| item.getPrimitiveType().equals(IEGLConstants.KEYWORD_UNICODE) 
				|| item.getPrimitiveType().equals(SQLConstants.LIMITED_STRING) ) {
			builder.append(SQLConstants.LPAREN);
			builder.append(item.getLength());
			builder.append(SQLConstants.RPAREN);
		} else if (item.getPrimitiveType().equals(IEGLConstants.KEYWORD_DECIMAL)) {
			builder.append(SQLConstants.LPAREN);
			builder.append(item.getLength());
			if(item.getDecimals() != null) {
				builder.append(",");
				builder.append(item.getDecimals());
			}
			builder.append(SQLConstants.RPAREN);
		}
		if (item.isNullable()) {
			builder.append("?");
		}
		
		boolean isPartOfPK = column.isPartOfPrimaryKey();
		
		if (isPartOfPK) {
			builder.append("{");
		}
		if (column.isPartOfPrimaryKey()) {
			builder.append(" @id ");
		}		
		if (colNameAlias!= null) {
			if (column.isPartOfPrimaryKey()) {
				builder.append(SQLConstants.COMMA_AND_SPACE);
				builder.append("@column{ name =");
			} else {
				builder.append("{@column{ name =");
			}
			
			builder.append(SQLConstants.DOUBLE_QUOTE);
			builder.append(item.getColumnName());
			builder.append(SQLConstants.DOUBLE_QUOTE);
			if (column.isPartOfPrimaryKey()) {
				builder.append("}");
			} else {
				builder.append("}");
				builder.append("}");
			}
			
		}
		
		if (isPartOfPK) {
			builder.append("}");
		}
		
		builder.append(";");
		out.println(builder.toString());
	}
	
	private String getAliasName(String itemName,EglSourceContext ctx) {
		String alias = null;
		
		if(EGLKeywordHandler.getKeywordHashSet().contains(itemName.toLowerCase(Locale.ENGLISH))) {
			alias = itemName + ctx.nextTempIndex();
		} else {
			alias = CoreUtility.getCamelCaseString(itemName);
		}
		
		return alias;
	}
}
