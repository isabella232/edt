/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class EGLDDConfiguration extends EGLFileConfiguration {

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setFPackage(""); //$NON-NLS-1$
	}
	
	public String getFileExtension()
	{
		return EGLDDRootHelper.EXTENSION_EGLDD;
	}
	
}
