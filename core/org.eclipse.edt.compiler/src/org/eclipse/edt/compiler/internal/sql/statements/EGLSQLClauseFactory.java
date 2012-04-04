/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.sql.statements;


import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;


public class EGLSQLClauseFactory {

	public static String addItemNamesToClause(String clause, String[] structureItemNames, String ioObjectName) {

		if (structureItemNames == null) {
			return null;
		}

		int numItemNames = structureItemNames.length;

		String temp;
		int lineLength = 0;
		for (int i = 0; i < numItemNames; i++) {

			temp = ioObjectName + SQLConstants.QUALIFICATION_DELIMITER + structureItemNames[i];

			if (i + 1 < numItemNames) {
				temp = temp + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + temp.length();
			if ((lineLength > SQLConstants.FORMAT_WIDTH) && i > 0) {
				clause = clause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = temp.length();
			}

			clause = clause + temp;
		}

		return clause;
	}

	public static String addHostVariableNamesToClause(String clause, String[] structureItemNames, String ioObjectName) {

		if (structureItemNames == null) {
			return null;
		}

		int numItemNames = structureItemNames.length;

		String temp;
		int lineLength = 0;
		for (int i = 0; i < numItemNames; i++) {

			temp = SQLConstants.PARAMETER_MARKER;

			if (i + 1 < numItemNames) {
				temp = temp + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + temp.length();
			if ((lineLength > SQLConstants.FORMAT_WIDTH) && i > 0) {
				clause = clause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = temp.length();
			}

			clause = clause + temp;
		}

		return clause;
	}

	public static String addNamesToClause(String clause, String[] names) {

		// Should have at least one name.  Otherwise, this would have been flagged as an error and should
		// never get here but just in case return if names is null.
		if (names == null) {
			return null;
		}

		int numNames = names.length;

		String temp;
		int lineLength = 0;
		for (int i = 0; i < numNames; i++) {

			temp = names[i];
			temp = removeEscapeCharactersFromName(temp);
			if (i + 1 < numNames) {
				temp = temp + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + temp.length();
			if ((lineLength > SQLConstants.FORMAT_WIDTH) && i > 0) {
				clause = clause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = temp.length();
			}

			clause = clause + temp;
		}

		return clause;
	}

	public static String addNamesToClause(String clause, String[] names, String[] labels) {

		// Should have at least one name.  Otherwise, this would have been flagged as an error and should
		// never get here but just in case return if names is null.
		if (names == null) {
			return null;
		}

		int numNames = names.length;

		String temp;
		int lineLength = 0;
		for (int i = 0; i < numNames; i++) {

			temp = names[i];
			temp = removeEscapeCharactersFromName(temp);
			
			String label = labels[i];
			if (label != null && numNames > 1) {
				label = removeEscapeCharactersFromName(label);
				temp = temp + SQLConstants.SPACE + label;
			}
			
			if (i + 1 < numNames) {
				temp = temp + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + temp.length();
			if ((lineLength > SQLConstants.FORMAT_WIDTH) && i > 0) {
				clause = clause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = temp.length();
			}

			clause = clause + temp;
		}

		return clause;
	}

	
	public static String createDefaultColumnsClause(String[] columnNames) {

		// Should not get here if columnNames is null but just in case.
		if (columnNames == null) {
			return null;
		}

		String columnsClause = SQLConstants.TAB + SQLConstants.LPAREN;

		// Should have at least one column.  Otherwise, this would have been flagged as an error and should
		// never get here. 
		columnsClause = addNamesToClause(columnsClause, columnNames);

		return columnsClause + SQLConstants.RPAREN + SQLConstants.CRLF;
	}

	public static String createDefaultDeleteFromClause(String[] tableNames, String[] labels) {

		if (tableNames == null) {
			return null;
		}

		String deleteFromClause = SQLConstants.DELETE + SQLConstants.SPACE + SQLConstants.FROM + SQLConstants.SPACE;

		// Should have only one table name.  No table name or more than one is an error.  Should not get here
		// if there are errors.
		deleteFromClause = addNamesToClause(deleteFromClause, tableNames, labels);

		return deleteFromClause + SQLConstants.CRLF;
	}

	public static String createDefaultForUpdateOfClause(String[] columnNames) {

		if (columnNames == null) {
			return null;
		}

		String forUpdateOfClause = SQLConstants.FOR_UPDATE_OF + SQLConstants.CRLF + SQLConstants.TAB;

		// Should have at least one column.  Otherwise, this would have been flagged as an error and should
		// never get here.  This list of columns excludes readonly and key columns.
		forUpdateOfClause = addNamesToClause(forUpdateOfClause, columnNames);

		return forUpdateOfClause + SQLConstants.CRLF;
	}

	public static String createDefaultFromClause(String[] tableNames, String[] tableLabels) {

		// Table names are required but table labels are now optional.
		if (tableNames == null) {
			return null;
		}

		String fromClause = SQLConstants.FROM + SQLConstants.SPACE;
		int numTables = tableNames.length;

		String tableSpec;
		String tableLabel;
		int lineLength = fromClause.length();

		for (int i = 0; i < numTables; i++) {

			tableSpec = tableNames[i];
			tableSpec = removeEscapeCharactersFromName(tableSpec);
			tableLabel = tableLabels[i];
			tableLabel = removeEscapeCharactersFromName(tableLabel);
			if (tableLabel != null && numTables > 1) {
				tableSpec = tableSpec + SQLConstants.SPACE + tableLabel;
			}

			if (i + 1 < numTables) {
				tableSpec = tableSpec + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + tableSpec.length();
			if (lineLength > SQLConstants.FORMAT_WIDTH) {
				fromClause = fromClause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = 0;
			}

			fromClause = fromClause + tableSpec;
		}

		return fromClause + SQLConstants.CRLF;
	}

	public static String createDefaultInsertIntoClause(String[] tableNames) {

		if (tableNames == null) {
			return null;
		}

		String insertIntoClause = SQLConstants.INSERT_INTO + SQLConstants.SPACE;

		// Should have only one table name.  No table name or more than one is an error.  Should not get here
		// if there are errors.
		insertIntoClause = addNamesToClause(insertIntoClause, tableNames);

		return insertIntoClause + SQLConstants.CRLF;
	}

	public static String createDefaultIntoClause(String[] structureItemNames, String ioObjectName, boolean buildIntoForEditor) {

		if (structureItemNames == null) {
			return null;
		}

		String intoClause;

		// At least one item name is required.  Otherwise, this is an error and should not get here.
		if (buildIntoForEditor) {
			intoClause = IEGLConstants.KEYWORD_INTO + " "; //$NON-NLS-1$
			intoClause = addItemNamesToClause(intoClause, structureItemNames, ioObjectName);
		} else {
			intoClause = SQLConstants.INTO + SQLConstants.CRLF + SQLConstants.TAB;
			intoClause = addHostVariableNamesToClause(intoClause, structureItemNames, ioObjectName);
			return intoClause + SQLConstants.CRLF;
		}

		return intoClause;
	}

	public static String createDefaultOrderByClause(String[][] keyItemAndColumnNames) {

		if (keyItemAndColumnNames == null || keyItemAndColumnNames.length == 0) {
			return null;
		}

		String orderByClause = SQLConstants.ORDER_BY + SQLConstants.CRLF + SQLConstants.TAB;

		String temp;
		int lineLength = 0;

		for (int i = 0; i < keyItemAndColumnNames.length; i++) {

			temp = keyItemAndColumnNames[i][1];
			temp = removeEscapeCharactersFromName(temp);
			if (i + 1 < keyItemAndColumnNames.length) {
				temp = temp + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + temp.length();
			if ((lineLength > SQLConstants.FORMAT_WIDTH) && i > 0) {
				orderByClause = orderByClause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = temp.length();
			}

			orderByClause = orderByClause + temp;
		}

		orderByClause = orderByClause + SQLConstants.SPACE + SQLConstants.ASC;

		return orderByClause + SQLConstants.CRLF;
	}

	public static String createDefaultSelectClause(String[] columnNames) {

		if (columnNames == null) {
			return null;
		}

		String selectClause = SQLConstants.SELECT + SQLConstants.CRLF + SQLConstants.TAB;

		// Should have at least one column.  Otherwise, this would have been flagged as an error and should
		// never get here. 
		selectClause = addNamesToClause(selectClause, columnNames);

		return selectClause + SQLConstants.CRLF;
	}

	public static String createDefaultSetClause(String[] columnNames, String[] structureItemNames, String ioObjectName) {

		if (columnNames == null || structureItemNames == null) {
			return null;
		}

		String setClause = SQLConstants.SET + SQLConstants.CRLF;
		int numColumnNames = columnNames.length;
		String columnName;

		for (int i = 0; i < numColumnNames; i++) {
			columnName = removeEscapeCharactersFromName(columnNames[i]);
			setClause =
				setClause
					+ SQLConstants.TAB
					+ columnName
					+ SQLConstants.EQUALS
					+ SQLConstants.PARAMETER_MARKER;

			if (i + 1 < numColumnNames) {
				setClause = setClause + SQLConstants.COMMA + SQLConstants.CRLF;
			}
		}

		return setClause + SQLConstants.CRLF;
	}

	public static String createDefaultUpdateClause(String[] tableNames, String[] labels) {

		if (tableNames == null) {
			return null;
		}

		String updateClause = SQLConstants.UPDATE + SQLConstants.SPACE;

		// Should have only one table name.  No table name or more than one is an error.  Should not get here
		// if there are errors.
		updateClause = addNamesToClause(updateClause, tableNames, labels);

		return updateClause + SQLConstants.CRLF;
	}

	public static String createDefaultValuesClause(String[] structureItemNames, String ioObjectName) {

		if (structureItemNames == null) {
			return null;
		}

		String valuesClause = SQLConstants.VALUES + SQLConstants.CRLF + SQLConstants.TAB + SQLConstants.LPAREN;

		// At least one item name is required.  Otherwise, this is an error and should not get here.
		valuesClause = addHostVariableNamesToClause(valuesClause, structureItemNames, ioObjectName);

		return valuesClause + SQLConstants.RPAREN + SQLConstants.CRLF;
	}
	
	public static String createEglUsingClauseForGet(String[][] keyItemAndColumnNames, String ioObjectName) {
		if (keyItemAndColumnNames == null) {
			return null;
		}

		String usingClause = SQLConstants.USING + SQLConstants.SPACE;
		
		for (int i = 0; i < keyItemAndColumnNames.length; i++) {
			if(i > 0) {
				usingClause = usingClause + SQLConstants.COMMA;
			} 
			
			usingClause = usingClause + ioObjectName + SQLConstants.QUALIFICATION_DELIMITER + keyItemAndColumnNames[i][0];
		}
		
		return usingClause + SQLConstants.SPACE + SQLConstants.CRLF;
	}

	public static String createDefaultWhereClause(
		String defaultSelectConditions,
		String[][] keyItemAndColumnNames,
		String ioType,
		String ioObjectName,
		boolean isDynamicArrayRecord,
		boolean useRecordKeys) {

		String whereClause = SQLConstants.WHERE + SQLConstants.CRLF + SQLConstants.TAB;
		String whereString = null;

		if (ioType.equalsIgnoreCase(SQLConstants.GET_IO_TYPE) || ioType.equalsIgnoreCase(SQLConstants.GET_FORUPDATE_IO_TYPE) || ioType.equalsIgnoreCase(SQLConstants.REPLACE_IO_TYPE) || ioType.equalsIgnoreCase(SQLConstants.DELETE_IO_TYPE)) {
			whereString =
				createDefaultWhereForGetByKey(
					defaultSelectConditions,
					keyItemAndColumnNames,
					ioObjectName,
					isDynamicArrayRecord,
					useRecordKeys);
		} else {
			whereString = createDefaultWhereForOpen(defaultSelectConditions, keyItemAndColumnNames, ioObjectName, useRecordKeys);
		}

		if (whereString == null || whereString.trim().length() == 0) {
			return null;
		} else {
			whereClause = whereClause + whereString;
		}

		return whereClause + SQLConstants.CRLF;
	}

	private static String createDefaultWhereForGetByKey(
		String defaultSelectConditions,
		String[][] keyItemAndColumnNames,
		String ioObjectName,
		boolean isDynamicArrayRecord,
		boolean useRecordKeys) {

		String whereClause = null;
		if (defaultSelectConditions != null || keyItemAndColumnNames.length > 0) {
			whereClause = new String();
		}

		boolean recordKeysInvalid = false;
		if (keyItemAndColumnNames.length > 0 && isDynamicArrayRecord && useRecordKeys) {
			recordKeysInvalid = true;
		}

		// Put parens around the default select conditions if there are key columns.
		if (defaultSelectConditions != null) {
			if (keyItemAndColumnNames.length == 0 || recordKeysInvalid) {
				whereClause = whereClause + defaultSelectConditions;
			} else {
				whereClause = whereClause + SQLConstants.LPAREN + defaultSelectConditions + SQLConstants.RPAREN;
			}
		}

		if (isDynamicArrayRecord && !useRecordKeys) {
			// If the I/O object is an SQL record array and the usingKeys clause is specified, generate the WHERE clause with
			// the new EGL algorithm.
			whereClause = addKeyClausesToGetResultSet(whereClause, defaultSelectConditions, keyItemAndColumnNames);
		} else if (!recordKeysInvalid && defaultSelectConditions == null) {
			// If this is a single record with or without usingKeys, generate using the old VG algorithm.
			// If no usingKeys clause is specified for a dynamic array, the record keys are ignored completely for the WHERE clause.
			whereClause =
				addKeyClausesToGetSingleRow(whereClause, defaultSelectConditions, keyItemAndColumnNames, ioObjectName, useRecordKeys);
		}

		return whereClause;
	}

	private static String addKeyClausesToGetResultSet(
		String whereClause,
		String defaultSelectConditions,
		String[][] keyItemAndColumnNames) {

		int i, j, len;
		String columnName;

		for (i = 0; i < keyItemAndColumnNames.length; i++) {
			if (i == 0) {
				if (defaultSelectConditions != null) {
					whereClause =
						whereClause + SQLConstants.CRLF + SQLConstants.TAB + SQLConstants.TAB + SQLConstants.AND + SQLConstants.SPACE;
				}
				if (keyItemAndColumnNames.length > 1) {
					whereClause = whereClause + SQLConstants.LPAREN;
				}
			} else {
				whereClause = whereClause + SQLConstants.CRLF + SQLConstants.TAB + SQLConstants.TAB;
				if (defaultSelectConditions != null) {
					whereClause = whereClause + SQLConstants.TAB;
				}
				whereClause = whereClause + SQLConstants.OR + SQLConstants.SPACE;
			}

			len = keyItemAndColumnNames.length - i;

			if (keyItemAndColumnNames.length > 1) {
				whereClause = whereClause + SQLConstants.LPAREN;
			}

			for (j = 0; j < len; j++) {
				if (j != 0) {
					whereClause = whereClause + SQLConstants.SPACE + SQLConstants.AND + SQLConstants.SPACE;
				}

				columnName = removeEscapeCharactersFromName(keyItemAndColumnNames[j][1]);
				whereClause = whereClause + columnName;

				if ((i == 0) && (j == keyItemAndColumnNames.length - 1)) {
					whereClause = whereClause + SQLConstants.GREATER_THAN_OR_EQUAL;
				} else if (j == len - 1) {
					whereClause = whereClause + SQLConstants.GREATER_THAN;
				} else {
					whereClause = whereClause + SQLConstants.EQUALS;
				}

				whereClause = whereClause + SQLConstants.HOST_VARIABLE_INDICATOR + keyItemAndColumnNames[j][0];
			}

			if (keyItemAndColumnNames.length > 1) {
				whereClause = whereClause + SQLConstants.RPAREN;
			}
		}

		if (keyItemAndColumnNames.length > 1) {
			whereClause = whereClause + SQLConstants.RPAREN;
		}

		return whereClause;
	}

	private static String addKeyClausesToGetSingleRow(
		String whereClause,
		String defaultSelectConditions,
		String[][] keyItemAndColumnNames,
		String ioObjectName,
		boolean useRecordKeys) {

		String columnName;
		for (int i = 0; i < keyItemAndColumnNames.length; i++) {
			if ((i == 0 && defaultSelectConditions != null) || i > 0) {
				whereClause = whereClause + SQLConstants.CRLF + SQLConstants.TAB + SQLConstants.TAB + SQLConstants.AND + SQLConstants.SPACE;
			}

			columnName = removeEscapeCharactersFromName(keyItemAndColumnNames[i][1]);
			whereClause = whereClause + columnName + SQLConstants.EQUALS;

			if (useRecordKeys) {
				whereClause =
					whereClause
						+ SQLConstants.PARAMETER_MARKER;
			} else {
				whereClause = whereClause + SQLConstants.HOST_VARIABLE_INDICATOR + keyItemAndColumnNames[i][0];
			}
		}

		return whereClause;
	}

	private static String createDefaultWhereForOpen(
		String defaultSelectConditions,
		String[][] keyItemAndColumnNames,
		String ioObjectName,
		boolean useRecordKeys) {

		String whereClause = null;
		whereClause = new String();

		// Put parens around the default select conditions if there are key columns.
		if (defaultSelectConditions != null) {
			// VAGen allowed only one key when building key clauses for the open statement.  If more than one
			// key is specified, no key clauses are built.  The usingKeys is a new way to provide a list of keys
			// If the usingKeys clause is specified, multiple keys are supported when building the WHERE clause.
			if (keyItemAndColumnNames.length == 1 || (!useRecordKeys && keyItemAndColumnNames.length > 1)) {
				whereClause = whereClause + SQLConstants.LPAREN + defaultSelectConditions + SQLConstants.RPAREN;
			} else {
				whereClause = whereClause + defaultSelectConditions;
			}
		}

		if (useRecordKeys) {
			whereClause =
				addKeyClausesToGetResultSetBasedOnVAGenRules(
					whereClause,
					defaultSelectConditions,
					keyItemAndColumnNames,
					ioObjectName,
					useRecordKeys);
		} else {
			// usingKeys clause specified.  Generate key clauses using new algorithm.
			whereClause = addKeyClausesToGetResultSet(whereClause, defaultSelectConditions, keyItemAndColumnNames);
		}

		return whereClause;
	}

	private static String addKeyClausesToGetResultSetBasedOnVAGenRules(
		String whereClause,
		String defaultSelectConditions,
		String[][] keyItemAndColumnNames,
		String ioObjectName,
		boolean useRecordKeys) {

		if (keyItemAndColumnNames.length == 1) {
			if (defaultSelectConditions != null) {
				whereClause = whereClause + SQLConstants.CRLF + SQLConstants.TAB + SQLConstants.TAB + SQLConstants.AND + SQLConstants.SPACE;
			}

			String columnName = removeEscapeCharactersFromName(keyItemAndColumnNames[0][1]);
			whereClause = whereClause + columnName + SQLConstants.EQUALS + SQLConstants.SPACE + SQLConstants.PARAMETER_MARKER;
		}

		return whereClause;
	}

	public static String createIntoClauseFromItemNameList(List items) {

		if (items == null) {
			return null;
		}

		String intoClause = SQLConstants.INTO + SQLConstants.CRLF + SQLConstants.TAB;
		String itemName;

		String line;
		int lineLength = 0;
		for (int i = 0; i < items.size(); i++) {
			itemName = (String) items.get(i);
			line = SQLConstants.HOST_VARIABLE_INDICATOR + itemName;
			if (i + 1 < items.size()) {
				line = line + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + line.length();
			if ((lineLength > SQLConstants.FORMAT_WIDTH && i > 0)) {
				intoClause = intoClause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = line.length();
			}

			intoClause = intoClause + line;
		}

		return intoClause + SQLConstants.CRLF;
	}

	public static String createIntoClauseFromItemNameList(List items, String recordVariableName) {

		if (items == null) {
			return null;
		}

		String intoClause = SQLConstants.INTO + " "; //$NON-NLS-1$
		Iterator itemItr = items.iterator();
		String name;

		while (itemItr.hasNext()) {
			name = (String) itemItr.next();

			intoClause =
				intoClause + SQLConstants.HOST_VARIABLE_INDICATOR + recordVariableName + SQLConstants.QUALIFICATION_DELIMITER + name;

			if (itemItr.hasNext()) {
				intoClause = intoClause + SQLConstants.COMMA_AND_SPACE;
			}
		}

		return intoClause + SQLConstants.CRLF;
	}

	public static String createIntoClauseFromItemNameListForEditor(List items, String recordVariableName) {

		if (items == null) {
			return null;
		}

		String intoClause = IEGLConstants.KEYWORD_INTO + " "; //$NON-NLS-1$
		String name;

		String line;
		int lineLength = 0;
		for (int i = 0; i < items.size(); i++) {
			name = (String) items.get(i);

			line = recordVariableName + SQLConstants.QUALIFICATION_DELIMITER + name;

			if (i + 1 < items.size()) {
				line = line + SQLConstants.COMMA_AND_SPACE;
			}

			lineLength = lineLength + line.length();
			if ((lineLength > SQLConstants.FORMAT_WIDTH && i > 0)) {
				intoClause = intoClause + SQLConstants.CRLF + SQLConstants.TAB;
				lineLength = line.length();
			}

			intoClause = intoClause + line;
		}

		return intoClause;
	}

	private static String removeEscapeCharactersFromName(String name) {

		// Remove all backslash characters that are escape characters for double quotes in 
		// name.  Escape characters are always followed by a double quote.  Delimited names
		// begin and end with a double quote.  If the name does not begin with a double quote,
		// it is not a delimited name.  In this case, we can just return the name.
		if (name == null || name.length() == 0 || !(name.charAt(0) == '\"')) {
			return name;
		}

		StringBuffer newName = new StringBuffer(name.length());
		char currentChar;
		for (int i = 0; i < name.length(); i++) {
			currentChar = name.charAt(i);
			if (!(currentChar == '\\' && name.charAt(i + 1) == '\"' && i + 2 < name.length())) {
				// Current character is not a backslash followed by a double quote so 
				// the current character is not an escape character.
				newName.append(currentChar);
			}
		}

		return newName.toString();
	}

}
