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
package org.eclipse.edt.ide.ui.internal.externaltype;

import org.eclipse.edt.ide.ui.wizards.EGLPartConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class ExternalTypeConfiguration extends EGLPartConfiguration {
	/** ExternalType Types */
	public final static int BASIC_EXTERNALTYPE = 0;
	
	private String externalTypeName;
	private int externalTypeType;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setDefaultAttributes();
	}
	
	public String getExternalTypeName() {
		if (externalTypeName == null || externalTypeName.trim().length() == 0) {
			return getFileName();
		}
		return externalTypeName;
	}

	public int getExternalTypeType() {
		return externalTypeType;
	}

	public void setExternalTypeType(int i) {
		externalTypeType = i;
	}
	
	public void setExternalTypeName(String externalType) {
		externalTypeName = externalType;
	}
	
	private void setDefaultAttributes() {
		externalTypeName = ""; //$NON-NLS-1$
		externalTypeType = BASIC_EXTERNALTYPE;
	}
}
