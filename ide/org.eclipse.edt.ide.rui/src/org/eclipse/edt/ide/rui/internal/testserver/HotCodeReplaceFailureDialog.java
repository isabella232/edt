package org.eclipse.edt.ide.rui.internal.testserver;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * Dialog to notify the user about a problem with hot code replace on a test server. A button is provided for terminating the server.
 */
public class HotCodeReplaceFailureDialog extends ErrorDialog {

	protected static final int BUTTON_ID_TERMINATE_SERVER = IDialogConstants.OK_ID + IDialogConstants.DETAILS_ID + 1;
	
	private final IPreferenceStore prefStore;
	private final String prefKey;
	private final TestServerConfiguration config;
	
	private Button checkbox;
	
	public HotCodeReplaceFailureDialog(Shell parentShell, String dialogTitle, String message, IStatus status, IPreferenceStore prefStore, String prefKey, TestServerConfiguration config) {
		super(parentShell, dialogTitle, message, status, IStatus.WARNING | IStatus.ERROR | IStatus.INFO);
		this.prefStore = prefStore;
		this.prefKey = prefKey;
		this.config = config;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		if (prefStore != null && prefKey != null) {
			checkbox = new Button(composite, SWT.CHECK | SWT.LEFT);
			checkbox.setText(TestServerMessages.HotCodeReplaceFailureRememberDecision);
			checkbox.setFont(parent.getFont());
	
			GridData data = new GridData(SWT.NONE);
			data.horizontalSpan = 2;
			data.horizontalAlignment = GridData.BEGINNING;
			data.verticalIndent = 10;
			checkbox.setLayoutData(data);
			
			Link link = new Link(composite, SWT.WRAP);
			link.setText(TestServerMessages.HotCodeReplaceFailureChangeLater);
			link.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					PreferencesUtil.createPreferenceDialogOn(null, "org.eclipse.edt.ide.rui.testServerPreferences", null, null).open(); //$NON-NLS-1$
				};
				public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
					widgetSelected(e);
				};
			});
			link.setFont(parent.getFont());
			
			data = new GridData(SWT.NONE);
			data.horizontalSpan = 2;
			data.horizontalAlignment = GridData.BEGINNING;
			link.setLayoutData(data);
		}
		
		return composite;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setText(TestServerMessages.HotCodeReplaceFailureContinueButton);
		createButton(parent, BUTTON_ID_TERMINATE_SERVER, TestServerMessages.HotCodeReplaceFailureTerminateButton, false);
		
		Button details = getButton(IDialogConstants.DETAILS_ID);
		if (details != null) {
			details.setFocus();
		}
	}
	
	protected void storePreference(int button) {
		// Store preference if checked
		boolean checked = checkbox != null && checkbox.getSelection();
		if (checked) {
			prefStore.setValue(prefKey, button == BUTTON_ID_TERMINATE_SERVER ? IRUIPreferenceConstants.TESTSERVER_TERMINATE : IRUIPreferenceConstants.TESTSERVER_IGNORE);
		}
	}
	
	@Override
	protected void buttonPressed(final int id) {
		if (id == BUTTON_ID_TERMINATE_SERVER) {
			try {
				config.terminate();
			}
			catch (DebugException e) {
				ErrorDialog.openError(getShell(), TestServerMessages.HotCodeReplaceTerminateFailedTitle,
						NLS.bind(TestServerMessages.HotCodeReplaceTerminateFailedMsg, config.getProject().getName()),
						e.getStatus());
			}
			
			storePreference(id);
			okPressed();
		}
		else {
			if (id == IDialogConstants.OK_ID) {
				storePreference(id);
			}
			super.buttonPressed(id);
		}
	}
}
