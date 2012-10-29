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

package org.eclipse.edt.ide.internal.sql.util;


import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.sqm.core.definition.DatabaseDefinition;
import org.eclipse.datatools.connectivity.sqm.internal.core.connection.ConnectionInfo;
import org.eclipse.datatools.modelbase.dbdefinition.PredefinedDataTypeDefinition;
import org.eclipse.datatools.modelbase.sql.datatypes.IntervalDataType;
import org.eclipse.datatools.modelbase.sql.datatypes.IntervalQualifierType;
import org.eclipse.datatools.modelbase.sql.datatypes.PredefinedDataType;
import org.eclipse.datatools.modelbase.sql.datatypes.PrimitiveType;
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.edt.compiler.internal.EGLSQLKeywordHandler;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.sql.SQLPlugin;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.datatools.modelbase.sql.datatypes.XMLDataType;

/**
 * Utility to retrieve column information from SQL tables in a database.
 */
public class EGLSQLRetrieveUtility {

	private boolean isSQLJoin = false;
	private String[][] sqlTables;
	private Object[][] sqlTableVariables;
	private EGLSQLRetrieveResults retrieveResults;
	private ConnectionInfo connectionInfo;
	private IConnectionProfile connectionProfile;
	private List undefinedTables;
	private List matchingTables;
	private String dbName;
	private boolean isCharType;
	private boolean isMBCharType;
	private boolean isCharacterUnicode;
	private boolean isNationalCharacterUnicode;
	private boolean isCharacterLimitedString;
	private boolean isNationalCharacterLimitedString;
	private boolean isNationalCharacterString;
	private boolean isLowercaseItemName;
	private boolean isLowercaseItemNameAndUppercaseCharacterAfterUnderscore = true;
	private boolean isRemoveUnderscoresInName = true;
	private String typeForDateTimeTypes = "timestamp";
	private boolean addSqlDataCode;
	//private EGLSQLMeta[] parsedMetaData;
	private final static char UNDERSCORE = '_'; //$NON-NLS-1$
	private static EGLSQLRetrieveUtility INSTANCE = new EGLSQLRetrieveUtility();
	
	public static EGLSQLRetrieveUtility getInstance() {
		 return INSTANCE ;
	}
	
	/*public EGLSQLRetrieveResults retrieveColumnInformation(
			String[][] tableList, Object[][] tableVariableList, Shell shell, String recordName) {

		setPreferences();
		retrieveResults = new EGLSQLRetrieveResults();
		if (shell == null) {
			return retrieveResults;
		}
		sqlTables = tableList;
		sqlTableVariables = tableVariableList;
		connectionProfile = EGLSQLUtility.getCurrentConnectionProfile();
		
		if (connectionProfile == null) {
			String messageText = EGLMessage.createEGLValidationErrorMessage(
						EGLMessage.EGLMESSAGE_ERROR_NO_CONNECTION_OBJECT_SELECTED, null, "").getBuiltMessage();
			addMessageToResults(messageText);
			retrieveResults.setRetrieveFailed(true);
			return retrieveResults;
		}

		IRunnableWithProgress progressOp = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				setIsSQLJoin();
				connectionInfo = doConnect();
				if (connectionInfo != null) {
					createDataItemsFromDatabase(connectionInfo);
				}
			}
		};
		ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
		try {
			dlg.run(false, true, progressOp);
		} catch (Exception e) {
			
		} finally {
			closeConnection(this.connectionProfile);
		}

		return retrieveResults;
	}*/

	private void setCharControlOptionsBasedOnSQLPreferences() {
		isCharType = getEGLBasePlugin().isCharacterOptionChar();
		isMBCharType = getEGLBasePlugin().isCharacterOptionMBChar();
		isCharacterUnicode = getEGLBasePlugin().isCharacterOptionUnicode();
		isCharacterLimitedString = getEGLBasePlugin().isCharacterOptionLimitedString();
	}
	
	private void setNationalCharOptionsBasedOnSQLPreferences() {
		isNationalCharacterUnicode = getEGLBasePlugin().isNationalCharOptionUnicode();
		isNationalCharacterString = getEGLBasePlugin().isNationalCharOptionString();
		isNationalCharacterLimitedString = getEGLBasePlugin().isNationalCharOptionLimitedString();
	}

	private void setItemNameControlOptionsBasedOnSQLPreferences() {
		isLowercaseItemName = getEGLBasePlugin().isLowercaseItemNameCaseOption();
		isLowercaseItemNameAndUppercaseCharacterAfterUnderscore = getEGLBasePlugin().isLowercaseNameAndUppercaseCharacterAfterUnderscoreOption();
		isRemoveUnderscoresInName = getEGLBasePlugin().isRemoveUnderscoresInNameOption();
	}

	private void setIsSQLJoin() {
		if (sqlTableVariables == null) {
			sqlTableVariables = new Object[0][0];
		}
		if (sqlTables.length > 1 || sqlTableVariables.length > 1
				|| sqlTables.length + sqlTableVariables.length > 1) {
			isSQLJoin = true;
		}
	}

