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


import java.util.List;

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;


public class EGLSQLGetByKeyForUpdateStatementFactory extends EGLSQLDeclareStatementFactory {
	String forUpdateOfClause = null;
	String[] columnsExcludingReadOnlyAndKeys = null;

	public EGLSQLGetByKeyForUpdateStatementFactory(
		IDataBinding recordBinding,
		String ioObjectName,
		List userDefinedIntoItemNames,
		String[][] keyItemAndColumnNames,
		boolean isDynamicArrayRecord,
		ICompilerOptions compilerOptions) {
		super(recordBinding, ioObjectName, userDefinedIntoItemNames, keyItemAndColumnNames, isDynamicArrayRecord, compilerOptions);
	}

	public EGLSQLGetByKeyForUpdateStatementFactory(IDataBinding recordBinding, String ioObjectName, ICompilerOptions compilerOptions) {
		super(recordBinding, ioObjectName, null, null, false, compilerOptions);
	}

	public String buildDefaultSQLStatement() {

		// Call super to build the default select, into, from and where clauses.
		super.buildDefaultSQLStatement();

		if (sqlStatement != null) {
			// Add the for update of clause which consists of a list of columns that excludes readonly and key
			// columns.
			columnsExcludingReadOnlyAndKeys = getColumnsExcludingReadOnlyAndKeys();
			if (columnsExcludingReadOnlyAndKeys != null) {
				forUpdateOfClause = EGLSQLClauseFactory.createDefaultForUpdateOfClause(columnsExcludingReadOnlyAndKeys);
				if (forUpdateOfClause != null) {
					sqlStatement = sqlStatement + forUpdateOfClause;
				}
			}
		}

		return sqlStatement;
	}

	private String[] getColumnsExcludingReadOnlyAndKeys() {

		String[] columns = new String[numSQLDataItems];
		int numNonReadOnlyAndKeys = 0;

		if (structureItemBindings != null) {
			IDataBinding itemBinding;
			String columnName;
			boolean isReadOnly;
			for (int i = 0; i < numSQLDataItems; i++) {
				itemBinding = structureItemBindings[i];
				columnName = getColumnName(itemBinding);
				isReadOnly = getIsReadOnly(itemBinding);
				if (!(isReadOnly || isKey(itemBinding))) {
					columns[numNonReadOnlyAndKeys] = columnName;
					numNonReadOnlyAndKeys++;
				}
			}
		}

		if (numNonReadOnlyAndKeys != 0) {
			String[] readWriteColumns = new String[numNonReadOnlyAndKeys];
			for (int i = 0; i < numNonReadOnlyAndKeys; i++) {
				readWriteColumns[i] = columns[i];
			}
			return readWriteColumns;
		}

		return null;
	}

	public String getForUpdateOfClause() {
		return forUpdateOfClause;
	}

	public String getIOType() {
		return SQLConstants.GET_FORUPDATE_IO_TYPE.toUpperCase();
	}

	protected boolean isIoObjectValid() {

		boolean isValid = super.isIoObjectValid();

		// SQL record must NOT be defined with more than one table (join) and must be defined with at least one
		// column that is not a key and not read only.
		if (!validateSQLRecordNotJoinAndContainsOneNonReadOnlyOrNonKeyColumn()) {
			isValid = false;
		}

		return isValid;
	}

	private boolean isKey(IDataBinding itemBinding) {
		if (useRecordKeys) {
			return isRecordKeyItem(itemBinding);
		} else {
			return isUsingKeyColumn(itemBinding);
		}
	}

	private boolean isUsingKeyColumn(IDataBinding itemBinding) {
		return SQLUtility.isUsingKeyColumn(itemBinding, keyItemAndColumnNames, sqlRecordData);
	}

	protected boolean containsOnlyKeyOrReadOnlyColumns() {
		if (!useRecordKeys) {
			return SQLUtility.containsOnlyKeyOrReadOnlyColumns(sqlRecordData, keyItemAndColumnNames);
		}
		return false;
	}
}
