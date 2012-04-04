/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;

public class BindingBaseConfiguration extends EGLPartConfiguration {

	protected String fBindingName=""; //$NON-NLS-1$
	protected String fEGLServiceOrInterface=""; //$NON-NLS-1$
	protected IProject fProj;
	
	public String getBindingName() {
		return fBindingName;
	}
	public void setBindingName(String bindingName) {
		fBindingName = bindingName;
	}
	public String getEGLServiceOrInterface() {
		return fEGLServiceOrInterface;
	}
	public void setEGLServiceOrInterface(String serviceOrInterface) {
		fEGLServiceOrInterface = serviceOrInterface;
	}
	public IProject getProject() {
		return fProj;
	}
	
	
}
