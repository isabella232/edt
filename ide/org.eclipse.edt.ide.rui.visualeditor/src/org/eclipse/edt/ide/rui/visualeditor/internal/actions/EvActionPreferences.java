/*******************************************************************************
 * Copyright © 1994, 2012 IBM Corporation and others.
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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class EvActionPreferences extends Action {

	protected static String	PREFERENCE_PAGE_ID	= "org.eclipse.edt.ide.rui.visualeditor.preferences";
	
	public EvActionPreferences() {
		super( "" );
	}

	/**
	 * Method declared in Action.
	 */
	public void run() {
		Shell shell = Display.getCurrent().getActiveShell();
		PreferencesUtil.createPreferenceDialogOn( shell, PREFERENCE_PAGE_ID, null, null ).open();
	}
}
