/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.wizards;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.ui.ProfileImageRegistry;
import org.eclipse.datatools.connectivity.ui.actions.ViewPropertyAction;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ConnectionDisplayProperty;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.sql.ISQLPreferenceConstants;
import org.eclipse.edt.ide.sql.SQLNlsStrings;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.util.UISQLUtility;
import org.eclipse.edt.ide.ui.wizards.BindingSQLDatabaseConfiguration;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

public class SQLDatabaseBindingWizardPage extends EGLDDBindingWizardPage implements Listener {
	public static final String WIZPAGENAME_SQLDatabaseBindingWizardPage = "WIZPAGENAME_SQLDatabaseBindingWizardPage"; //$NON-NLS-1$
	
	//private Text connectionSecondaryID;
	private Button newButton;
	private Button editButton;
	private TreeViewer existingConnectionsList;
	private Label propertiesLabel;
	private Table connectionPropertiesTable;

	private Hashtable existingConnections;
	
	private Button connProfileUriRadio;
	private Button connProfileInlinedRadio;
	
	private Group connectionGroup;
	private ControlEnableState connectionGroupEnablement;
	
	public SQLDatabaseBindingWizardPage(String pageName){
		super(pageName);
		setTitle(NewWizardMessages.TitleAddSQLDatabaseBinding);
		setDescription(NewWizardMessages.DescAddSQLDatabaseBinding);
		nColumns = 2;
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.MODULE_SQLDATABASEBINDING);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
//		getEGLDDBindingConfiguration().getBindingSQLConfiguration();
		
		createBindingChoiceSection(composite);
		createConnectionGroup(composite);
		initializeValues();
		
