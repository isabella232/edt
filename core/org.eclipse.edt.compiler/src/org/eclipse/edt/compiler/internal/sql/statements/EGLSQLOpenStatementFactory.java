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


public class EGLSQLOpenStatementFactory extends EGLSQLGetByKeyStatementFactory {

	public EGLSQLOpenStatementFactory(
		IDataBinding recordBinding,
		String ioObjectName,
		List userDefinedIntoItemNames,
		String[][] keyItemAndColumnNames,
		ICompilerOptions compilerOptions) {
		super(recordBinding, ioObjectName, userDefinedIntoItemNames, keyItemAndColumnNames, false, compilerOptions);
	}

	public EGLSQLOpenStatementFactory(IDataBinding recordBinding, String ioObjectName, ICompilerOptions compilerOptions) {
		super(recordBinding, ioObjectName, null, null, false, compilerOptions);
	}

	public String buildDefaultSQLStatement() {
		// Call super to build the default select, into, from and where clauses.
		super.buildDefaultSQLStatement();

		if (sqlStatement != null) {
			// The order by clause is an optional clause that is only built if there are key columns.
			//orderByClause = EGLSQLClauseFactory.createDefaultOrderByClause(keyItemAndColumnNames);
			if (orderByClause != null) {
				sqlStatement = sqlStatement + orderByClause;
			}
		}

		return sqlStatement;
	}

	public String getIOType() {
		return SQLConstants.OPEN_IO_TYPE.toUpperCase();
	}
}