	/*private void createDataItemsFromDatabase(ConnectionInfo connection) {
		EList columns;
		Iterator columnIterator;
		Database thisDB;
		Table thisTable;
		String tableLabel;
		String tableName;
		Column thisColumn;
		undefinedTables = new ArrayList();
		matchingTables = new ArrayList();
		Set columnNames = new HashSet();
		boolean isDup;
		
		String secondaryID = EGLSQLUtility.getSecondaryID(connectionProfile).trim();
		if (secondaryID.length() > 0) {
			java.sql.Statement stmt = null;
			try {
				Connection con = connection.getSharedConnection();
				stmt = con.createStatement();
				String setStatement = "SET CURRENT SQLID = '" + secondaryID + "'"; //$NON-NLS-2$ //$NON-NLS-1$
				stmt.execute(setStatement);
			} catch (SQLException ex) {
				String errorString = "SQLException: " + ex.getMessage() + " ErrorCode:  " + String.valueOf(ex.getErrorCode()); //$NON-NLS-1$ //$NON-NLS-2$
				System.out.print(errorString);
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) { }
			}
		}
		
		
		thisDB = connection.getSharedDatabase();
		buildTableLists(thisDB, connection);
		// If one or more of the tables we're retrieving is not defined in the
		// database issue an error message and cancel this action.
		if (!undefinedTables.isEmpty()) {
			handleUndefinedTablesError(undefinedTables);
			return;
		}
		Object[] tableInfo;
		for (int i = 0; i < matchingTables.size(); i++) {
			tableInfo = (Object[]) matchingTables.get(i);
			thisTable = (Table) tableInfo[0];
			tableLabel = (String) tableInfo[1];
			tableName = (String) tableInfo[2];
			columns = thisTable.getColumns();
			columnIterator = columns.iterator();
			EList keys = null;
			if (thisTable instanceof BaseTable) {
				PrimaryKey primaryKey = ((BaseTable) thisTable).getPrimaryKey();
				if (primaryKey != null) {
					keys = primaryKey.getMembers();
				}
			}
			while (columnIterator.hasNext()) {
				isDup = false;
				thisColumn = (Column) columnIterator.next();
				if (columnNames.contains(thisColumn.getName())) {
					isDup = true;
				}
				else {
					columnNames.add(thisColumn.getName());
				}
				addDataItemForThisColumn(connection, thisColumn, tableLabel, tableName, keys, true, isDup);
			}
		}
	}*/

	/*private void buildTableLists(Database database, ConnectionInfo connection) {
		Table thisRDBTable;
		String tableName = ""; //$NON-NLS-1$
		dbName = ""; //$NON-NLS-1$
		String qualifiedTableName = ""; //$NON-NLS-1$
		String schemaName = ""; //$NON-NLS-1$

		for (int i = 0; i < sqlTables.length; i++) {
			tableName = parsedMetaData[i].getTableName();
			if (tableName != null) {
				schemaName = parsedMetaData[i].getSchemaName();
				dbName = parsedMetaData[i].getDatabaseName();
				if (schemaName == SQLConstants.SYSTEMID) {
					Connection con = connection.getSharedConnection();
					try {
						schemaName = con.getMetaData().getUserName();
					} catch (SQLException e) {
						schemaName = ""; //$NON-NLS-1$ 
					}
				}
				qualifiedTableName = dbName != null ? dbName + SQLConstants.QUALIFICATION_DELIMITER 
						+ schemaName + SQLConstants.QUALIFICATION_DELIMITER + tableName
						: schemaName + SQLConstants.QUALIFICATION_DELIMITER + tableName;
				thisRDBTable = EGLSQLUtility.findTable(database, qualifiedTableName);
				if ( EGLSQLUtility.eglRBDSQLUtil != null )
				{
					thisRDBTable = EGLSQLUtility.eglRBDSQLUtil.resolveZSeriesCatalogSynonym( thisRDBTable );
				}

			} else {
				thisRDBTable = null;
				tableName = ""; //$NON-NLS-1$
				qualifiedTableName = ""; //$NON-NLS-1$				
			}

			if (thisRDBTable == null) {
				undefinedTables.add(qualifiedTableName);
			} else {
				Object[] tableInfo = new Object[3];
				tableInfo[0] = thisRDBTable;
				tableInfo[1] = sqlTables[i][1];
				tableInfo[2] = tableName;
				matchingTables.add(tableInfo);
			}
		}
	}*/

	/*private void handleUndefinedTablesError(List undefinedTables) {
		String[] inserts;
		String messageText;
		String messageKey;

		if (undefinedTables.size() == 1) {
			inserts = new String[] { (String) undefinedTables.get(0), dbName };
			messageKey = EGLMessage.EGLMESSAGE_ERROR_TABLE_NOT_FOUND_ON_RETRIEVE;
		} else {
			String missingTablesInsert = ""; //$NON-NLS-1$
			for (int i = 0; i < undefinedTables.size(); i++) {
				missingTablesInsert = missingTablesInsert.concat((String) undefinedTables.get(i));
				if (i + 1 < undefinedTables.size()) {
					missingTablesInsert = missingTablesInsert.concat(SQLConstants.COMMA_AND_SPACE);
				}
			}
			inserts = new String[] { missingTablesInsert, dbName };
			messageKey = EGLMessage.EGLMESSAGE_ERROR_TABLES_NOT_FOUND_ON_RETRIEVE;
		}
		messageText = EGLMessage.createEGLValidationErrorMessage(messageKey, null, inserts).getBuiltMessage();

		// Add error to error list.
		addMessageToResults(messageText);
		retrieveResults.setRetrieveFailed(true);
	}*/
	
