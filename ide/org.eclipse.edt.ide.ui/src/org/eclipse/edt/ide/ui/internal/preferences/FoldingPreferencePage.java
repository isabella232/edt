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
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jdt.internal.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class FoldingPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private IPreferenceConfigurationBlock fConfigurationBlock;
	private OverlayPreferenceStore fOverlayStore;
	
	public FoldingPreferencePage() {
		setDescription(UINlsStrings.EditorFoldingPreferencePageDescription);
		setPreferenceStore(EDTUIPlugin.getDefault().getPreferenceStore());
		fOverlayStore = new OverlayPreferenceStore(getPreferenceStore(), new OverlayPreferenceStore.OverlayKey[] {});
		fConfigurationBlock= createConfigurationBlock(fOverlayStore);
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IUIHelpConstants.EDITOR_FOLDING_PREFERENCE_CONTEXT);
	}
	
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new FoldingConfigurationBlock(overlayPreferenceStore);
	}
	
	protected Control createContents(Composite parent) {
		fOverlayStore.load();
		fOverlayStore.start();		
		Control content = fConfigurationBlock.createControl(parent);		
		initialize();		
		Dialog.applyDialogFont(content);
		return content;		
	}

	private void initialize() {
		fConfigurationBlock.initialize();
	}
	
	public void init(IWorkbench workbench) {		
	}
	
	public boolean performOk() {
		fConfigurationBlock.performOk();
		fOverlayStore.propagate();		
		EDTUIPlugin.getDefault().saveUIPluginPreferences();
		return true;		
	}
	
	public void performDefaults() {		
		fOverlayStore.loadDefaults();
		fConfigurationBlock.performDefaults();
		super.performDefaults();
	}
	
	public void dispose() {		
		fConfigurationBlock.dispose();		
		if (fOverlayStore != null) {
			fOverlayStore.stop();
			fOverlayStore= null;
		}		
		super.dispose();
	}	
	
}
