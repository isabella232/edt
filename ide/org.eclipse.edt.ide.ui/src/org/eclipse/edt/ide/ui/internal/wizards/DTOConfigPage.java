/*******************************************************************************
 * Copyright 漏 2011 IBM Corporation and others.
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

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.datatools.connectivity.sqm.core.connection.ConnectionInfo;
import org.eclipse.datatools.connectivity.sqm.core.internal.ui.util.resources.ImagePath;
import org.eclipse.datatools.connectivity.sqm.internal.core.util.ConnectionUtil;
import org.eclipse.datatools.connectivity.ui.actions.AddProfileViewAction;
import org.eclipse.datatools.modelbase.sql.schema.Catalog;
import org.eclipse.datatools.modelbase.sql.schema.Database;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.edt.ide.internal.sql.util.RDBConnectionUtility;
import org.eclipse.edt.ide.sql.SQLPlugin;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusUtil;
import org.eclipse.edt.ide.ui.internal.record.NewRecordWizardMessages;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class DTOConfigPage extends WizardPage implements SelectionListener, ICheckStateListener {

	private IStatus fCurrStatus;
	private boolean fPageVisible;

	private Combo dbConnectionCombo;
	private Link newDatabaseConnectionLink;

	private CheckboxTreeViewer dbTableViewer;
	private Label numSelectedLabel;

	private Button qualifyTableNamesCheckbox;
	private Button saveConnectionToDDCheckbox;

	// Page Validation

	private StatusInfo databaseStatus, connectionStatus, tableStatus;
	private boolean confirmOverwrite = false;

	private DTOConfiguration configuration;
	private Hashtable existingConnections;

	protected boolean isBidi = false; // @bd1a

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public DTOConfigPage(DTOConfiguration config) {
		super(DTOConfigPage.class.getName());
		setTitle(NewWizardMessages.FromSqlDatabasePage_Title);

		this.configuration = config;

		databaseStatus = new StatusInfo();
		connectionStatus = new StatusInfo();
		tableStatus = new StatusInfo();

		fPageVisible = false;
		fCurrStatus = new StatusInfo();
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		// Create main composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		createDatabaseControls(composite);
		
		createTableControls(composite);
		
		qualifyTableNamesCheckbox = new Button(composite, SWT.CHECK);
		qualifyTableNamesCheckbox.setText(NewWizardMessages.FromSqlDatabasePage_QualifyTableNames);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		qualifyTableNamesCheckbox.setLayoutData(data);
		qualifyTableNamesCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button)e.widget).getSelection();
				configuration.setQualifiedTableNames(isSelected);
			}
		});

		saveConnectionToDDCheckbox = new Button(composite, SWT.CHECK);
		saveConnectionToDDCheckbox.setText(NewWizardMessages.FromSqlDatabasePage_SaveConnectionToDD);
		data = new GridData();
		data.horizontalSpan = 2;
		saveConnectionToDDCheckbox.setLayoutData(data);
		saveConnectionToDDCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isSelected = ((Button)e.widget).getSelection();
				configuration.setSaveConnectionToDeploymentDescriptor(isSelected);
			}
		});

		setErrorMessage(null);
		setMessage(null);

		setControl(composite);

		// validatePage();
		Dialog.applyDialogFont(parent);
	}


	public IWizardPage getNextPage() {
		IWizardPage page = super.getNextPage();
		return page;
	}

	private void createDatabaseControls(Composite composite) {
		// Create the composite for the database controls
		Composite dbComposite = new Composite(composite, SWT.NONE);
		dbComposite.setFont(composite.getFont());

		GridLayout dbLayout = new GridLayout();
		dbLayout.marginWidth = dbLayout.marginHeight = 0;
		dbLayout.numColumns = 2;

		GridData dbData = new GridData(GridData.FILL_HORIZONTAL);

		dbComposite.setLayout(dbLayout);
		dbComposite.setLayoutData(dbData);

		// Create the database controls
		Label dbLabel = new Label(dbComposite, SWT.NONE);
		dbLabel.setText(NewWizardMessages.FromSqlDatabasePage_DBConnectionlabel);

		dbConnectionCombo = new Combo(dbComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		dbConnectionCombo.setLayoutData(gd);
		dbConnectionCombo.addSelectionListener(this);

		new Label(dbComposite, SWT.NONE);

		newDatabaseConnectionLink = new Link(dbComposite, SWT.PUSH);
		newDatabaseConnectionLink.setText(NewWizardMessages.FromSqlDatabasePage_CreateDBLink);
																				
		newDatabaseConnectionLink.addSelectionListener(this);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		newDatabaseConnectionLink.setLayoutData(gd);

		initializeDatabaseValues();
	}

	private void createTableControls(Composite composite) {
		// Create the composite for the table controls
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setFont(composite.getFont());

		GridLayout tableLayout = new GridLayout(1, false);
		tableLayout.marginWidth = tableLayout.marginHeight = 0;

		GridData tableData = new GridData(GridData.FILL_BOTH);

		tableComposite.setLayout(tableLayout);
		tableComposite.setLayoutData(tableData);

		Label tableLabel = new Label(tableComposite, SWT.NONE);
		tableLabel.setText(NewWizardMessages.FromSqlDatabasePage_TablesLabel);

		dbTableViewer = new CheckboxTreeViewer(tableComposite);
		dbTableViewer.setContentProvider(new DatabaseTableContentProvider());
		dbTableViewer.setLabelProvider(new DatabaseTableLabelProvider());

		tableData = new GridData(GridData.FILL_BOTH);
		tableData.verticalSpan = 2;
		dbTableViewer.getTree().setLayoutData(tableData);

		dbTableViewer.setInput(configuration.getDatabaseConnection());

		dbTableViewer.addCheckStateListener(this);
		
		numSelectedLabel = new Label(tableComposite, 0);
		tableData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		tableData.horizontalSpan = 2;
		numSelectedLabel.setLayoutData(tableData);		
	}

	private void initializeDatabaseValues() {
		dbConnectionCombo.removeAll();

		IConnectionProfile[] allProfiles = ProfileManager.getInstance().getProfiles();
		ArrayList<IConnectionProfile> profiles = new ArrayList<IConnectionProfile>();

		for (int profileCount = 0; profileCount < allProfiles.length; profileCount++) {
			IConnectionProfile profile = allProfiles[profileCount];
			Map factories = allProfiles[profileCount].getProvider().getConnectionFactories();
			if ((factories == null) || (!factories.containsKey("java.sql.Connection"))) {
				continue;
			} else {
				profiles.add(profile);
			}
		}

		if (profiles != null) {
			existingConnections = new Hashtable();
			sortConnections(profiles);
			Iterator connections = profiles.iterator();
			while (connections.hasNext()) {
				IConnectionProfile con = (IConnectionProfile) connections.next();
				existingConnections.put(con.getName(), con);
				dbConnectionCombo.add(con.getName());
			}
		}
	}

	private void sortConnections(java.util.List connections) {
		Comparator c = new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = ((IConnectionProfile) o1).getName();
				String s2 = ((IConnectionProfile) o2).getName();
				return s1.compareToIgnoreCase(s2);
			}
		};
		Collections.sort(connections, c);
	}

	private void validatePage() {
		databaseStatus.setOK();
		tableStatus.setOK();

		IConnectionProfile databaseConnection = configuration.getDatabaseConnection();
		List tableContents = configuration.getSelectedTables();

		String strMessage = null, strErrorMessage = null;

		validateDatabaseConnection(databaseConnection, databaseStatus);

		validateTables(tableContents, tableStatus);

		updateStatus(new IStatus[] { connectionStatus, databaseStatus, tableStatus });
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		fPageVisible = visible;

		if (visible && fCurrStatus.matches(IStatus.ERROR)) {
			StatusInfo status = new StatusInfo();
			status.setError(""); //$NON-NLS-1$
			fCurrStatus = status;
		}
		updateStatus(fCurrStatus);
	}

	private void validatePageConnections() {
		updateStatus(new IStatus[] { connectionStatus, databaseStatus, tableStatus });
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == dbConnectionCombo) {
			String connectionName = dbConnectionCombo.getText();

			updateDatabaseCombo(connectionName);

			if (confirmOverwrite)
				confirmOverwrite = false;

		} else if (e.getSource() == newDatabaseConnectionLink) {
			IConnectionProfile profile = createNewProfile();
			if (profile != null) {
				dbConnectionCombo.add(profile.getName());
				existingConnections.put(profile.getName(), profile);
				dbConnectionCombo.select(dbConnectionCombo.indexOf(profile.getName()));
				updateDatabaseCombo(dbConnectionCombo.getText());
			}

		}

		//EGLDataPartsPagesMainPage

		validatePage();

		if (e.getSource() == dbConnectionCombo || e.getSource() == newDatabaseConnectionLink) {
			validatePageConnections();
		}
	}

	protected IConnectionProfile createNewProfile() {
		AddProfileViewAction action = new AddProfileViewAction();
		action.run();

		return action.getAddedProfile();
	}
	
	private void updateDatabaseCombo(String connectionName) {
		final IConnectionProfile selectedConnection = ((IConnectionProfile)existingConnections.get(connectionName));
		configuration.setDatabaseConnection(selectedConnection); //This step must be done before inputChanged() is called
		
		if(selectedConnection != null) {
			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					monitor.beginTask(NewWizardMessages.FromSqlDatabasePage_RetrievingTablesTask, 1);
					
					if(selectedConnection!=null) {
						dbTableViewer.setInput(selectedConnection);
					}
						
					monitor.worked(1);
				}
			};
			try {
				new ProgressMonitorDialog(getControl().getShell()).run(false, false, runnable);
			} catch(InterruptedException exp) {
				
			} catch(InvocationTargetException exp) {
				
			}
		}
	}

	public void checkStateChanged(CheckStateChangedEvent event) {
		if (event.getSource() == dbTableViewer) {
			if (event.getChecked()) {
				// . . . check all its children
				dbTableViewer.setSubtreeChecked(event.getElement(), true);
			} else {
				dbTableViewer.setSubtreeChecked(event.getElement(), false);
			}

			handleTablesSelected();
		}

		validatePage();
	}

	private void handleTablesSelected() {
		List selectedTables = new ArrayList();
		for (Object o : dbTableViewer.getCheckedElements()) {
			if (o instanceof Table) {
				selectedTables.add(o);
			}
		}
		
		configuration.setSelectedTables(selectedTables);
		
		Integer tableSelected = new Integer(selectedTables.size());
		numSelectedLabel.setText(NewRecordWizardMessages.bind(NewWizardMessages.FromSqlDatabasePage_TablesSelected,new String[] {tableSelected.toString()}));
		//numSelectedLabel.setText(selectedTables.size() + " table(s) selected.");
		numSelectedLabel.getParent().layout();
	}

	protected void validateDatabaseConnection(IConnectionProfile dbConnection, StatusInfo status) {
		if (dbConnection == null) {
			status.setError(NewWizardMessages.FromSqlDatabasePage_Validation_NoConnection);
		}
	}

	protected void validateTables(List selectedTables, StatusInfo status) {
		if (selectedTables == null || selectedTables.isEmpty()) {
			status.setError(NewWizardMessages.FromSqlDatabasePage_Validation_NoTable);
		}
	}

	/**
	 * Updates the status line and the ok button according to the given status
	 * 
	 * @param status
	 *            status to apply
	 */
	protected void updateStatus(IStatus status) {
		fCurrStatus = status;
		setPageComplete(!status.matches(IStatus.ERROR));
		if (fPageVisible) {
			StatusUtil.applyToStatusLine(this, status);	
		}
	}

	/**
	 * Updates the status line and the ok button according to the status
	 * evaluate from an array of status. The most severe error is taken. In case
	 * that two status with the same severity exists, the status with lower
	 * index is taken.
	 * 
	 * @param status
	 *            the array of status
	 */
	protected void updateStatus(IStatus[] status) {
		updateStatus(StatusUtil.getMostSevere(status));
	}

	public class DatabaseTableLabelProvider implements ILabelProvider {

		private Image schemaImage;
		private Image tableImage;

		public DatabaseTableLabelProvider() {
			try {
				URL url = new URL(ImagePath.CORE_UI_ICONS_FOLDER_URL + ImagePath.TABLE);
				tableImage = ImageDescriptor.createFromURL(url).createImage();

				url = new URL(ImagePath.CORE_UI_ICONS_FOLDER_URL + ImagePath.SCHEMA);
				schemaImage = ImageDescriptor.createFromURL(url).createImage();
			} catch (Exception ex) {
			}
		}

		public void addListener(ILabelProviderListener listener) {

		}

		public void removeListener(ILabelProviderListener listener) {

		}

		public void dispose() {
			if (schemaImage != null)
				schemaImage.dispose();
			if (tableImage != null)
				tableImage.dispose();
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public Image getImage(Object element) {
			if (element instanceof Schema) {
				return schemaImage;
			} else if (element instanceof org.eclipse.datatools.modelbase.sql.tables.Table) {
				return tableImage;
			} else {
				return null;
			}
		}

		public String getText(Object element) {
			if (element instanceof org.eclipse.datatools.modelbase.sql.tables.Table) {
				String tableName = ((org.eclipse.datatools.modelbase.sql.tables.Table) element).getName();
				// TODO: need to figure out where bidisettings is declared
				// @bd1a start
				/*
				 * if (isBidi && null != bidiSettings.getOptions() &&
				 * (bidiSettings.getOptions().size() > 0)) { tableName =
				 * BidiUtils.bidiConvert(tableName, bidiSettings.getOptions());
				 * }
				 */
				// @bd1a end
				return tableName; //$NON-NLS-1$
			} else if (element instanceof Schema) {
				return ((Schema) element).getName();
			}
			return ""; //$NON-NLS-1$
		}
	}

	public class DatabaseTableContentProvider implements ITreeContentProvider {
		IConnectionProfile model = null;
		EList allSchemas;

		public Object[] getElements(Object inputElement) { //inputElement should be a ConnectionInfo (may be null)
			if (allSchemas != null) {
				return allSchemas.toArray();
			}
			
			return null;
		}

		@Override
		public Object[] getChildren(Object arg0) {
			List ret = new ArrayList();

			if (arg0 instanceof Schema) {
				Schema schema = (Schema) arg0;
				Iterator tableIterator = ((EList) schema.getTables()).iterator();
				while (tableIterator.hasNext()) {
					// may be a view
					Object potentialTable = tableIterator.next();
					if (potentialTable instanceof org.eclipse.datatools.modelbase.sql.tables.Table)
						ret.add(((org.eclipse.datatools.modelbase.sql.tables.Table) potentialTable));
				}
			}

			return ret.toArray();
		}

		@Override
		public Object getParent(Object arg0) {
			if (arg0 instanceof org.eclipse.datatools.modelbase.sql.tables.Table) {
				return ((org.eclipse.datatools.modelbase.sql.tables.Table) arg0).getSchema();
			} else {
				return null;
			}

		}

		@Override
		public boolean hasChildren(Object arg0) {
			// TODO Auto-generated method stub
			if (arg0 instanceof Schema) {
				Schema schema = (Schema) arg0;
				// return schema.getTables().size() > 0;
				return true;
			} else {
				return false;
			}
		}

		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if(newInput instanceof IConnectionProfile) {
				model = ((IConnectionProfile)newInput);
				
				updateTableModel();
			}
		}
		
		private void updateTableModel() {
			Database database = null;

			if (model != null) {
				try {
					if (ensureConnection()) {
						if (model.getConnectionState() == IConnectionProfile.CONNECTED_STATE) {
							IManagedConnection managedConnection = model.getManagedConnection(ConnectionUtil.CONNECTION_TYPE);
							if (managedConnection != null) {
								ConnectionInfo info = (ConnectionInfo) managedConnection.getConnection().getRawConnection();
								database = info.getSharedDatabase();
								// retrieve schemas, tables, and columns.
								EList schemas = getSchemas(database);
								allSchemas = new BasicEList();
								
								Iterator schemaInterator = schemas.listIterator();
								while (schemaInterator.hasNext()) {
									Schema schema = (Schema) schemaInterator.next();
									if (schema.getTables().size() > 0) {
										allSchemas.add(schema);
									}
								}		
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private EList getSchemas(Database database) {
			EList allSchemas = new BasicEList();
			EList catalogs = database.getCatalogs();
			if (catalogs != null && catalogs.size() > 0) {
				Iterator itCatalogs = catalogs.iterator();
	            while (itCatalogs.hasNext()){
	            	Catalog catalog = (Catalog) itCatalogs.next();
	                if (catalog.getSchemas() != null && catalog.getSchemas().size() > 0){
	                	for (Iterator it = catalog.getSchemas().iterator(); it.hasNext();)
	                    {
	                		allSchemas.add(it.next());
	                    }
	                }
				
	            }
			} else {
				EList schemas = database.getSchemas();
				if (schemas != null && schemas.size() > 0) {
					allSchemas.addAll(schemas);
				}
			}
			return allSchemas;
		}
	}

	public static String getSQLDatabaseVendorPreference(IConnectionProfile profile) {
		String name = "";
		if (profile != null) {
			name = (String) profile.getBaseProperties().get(IJDBCDriverDefinitionConstants.DATABASE_VENDOR_PROP_ID);
		}
		return name;
	}

	public static String getSQLJDBCDriverClassPreference(IConnectionProfile profile) {
		String name = "";
		if (profile != null) {
			name = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DRIVER_CLASS_PROP_ID);
		}
		return name;
	}

	public static String getSQLDatabasePreference(IConnectionProfile profile) {
		String name = "";
		if (profile != null) {
			name = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.DATABASE_NAME_PROP_ID);
		}
		return name;
	}

	public static String getSQLConnectionURLPreference(IConnectionProfile profile) {
		String name = "";
		if (profile != null) {
			name = profile.getBaseProperties().getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID);
		}
		return name;
	}

	private boolean ensureConnection() {
		boolean isConnected = false;
		IConnectionProfile profile = configuration.getDatabaseConnection();

		if (profile == null) {
			connectionStatus.setError(NewWizardMessages.FromSqlDatabasePage_Validation_UnableToConnect);
			isConnected = false;
		} else {
			if (profile.getConnectionState() == IConnectionProfile.CONNECTED_STATE) {
				isConnected = true;
			} else {
				IStatus status = RDBConnectionUtility.connectWithPromptIfNeeded(profile, SQLPlugin.getPlugin().getSQLPromptDialogOption());
				if (!status.isOK()) {
					connectionStatus.setError(NewWizardMessages.FromSqlDatabasePage_Validation_UnableToConnect);
				} else {
					isConnected = true;
				}
			}
		}

		if (isConnected) {
			connectionStatus.setOK();
		}
		return isConnected;
	}

}
