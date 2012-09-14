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


public class EGLSQLOpenForUpdateStatementFactory extends EGLSQLGetByKeyForUpdateStatementFactory {

	public EGLSQLOpenForUpdateStatementFactory(
		Member recordBinding,
		String ioObjectName,
		List userDefinedIntoItemNames,
		String[][] keyItemAndColumnNames) {
		super(recordBinding, ioObjectName, userDefinedIntoItemNames, keyItemAndColumnNames, false);
	}

	public EGLSQLOpenForUpdateStatementFactory(Member recordBinding, String ioObjectName) {
		super(recordBinding, ioObjectName, null, null, false);
	}

	public String getIOType() {
		return SQLConstants.OPEN_FORUPDATE_IO_TYPE.toUpperCase();
	}

}
