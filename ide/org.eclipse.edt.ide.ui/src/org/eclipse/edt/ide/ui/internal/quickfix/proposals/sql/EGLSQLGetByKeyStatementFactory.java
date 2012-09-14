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
import org.eclipse.edt.mof.egl.Member;



public class EGLSQLGetByKeyStatementFactory extends EGLSQLDeclareStatementFactory {
	String orderByClause = null;

	public EGLSQLGetByKeyStatementFactory(
		Member recordBinding,
		String ioObjectName,
		List userDefinedIntoItemNames,
		String[][] keyItemAndColumnNames,
		boolean isDynamicArrayRecord) {
		super(recordBinding, ioObjectName, userDefinedIntoItemNames, keyItemAndColumnNames, isDynamicArrayRecord);
	}

	public EGLSQLGetByKeyStatementFactory(Member recordBinding, String ioObjectName) {
		super(recordBinding, ioObjectName, null, null, false);
	}

	public String getIOType() {
		return SQLConstants.GET_IO_TYPE.toUpperCase();
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public String buildDefaultSQLStatement() {
		// Call super to build the default select, into, from and where clauses.
		super.buildDefaultSQLStatement();

		if (isDynamicArrayRecord && sqlStatement != null) {
			// The order by clause is an optional clause that is only built if there are key columns.
			orderByClause = EGLSQLClauseFactory.createDefaultOrderByClause(keyItemAndColumnNames);
			if (orderByClause != null) {
				sqlStatement = sqlStatement + orderByClause;
			}
		}

		return sqlStatement;
	}
}
