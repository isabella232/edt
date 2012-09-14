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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public abstract class EGLSQLDeclareStatementFactory extends EGLSQLStatementFactory {
	String selectClause = null;
	String intoClause = null;
	String fromClause = null;
	String whereClause = null;
	String eglUsingClause = null;
	List userDefinedIntoItemNames;
	boolean addIntoClauseToStatement = true;
	boolean buildIntoClause = true;
	boolean buildIntoClauseForEditor = false;
	protected boolean isDynamicArrayRecord = false;

	public EGLSQLDeclareStatementFactory(
		Member recordBinding,
		String ioObjectName,
		List userDefinedIntoItemNames,
		String[][] keyItemAndColumnNames,
		boolean isDynamicArrayRecord) {

		super(recordBinding, ioObjectName);
		this.userDefinedIntoItemNames = userDefinedIntoItemNames;
		this.keyItemAndColumnNames = keyItemAndColumnNames;
		this.isDynamicArrayRecord = isDynamicArrayRecord;
	}

	public String buildDefaultSQLStatement() {

		if (!setupSQLInfo()) {
			return null;
		}

		// The select, into, from clause are required clauses.
		selectClause = EGLSQLClauseFactory.createDefaultSelectClause(columnNames);
		if (selectClause != null) {
			sqlStatement = selectClause;
		} else {
			sqlStatement = ""; //$NON-NLS-1$
		}

		if (buildIntoClause) {
			// If INTO item list is defined on the I/O statement, build the INTO clause based 
			// on this list.  Otherwise, build the INTO clause based on the structure items in
			// the SQL record.
			if (userDefinedIntoItemNames != null) {
				intoClause = EGLSQLClauseFactory.createIntoClauseFromItemNameList(userDefinedIntoItemNames);
			} else {
				intoClause = EGLSQLClauseFactory.createDefaultIntoClause(itemNames, ioObjectName, buildIntoClauseForEditor);
			}
			if (intoClause != null && addIntoClauseToStatement) {
				sqlStatement = sqlStatement + intoClause;
			}
		}

		fromClause = EGLSQLClauseFactory.createDefaultFromClause(tableNames, tableLabels);
		if (fromClause != null) {
			sqlStatement = sqlStatement + fromClause;
		}

		// The where clause is an optional clause that is only built if there are default selection
		// conditions and/or key columns.
		if(!(this.sqlRecordData.getType() instanceof ArrayType)) {
		  whereClause =
			  EGLSQLClauseFactory.createDefaultWhereClause(
				  getDefaultSelectConditions(),
				  keyItemAndColumnNames,
				  getIOType(),
				  ioObjectName,
				  isDynamicArrayRecord,
				  useRecordKeys);
		  if (whereClause != null) {
			  sqlStatement = sqlStatement + whereClause;
			  eglUsingClause = EGLSQLClauseFactory.createEglUsingClauseForGet(keyItemAndColumnNames, ioObjectName);
		  }
		}

		return sqlStatement;
	}

	public String getFromClause() {
		return fromClause;
	}

	public String getIntoClause() {
		return intoClause;
	}

	public String getSelectClause() {
		return selectClause;
	}

	public String getWhereClause() {
		return whereClause;
	}
	
	@Override
	public  String getEglUsingClause() {
	    return eglUsingClause;
	}

	protected boolean isIoObjectValid() {

		boolean isValid = super.isIoObjectValid();

		// SQL Record must at have at least one structure item that is persistent.
		if (numSQLDataItems == 0) {
			errorMessages.add(getContainsNoItemsMessage());
			isValid = false;
		}

		return isValid;
	}

	protected Problem getContainsNoItemsMessage() {
		return new Problem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.IO_OBJECT_CONTAINS_NO_STRUCTURE_ITEMS, new String[] {ioObjectName});
	}

	protected void setupItemColumnAndKeyInfo() {

		itemNames = new String[numSQLDataItems];
		columnNames = new String[numSQLDataItems];

		if (keyItemAndColumnNames == null) {
			if (getKeyItems() != null) {
				keyItemAndColumnNames = new String[getKeyItems().size()][2];
			} else {
				keyItemAndColumnNames = new String[0][0];
			}
		} else {
			useRecordKeys = false;
		}

		int numKeys = 0;

		if (structureItemBindings != null) {
			Field itemBinding;
			for (int i = 0; i < numSQLDataItems; i++) {
				itemBinding = structureItemBindings.get(i);
				itemNames[i] = itemBinding.getName();
				Type typeBinding = itemBinding.getType();
				if(TypeUtils.Type_STRING.equals(typeBinding)) {
					columnNames[i] = IEGLConstants.SQLKEYWORD_RTRIM + SQLConstants.LPAREN +
							getColumnName(itemBinding) + SQLConstants.RPAREN;
				} else {
					columnNames[i] = getColumnName(itemBinding);
				}
				
				if (useRecordKeys && isKeyItem(itemNames[i])) {
					keyItemAndColumnNames[numKeys][0] = itemNames[i];
					keyItemAndColumnNames[numKeys][1] = columnNames[i];
					numKeys++;
				}
			}
		}

		if (useRecordKeys && numKeys == 0 && getKeyItems().size() > 0) {
			keyItemAndColumnNames = new String[0][0];
		}
	}

	private boolean isKeyItem(String itemName) {

		if (useRecordKeys) {
			if (getKeyItems() != null) {
				for (int i = 0; i < keyItems.size(); i++) {
					if (itemName.equalsIgnoreCase(keyItems.get(i).getName())) {
						return true;
					}
				}
			}
		} else if (keyItemAndColumnNames != null) {
			for (int i = 0; i < keyItemAndColumnNames.length; i++) {
				if (itemName.equalsIgnoreCase(keyItemAndColumnNames[i][0])) {
					return true;
				}
			}
		}

		return false;
	}

	public String getSQLStatementType() {
		return SQLConstants.SELECT.toUpperCase();
	}

	public boolean isAddIntoClauseToStatement() {
		return addIntoClauseToStatement;
	}

	public void setAddIntoClauseToStatement(boolean b) {
		addIntoClauseToStatement = b;
	}

	public boolean isBuildIntoClause() {
		return buildIntoClause;
	}

	public boolean isBuildIntoClauseForEditor() {
		return buildIntoClauseForEditor;
	}

	public void setBuildIntoClause(boolean b) {
		buildIntoClause = b;
	}

	public void setBuildIntoClauseForEditor(boolean b) {
		buildIntoClauseForEditor = b;
	}

	public String[][] getKeyItemAndColumnNames() {
		return keyItemAndColumnNames;
	}

}
