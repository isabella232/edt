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
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;



public class EGLSQLGetByPositionStatementFactory extends EGLSQLStatementFactory {

	String intoClause = null;
	boolean buildIntoClause = false;

	public EGLSQLGetByPositionStatementFactory(IDataBinding recordBinding, String ioObjectName, boolean buildIntoClause, ICompilerOptions compilerOptions) {
		super(recordBinding, ioObjectName, compilerOptions);
		this.buildIntoClause = buildIntoClause;
	}

	public String buildDefaultSQLStatement() {
		if (buildIntoClause) {
			// The INTO clause is only built for this statement in the editor.  We 
			// never build the INTO clause for this statement for generation.
			if (!setupSQLInfo()) {
				return null;
			}
			intoClause = EGLSQLClauseFactory.createDefaultIntoClause(itemNames, ioObjectName, true);

		}
		return SQLConstants.GET_BY_POSITION_STATEMENT;
	}

	public String getIOType() {
		return SQLConstants.GET_BY_POSITION_IO_TYPE.toUpperCase();
	}

	public String getSQLStatementType() {
		return "FETCH"; //$NON-NLS-1$
	}

	public String getIntoClause() {
		return intoClause;
	}

	public void setIntoClause(String string) {
		intoClause = string;
	}

	protected void setupItemColumnAndKeyInfo() {
		// Only need list of items for the INTO clause.
		// Column and key info not needed so don't set up.
		itemNames = new String[numSQLDataItems];

		if (structureItemBindings != null) {
			IDataBinding itemBinding;
			for (int i = 0; i < numSQLDataItems; i++) {
				itemBinding = structureItemBindings[i];
				itemNames[i] = itemBinding.getName();
			}
		}
	}

	protected void setupTableInfo() {
		// Table info not needed.
	}

}
