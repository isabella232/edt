/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.internal.sql.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IConnectionProfileProvider;
import org.eclipse.datatools.connectivity.IPropertiesPersistenceHook;
import org.eclipse.datatools.connectivity.internal.ConnectionProfileProvider;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.edt.ide.sql.SQLNlsStrings;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class RDBConnectionUtility {
	
	public static IStatus connectWithPromptIfNeeded(IConnectionProfile profile, boolean reprompt) {
		String profileName = profile.getName();
		
		Shell shell = Display.getCurrent().getActiveShell();
		IStatus connectionStatus = null;
		ConnectionInfo info = null;
		if (profile != null) {
			if (shell == null) {
				connectionStatus = profile.connect();
			}
			else {
				connectionStatus = profile.connectWithoutJob();
				if ( reprompt && profile.getConnectionState() != IConnectionProfile.CONNECTED_STATE && connectionStatus.getCode() != IStatus.OK ) // could be marked OK if the profile can't be found for some reason
				{
					String title = NLS.bind(SQLNlsStrings.SQL_CONNECTION_FAILURE_MSG, profile.getName());
					MessageDialog.openInformation(shell, title, connectionStatus.getChildren()[0].getException().getLocalizedMessage());

					// Prompt to fix properties
					PropertyDialogAction propertyDialogAction = new PropertyDialogAction(
							new SameShellProvider(shell), 
							new ConnectionPropertiesWizardSelectionProvider(profile));

					
					StructuredSelection selection = new StructuredSelection(profile);
					propertyDialogAction.selectionChanged(selection);
					if (!profile.arePropertiesComplete() && propertyDialogAction.isApplicableForSelection()) {
						//something in createDialog is failing to initialize the properties correctly the first time 
						//around. I can't debug it because it crashes the debugger when I set a breakpoint, so I'll 
						//call twice for now to get the initialization to happen until I figure it out.
						PreferenceDialog dialog = propertyDialogAction.createDialog();
						dialog = propertyDialogAction.createDialog();
						String shellText = NLS.bind(SQLNlsStrings.SQL_CONNECTION_PROPERTIES_FOR, profile.getName());
						dialog.getShell().setText(shellText);
						IConnectionProfileProvider provider = profile.getProvider();
						IPropertiesPersistenceHook hook = ((ConnectionProfileProvider)provider).getPropertiesPersistenceHook();
						String initialPage = hook.getConnectionPropertiesPageID();
						if (initialPage != null) {
							((IWorkbenchPreferenceContainer) dialog).openPage(initialPage, null);
						}
						if (dialog.open() == Dialog.CANCEL) {
							reprompt = false;
						}
					}
					if ( reprompt )
					{
						connectionStatus = profile.connectWithoutJob();
					}
				}
			}
		}
		
		return connectionStatus;
	}
	
	private static final class ConnectionPropertiesWizardSelectionProvider implements ISelectionProvider {
		private IStructuredSelection selection;

		public ConnectionPropertiesWizardSelectionProvider(
				IConnectionProfile connectionProfile) {
			selection = new StructuredSelection(connectionProfile);
		}

		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
		}

		public ISelection getSelection() {
			return selection;
		}

		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
		}

		public void setSelection(ISelection selection) {
		}
	}
}
