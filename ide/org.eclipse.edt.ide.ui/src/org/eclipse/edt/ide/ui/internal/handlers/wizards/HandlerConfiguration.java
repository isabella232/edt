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
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import org.eclipse.edt.ide.ui.wizards.EGLPartConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;

public class HandlerConfiguration extends EGLPartConfiguration {
	/** Handler Types */
	public final static int BASIC_HANDLER = 0;
	public final static int HANDLER_HANDLER = 1;
	public final static int WIDGET_HANDLER = 2;

	private String handlerName;
	private int handlerType;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setDefaultAttributes();
	}
	
	public String getHandlerName() {
		if (handlerName == null || handlerName.trim().length() == 0) {
			return getFileName();
		}
		return handlerName;
	}

	public int getHandlerType() {
		return handlerType;
	}

	public void setHandlerType(int i) {
		handlerType = i;
	}
	
	public void setHandlerName(String string) {
		handlerName = string;
	}
	
	private void setDefaultAttributes() {
		handlerName = ""; //$NON-NLS-1$
		handlerType = BASIC_HANDLER;
	}
}
