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
package org.eclipse.edt.ide.rui.visualeditor.internal.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiFormat;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;


/**
 * Preference page for Bidi preferences of EGL RichUI Visual Editor
 */
public class EvPreferencePageBidi extends PreferencePage implements IWorkbenchPreferencePage, IPropertyChangeListener {
	
	Group groupBidi;
	
	public EvPreferencePageBidi() {
		EDTCoreIDEPlugin.getPlugin().getPreferenceStore().addPropertyChangeListener(this);
		setDescription(Messages.NL_BIDI_Panel_Instructions);
		
	}
	

	/**
	 * Returns the help ID for this page.
	 */
	public String getHelpID(){
		return EvHelp.PREFERENCES_BIDI;
	}

	/**
	 * Declared in PreferencePage and overridden here.  Called when a person presses the Restore Defaults button.
	 */
	public void performDefaults() {
		BidiUtils.performDefaults();
	}

	/**
	 * Called when either the Apply or Ok buttons are pressed.
	 */
	public boolean performOk() {
		BidiUtils.performOk();
		return true;
	}

	protected Control createContents(Composite parent) {
		Composite control = new Composite(parent, 0);
		
		GridLayout gridLayout = new GridLayout();
		control.setLayout( gridLayout );
		
		groupBidi = new Group( control, SWT.NULL );
		
		BidiFormat bidiFormat = BidiUtils.isBidi() ? 
				BidiUtils.getBidiFormatFromPreferences()
				: BidiUtils.getDefaultBidiFormat();
		
		BidiUtils.createBidiControls(groupBidi, bidiFormat);
		BidiUtils.updateBidiPropertiesEnablement();
		
		Dialog.applyDialogFont( parent );
		EvHelp.setHelp( control, EvHelp.PREFERENCES_BIDI );
		return control;
	}


	public void propertyChange(PropertyChangeEvent event) {
//TODO EDT BIDI
//		if (event.getProperty().equals(EDTCoreIDEPlugin.BIDI_ENABLED_OPTION)){
//			if (!groupBidi.isDisposed())
//				BidiUtils.updateBidiPropertiesEnablement();
//		}
	}


	public void init(IWorkbench workbench) {
	}
}
