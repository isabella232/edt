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
package org.eclipse.edt.ide.ui.internal.record;

import org.eclipse.edt.ide.ui.wizards.EGLPartConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class RecordConfiguration extends EGLPartConfiguration {
	/** Record Types */
	public final static int BASIC_RECORD = 0;
	public final static int SQL_RECORD = 1;

	/** The name of the record */
	private String recordName;

	/** The type of record */
	private int recordType;
	
	/**
	 * Imports required by the record.
	 */
	private String imports;

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setDefaultAttributes();
	}

	/**
	 * @return
	 */
	public int getRecordType() {
		return recordType;
	}

	/**
	 * @param i
	 */
	public void setRecordType(int i) {
		recordType = i;
	}

	/**
	 * @return
	 */
	public String getRecordName() {
		if (recordName == null || recordName.trim().length() == 0) {
			return getFileName();
		}
		return recordName;
	}

	/**
	 * @param string
	 */
	public void setRecordName(String string) {
		recordName = string;
	}

	private void setDefaultAttributes() {
		recordType = 0;
		recordName = ""; //$NON-NLS-1$
	}
	
	public void setImports(String imports) {
		this.imports = imports;
	}
	
	public String getImports() {
		return imports;
	}
}
