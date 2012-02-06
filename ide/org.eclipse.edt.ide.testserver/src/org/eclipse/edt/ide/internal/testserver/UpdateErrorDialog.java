/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.ide.testserver.ITestServerPreferenceConstants;
import org.eclipse.edt.ide.testserver.TestServerConfiguration;
import org.eclipse.edt.ide.testserver.TestServerMessages;
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
 * Dialog to notify the user about a problem with hot code replace or classpath change on a test server. A button is provided for terminating the server(s).
 */
public class UpdateErrorDialog extends ErrorDialog {

	protected static final int BUTTON_ID_TERMINATE_SERVER = IDialogConstants.OK_ID + IDialogConstants.DETAILS_ID + 1;
	
	private final IPreferenceStore prefStore;
	private final String prefKey;
	private final TestServerConfiguration[] configs;
	
	private Button checkbox;
	
	/**
	 * Constructor for when a single server is in error.
	 */
	public UpdateErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status, IPreferenceStore prefStore, String prefKey, TestServerConfiguration config) {
		this(parentShell, dialogTitle, message, status, prefStore, prefKey, new TestServerConfiguration[]{config});
	}
	
	/**
	 * Constructor for when multiple servers are in error.
	 */
	public UpdateErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status, IPreferenceStore prefStore, String prefKey, TestServerConfiguration[] configs) {
		super(parentShell, dialogTitle, message, status, IStatus.WARNING | IStatus.ERROR | IStatus.INFO);
		this.prefStore = prefStore;
		this.prefKey = prefKey;
		this.configs = configs;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		if (prefStore != null && prefKey != null) {
			checkbox = new Button(composite, SWT.CHECK | SWT.LEFT);
			checkbox.setText(TestServerMessages.ErrorDialogRememberDecision);
			checkbox.setFont(parent.getFont());
	
			GridData data = new GridData(SWT.NONE);
			data.horizontalSpan = 2;
			data.horizontalAlignment = GridData.BEGINNING;
			data.verticalIndent = 10;
			checkbox.setLayoutData(data);
			
			Link link = new Link(composite, SWT.WRAP);
			link.setText(TestServerMessages.ErrorDialogChangeLater);
			link.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					PreferencesUtil.createPreferenceDialogOn(null, "org.eclipse.edt.ide.testserver.testServerPreferences", null, null).open(); //$NON-NLS-1$
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
		createDetailsButton(parent);
		createButton(parent, BUTTON_ID_TERMINATE_SERVER, configs.length > 1 ? TestServerMessages.ErrorDialogTerminatePluralButton : TestServerMessages.ErrorDialogTerminateButton, false);
		createButton(parent, IDialogConstants.OK_ID, TestServerMessages.ErrorDialogContinueButton, false);
		
		Button details = getButton(IDialogConstants.DETAILS_ID);
		if (details != null) {
			details.setFocus();
		}
	}
	
	protected void storePreference(int button) {
		// Store preference if checked
		boolean checked = checkbox != null && checkbox.getSelection();
		if (checked) {
			prefStore.setValue(prefKey, button == BUTTON_ID_TERMINATE_SERVER ? ITestServerPreferenceConstants.TESTSERVER_TERMINATE : ITestServerPreferenceConstants.TESTSERVER_IGNORE);
		}
	}
	
	@Override
	protected void buttonPressed(final int id) {
		if (id == BUTTON_ID_TERMINATE_SERVER) {
			for (TestServerConfiguration config : configs) {
				try {
					config.terminate();
				}
				catch (DebugException e) {
					ErrorDialog.openError(getShell(), TestServerMessages.TerminateFailedTitle,
							NLS.bind(TestServerMessages.TerminateFailedMsg, config.getProject().getName()),
							e.getStatus());
				}
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
