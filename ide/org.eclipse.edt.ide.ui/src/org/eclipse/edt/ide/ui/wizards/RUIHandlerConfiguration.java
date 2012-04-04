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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class RUIHandlerConfiguration extends EGLPartConfiguration {
	
	
	/** The name of the handler */
	private String handlerName;
	
	/** The title of the handler */
	private String handlerTitle;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		setDefaultAttributes();
	}

	/**
	 * @return
	 */
	public String gethandlerName() {
		return handlerName;
	}

	/**
	 * @param string
	 */
	public void setHandlerName(String string) {
		handlerName = string;
	}
	
	private void setDefaultAttributes() {
		handlerName = ""; //$NON-NLS-1$
		handlerTitle = ""; //$NON-NLS-1$
	}
	
	public String getHandlerTitle() {
		return handlerTitle;
	}
	
	public void setHandlerTitle(String string){
		handlerTitle = string;
	}

}
