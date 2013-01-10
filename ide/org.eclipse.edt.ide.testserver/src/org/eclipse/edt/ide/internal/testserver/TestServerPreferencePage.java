/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.internal.testserver;

import org.eclipse.edt.ide.testserver.ITestServerPreferenceConstants;
import org.eclipse.edt.ide.testserver.TestServerMessages;
import org.eclipse.edt.ide.testserver.TestServerPlugin;
import org.eclipse.edt.ide.ui.internal.preferences.AbstractPreferencePage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class TestServerPreferencePage extends AbstractPreferencePage {
	
	private Button enableDebug;
	
	private Button hcrFailedPrompt;
	private Button hcrFailedIgnore;
	private Button hcrFailedTerminate;
	
	private Button hcrUnsupportedPrompt;
	private Button hcrUnsupportedIgnore;
	private Button hcrUnsupportedTerminate;
	
	private Button obsoletePrompt;
	private Button obsoleteIgnore;
	private Button obsoleteTerminate;
	
	private Button cpChangedPrompt;
	private Button cpChangedIgnore;
	private Button cpChangedTerminate;
	
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = createComposite(parent, 1);
		
		enableDebug = createCheckBox(composite, TestServerMessages.PreferenceEnableDebugLabel);
		createCPChangedComposite(composite);
		createHCRComposite(composite);
		
		loadPreferences();
		
		Dialog.applyDialogFont(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ITestServerPreferenceConstants.TEST_SERVER_PREFERENCE_PAGE_HELP_ID);
		
		return composite;
	}	
	
	private void createHCRComposite(Composite parent) {
		Group group = createGroup(parent, 1);
		group.setText(TestServerMessages.PreferenceHCRGroupLabel);
		
		GridData gd;
		int indentAmount = 10;
		
		// HCR Failed
		Composite failedComposite = new Composite(group, SWT.NULL);
		failedComposite.setLayout(new GridLayout(1, false));
		new Label(failedComposite, SWT.NULL).setText(TestServerMessages.PreferenceHCRFailedLabel);
		hcrFailedPrompt = new Button(failedComposite, SWT.RADIO);
		hcrFailedPrompt.setText(TestServerMessages.PreferencePromptLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		hcrFailedPrompt.setLayoutData(gd);
		
		hcrFailedIgnore = new Button(failedComposite, SWT.RADIO);
		hcrFailedIgnore.setText(TestServerMessages.PreferenceDoNothingLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		hcrFailedIgnore.setLayoutData(gd);
		
		hcrFailedTerminate = new Button(failedComposite, SWT.RADIO);
		hcrFailedTerminate.setText(TestServerMessages.PreferenceTerminateLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		hcrFailedTerminate.setLayoutData(gd);
		
		// HCR Not Supported
		Composite unsupportedComposite = new Composite(group, SWT.NULL);
		unsupportedComposite.setLayout(new GridLayout(1, false));
		new Label(unsupportedComposite, SWT.NULL).setText(TestServerMessages.PreferenceHCRUnsupportedLabel);
		hcrUnsupportedPrompt = new Button(unsupportedComposite, SWT.RADIO);
		hcrUnsupportedPrompt.setText(TestServerMessages.PreferencePromptLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		hcrUnsupportedPrompt.setLayoutData(gd);
		
		hcrUnsupportedIgnore = new Button(unsupportedComposite, SWT.RADIO);
		hcrUnsupportedIgnore.setText(TestServerMessages.PreferenceDoNothingLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		hcrUnsupportedIgnore.setLayoutData(gd);
		
		hcrUnsupportedTerminate = new Button(unsupportedComposite, SWT.RADIO);
		hcrUnsupportedTerminate.setText(TestServerMessages.PreferenceTerminateLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		hcrUnsupportedTerminate.setLayoutData(gd);
		
		// Obsolete Methods
		Composite obsoleteComposite = new Composite(group, SWT.NULL);
		obsoleteComposite.setLayout(new GridLayout(1, false));
		new Label(obsoleteComposite, SWT.NULL).setText(TestServerMessages.PreferenceObsoleteMethodsLabel);
		obsoletePrompt = new Button(obsoleteComposite, SWT.RADIO);
		obsoletePrompt.setText(TestServerMessages.PreferencePromptLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		obsoletePrompt.setLayoutData(gd);
		
		obsoleteIgnore = new Button(obsoleteComposite, SWT.RADIO);
		obsoleteIgnore.setText(TestServerMessages.PreferenceDoNothingLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		obsoleteIgnore.setLayoutData(gd);
		
		obsoleteTerminate = new Button(obsoleteComposite, SWT.RADIO);
		obsoleteTerminate.setText(TestServerMessages.PreferenceTerminateLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		obsoleteTerminate.setLayoutData(gd);
	}
	
	private void createCPChangedComposite(Composite parent) {
		Group group = createGroup(parent, 1);
		group.setText(TestServerMessages.PreferenceCPGroupLabel);
		
		GridData gd;
		int indentAmount = 10;
		
		// HCR Failed
		Composite failedComposite = new Composite(group, SWT.NULL);
		failedComposite.setLayout(new GridLayout(1, false));
		new Label(failedComposite, SWT.NULL).setText(TestServerMessages.PreferenceCPChangedLabel);
		cpChangedPrompt = new Button(failedComposite, SWT.RADIO);
		cpChangedPrompt.setText(TestServerMessages.PreferencePromptLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		cpChangedPrompt.setLayoutData(gd);
		
		cpChangedIgnore = new Button(failedComposite, SWT.RADIO);
		cpChangedIgnore.setText(TestServerMessages.PreferenceDoNothingLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		cpChangedIgnore.setLayoutData(gd);
		
		cpChangedTerminate = new Button(failedComposite, SWT.RADIO);
		cpChangedTerminate.setText(TestServerMessages.PreferenceTerminateLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = indentAmount;
		cpChangedTerminate.setLayoutData(gd);
	}
	
	@Override
	protected void initializeValues() {
		super.initializeValues();
		
		IPreferenceStore store = doGetPreferenceStore();
		enableDebug.setSelection(store.getBoolean(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_ENABLE_DEBUG));
		updateHCRFailed(store.getInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED));
		updateHCRUnsupported(store.getInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED));
		updateObsoleteMethods(store.getInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS));
		updateCPChanged(store.getInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_CLASSPATH_CHANGED));
	}
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		
		IPreferenceStore store = doGetPreferenceStore();
		enableDebug.setSelection(store.getDefaultBoolean(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_ENABLE_DEBUG));
		updateHCRFailed(store.getDefaultInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED));
		updateHCRUnsupported(store.getDefaultInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED));
		updateObsoleteMethods(store.getDefaultInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS));
		updateCPChanged(store.getDefaultInt(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_CLASSPATH_CHANGED));
	}
	
	@Override
	protected void storeValues() {
		IPreferenceStore store = doGetPreferenceStore();
		
		store.setValue(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_ENABLE_DEBUG, enableDebug.getSelection());
		
		int hcrFailedValue;
		if (hcrFailedIgnore.getSelection()) {
			hcrFailedValue = ITestServerPreferenceConstants.TESTSERVER_IGNORE;
		}
		else if (hcrFailedTerminate.getSelection()) {
			hcrFailedValue = ITestServerPreferenceConstants.TESTSERVER_TERMINATE;
		}
		else {
			hcrFailedValue = ITestServerPreferenceConstants.TESTSERVER_PROMPT;
		}
		store.setValue(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED, hcrFailedValue);
		
		int hcrNotSupportedValue;
		if (hcrUnsupportedIgnore.getSelection()) {
			hcrNotSupportedValue = ITestServerPreferenceConstants.TESTSERVER_IGNORE;
		}
		else if (hcrUnsupportedTerminate.getSelection()) {
			hcrNotSupportedValue = ITestServerPreferenceConstants.TESTSERVER_TERMINATE;
		}
		else {
			hcrNotSupportedValue = ITestServerPreferenceConstants.TESTSERVER_PROMPT;
		}
		store.setValue(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED, hcrNotSupportedValue);
		
		int obsoleteValue;
		if (obsoleteIgnore.getSelection()) {
			obsoleteValue = ITestServerPreferenceConstants.TESTSERVER_IGNORE;
		}
		else if (obsoleteTerminate.getSelection()) {
			obsoleteValue = ITestServerPreferenceConstants.TESTSERVER_TERMINATE;
		}
		else {
			obsoleteValue = ITestServerPreferenceConstants.TESTSERVER_PROMPT;
		}
		store.setValue(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS, obsoleteValue);
		
		int cpChangedValue;
		if (cpChangedIgnore.getSelection()) {
			cpChangedValue = ITestServerPreferenceConstants.TESTSERVER_IGNORE;
		}
		else if (cpChangedTerminate.getSelection()) {
			cpChangedValue = ITestServerPreferenceConstants.TESTSERVER_TERMINATE;
		}
		else {
			cpChangedValue = ITestServerPreferenceConstants.TESTSERVER_PROMPT;
		}
		store.setValue(ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_CLASSPATH_CHANGED, cpChangedValue);
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return TestServerPlugin.getDefault().getPreferenceStore();
	}
	
	private void updateHCRFailed(int selection) {
		hcrFailedIgnore.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_IGNORE);
		hcrFailedTerminate.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_TERMINATE);
		hcrFailedPrompt.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_PROMPT);
	}
	
	private void updateHCRUnsupported(int selection) {
		hcrUnsupportedIgnore.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_IGNORE);
		hcrUnsupportedTerminate.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_TERMINATE);
		hcrUnsupportedPrompt.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_PROMPT);
	}
	
	private void updateObsoleteMethods(int selection) {
		obsoleteIgnore.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_IGNORE);
		obsoleteTerminate.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_TERMINATE);
		obsoletePrompt.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_PROMPT);
	}
	
	private void updateCPChanged(int selection) {
		cpChangedIgnore.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_IGNORE);
		cpChangedTerminate.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_TERMINATE);
		cpChangedPrompt.setSelection(selection == ITestServerPreferenceConstants.TESTSERVER_PROMPT);
	}
}
