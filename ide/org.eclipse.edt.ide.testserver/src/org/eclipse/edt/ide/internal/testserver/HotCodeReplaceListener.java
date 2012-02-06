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
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.ide.testserver.TestServerPlugin;
import org.eclipse.edt.ide.testserver.ITestServerPreferenceConstants;
import org.eclipse.edt.ide.testserver.TestServerConfiguration;
import org.eclipse.edt.ide.testserver.TestServerMessages;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaHotCodeReplaceListener;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

/**
 * Handles errors due to hot code replace.
 */
public class HotCodeReplaceListener implements IJavaHotCodeReplaceListener {
	
	private final TestServerConfiguration config;
	
	public HotCodeReplaceListener(TestServerConfiguration config) {
		this.config = config;
	}
	
	@Override
	public void hotCodeReplaceFailed(IJavaDebugTarget target, DebugException exception) {
		if (exception == null) {
			displayDialog(TestServerMessages.HCRFailedTitle,
					new Status(IStatus.WARNING, TestServerPlugin.PLUGIN_ID, NLS.bind(TestServerMessages.HCRUnsupportedMsg, config.getProject().getName())
							+ "\n\n" + TestServerMessages.ErrorDialogTerminateMsg), //$NON-NLS-1$
					TestServerPlugin.getDefault().getPreferenceStore(), ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_UNSUPPORTED);
		}
		else {
			displayDialog(TestServerMessages.HCRFailedTitle,
					new Status(IStatus.WARNING, TestServerPlugin.PLUGIN_ID, NLS.bind(TestServerMessages.HCRFailedMsg, config.getProject().getName())
							+ "\n\n" + TestServerMessages.ErrorDialogTerminateMsg, exception), //$NON-NLS-1$
					TestServerPlugin.getDefault().getPreferenceStore(), ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_HCR_FAILED);
		}
	}
	
	@Override
	public void obsoleteMethods(IJavaDebugTarget target) {
		displayDialog(TestServerMessages.ObsoleteMethodsTitle,
				new Status(IStatus.WARNING, TestServerPlugin.PLUGIN_ID, NLS.bind(TestServerMessages.ObsoleteMethodsMsg, config.getProject().getName())
						+ "\n\n" + TestServerMessages.ErrorDialogTerminateMsg), //$NON-NLS-1$
				TestServerPlugin.getDefault().getPreferenceStore(), ITestServerPreferenceConstants.PREFERENCE_TESTSERVER_OBSOLETE_METHODS);
	}
	
	protected void displayDialog(final String title, final IStatus status, final IPreferenceStore store, final String prefKey) {
		switch (store.getInt(prefKey)) {
			case ITestServerPreferenceConstants.TESTSERVER_IGNORE:
				break;
			case ITestServerPreferenceConstants.TESTSERVER_TERMINATE:
				try {
					config.terminate();
				}
				catch (final DebugException e) {
					final Display display = TestServerPlugin.getDisplay();
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							ErrorDialog.openError(TestServerPlugin.getShell(), TestServerMessages.TerminateFailedTitle, 
									NLS.bind(TestServerMessages.TerminateFailedMsg, config.getProject().getName()),
									e.getStatus());
						}
					});
				}
				break;
			case ITestServerPreferenceConstants.TESTSERVER_PROMPT:
			default:
				final Display display = TestServerPlugin.getDisplay();
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						if (display.isDisposed()) {
							return;
						}
						UpdateErrorDialog dialog = new UpdateErrorDialog(TestServerPlugin.getShell(), title, null, status, store, prefKey, config);
						dialog.setBlockOnOpen(false);
						dialog.open();
					}
				});
				break;
		}
		
	}
	
	@Override
	public void hotCodeReplaceSucceeded(IJavaDebugTarget target) {
	}
}
