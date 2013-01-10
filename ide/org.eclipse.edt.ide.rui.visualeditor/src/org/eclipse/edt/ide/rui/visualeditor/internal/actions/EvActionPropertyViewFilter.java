/*******************************************************************************
 * Copyright © 1994, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.actions;

import org.eclipse.edt.ide.rui.visualeditor.internal.properties.PropertySheetPage;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;


public class EvActionPropertyViewFilter extends Action {
	
	private int filterType;
	private PropertySheetPage propertyPage;

	public EvActionPropertyViewFilter( PropertySheetPage propertyPage, int filterType ) {
		super( "" );
		this.filterType = filterType;
		this.propertyPage = propertyPage;
	}

	/**
	 * Method declared in Action.
	 */
	public void run() {
		PropertySheetPage.filter_Type = filterType;
		propertyPage.refreshWidgetControl();
	}
}
