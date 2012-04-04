/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

public class TemplatePreferencePage extends org.eclipse.ui.texteditor.templates.TemplatePreferencePage {
	public TemplatePreferencePage() {
		setPreferenceStore(EDTUIPlugin.getDefault().getPreferenceStore());
		setTemplateStore(EDTUIPlugin.getDefault().getTemplateStore());
		setContextTypeRegistry(EDTUIPlugin.getDefault().getTemplateContextRegistry());
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IUIHelpConstants.EGL_EDIT_TEMPLATE_CONTEXT);
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok = super.performOk();
		EDTUIPlugin.getDefault().saveUIPluginPreferences();
		return ok;
	}

	protected Control createContents(Composite ancestor) {
		Control control = super.createContents(ancestor);
		return control;
	}

}
