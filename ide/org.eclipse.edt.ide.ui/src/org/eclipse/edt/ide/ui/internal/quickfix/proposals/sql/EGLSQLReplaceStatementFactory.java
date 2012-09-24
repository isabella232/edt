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


import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.mof.egl.Member;


public class EGLSQLReplaceStatementFactory extends EGLSQLStatementFactory {
	String updateClause = null;
	String setClause = null;
	String whereClause = null;

	boolean noCursor = false;

	public EGLSQLReplaceStatementFactory(Member recordBinding, String ioObjectName, String[][] keyItemAndColumnNames, boolean noCursor) {
		super(recordBinding, ioObjectName);
		this.keyItemAndColumnNames = keyItemAndColumnNames;
		this.noCursor = noCursor;
	}

	public String buildDefaultSQLStatement() {

		if (!setupSQLInfo()) {
			return null;
		}

		// The update and set clauses are required.
		updateClause = EGLSQLClauseFactory.createDefaultUpdateClause(tableNames, tableLabels);
		if (updateClause != null) {
			sqlStatement = updateClause;
		} else {
			sqlStatement = ""; //$NON-NLS-1$
		}
		setClause = EGLSQLClauseFactory.createDefaultSetClause(columnNames, itemNames, ioObjectName);
		if (setClause != null) {
			sqlStatement = sqlStatement.concat(setClause);
		}
		
		// The where clause is an optional clause that is only built if there are default selection
		// conditions and/or key columns and the NOCURSOR modifier is used
		//Currently EDT does not support NOCURSOR modifier
		whereClause =
				EGLSQLClauseFactory.createDefaultWhereClause(
					getDefaultSelectConditions(),
					keyItemAndColumnNames,
					getIOType(),
					ioObjectName,
					false,
					useRecordKeys);
			if (whereClause != null) {
				sqlStatement = sqlStatement + whereClause;
			}


		return sqlStatement;
	}
	public String getIOType() {
		return SQLConstants.REPLACE_IO_TYPE.toUpperCase();
	}

	public String getSetClause() {
		return setClause;
	}

	public String getUpdateClause() {
		return updateClause;
	}

	protected boolean isIoObjectValid() {

		boolean isValid = super.isIoObjectValid();
		
		isValid = hasPersistentItems();
		// SQL record must NOT be defined with more than one table (join) and must be defined with at least one
		// column that is not a key and not read only.
		if (!validateSQLRecordNotJoinAndContainsOneNonReadOnlyOrNonKeyColumn()) {
			isValid = false;
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
		// For replace statements, need to create lists of items and columns that are not readonly or keys.
		setupForSQLUpdateStatement();
	}

	public String getSQLStatementType() {
		return SQLConstants.UPDATE.toUpperCase();
	}
	
	protected Problem getContainsNoItemsMessage() {
		return new Problem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.IO_OBJECT_CONTAINS_NO_STRUCTURE_ITEMS, new String[] {ioObjectName});
	}
	
}
