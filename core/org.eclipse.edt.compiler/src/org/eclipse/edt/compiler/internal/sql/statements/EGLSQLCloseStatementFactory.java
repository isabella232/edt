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

import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sql.SQLConstants;






public class EGLSQLCloseStatementFactory extends EGLSQLStatementFactory {
	
	public EGLSQLCloseStatementFactory(ICompilerOptions compilerOptions) {
		super(compilerOptions);
	}
	
	public String buildDefaultSQLStatement() {
		return SQLConstants.CLOSE_STATEMENT;
	}

	public String getIOType() {
		return SQLConstants.CLOSE_IO_TYPE.toUpperCase();
	}

	public String getSQLStatementType() {
		return SQLConstants.CLOSE_IO_TYPE.toUpperCase();	
	}

}
