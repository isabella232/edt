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
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.sql.StringToken;
import org.eclipse.edt.gen.generator.eglsource.EglSourceContext;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLRetrieveUtility;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLStructureItem;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DTO2EglSource;
import org.eclipse.edt.ide.ui.internal.dataaccess.conversion.sqldb.DataToolsSqlTemplate;
import org.eclipse.edt.ide.ui.internal.record.conversion.IMessageHandler;

public class DataToolsSqlColumnTemplate extends DataToolsSqlTemplate {
	
	public void genColumn(Column column, EglSourceContext ctx){
		if(!validateColumn(column, ctx)){
			return;
		} else {
			ctx.appendVariableValue(RECORD_FILE_CONTENT, getFieldDefinition(column, ctx, true), "");
		}
	}
	
	protected boolean validateColumn(Column column, EglSourceContext ctx){
		IMessageHandler messageHandler = (IMessageHandler)ctx.get(DTO2EglSource.DB_MESSAGE_HANDLER);
		if(column.getContainedType() == null) {
			messageHandler.addMessage("Cannot get valid message from column metadata.");
			return false;
		} else if(isUnsupportedColumnTpe(column.getContainedType().getName())){
			StringBuilder builder = new StringBuilder();
			builder.append("Column Type: ");
			builder.append(column.getContainedType().getName());
			builder.append(" for ");
			builder.append(column.getTable().getSchema().getName());
			builder.append(SQLConstants.QUALIFICATION_DELIMITER);
			builder.append(column.getTable().getName());
			builder.append(SQLConstants.QUALIFICATION_DELIMITER);
			builder.append(column.getName());
			builder.append(" is not supported yet and will be skipped for its generation");
			messageHandler.addMessage(builder.toString());
			return false;
		} else {
			return true;
		}
	}
	protected String getFieldDefinition(Column column, EglSourceContext ctx, boolean isEntityRecord){

		EGLSQLStructureItem item = new EGLSQLStructureItem();
		String colNameAlias = null;
		
		DatabaseDefinition def = (DatabaseDefinition)ctx.get(DTO2EglSource.DATA_DEFINITION_OBJECT);
		
		EGLSQLRetrieveUtility.getInstance().populateStructureItem(def, column, item);				
		colNameAlias = getAliasName(item.getName().trim());

		StringBuilder builder = new StringBuilder();
		builder.append("\t");
		builder.append(getFieldName(column, item));
		builder.append(" ");
		builder.append(getFieldType(column, item));
		
		if(isEntityRecord){
			builder.append(getFieldAnnotation(column, colNameAlias, item));
		}

		builder.append(";");
		return builder.toString();
		
	}
	
	protected String getFieldName(Column column, EGLSQLStructureItem item){
		String colNameAlias = getAliasName(item.getName().trim());
		if( colNameAlias != null) {
			return StringToken.trim(colNameAlias).toLowerCase();
		} else {
			return StringToken.trim(item.getName()).toLowerCase();
		}
	}
	
	protected String getFieldType(Column column, EGLSQLStructureItem item){
		StringBuilder builder = new StringBuilder();
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
		return builder.toString();
	}
	
	protected String getFieldAnnotation(Column column, String colNameAlias, EGLSQLStructureItem item){
		boolean isPartOfPK = column.isPartOfPrimaryKey();

		StringBuilder builder = new StringBuilder();
		
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
			//TODO why column.getname()?
			builder.append(StringToken.trim(item.getColumnName()));
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
		return builder.toString();
	}
	
	
	private boolean isUnsupportedColumnTpe(String columnTypeName) {
		boolean isUnSupported = false;
		
		for(String type : unsupportedColumnType) {
			if(type.equals(columnTypeName.toLowerCase())) {
				isUnSupported = true;
				break;
			}
		}
		
		return isUnSupported;
	}
	
	private static String[] unsupportedColumnType =
		{
		   "blob", //$NON-NLS-1$
		   "clob", //$NON-NLS-1$
		   "graphic", //$NON-NLS-1$
		   "time", //$NON-NLS-1$
		   "vargraphic", //$NON-NLS-1$
		   "binary", //$NON-NLS-1$
		   "hex", //$NON-NLS-1$
		   "varchar for bit data", //$NON-NLS-1$
		};
}
