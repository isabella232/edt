package org.eclipse.edt.ide.rui.internal.testserver;

import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.internal.HelpContextIDs;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
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
	
	private Button hcrFailedPrompt;
	private Button hcrFailedIgnore;
	private Button hcrFailedTerminate;
	
	private Button hcrUnsupportedPrompt;
	private Button hcrUnsupportedIgnore;
	private Button hcrUnsupportedTerminate;
	
	private Button obsoletePrompt;
	private Button obsoleteIgnore;
	private Button obsoleteTerminate;
	
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = createComposite(parent, 1);
		
		createTestServerComposite(composite);
		
		loadPreferences();
		
		Dialog.applyDialogFont(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, HelpContextIDs.RUI_TEST_SERVER_PREFERENCE_PAGE);
		
		return composite;
	}	
	
	private void createTestServerComposite(Composite parent) {
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
	
	@Override
	protected void initializeValues() {
		super.initializeValues();
		
		IPreferenceStore store = doGetPreferenceStore();
		updateHCRFailed(store.getInt(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED));
		updateHCRUnsupported(store.getInt(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED));
		updateObsoleteMethods(store.getInt(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS));
	}
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		
		IPreferenceStore store = doGetPreferenceStore();
		updateHCRFailed(store.getDefaultInt(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED));
		updateHCRUnsupported(store.getDefaultInt(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED));
		updateObsoleteMethods(store.getDefaultInt(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS));
	}
	
	@Override
	protected void storeValues() {
		int hcrFailedValue;
		if (hcrFailedIgnore.getSelection()) {
			hcrFailedValue = IRUIPreferenceConstants.TESTSERVER_IGNORE;
		}
		else if (hcrFailedTerminate.getSelection()) {
			hcrFailedValue = IRUIPreferenceConstants.TESTSERVER_TERMINATE;
		}
		else {
			hcrFailedValue = IRUIPreferenceConstants.TESTSERVER_PROMPT;
		}
		doGetPreferenceStore().setValue(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED, hcrFailedValue);
		
		int hcrNotSupportedValue;
		if (hcrUnsupportedIgnore.getSelection()) {
			hcrNotSupportedValue = IRUIPreferenceConstants.TESTSERVER_IGNORE;
		}
		else if (hcrUnsupportedTerminate.getSelection()) {
			hcrNotSupportedValue = IRUIPreferenceConstants.TESTSERVER_TERMINATE;
		}
		else {
			hcrNotSupportedValue = IRUIPreferenceConstants.TESTSERVER_PROMPT;
		}
		doGetPreferenceStore().setValue(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED, hcrNotSupportedValue);
		
		int obsoleteValue;
		if (obsoleteIgnore.getSelection()) {
			obsoleteValue = IRUIPreferenceConstants.TESTSERVER_IGNORE;
		}
		else if (obsoleteTerminate.getSelection()) {
			obsoleteValue = IRUIPreferenceConstants.TESTSERVER_TERMINATE;
		}
		else {
			obsoleteValue = IRUIPreferenceConstants.TESTSERVER_PROMPT;
		}
		doGetPreferenceStore().setValue(IRUIPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS, obsoleteValue);
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	private void updateHCRFailed(int selection) {
		hcrFailedIgnore.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_IGNORE);
		hcrFailedTerminate.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_TERMINATE);
		hcrFailedPrompt.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_PROMPT);
	}
	
	private void updateHCRUnsupported(int selection) {
		hcrUnsupportedIgnore.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_IGNORE);
		hcrUnsupportedTerminate.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_TERMINATE);
		hcrUnsupportedPrompt.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_PROMPT);
	}
	
	private void updateObsoleteMethods(int selection) {
		obsoleteIgnore.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_IGNORE);
		obsoleteTerminate.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_TERMINATE);
		obsoletePrompt.setSelection(selection == IRUIPreferenceConstants.TESTSERVER_PROMPT);
	}
}
