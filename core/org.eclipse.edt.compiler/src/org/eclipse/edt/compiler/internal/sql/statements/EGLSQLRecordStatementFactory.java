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


public class EGLSQLRecordStatementFactory extends EGLSQLDeclareStatementFactory {


	public EGLSQLRecordStatementFactory(IDataBinding recordBinding, String ioObjectName, ICompilerOptions compilerOptions) {
		super(recordBinding, ioObjectName, null, null, false, compilerOptions);
	}

	public String getIOType() {
		return ""; //$NON-NLS-1$
	}

	protected Problem getCouldNotBuildDefaultMessage() {
		return new Problem(0, 0, IMarker.SEVERITY_ERROR,
			IProblemRequestor.COULD_NOT_BUILD_DEFAULT_STATEMENT_FOR_SQL_RECORD,
			new String[] {ioObjectName});
	}

	protected Problem getContainsNoItemsMessage() {
		return new Problem(0, 0, IMarker.SEVERITY_ERROR,
			IProblemRequestor.SQL_RECORD_CONTAINS_NO_STRUCTURE_ITEMS,
			new String[0]);
	}

	public String buildDefaultSQLStatement() {
		// Call super to build the default select, into, from and where clauses.
		super.buildDefaultSQLStatement();

		return sqlStatement;
	}

}
