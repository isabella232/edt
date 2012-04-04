/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.bindings;



/**
 * @author svihovec
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class WorkspaceImportContainer extends AbstractImportContainer {

	public static final String DEFAULT_FOLDER_NAME = ""; //$NON-NLS-1$

	/**
	 * Constructor for WorkspaceImportContainer.
	 * @param importStatement
	 */
	public WorkspaceImportContainer(String importStatement) {
		super(importStatement);
	}

	/**
	 * @see com.ibm.etools.edt.common.internal.bindings.AbstractImportContainer#getDefaultFolderName()
	 */
	public String getDefaultFolderName() {
		return DEFAULT_FOLDER_NAME;
	}

}