	public boolean resolveToEGLType(DatabaseDefinition databaseDefinition, PredefinedDataType type, String tableName, String itemName, EGLSQLStructureItem sqlStructureItem, boolean allowTextTypesForDateTimeTypes, boolean isNullable) {
		setPreferences();
		PredefinedDataTypeDefinition typeDefinition = databaseDefinition.getPredefinedDataTypeDefinition(type.getName());
		boolean recognizedType = true;
		int intType;
		
		if (type.getName().equals("DATETIME") && type instanceof IntervalDataType) {
			intType = setJDBCType(type) ;
		} else if(type.getName().equals("XML") && type instanceof XMLDataType) {
			intType = Types.SQLXML;
		} else {
			intType = typeDefinition.getJdbcEnumType();
		}
		switch (intType) {
		case Types.SQLXML: // Treated as String in current release
		case Types.CHAR: {
			sqlStructureItem.setSQLVar(false);
			recognizedType = handleCharType(type, sqlStructureItem, itemName);
			break;
		}
		case Types.VARCHAR: {
			sqlStructureItem.setSQLVar(true);
			recognizedType = handleCharType(type, sqlStructureItem, itemName);
			break;
		}
		case Types.LONGVARCHAR: {
			sqlStructureItem.setSQLVar(true);
			recognizedType = handleLongVarCharType(type, sqlStructureItem, itemName);
			break;
		}
		case Types.DATE: {
			handleDateType( sqlStructureItem, allowTextTypesForDateTimeTypes, isNullable );
			break;
		}
		case Types.TIME: {
			handleTimeType( sqlStructureItem, allowTextTypesForDateTimeTypes, isNullable );
			break;
		}
		case Types.TIMESTAMP: {
			handleTimestampType( sqlStructureItem, allowTextTypesForDateTimeTypes, isNullable );
			break;
		}
		case Types.TINYINT: // Treated as SMALLINT
		case Types.SMALLINT: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_SMALLINT);
			break;
		}
		case Types.BIGINT: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_BIGINT);
			break;
		}
		case Types.REAL: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_SMALLFLOAT);
			break;
		}
		case Types.INTEGER: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_INT);
			break;
		}
		case Types.FLOAT: // Treated as Double
		case Types.DOUBLE: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_FLOAT);
			break;
		}
		case Types.NUMERIC: {
			//handleNumericType(type, sqlStructureItem, itemName); 
			//TODO : deal with DB numeric type as decimal type since EDT does not support 'num' yet.
			handleDecimalType(type, sqlStructureItem, itemName);
			break;
		}
		
		case Types.DECIMAL: {
			handleDecimalType(type, sqlStructureItem, itemName);
			break;
		}
		case Types.BINARY:
		case Types.VARBINARY: {
			handleBinaryType(type, sqlStructureItem, itemName);
			break;
		}
		case Types.LONGVARBINARY: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_HEX);
			// The size is not specified when LONGVARBINARY columns are
			// defined. It can be up to 2 gigabytes in size which is larger
			// than we can handle so set it to the maximum length that EGL
			// supports.
			
			//sqlStructureItem.getMessages().add(
					//getInfoMessage(EGLMessage.EGLMESSAGE_INFO_HEX_LENGTH_SHORTENED_ON_RETRIEVE, new String[] { itemName })); // TODO
			
			sqlStructureItem.setLength("65534"); //$NON-NLS-1$
			break;
		}
		case Types.CLOB: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_CLOB);
			break;
		}
		case Types.BLOB: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_BLOB);
			break;
		}
		case Types.BIT: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_SMALLINT);
			break;
		}
		case Types.BOOLEAN: {
			sqlStructureItem.setPrimitiveType(IEGLConstants.KEYWORD_BOOLEAN);
			break;
		}
		default: {
			recognizedType = false;
			break;
		}
		}
		
		return recognizedType;
	}

	private int setJDBCType(PredefinedDataType type) {
		int intType;
		IntervalQualifierType qualifier = ((IntervalDataType) type).getLeadingQualifier();
		switch (qualifier.getValue()) {
		case 3:
		case 4:
		case 5:
		case 6:
			intType = Types.TIME;
			break;
		default:
			intType = Types.TIMESTAMP;
		}
		return intType;
	}

	/**
	 * NOTE: This method was added to support MDD database imports, and will be removed during a future
	 * refactoring of this class. DO NOT use this method unless you understand exactly what it is doing 
	 * and why.
	 * @param definition
	 * @param column
	 * @param item
	 * @return true if the column was successfully imported
	 */
	public boolean populateStructureItem(DatabaseDefinition definition, Column column, EGLSQLStructureItem item) {
		boolean success = false;
		
		setPreferences();
		String name = column.getName();
		String itemName = changeItemNameBasedOnControlOptionsInSQLRetrievePreferences(name);
		PredefinedDataType type = (PredefinedDataType) column.getContainedType();
		if (type == null) {
			item.getMessages().add(getInfoMessage(
					EGLMessage.EGLMESSAGE_INFO_UNSUPPORTED_SQL_TYPE_ON_RETRIEVE,
					new String[] { itemName, "<UNKNOWN>" }));
		}
		else {
			if (resolveToEGLType(definition, type, column.getTable().getName(), itemName, item, false, column.isNullable())) {
				item.setDescription(name);
				item.setNullable(column.isNullable());
				item.setColumnName(name);
				item.setReadOnly(false);
				item.setName(itemName);
				item.setColumnName(addEscapeCharactersWhenNecessaryInName(item.getColumnName()));
				success = true;
			} else {
				item.getMessages().add(getInfoMessage(
						EGLMessage.EGLMESSAGE_INFO_UNSUPPORTED_SQL_TYPE_ON_RETRIEVE,
						new String[] { itemName, type.getName() })); 
			}
		}
		
		return success;
	}
	
	/*private void addDataItemForThisColumn(ConnectionInfo connection, Column column, String tableLabel, 
							String tableName, EList keys, boolean shouldRetrieveResults, boolean isDup) {

		EGLSQLStructureItem newItem = new EGLSQLStructureItem();
		String name = column.getName();
		String itemName = changeItemNameBasedOnControlOptionsInSQLRetrievePreferences(name);
		PredefinedDataType type = (PredefinedDataType) column.getContainedType();
		if (type == null) {
			addInfoMessage(
					EGLMessage.EGLMESSAGE_INFO_UNSUPPORTED_SQL_TYPE_ON_RETRIEVE,
					new String[] { itemName, "<UNKNOWN>" });
			return;
		}
		if (resolveToEGLType(connection.getDatabaseDefinition(), type, tableName, itemName, newItem, true, column.isNullable())) {
			if (newItem.getMessages().size() > 0 && retrieveResults != null) {
				//copy messages from item to the result set
				Iterator messages = newItem.getMessages().iterator();
				while (messages.hasNext()) {
					EGLMessage message = (EGLMessage)messages.next();
					retrieveResults.getMessages().add(message.getBuiltMessage());
				}
			}
			newItem.getMessages().clear();
			newItem.setDescription(name);
			newItem.setNullable(column.isNullable());
			String validName = itemName;
			if (isSQLJoin) {
				String columnName = name;
				// Table label is optional but table name is not! Qualify with
				// table label if there is one.  Otherwise, qualify with the table name.
				if (tableLabel != null && tableLabel.trim().length() != 0) {
					columnName = tableLabel + '.' + name;
				} else {
					columnName = tableName + '.' + name;
				}
				if (isDup) {
					validName = tableName.toLowerCase() + '_' + itemName;
				}
				newItem.setColumnName(columnName);
				newItem.setReadOnly(true);
			} else {
				newItem.setColumnName(name);
				newItem.setReadOnly(false);
			}
			newItem.setName(validName);
			if (shouldRetrieveResults) {
				retrieveResults.getStructureItems().add(newItem);
				if (EGLSQLPlugin.getPlugin().getRetrievePrimaryKeyOption() && isKey(name, keys)) {
					retrieveResults.getKeys().add(validName);
				}
			}
			setMaxPrimitiveTypeInfoLengths(newItem);
			newItem.setColumnName(addEscapeCharactersWhenNecessaryInName(newItem.getColumnName()));
		} else {
			addInfoMessage(
					EGLMessage.EGLMESSAGE_INFO_UNSUPPORTED_SQL_TYPE_ON_RETRIEVE,
					new String[] { itemName, type.getName() });
		}	
	}*/

	private boolean handleCharType(PredefinedDataType type, EGLSQLStructureItem newItem, String itemName) {
		int length = 0;
		String stringLength = "0"; //$NON-NLS-1$
		boolean charType = true;
		boolean recognizedType = true;

		EStructuralFeature feature = type.eClass().getEStructuralFeature("length");
		
		switch (type.getPrimitiveType().getValue()) {
		   case PrimitiveType.CHARACTER:
		   case PrimitiveType.CHARACTER_VARYING:
		   case PrimitiveType.XML_TYPE:
			    break;
		   case PrimitiveType.NATIONAL_CHARACTER:
		   case PrimitiveType.NATIONAL_CHARACTER_VARYING:
			    charType = false;
			    break;
		   default:
			    recognizedType = false;
			    break;
		}
		if (recognizedType) {
			if(!(type instanceof XMLDataType)){
				try {
					stringLength =  ((Integer) type.eGet(feature)).toString();
					length = Integer.parseInt(stringLength);
				} catch (NumberFormatException e) {
					// The string does not represent a number, so assume it is zero
					newItem.getMessages().add(
									getErrorMessage(
											EGLMessage.EGLMESSAGE_INFO_INVALID_LENGTH_SET_TO_ZERO_ON_RETRIEVE,
											new String[] { stringLength, itemName })); 
					stringLength = "0"; //$NON-NLS-1$
				}
			}
			
			if (charType) {
				newItem.setPrimitiveType(changeCharacterTypeBasedOnPreferences()); //$NON-NLS-1$
				newItem.setLength(stringLength);
				if (newItem.getPrimitiveType().equals(IEGLConstants.KEYWORD_UNICODE) && length > 16383) {
					newItem.getMessages().add(
									getInfoMessage(
											EGLMessage.EGLMESSAGE_INFO_UNICODE_LENGTH_SHORTENED_FROM_ON_RETRIEVE,
											new String[] { itemName,
													stringLength })); 
					newItem.setLength("16383"); //$NON-NLS-1$
				}
			} else {
				newItem.setPrimitiveType(changeNationalCharacterTypeBasedOnPreferences());
				newItem.setLength(stringLength);
			}
		}

		return recognizedType;
	}

	private void handleDateType( EGLSQLStructureItem newItem, 
			boolean allowTextTypesForDateTimeTypes, boolean isNullable ){
		if ( allowTextTypesForDateTimeTypes && typeForDateTimeTypes.length() > 0 ) {
			newItem.setPrimitiveType( typeForDateTimeTypes );
			newItem.setLength( "10" );
			if ( addSqlDataCode )
			{
				newItem.setSqlDataCode( isNullable ? "384" : "385" );
			}
		} else {
			newItem.setPrimitiveType( IEGLConstants.KEYWORD_DATE );
		}
	}
	
	private void handleTimeType( EGLSQLStructureItem newItem, 
			boolean allowTextTypesForDateTimeTypes, boolean isNullable ) {
		if ( allowTextTypesForDateTimeTypes && typeForDateTimeTypes.length() > 0 ) {
			newItem.setPrimitiveType( typeForDateTimeTypes );
			newItem.setLength( "8" );
			if ( addSqlDataCode ) {
				newItem.setSqlDataCode( isNullable ? "388" : "389" );
			}
		} else {
			newItem.setPrimitiveType( IEGLConstants.KEYWORD_TIME );
		}
	}
	
	private void handleTimestampType( EGLSQLStructureItem newItem,
			boolean allowTextTypesForDateTimeTypes, boolean isNullable ) {
		if ( allowTextTypesForDateTimeTypes && typeForDateTimeTypes.length() > 0 ) {
			newItem.setPrimitiveType( typeForDateTimeTypes );
			newItem.setLength( "26" );
			if ( addSqlDataCode ) {
				newItem.setSqlDataCode( isNullable ? "392" : "393" );
			}
		} else {
			newItem.setPrimitiveType( IEGLConstants.KEYWORD_TIMESTAMP_WITH_PATTERN );
		}
	}

	private boolean handleLongVarCharType(PredefinedDataType type, EGLSQLStructureItem newItem, String itemName) {
		int length = 0;
		String stringLength = "0"; //$NON-NLS-1$
		boolean charType = true;
		boolean recognizedType = true;

		EStructuralFeature feature = type.eClass().getEStructuralFeature("length");
		stringLength =  ((Integer) type.eGet(feature)).toString();
		switch (type.getPrimitiveType().getValue()) {
		  case PrimitiveType.CHARACTER:
		  case PrimitiveType.CHARACTER_VARYING:
		  case PrimitiveType.CHARACTER_LARGE_OBJECT:
			   break;
		  case PrimitiveType.NATIONAL_CHARACTER:
		  case PrimitiveType.NATIONAL_CHARACTER_VARYING:
		  case PrimitiveType.NATIONAL_CHARACTER_LARGE_OBJECT:
			   charType = false;
			   break;
		  default:
			   recognizedType = false;
			   break;
		}

		if (recognizedType) {
			try {
				length = Integer.parseInt(stringLength);
			} catch (NumberFormatException e) {
				// The string does not represent a number, so assume it is zero
				newItem.getMessages().add(
								getErrorMessage(
										EGLMessage.EGLMESSAGE_INFO_INVALID_LENGTH_SET_TO_ZERO_ON_RETRIEVE,
										new String[] { stringLength, itemName })); 
				stringLength = "0"; //$NON-NLS-1$
			}
			if (charType) {
				newItem.setPrimitiveType(changeCharacterTypeBasedOnPreferences()); //$NON-NLS-1$
				newItem.setLength(stringLength);
				if (newItem.getPrimitiveType().equals(IEGLConstants.KEYWORD_UNICODE) && length > 16383) {
					newItem.getMessages().add(
									getInfoMessage(
											EGLMessage.EGLMESSAGE_INFO_UNICODE_LENGTH_SHORTENED_FROM_ON_RETRIEVE,
											new String[] { itemName,
													stringLength })); 
					newItem.setLength("16383"); //$NON-NLS-1$
				} else if (length > 32767) {
					newItem.getMessages().add(
									getInfoMessage(
											EGLMessage.EGLMESSAGE_INFO_CHAR_LENGTH_SHORTENED_FROM_ON_RETRIEVE,
											new String[] { itemName,
													stringLength })); 
					newItem.setLength("32767"); //$NON-NLS-1$
				}
			} else {
				newItem.setPrimitiveType(changeNationalCharacterTypeBasedOnPreferences());
				newItem.setLength(stringLength);
				if (length > 16383) {
					newItem.getMessages().add(
									getInfoMessage(
											EGLMessage.EGLMESSAGE_INFO_DBCHAR_LENGTH_SHORTENED_FROM_ON_RETRIEVE,
											new String[] { itemName,
													stringLength })); 
					newItem.setLength("16383"); //$NON-NLS-1$
				}
			}
		}

		return recognizedType;
	}
	
	private void handleNumericType(PredefinedDataType type, EGLSQLStructureItem newItem, String itemName) {
		int length = 0;
		int decimals = 0;
		String stringLength = "0";
		String stringDecimal = "0";

		newItem.setPrimitiveType(IEGLConstants.KEYWORD_NUM);
		
		EStructuralFeature fPrecision = type.eClass().getEStructuralFeature("precision");
		EStructuralFeature fScale = type.eClass().getEStructuralFeature("scale");
		stringLength =  ((Integer) type.eGet(fPrecision)).toString();
		stringDecimal = ((Integer) type.eGet(fScale)).toString();
		
		try {
			length = Integer.parseInt(stringLength);
			if ( length == 0 ) {
				// In Oracle, a NUMBER column with no precision can store 38-digit floating-point values, so use float.
				newItem.setPrimitiveType(IEGLConstants.KEYWORD_FLOAT);
				return;
			}
		} catch (NumberFormatException e) {
			// The string does not represent a number, so assume it is zero
			newItem.getMessages().add(
							getErrorMessage(
									EGLMessage.EGLMESSAGE_INFO_INVALID_LENGTH_SET_TO_ZERO_ON_RETRIEVE,
									new String[] { stringLength, itemName })); 
			stringLength = "0"; //$NON-NLS-1$
		}

		try {
			decimals = Integer.parseInt(stringDecimal);
		} catch (NumberFormatException e) {
			// The string does not represent a number, so assume it is zero
			newItem.getMessages().add(
							getErrorMessage(
									EGLMessage.EGLMESSAGE_INFO_INVALID_DECIMALS_SET_TO_ZERO_ON_RETRIEVE,
									new String[] { stringDecimal, itemName })); 
			stringDecimal = "0"; //$NON-NLS-1$
		}

		if (length > 32) {
			newItem.getMessages().add(
							getInfoMessage(
									EGLMessage.EGLMESSAGE_INFO_DECIMAL_LENGTH_SHORTENED_FROM_ON_RETRIEVE,
									new String[] { itemName, stringLength })); 
			newItem.setLength("32"); //$NON-NLS-1$
		} else {
			newItem.setLength(stringLength);
		}

		if (decimals < 0) {
			// Oracle supports NUMBER(x,-y).
			decimals = -decimals;
			stringDecimal = Integer.toString( decimals );
		}
		
		if (decimals > 32) {
			newItem.getMessages().add(
							getInfoMessage(
									EGLMessage.EGLMESSAGE_INFO_DECIMAL_DECIMALS_SHORTENED_FROM_ON_RETRIEVE,
									new String[] { itemName, stringDecimal })); 
			newItem.setDecimals("32"); //$NON-NLS-1$
		} else if (decimals > 0) {
			newItem.setDecimals(stringDecimal);
		}
	}

	private void handleDecimalType(PredefinedDataType type, EGLSQLStructureItem newItem, String itemName) {
		int length = 0;
		int decimals = 0;
		String stringLength = "0";
		String stringDecimal = "0";
		
		EStructuralFeature fPrecision = type.eClass().getEStructuralFeature("precision");
		EStructuralFeature fScale = type.eClass().getEStructuralFeature("scale");
		stringLength =  ((Integer) type.eGet(fPrecision)).toString();
		stringDecimal = ((Integer) type.eGet(fScale)).toString();
		
		if (((PredefinedDataType) type).getName().equalsIgnoreCase(IEGLConstants.KEYWORD_MONEY)) {
			newItem.setPrimitiveType(IEGLConstants.KEYWORD_MONEY);
		} else {
			newItem.setPrimitiveType(IEGLConstants.KEYWORD_DECIMAL);
		}

		try {
			length = Integer.parseInt(stringLength);
			if (newItem.getPrimitiveType().equals(IEGLConstants.KEYWORD_MONEY) && length == 0) 
				return;
		} catch (NumberFormatException e) {
			// The string does not represent a number, so assume it is zero
			newItem.getMessages().add(
							getErrorMessage(
									EGLMessage.EGLMESSAGE_INFO_INVALID_LENGTH_SET_TO_ZERO_ON_RETRIEVE,
									new String[] { stringLength, itemName })); 
			stringLength = "0"; //$NON-NLS-1$
		}

		try {
			decimals = Integer.parseInt(stringDecimal);
		} catch (NumberFormatException e) {
			// The string does not represent a number, so assume it is zero
			newItem.getMessages().add(
							getErrorMessage(
									EGLMessage.EGLMESSAGE_INFO_INVALID_DECIMALS_SET_TO_ZERO_ON_RETRIEVE,
									new String[] { stringDecimal, itemName }));
			stringDecimal = "0"; //$NON-NLS-1$
		}

		if (length > 32) {
			newItem.getMessages().add(
							getInfoMessage(
									EGLMessage.EGLMESSAGE_INFO_DECIMAL_LENGTH_SHORTENED_FROM_ON_RETRIEVE,
									new String[] { itemName, stringLength }));
			newItem.setLength("32"); //$NON-NLS-1$
		} else {
			newItem.setLength(stringLength);
		}
		if (decimals > 32) {
			newItem.getMessages().add(
							getInfoMessage(
									EGLMessage.EGLMESSAGE_INFO_DECIMAL_DECIMALS_SHORTENED_FROM_ON_RETRIEVE,
									new String[] { itemName, stringDecimal }));
			newItem.setDecimals("32"); //$NON-NLS-1$
		} else if (decimals > 0) {
			newItem.setDecimals(stringDecimal);
		}
	}

	private void handleBinaryType(PredefinedDataType type, EGLSQLStructureItem newItem, String itemName) {
		int length = 0;
		String stringLength = "0"; //$NON-NLS-1$
		EStructuralFeature feature = type.eClass().getEStructuralFeature("length");
		stringLength =  ((Integer) type.eGet(feature)).toString();
		try {
			length = Integer.parseInt(stringLength);
			if (length > 65534) {
				newItem.getMessages().add(
								getInfoMessage(
										EGLMessage.EGLMESSAGE_INFO_HEX_LENGTH_SHORTENED_FROM_ON_RETRIEVE,
										new String[] { itemName, stringLength }));
				length = 65534;
				stringLength = String.valueOf(length);
			}
		} catch (NumberFormatException e) {
			// The string does not represent a number, so assume it is zero
			newItem.getMessages().add(
							getErrorMessage(
									EGLMessage.EGLMESSAGE_INFO_INVALID_LENGTH_SET_TO_ZERO_ON_RETRIEVE,
									new String[] { stringLength, itemName }));
		}

		newItem.setPrimitiveType(IEGLConstants.KEYWORD_HEX);
		newItem.setLength(stringLength);
	}

	private boolean isKey(String name, EList keys) {
		if (keys != null) {
			Iterator keyIterator = keys.iterator();
			Column thisColumn;
			while (keyIterator.hasNext()) {
				thisColumn = (Column) keyIterator.next();
				if (name != null && name.equalsIgnoreCase(thisColumn.getName()))
					return true;
			}
		}
		return false;
	}

