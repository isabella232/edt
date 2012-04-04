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


import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;


public class EGLSQLExecuteStatementFactory extends EGLSQLStatementFactory {
	boolean createUpdate;
	boolean createDelete;
	boolean createInsert;

	public EGLSQLExecuteStatementFactory(
		IDataBinding recordData,
		String ioObjectName,
		boolean createUpdate,
		boolean createDelete,
		boolean createInsert,
		ICompilerOptions compilerOptions) {
		super(recordData, ioObjectName, compilerOptions);
		this.createUpdate = createUpdate;
		this.createDelete = createDelete;
		this.createInsert = createInsert;
	}

	public String buildDefaultSQLStatement() {

		if (createUpdate || createDelete || createInsert) {
			// If model is delete or update, and the record is not a SQL record, we cannot build the default statement.  
			if (!setupSQLInfo()) {
				return null;
			}

			if (createDelete) {
				sqlStatement = EGLSQLClauseFactory.createDefaultDeleteFromClause(tableNames, tableLabels);
			} else if (createUpdate) {
				sqlStatement = EGLSQLClauseFactory.createDefaultUpdateClause(tableNames, tableLabels);
				if (sqlStatement != null) {
					String setClause = EGLSQLClauseFactory.createDefaultSetClause(columnNames, itemNames, ioObjectName);
					if (setClause != null) {
						sqlStatement = sqlStatement + setClause;
					}
				}
			} else {
				String clause;
				clause = EGLSQLClauseFactory.createDefaultInsertIntoClause(tableNames);
				if (clause != null) {
					sqlStatement = clause;
				} else {
					sqlStatement = ""; //$NON-NLS-1$
				}

				clause = EGLSQLClauseFactory.createDefaultColumnsClause(columnNames);
				if (clause != null) {
					sqlStatement = sqlStatement + clause;
				}

				clause = EGLSQLClauseFactory.createDefaultValuesClause(itemNames, ioObjectName);
				if (clause != null) {
					sqlStatement = sqlStatement + clause;
				}
			}

			// The where clause is an optional clause that is only built if there are default selection
			// conditions and/or key columns.
			if ((createDelete || createUpdate) && sqlStatement != null) {
				String whereClause =
					EGLSQLClauseFactory.createDefaultWhereClause(
						getDefaultSelectConditions(),
						keyItemAndColumnNames,
						SQLConstants.GET_FORUPDATE_IO_TYPE,
						ioObjectName,
						false,
						true);
				if (whereClause != null) {
					sqlStatement = sqlStatement + whereClause;
				}
			}
		}

		return sqlStatement;
	}
	protected boolean isIoObjectSQLRecord() {

		boolean isValidIoObject = SQLUtility.isEntityRecord(sqlRecordData);

		if (createUpdate || createDelete || createInsert) {
			if (ioObjectName == null || ioObjectName.trim().length() == 0) {
				errorMessages.add(new Problem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.IO_OBJECT_REQUIRED_FOR_SQLEXEC, new String[0]));
				isValidIoObject = false;
			}
		}

		return isValidIoObject;
	}
	public String getIOType() {
		return SQLConstants.EXECUTE_IO_TYPE.toUpperCase();
	}

	protected boolean isIoObjectValid() {

		boolean isValid = true;

		if (createUpdate || createDelete || createInsert) {

			isValid = super.isIoObjectValid();

			if (createDelete) {
				// SQL record must not be defined with more than one table (join).
				if (!validateSQLRecordNotJoin()) {
					isValid = false;
				}
			} else if (createUpdate) {
				isValid = hasPersistentItems();
				// SQL record must NOT be defined with more than one table (join)
				if (!validateSQLRecordNotJoinAndContainsOneNonReadOnlyOrNonKeyColumn()) {
					isValid = false;
				}
			} else if (createInsert) {
				isValid = hasPersistentItems();
				// SQL record must not be defined with more than one table (join) 
				//  and must be defined with at least one column that is not read only.
				if (!validateSQLRecordNotJoinAndContainsReadWriteColumns()) {
					isValid = false;
				}
			}

		}

		return isValid;
	}

	protected boolean hasPersistentItems() {

		boolean isValid = true;

		// SQL Record must at have at least one structure item that is persistent.
		if (numSQLDataItems == 0) {
			errorMessages.add(getContainsNoItemsMessage());
			isValid = false;
		}

		return isValid;
	}
	
	protected void setupItemColumnAndKeyInfo() {
		if (createUpdate || createDelete) {
			setupForSQLUpdateStatement();
		} else if (createInsert) {
			setupForSQLInsertStatement();
		}
	}

	public String getSQLStatementType() {
		if (createUpdate) {
			return SQLConstants.UPDATE.toUpperCase();
		}
		if (createDelete) {
			return SQLConstants.DELETE_IO_TYPE.toUpperCase();
		}
		if (createInsert) {
			return SQLConstants.INSERT.toUpperCase();
		}
		return ""; //$NON-NLS-1$
	}
	
	protected Problem getContainsNoItemsMessage() {
		return new Problem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.IO_OBJECT_CONTAINS_NO_STRUCTURE_ITEMS, new String[] {ioObjectName});
	}
	
}
