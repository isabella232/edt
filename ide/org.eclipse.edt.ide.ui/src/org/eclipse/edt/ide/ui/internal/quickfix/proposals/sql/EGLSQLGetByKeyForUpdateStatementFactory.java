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
package org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql;


import java.util.List;

import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementUtility;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;

public class EGLSQLGetByKeyForUpdateStatementFactory extends EGLSQLDeclareStatementFactory {
	String forUpdateOfClause = null;
	String[] columnsExcludingReadOnlyAndKeys = null;

	public EGLSQLGetByKeyForUpdateStatementFactory(
		Member recordBinding,
		String ioObjectName,
		List userDefinedIntoItemNames,
		String[][] keyItemAndColumnNames,
		boolean isDynamicArrayRecord) {
		super(recordBinding, ioObjectName, userDefinedIntoItemNames, keyItemAndColumnNames, isDynamicArrayRecord);
	}

	public EGLSQLGetByKeyForUpdateStatementFactory(Member recordBinding, String ioObjectName) {
		super(recordBinding, ioObjectName, null, null, false);
	}

	@Override
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
			String columnName;
			boolean isReadOnly;
			for (Field itemBinding : structureItemBindings) {
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

	@Override
	public String getIOType() {
		return SQLConstants.GET_FORUPDATE_IO_TYPE.toUpperCase();
	}

	@Override
	protected boolean isIoObjectValid() {

		boolean isValid = super.isIoObjectValid();

		// SQL record must NOT be defined with more than one table (join) and must be defined with at least one
		// column that is not a key and not read only.
		if (!validateSQLRecordNotJoinAndContainsOneNonReadOnlyOrNonKeyColumn()) {
			isValid = false;
		}

		return isValid;
	}

	private boolean isKey(Field itemBinding) {
		if (useRecordKeys) {
			return isRecordKeyItem(itemBinding);
		} else {
			return isUsingKeyColumn(itemBinding);
		}
	}

	private boolean isUsingKeyColumn(Field itemBinding) {
		return SQLIOStatementUtility.isUsingKeyColumn(itemBinding, keyItemAndColumnNames, getSQLRecordTypeBinding());
	}

	@Override
	protected boolean containsOnlyKeyOrReadOnlyColumns() {
		if (!useRecordKeys) {
			return SQLIOStatementUtility.containsOnlyKeyOrReadOnlyColumns(getSQLRecordTypeBinding(), keyItemAndColumnNames);
		}
		return false;
	}
}