/*	private void closeConnection(IConnectionProfile connection) {
		if (connection != null) {
			EGLSQLUtility.shutdownConnection(connection);
		}
	}*/

	/*private ConnectionInfo doConnect() {
		String messageText = null;
		ConnectionInfo connection = null;

		try {
			buildSchemaAndTableLists();
			IStatus status = EGLRDBConnectionUtility.connectWithPromptIfNeeded(connectionProfile, true);
			if (status.getCode() == IStatus.OK && connectionProfile.getConnectionState() == IConnectionProfile.CONNECTED_STATE) {
				IManagedConnection managedConnection = connectionProfile.getManagedConnection(ConnectionUtil.CONNECTION_TYPE);
				if (managedConnection != null) {
					connection = (ConnectionInfo) managedConnection.getConnection().getRawConnection();
				}
			} else {
				messageText = EGLMessage.createEGLValidationErrorMessage(
						EGLMessage.EGLMESSAGE_ERROR_CONNECT_ERROR_ON_RETRIEVE, null, status.getMessage()).getBuiltMessage();
			}
		} catch (Exception e) {
			String errorString = e.getMessage();
			messageText = EGLMessage.createEGLValidationErrorMessage(
					EGLMessage.EGLMESSAGE_ERROR_CONNECT_ERROR_ON_RETRIEVE, null, errorString).getBuiltMessage();
		}
		
		if (messageText != null) {
			// Put error message in SQL error viewer and in popup dialog.
			addMessageToResults(messageText);
			retrieveResults.setRetrieveFailed(true);
		}
		
		return connection;
	}*/
	
	private EGLMessage getErrorMessage(String messageID, String[] inserts) {
		return EGLMessage.createEGLValidationErrorMessage(messageID, null,
				inserts);
	}

	private void addInfoMessage(String messageID, String[] inserts) {
		if (retrieveResults != null) {
			retrieveResults.getMessages().add(getInfoMessage(messageID, inserts).getBuiltMessage());
		}
	}
	
	private EGLMessage getInfoMessage(String messageID, String[] inserts) {
		return EGLMessage.createEGLValidationInformationalMessage(
				messageID, null, inserts);
	}

	private void addMessageToResults(String messageText) {
		if (retrieveResults != null) {
			retrieveResults.getMessages().add(messageText);
		}
	}

	/*private void buildSchemaAndTableLists() throws InvocationTargetException {
		String tableName = null;
		String schemaName = null;
		parsedMetaData = new EGLSQLMeta[sqlTables.length];
		// Delimited schema and table names are names with special characters
		// including periods and double quotes. Double quotes within the name must 
		// be delimited with a double quote. Delimited names must be
		// double quoted.
		for (int i = 0; i < sqlTables.length; i++) {
			tableName = sqlTables[i][0];
			if (tableName != null) {
				parsedMetaData[i] = parseTableName(tableName);
				if (parsedMetaData[i].getSchemaName() == null) {
					if (schemaName == null) {
						// If the secondary authentication ID is set in the SQL
						// preferences, add this ID to the schemas list.
						// Otherwise, add the user ID from the SQL preferences.
						String secondaryID = EGLSQLUtility.getSecondaryID(connectionProfile).trim();
						if (secondaryID.length() > 0) {
							schemaName = secondaryID;
						} else {
							String userName = EGLSQLUtility.getSQLUserId(connectionProfile);
							if (userName != null && userName.trim().length() > 0) {
								schemaName = userName;
							} else {
								schemaName = SQLConstants.SYSTEMID;
							}
						}
					}
					parsedMetaData[i].setSchemaName(schemaName);
				}
			}
		}
	}*/

