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

public class RUIWidgetConfiguration extends EGLPartConfiguration {
	/** The name of the widget */
	private String widgetName;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		setDefaultAttributes();
	}

	/**
	 * @return
	 */
	public String getWidgetName() {
		return widgetName;
	}

	/**
	 * @param string
	 */
	public void setWidgetName(String string) {
		widgetName = string;
	}

	private void setDefaultAttributes() {
		widgetName = ""; //$NON-NLS-1$
	}

}
