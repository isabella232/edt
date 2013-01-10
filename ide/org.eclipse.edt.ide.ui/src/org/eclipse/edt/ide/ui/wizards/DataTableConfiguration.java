/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class DataTableConfiguration extends EGLPartConfiguration {
	
	/** Table Types */
	public final static int NONE = 0;
	public final static int MESSAGE = 1;
	public final static int MATCH_VALID = 2;
	public final static int MATCH_INVALID = 3;
	public final static int RANGE_CHECK = 4;
	
	/** The name of the table */
	private String tableName;
	
	/** The type of program */
	private int tableType;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		setDefaultAttributes();
	}

	/**
	 * @return
	 */
	public int getDataTableType() {
		return tableType;
	}

	/**
	 * @param i
	 */
	public void setDataTableType(int i) {
		tableType = i;
	}

	/**
	 * @return
	 */
	public String getDataTableName() {
		return tableName;
	}

	/**
	 * @param string
	 */
	public void setDataTableName(String string) {
		tableName = string;
	}
	
	private void setDefaultAttributes() {
		tableName = ""; //$NON-NLS-1$
	}

}