		setControl(composite);
		Dialog.applyDialogFont(parent);
	}
	
	private void createBindingChoiceSection(Composite parent) {
		Label l = new Label(parent, SWT.WRAP);
		l.setText(NewWizardMessages.SQLBindingDescription);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		l.setLayoutData(gd);
		
		Label spacer = new Label(parent, SWT.WRAP);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		spacer.setLayoutData(gd);
		
		connProfileUriRadio = new Button(parent, SWT.RADIO);
		connProfileUriRadio.setText(NewWizardMessages.SQLBindingWorkspaceUriLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		connProfileUriRadio.setLayoutData(gd);
		connProfileUriRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWorkspaceURIRadio();
				
				if (connectionGroupEnablement != null) {
					connectionGroupEnablement.restore();
					connectionGroupEnablement = null;
				}
			}
		});
		
		connProfileInlinedRadio = new Button(parent, SWT.RADIO);
		connProfileInlinedRadio.setText(NewWizardMessages.SQLBindingHardcodedLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		connProfileInlinedRadio.setLayoutData(gd);
		connProfileInlinedRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleInlinedRadio();
		
				if (connectionGroupEnablement != null) {
					connectionGroupEnablement.restore();
					connectionGroupEnablement = null;
				}
			}
		});
	}
	
	private void handleWorkspaceURIRadio() {
		getConfiguration().setUseUri(true);
		updateConnectionProperties();
		determinePageCompletion();
	}
	
	private void handleInlinedRadio() {
		getConfiguration().setUseUri(false);
		updateConnectionProperties();
		determinePageCompletion();
	}
	
	private void createConnectionGroup(Composite parent) {

		Composite groupParent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		groupParent.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = nColumns;
		//data.heightHint = 300;
		groupParent.setLayoutData(data);

		connectionGroup = new Group(groupParent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		connectionGroup.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_BOTH);
		connectionGroup.setLayoutData(gd);
		connectionGroup.setText(SQLNlsStrings.SQL_CONNECTION_LABEL_GROUP);

		Composite groupComp = new Composite(connectionGroup, SWT.NONE);
		GridData gdComp = new GridData(GridData.FILL_BOTH);
		GridLayout gl2 = new GridLayout();
		gl2.numColumns = 2;
		groupComp.setLayout(gl2);
		groupComp.setLayoutData(gdComp);

		createConnectionSection(groupComp);
		createPropertySection(groupComp);

		Composite buttonComp = new Composite(connectionGroup, SWT.NULL);
		GridLayout gl3 = new GridLayout();
		buttonComp.setLayout(gl3);
		GridData gd3 = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonComp.setLayoutData(gd3);

		createButtons(buttonComp);
	}
	
	/**
	 * Initializes states of the controls from the preference store.
	 */
	private void initializeValues() {
		IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();

		if (profiles != null && profiles.length > 0) {
			updateTreeData();

			IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
			String connectionName = store
					.getString(ISQLPreferenceConstants.SQL_CONNECTION_NAMED_CONNECTION);
			if (connectionName.length() != 0
					&& existingConnections.containsKey(connectionName)) {
				TreeItem item = findTreeItemByName(existingConnectionsList
						.getTree().getItems(), connectionName);
				if (item != null) {
					existingConnectionsList.getTree().setSelection(item);
					updateConnectionProperties();
				}
			}

			if (existingConnectionsList.getTree().getSelectionCount() > 0) {
				enableConnectionsControls(true, true);
			} else {
				enableConnectionsControls(false, true);
			}
		} else {
			enableConnectionsControls(false, false);
		}
		
		connProfileUriRadio.setSelection(true);
		handleWorkspaceURIRadio(); // previous line won't fire an event - manually run the code
	}
	
	private void createConnectionSection(Composite parent) {

		existingConnectionsList = new TreeViewer(parent, SWT.BORDER
				| SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 100;
		existingConnectionsList.getTree().setLayoutData(gd);
		existingConnectionsList.getTree().addListener(SWT.Selection, this);
	}
	
	private void createButtons(Composite parent) {
		newButton = new Button(parent, SWT.PUSH);
		newButton.setText(SQLNlsStrings.SQL_CONNECTION_NEW_BUTTON);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		newButton.setLayoutData(gd);
		newButton.addListener(SWT.Selection, this);

		editButton = new Button(parent, SWT.PUSH);
		editButton.setText(SQLNlsStrings.SQL_CONNECTION_EDIT_BUTTON);
		GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
		editButton.setLayoutData(gd3);
		editButton.addListener(SWT.Selection, this);
	}
	
	public void handleEvent(Event e) {
		Widget source = e.widget;

		if (source.equals(newButton)) {
			IConnectionProfile newProfile = EGLSQLUtility.createNewProfile();
			if (newProfile != null) {
				updateTreeData();
				
				// Select the new connection.  The user just made it, so they must intend to use it.
				TreeItem item = findTreeItemByName(existingConnectionsList
						.getTree().getItems(), newProfile.getName());
				if (item != null) {
					existingConnectionsList.getTree().select(item);
				}
				updateConnectionProperties();
				enableConnectionsControls(true, true);
			}
		} else if (source.equals(editButton)) {
			ViewPropertyAction editConnectionProfileAction = new ViewPropertyAction(
					existingConnectionsList);
			editConnectionProfileAction.run();
			updateTreeData();
		} else if (source.equals(existingConnectionsList.getTree())) {
			updateConnectionProperties();
		}

		determinePageCompletion();
	}
	
	private void updateTreeData() {
		IConnectionProfile currentSelection = getSelectedConnection();
		existingConnectionsList.getTree().removeAll();
		IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();
		existingConnections = new Hashtable();
		Iterator connections = Arrays.asList(profiles).iterator();
		IConnectionProfile connection;
		while (connections.hasNext()) {
			connection = (IConnectionProfile) connections.next();
			existingConnections.put(connection.getName(), connection);
		}
		Object[] sortedNames = this.sortItems(existingConnections.keySet()
				.toArray());
		for (int index = 0; index < sortedNames.length; index++) {
			String name = (String) sortedNames[index];
			IConnectionProfile profile = (IConnectionProfile) existingConnections
					.get(name);
			TreeItem item = new TreeItem(existingConnectionsList.getTree(),
					SWT.NONE);
			item.setText(name);
			item.setData(existingConnections.get(name));
			item.setImage(ProfileImageRegistry.getInstance().getProfileImage(
					profile.getProvider()));

			if (currentSelection != null && profile == currentSelection) {
				existingConnectionsList.getTree().setSelection(item);
			}
		}
	}
	
	private IConnectionProfile getSelectedConnection() {
		IConnectionProfile profile = null;
		TreeItem[] selection = existingConnectionsList.getTree().getSelection();
		if (selection.length > 0) {
			profile = (IConnectionProfile) selection[0].getData();
		}
		return profile;
	}
	
	private Object[] sortItems(Object[] names) {
		Arrays.sort(names, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				int result = -1;
				if ((arg0 != null) && (arg1 != null)) {
					result = ((String) arg0).compareToIgnoreCase((String) arg1);
				}
				return result;
			}
		});
		return names;
	}
	
	private TreeItem findTreeItemByName(TreeItem[] items, String name) {
		TreeItem foundItem = null;
		for (int i = 0; i < items.length; i++) {
			if (items[i].getText().equals(name)) {
				foundItem = items[i];
				break;
			}
		}

		return foundItem;
	}
	
	private void updateConnectionProperties() {
		connectionPropertiesTable.removeAll();
		int selectionCount = existingConnectionsList.getTree()
				.getSelectionCount();
		if (selectionCount == 0) {
			enableConnectionsControls(false, true);
		} else if (selectionCount > 0) {
			enableConnectionsControls(true, true);
			IConnectionProfile profile = getSelectedConnection();
			if (profile != null) {
				ConnectionDisplayProperty[] properties = EGLSQLUtility.getConnectionDisplayProperties(profile);
				if (properties != null) {
					int propertyCount = properties.length;
					for (int index = 0; index < propertyCount; index++) {
						TableItem tableItem = new TableItem(
								connectionPropertiesTable, SWT.NONE);
						if(SQLNlsStrings.SQL_CONNECTION_USER_PASSWORD_PROPERTY.equals(properties[index].getPropertyName())
							 &&	(properties[index].getValue() != null)) {
							StringBuilder builder = new StringBuilder();
							
							for(int i=0; i < properties[index].getValue().length(); i++) {
								builder.append("*"); //$NON-NLS-1$
							}
							tableItem.setText(new String[] {
									properties[index].getPropertyName(),
									builder.toString() });
						} else {
							tableItem.setText(new String[] {
									properties[index].getPropertyName(),
									properties[index].getValue() });
						}
					}
					
					if (connProfileInlinedRadio.getSelection()) {
						UISQLUtility.setBindingSQLDatabaseConfiguration(getConfiguration(), properties);
					}
					else if (connProfileUriRadio.getSelection()) {
						getConfiguration().setBindingName(profile.getName());
						getConfiguration().setUri("workspace://" + profile.getName()); //$NON-NLS-1$
					}
				}
			}
		}
	}
	
	private void enableConnectionsControls(boolean isEnabled, boolean enableTree) {
		existingConnectionsList.getTree().setEnabled(enableTree);
		propertiesLabel.setEnabled(isEnabled);
		connectionPropertiesTable.setEnabled(isEnabled);
		editButton.setEnabled(isEnabled);
		//connectionPassword.setEnabled(isEnabled);
	}
	
	private void createPropertySection(Composite parent) {

		propertiesLabel = new Label(parent, SWT.NONE);
		propertiesLabel
				.setText(SQLNlsStrings.SQL_CONNECTION_LABEL_PROPERTIES);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		propertiesLabel.setLayoutData(gd);

		connectionPropertiesTable = new Table(parent, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.heightHint = 150;
		connectionPropertiesTable.setLayoutData(gd);
		connectionPropertiesTable.setLinesVisible(true);
		connectionPropertiesTable.setHeaderVisible(true);

		TableColumn tc1 = new TableColumn(connectionPropertiesTable, SWT.NONE);
		tc1.setText(SQLNlsStrings.SQL_CONNECTION_COLUMN_PROPERTY);
		tc1.setResizable(true);
		tc1.setWidth(140);

		TableColumn tc2 = new TableColumn(connectionPropertiesTable, SWT.NONE);
		tc2.setText(SQLNlsStrings.SQL_CONNECTION_COLUMN_VALUE);
		tc2.setResizable(true);
		tc2.setWidth(350);
	}
	
	@Override
	protected boolean determinePageCompletion() {
		setErrorMessage(null);
		boolean result = true;
		
		if (connProfileInlinedRadio.getSelection() || connProfileUriRadio.getSelection()) {
			// Verify a connection was selected.
			if (getSelectedConnection() == null) {
				setErrorMessage(NewWizardMessages.SQLBindingNoConnectionSelected);
				result = false;
			}
		}
		
		setPageComplete(result);
		return result;
	}
	
	private BindingSQLDatabaseConfiguration getConfiguration(){
		return (BindingSQLDatabaseConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}
	
	protected BindingSQLDatabaseConfiguration getBindingSQLDatabaseConfiguration(){
		return (BindingSQLDatabaseConfiguration)((EGLPartWizard)getWizard()).getConfiguration(SQLDatabaseBindingWizardPage.WIZPAGENAME_SQLDatabaseBindingWizardPage);
	}
}