/*	private EGLSQLMeta parseTableName(String name) {
		// This method parses name into one of the following formats ...
		String databaseName = null;
		String schemaName = null;
		String tableName = null;
		StringBuffer buf = new StringBuffer(name.length());
		int nameIndex = 0;
		int dotCtr = 0;
		boolean invalidName = false;
		EGLSQLMeta sqlMeta = new EGLSQLMeta();

		while (nameIndex < name.length() && !invalidName) {
			switch (name.charAt(nameIndex)) {
			case '.': {
				if (dotCtr == 0) {
					dotCtr = 1;
					if (buf.length() == 0) {
						// Invalid string - leading dot. Treat the whole
						// name as the table name.
						sqlMeta.setTableName(name);
						return sqlMeta;
					}
					schemaName = buf.substring(0, buf.length());
					buf.delete(0, buf.length());
				} else if (dotCtr == 1) {
					dotCtr = 2;
					if (buf.length() == 0) {
						// Invalid string - double dot. Treat the whole name
						// as the table name.
						sqlMeta.setTableName(name);
						return sqlMeta;
					}
					if (schemaName != null) {
						databaseName = schemaName;
					}
					schemaName = buf.substring(0, buf.length());
					buf.delete(0, buf.length());
				} else {
					// Invalid string - third dot. Treat the whole name as
					// the table name.
					sqlMeta.setTableName(name);
					return sqlMeta;
				}
				break;
			}
			case '"': {
				buf.append(name.charAt(nameIndex));
				nameIndex++;
				while (nameIndex < name.length()) {
					if (name.charAt(nameIndex) == '"') {
						if (nameIndex + 1 < name.length() && name.charAt(nameIndex + 1) == '"') {
							buf.append(name.substring(nameIndex,nameIndex + 2));
							nameIndex = nameIndex + 2;
						} else {
							break;
						}
					} else {
						buf.append(name.charAt(nameIndex));
						nameIndex++;
					}
				}
				if (nameIndex >= name.length() || name.charAt(nameIndex) != '"') {
					// Invalid string - no end quote. Treat the whole name as the table name.
					sqlMeta.setTableName(name);
					return sqlMeta;
				} else {
					// copy closing quote
					buf.append(name.charAt(nameIndex));
				}
				break;
			}
			default: {
				// copy next char in string to working buf
				buf.append(name.charAt(nameIndex));
				break;
			}
			}
			nameIndex++; // next char in string
		}
		if (buf.length() > 0) {
			tableName = buf.substring(0, buf.length());
		} else {
			tableName = name;
		}
		if (schemaName != null) {
			sqlMeta.setSchemaName(schemaName);
		}
		if (databaseName != null) {
			sqlMeta.setDatabaseName(databaseName);
		}
		sqlMeta.setTableName(tableName);
		return sqlMeta;
	}*/
	
	/**
	 * Converts the name to the case control option specified in EGL SQL
	 * retrieve preferences.
	 */
	private String changeItemNameBasedOnControlOptionsInSQLRetrievePreferences(String name) {
		if (name == null) {
			return ""; //$NON-NLS-1$
		}
		String newName = convertCaseOfNameBasedOnPreferences(name);
		newName = handleUnderscoresInNameBasedOnPreferences(newName);

		return newName;
	}

	/**
	 * Converts the character type to the option specified in EGL SQL
	 * retrieve preferences.
	 */
	private String changeCharacterTypeBasedOnPreferences() {
		return IEGLConstants.KEYWORD_STRING;
		
		/*if (isCharType())
			return IEGLConstants.KEYWORD_CHAR;
		else if (isMBCharType())
			return IEGLConstants.KEYWORD_MBCHAR;
		else if (isCharacterUnicodeType())
			return IEGLConstants.KEYWORD_UNICODE;
		else if (isCharacterLimitedStringType())
			return SQLConstants.LIMITED_STRING;
		else
			return IEGLConstants.KEYWORD_STRING;*/
	}
	
	/**
	 * Converts the national character type to the option specified in EGL SQL
	 * retrieve preferences.
	 */
	private String changeNationalCharacterTypeBasedOnPreferences() {
		if (isNationalCharacterUnicodeType())
			return IEGLConstants.KEYWORD_UNICODE;
		else if (isNationalCharacterStringType())
			return IEGLConstants.KEYWORD_STRING;
		else if (isNationalCharacterLimitedStringType())
			return SQLConstants.LIMITED_STRING;
		else 
			return IEGLConstants.KEYWORD_DBCHAR;
	}

	/**
	 * Converts the name to the case control option specified in EGL SQL
	 * retrieve preferences.
	 */
	private String convertCaseOfNameBasedOnPreferences(String name) {
		if (name == null)
			return ""; //$NON-NLS-1$

		if (isLowercaseItemName())
			return name.toLowerCase();
		else if (isLowercaseItemNameAndUppercaseCharacterAfterUnderscore())
			return lowercaseNameAndUppercaseNextCharacterAfterUnderscore(name);

		// Name is not changed.
		return name;
	}

	/**
	 * Handles the underscores in the name based on the underscore option
	 * specified in EGL SQL retrieve preferences.
	 */
	private String handleUnderscoresInNameBasedOnPreferences(String name) {
		if (name == null)
			return ""; //$NON-NLS-1$

		if (isRemoveUnderscoresInName())
			return removeUnderscoresFromName(name);
		
		// Name is not changed.
		return name;
	}

	/**
	 * Return true if the EGL type char option is selected in SQL retrieve
	 * preferences.
	 */
	private boolean isCharType() {
		return isCharType;
	}

	/**
	 * Return true if the EGL type mbCar option is selected in SQL retrieve
	 * preferences.
	 */
	private boolean isMBCharType() {
		return isMBCharType;
	}

	/**
	 * Return true if the character unicode option is selected in SQL retrieve
	 * preferences.
	 */
	private boolean isCharacterUnicodeType() {
		return isCharacterUnicode;
	}
	
	/**
	 * Return true if the national character unicode option is selected in SQL retrieve
	 * preferences.
	 */
	private boolean isNationalCharacterUnicodeType() {
		return isNationalCharacterUnicode;
	}

	/**
	 * Return true if the character limited-length string option is selected in
	 * SQL retrieve preferences.
	 */
	private boolean isCharacterLimitedStringType() {
		return isCharacterLimitedString;
	}
	
	/**
	 * Return true if the national character limited-length string option is selected in
	 * SQL retrieve preferences.
	 */
	private boolean isNationalCharacterLimitedStringType() {
		return isNationalCharacterLimitedString;
	}
	
	/**
	 * Return true if the national character string option is selected in
	 * SQL retrieve preferences.
	 */
	private boolean isNationalCharacterStringType() {
		return isNationalCharacterString;
	}

	/**
	 * Return true if the lowercase option is selected in SQL retrieve
	 * preferences.
	 */
	private boolean isLowercaseItemName() {
		return isLowercaseItemName;
	}

	/**
	 * Return true if the lowercase and uppercase next character after
	 * underscore option is selected in SQL retrieve preferences.
	 */
	private boolean isLowercaseItemNameAndUppercaseCharacterAfterUnderscore() {
		return isLowercaseItemNameAndUppercaseCharacterAfterUnderscore;
	}

	/**
	 * Return true if the remove underscore option is selected in SQL retrieve
	 * preferences.
	 */
	private boolean isRemoveUnderscoresInName() {
		return isRemoveUnderscoresInName;
	}

	
	/**
	 * Return name lowercased and the character after the underscores
	 * uppercased.
	 */
	private String lowercaseNameAndUppercaseNextCharacterAfterUnderscore(String name) {
		String itemName = name.toLowerCase();
		StringBuffer buffer = new StringBuffer(itemName);
		// Uppercase each charater after an underscore and remove all underscores.
		for (int i = 0; i < buffer.length(); i++) {
			if (i + 1 != buffer.length() && buffer.charAt(i) == UNDERSCORE) {
				buffer.setCharAt(i + 1, Character.toUpperCase(buffer.charAt(i + 1)));
			}
		}
		return buffer.toString();
	}

	/**
	 * Return name with underscores removed.
	 */
	private String removeUnderscoresFromName(String name) {
		StringBuffer buffer = new StringBuffer(name);
		// Remove all underscores.
		for (int i = 0; i < buffer.length(); i++) {
			if (buffer.charAt(i) == UNDERSCORE) {
				buffer.deleteCharAt(i);
			}
		}
		return buffer.toString();
	}


	private String addEscapeCharactersWhenNecessaryInName(String name) {
		StringBuffer buffer = new StringBuffer();
		char currentChar;
		// The backslash escape character is added before any double quotes
		// or backslashes found in the name.
		for (int i = 0; i < name.length(); i++) {
			currentChar = name.charAt(i);
			if (currentChar == '"' || currentChar == '\\') {
				buffer.append('\\');
			}
			buffer.append(currentChar);
		}
		return buffer.toString();
	}

	private void setMaxPrimitiveTypeInfoLengths(EGLSQLStructureItem item) {
		int length = item.getName().length() + item.getPrimitiveType().length();
		Set sqlClauseArray = EGLSQLKeywordHandler.getSQLClauseKeywordNamesToLowerCaseAsSet();
		if (sqlClauseArray.contains(item.getName().toLowerCase())) {
			length = length + 4;
		}
		if (item.getLength() != null) {
			length = length + item.getLength().length();
		}
		if (item.getDecimals() != null) {
			length = length + item.getDecimals().length();
			retrieveResults.setHasColumnsDefinedWithDecimals(true);
		}
		if (length > retrieveResults.getMaxPrimitiveTypeInfoLength()) {
			retrieveResults.setMaxPrimitiveTypeInfoLength(length);
		}
	}

	private void setPreferences() {
		setCharControlOptionsBasedOnSQLPreferences();
		setNationalCharOptionsBasedOnSQLPreferences();
		setItemNameControlOptionsBasedOnSQLPreferences();
		typeForDateTimeTypes = getEGLBasePlugin().getTypeForDateTimeTypesOption();
		addSqlDataCode = getEGLBasePlugin().getAddSqlDataCodeForDateTimeTypesOption();
	}
	
	private SQLPlugin getEGLBasePlugin() {
		return SQLPlugin.getPlugin();
	}
}
