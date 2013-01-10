/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementUtility;
import org.eclipse.edt.mof.egl.Member;


public class EGLSQLDeleteStatementFactory extends EGLSQLStatementFactory {
	boolean appendWhereCurrentOfClause;
	String whereClause = null;
	boolean noCursor = false;

	public EGLSQLDeleteStatementFactory(Member recordBinding, String ioObjectName, boolean appendWhereCurrentOfClause) {
		super(recordBinding, ioObjectName);
		this.appendWhereCurrentOfClause = appendWhereCurrentOfClause;
	}

	public EGLSQLDeleteStatementFactory(Member recordBinding, String ioObjectName,  String[][] keyItemAndColumnNames, boolean noCursor) {
		super(recordBinding, ioObjectName);
		this.keyItemAndColumnNames = keyItemAndColumnNames;
		this.noCursor = noCursor;
}

	public String buildDefaultSQLStatement() {

		boolean isValidIoObject = true;

		if (!SQLIOStatementUtility.isEntityRecord(getSQLRecordTypeBinding()) && !SQLIOStatementUtility.isBasicRecord(getSQLRecordTypeBinding())) {
			isValidIoObject = false;
		} else if (!isIoObjectValid()) {
			isValidIoObject = false;
		}

		if (!isValidIoObject) {
			errorMessages.add(
				0,
				new Problem(0, 0, IMarker.SEVERITY_ERROR,
					IProblemRequestor.COULD_NOT_BUILD_DEFAULT_STATEMENT,					
					new String[] { getSQLStatementType(), getIOType(), ioObjectName }));
			return null;
		}

		if (!setupSQLInfo()) {
			return null;
		}
		
		setupTableInfo();

		String deleteStatement = null;
		if (tableNames != null) {
			deleteStatement = EGLSQLClauseFactory.createDefaultDeleteFromClause(tableNames, tableLabels);
			if (deleteStatement != null && appendWhereCurrentOfClause) {
				deleteStatement = deleteStatement.concat(getWhereCurrentOfClause());
			}
		}
		
		// The where clause is an optional clause that is only built if there are default selection
		// conditions and/or key columns and the NOCURSOR modifier is used
		//EDT currently does not support NOCURSOR modifier
		whereClause =
				EGLSQLClauseFactory.createDefaultWhereClause(
					getDefaultSelectConditions(),
					keyItemAndColumnNames,
					getIOType(),
					ioObjectName,
					false,
					useRecordKeys);
			if (whereClause != null) {
				if (deleteStatement == null) {
					deleteStatement = whereClause;
				}
				else {
					deleteStatement = deleteStatement + whereClause;
				}
			}


		return deleteStatement;
	}

	public String getIOType() {
		return SQLConstants.DELETE_IO_TYPE.toUpperCase();
	}

	protected boolean isIoObjectValid() {

		boolean isValid = super.isIoObjectValid();

		// SQL record must not be defined with more than one table (join).
		if (!validateSQLRecordNotJoin()) {
			isValid = false;
		}

		return isValid;
	}

	public String getSQLStatementType() {
		return SQLConstants.DELETE_IO_TYPE.toUpperCase();
	}

	protected void setupItemColumnAndKeyInfo() {
		// For replace statements, need to create lists of items and columns that are not readonly or keys.
		setupForSQLUpdateStatement();
	}

}
