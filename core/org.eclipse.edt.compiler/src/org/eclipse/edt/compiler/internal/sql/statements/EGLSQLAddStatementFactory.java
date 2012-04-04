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


public class EGLSQLAddStatementFactory extends EGLSQLStatementFactory {
	String insertIntoClause = null;
	String columnsClause = null;
	String valuesClause = null;

	public EGLSQLAddStatementFactory(IDataBinding recordBinding, String ioObjectName, ICompilerOptions compilerOptions) {
		super(recordBinding, ioObjectName, compilerOptions);
	}

	public String buildDefaultSQLStatement() {

		if (!setupSQLInfo()) {
			return null;
		}

		// The insertInto, columns, and values are required clauses.
		insertIntoClause = EGLSQLClauseFactory.createDefaultInsertIntoClause(tableNames);
		if (insertIntoClause != null) {
			sqlStatement = insertIntoClause;
		} else {
			sqlStatement = ""; //$NON-NLS-1$
		}

		columnsClause = EGLSQLClauseFactory.createDefaultColumnsClause(columnNames);
		if (columnsClause != null) {
			sqlStatement = sqlStatement + columnsClause;
		}

		valuesClause = EGLSQLClauseFactory.createDefaultValuesClause(itemNames, ioObjectName);
		if (valuesClause != null) {
			sqlStatement = sqlStatement + valuesClause;
		}

		return sqlStatement;
	}

	public String getColumnsClause() {
		return columnsClause;
	}

	public String getInsertIntoClause() {
		return insertIntoClause;
	}

	public String getIOType() {
		return SQLConstants.ADD_IO_TYPE.toUpperCase();
	}

	public String getValuesClause() {
		return valuesClause;
	}

	protected boolean isIoObjectValid() {

		boolean isValid = super.isIoObjectValid();

		isValid = hasPersistentItems();
		if (!validateSQLRecordNotJoinAndContainsReadWriteColumns()) {
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

		// For add statements, need to create lists of items and columns that are not readonly. In this case,
		// keys aren't treated as readonly. Add statements don't do anything special for keys so don't need
		// to set up key info.
		setupForSQLInsertStatement();
	}

	public String getSQLStatementType() {
		return SQLConstants.INSERT.toUpperCase();
	}
	
	protected Problem getContainsNoItemsMessage() {
		return new Problem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.IO_OBJECT_CONTAINS_NO_STRUCTURE_ITEMS, new String[] {ioObjectName});
	}
}
