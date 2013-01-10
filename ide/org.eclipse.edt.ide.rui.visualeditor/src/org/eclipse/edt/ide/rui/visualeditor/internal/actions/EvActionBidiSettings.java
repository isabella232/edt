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

import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvBidiSettingsDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiFormat;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class EvActionBidiSettings extends Action {
	EvBidiSettingsDialog dialog = null;

	public EvActionBidiSettings(BidiFormat bidiFormat) {
		super( "" );
		Shell shell = Display.getCurrent().getActiveShell();
		dialog = new  EvBidiSettingsDialog(shell, bidiFormat);
	}

	/**
	 * Method declared in Action.
	 */
	public void run() {
		dialog.open();
	}
	
	public BidiFormat getBidiFormat(){
		return dialog.getBidiFormat();
	}
}
