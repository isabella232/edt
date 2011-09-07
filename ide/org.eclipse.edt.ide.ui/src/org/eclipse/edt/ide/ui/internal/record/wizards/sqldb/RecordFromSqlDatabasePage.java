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

package org.eclipse.edt.ide.ui.internal.record.wizards.sqldb;

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

public class RecordFromSqlDatabasePage extends WizardPage implements SelectionListener, ICheckStateListener {

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

	private RecordFromSqlDatabaseWizardConfiguration configuration;
	private Hashtable existingConnections;

	// @bd1a start
	// Bidi Properties
	// private Button bidiSettingsButton;
	// static BidiPropertiesComposite bidiSettings;
	// private HashMap dbAttributes = null;
	// @bd1a end
	// private DBContentBidiFormatComposite dbBidiSettings; //@bd2a

	protected boolean isBidi = false; // @bd1a

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RecordFromSqlDatabasePage(RecordFromSqlDatabaseWizardConfiguration config) {
		super(RecordFromSqlDatabasePage.class.getName());
		setTitle(NewRecordWizardMessages.RecordFromSqlDatabasePage_Title);
		setDescription(NewRecordWizardMessages.RecordFromSqlDatabasePage_Description);

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

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
		// IEGLMDDUIHelpConstants.EGL_DPP_MAIN_PAGE);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		createDatabaseControls(composite);
		// @bd1a start
		if (configuration instanceof RecordFromSqlDatabaseWizardConfigurationBidi) {
			// creatBidiGroup(composite);
		}
		// @bd1a end
		createTableControls(composite);
		// createTableButtons(composite);

		qualifyTableNamesCheckbox = new Button(composite, SWT.CHECK);
		qualifyTableNamesCheckbox.setText(NewRecordWizardMessages.RecordFromSqlDatabasePage_QualifyTableNames);
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
		saveConnectionToDDCheckbox.setText(NewRecordWizardMessages.RecordFromSqlDatabasePage_SaveConnectionToDD);
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

	// @bd1a start
	/**
	 * Creates the bidi group composite.
	 * 
	 * @param composite
	 *            the parent composite
	 */
	// public void creatBidiGroup(Composite composite) {
	//
	// //@bd2a Start
	// Group mainPageBidiGroup = new Group(composite, SWT.NONE);
	// mainPageBidiGroup.setText(WizardMessages.bidiAttributes_Label_BidiMainGroup);
	//
	// GridLayout bidiGroupLayout = new GridLayout();
	// bidiGroupLayout.numColumns = 1;
	// GridData bidiGroupData = new GridData(GridData.FILL_HORIZONTAL);
	// bidiGroupData.horizontalSpan = 1;
	//
	// mainPageBidiGroup.setLayout(bidiGroupLayout);
	// mainPageBidiGroup.setLayoutData(bidiGroupData);
	// //@bd2a End
	//
	// bidiSettingsButton = new Button(mainPageBidiGroup, SWT.CHECK); //@bd2c
	// bidiSettingsButton.setText(WizardMessages.bidiAttributes_bidirectionalSettings);
	// bidiSettingsButton.setToolTipText(WizardMessages.bidiAttributes_bidirectionalSettings);
	// bidiSettingsButton.addSelectionListener(this);
	//
	// Composite mainPageBidiComposite= new Composite(mainPageBidiGroup,
	// SWT.NONE);
	// mainPageBidiComposite.setFont(composite.getFont());
	//
	// GridLayout bidiLayout = new GridLayout();
	// bidiLayout.numColumns = 2; //1; //@bd2c
	// GridData bidiData = new GridData(GridData.FILL_BOTH |
	// GridData.VERTICAL_ALIGN_BEGINNING); bidiData.horizontalSpan = 1; //@bd2c
	// bidiData.verticalAlignment = SWT.TOP;
	//
	// mainPageBidiComposite.setLayout(bidiLayout);
	// mainPageBidiComposite.setLayoutData(bidiData);
	//
	// bidiSettings = new BidiPropertiesComposite(mainPageBidiComposite,
	// SWT.NONE, configuration);
	// dbBidiSettings = new DBContentBidiFormatComposite(mainPageBidiComposite,
	// SWT.NONE, configuration); //@bd2a
	//
	// }
	// @bd1a end

	public IWizardPage getNextPage() {
		IWizardPage page = super.getNextPage();

		// @bd1a start
		// Set the selected tables bidi attributes according to the current set
		// of database attributes
		// if (isBidi) {
		// setBidiAttributesForSelectedTables();
		// //@bd2a Start
		// if(configuration!=null &&
		// dbBidiSettings.isBidiSettingsButtonSelected()){
		// if(dbBidiSettings.getBidiContentHashMap()!=null &&
		// dbBidiSettings.getBidiContentHashMap().size()>0)
		// configuration.setBidiContentHashMap(dbBidiSettings.getBidiContentHashMap());
		// if (dbBidiSettings.isClientVisual()){
		// configuration.removeBct(RecordFromSqlDatabaseWizardConfiguration.DB_KEY);
		// configuration.setClientVisual(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// true);
		// configuration.setDBVisual(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// dbBidiSettings.isDBVisual());
		// configuration.setDBRTL(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// dbBidiSettings.isDBRTLDirection());
		// configuration.setDBSymSwap(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// dbBidiSettings.isDBSymSwap());
		// configuration.setDBNumSwap(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// dbBidiSettings.isDBNumSwap());
		// configuration.setClientRTL(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// dbBidiSettings.isClientRTLDirection());
		// }
		// else if(dbBidiSettings.getBct()!=null){
		// configuration.setClientVisual(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// false);
		// configuration.setBct(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// dbBidiSettings.getBct());
		// }
		// }else{
		// configuration.setClientVisual(RecordFromSqlDatabaseWizardConfiguration.DB_KEY,
		// false);
		// configuration.removeBct(RecordFromSqlDatabaseWizardConfiguration.DB_KEY);
		// }
		// //@bd2a End
		// }
		// @bd1a end

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
		dbLabel.setText(NewRecordWizardMessages.RecordFromSqlDatabasePage_DBConnectionlabel);

		dbConnectionCombo = new Combo(dbComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		dbConnectionCombo.setLayoutData(gd);
		dbConnectionCombo.addSelectionListener(this);

		new Label(dbComposite, SWT.NONE);

		newDatabaseConnectionLink = new Link(dbComposite, SWT.PUSH);
		newDatabaseConnectionLink.setText(NewRecordWizardMessages.RecordFromSqlDatabasePage_CreateDBLink);
																				
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
		tableLabel.setText(NewRecordWizardMessages.RecordFromSqlDatabasePage_TablesLabel);

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
					monitor.beginTask(NewRecordWizardMessages.RecordFromSqlDatabasePage_RetrievingTablesTask, 1);
					
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
		numSelectedLabel.setText(NewRecordWizardMessages.bind(NewRecordWizardMessages.RecordFromSqlDatabasePage_TablesSelected,new String[] {tableSelected.toString()}));
		//numSelectedLabel.setText(selectedTables.size() + " table(s) selected.");
		numSelectedLabel.getParent().layout();
	}

	// @bd1a start
	/**
	 * Sets the bidi attributes for selected tables.
	 */
	// private void setBidiAttributesForSelectedTables() {
	// if (isBidi) {
	// if (null != bidiSettings)
	// dbAttributes = bidiSettings.getOptions();
	//
	// List selectedTablesList = configuration.getSelectedTables();
	// if (null != selectedTablesList && (selectedTablesList.size() > 0)) {
	// if (null != dbAttributes /*&& (dbAttributes.size() > 0)*/) { //@bd3c
	// HashMap newTableAttrs = new HashMap(dbAttributes);
	// newTableAttrs.put("TableInherits", "true");
	// for (int i=0; i<selectedTablesList.size(); i++) {
	// if (selectedTablesList.get(i) instanceof
	// org.eclipse.datatools.modelbase.sql.tables.Table) {
	// // Set the bidirectional options for the tables that inherits the
	// database attributes
	// // and for all tables that don't have attributes defined for them.
	// org.eclipse.datatools.modelbase.sql.tables.Table tbl =
	// (org.eclipse.datatools.modelbase.sql.tables.Table)
	// selectedTablesList.get(i);
	// if(!configuration.getMetadataInherit(tbl.getName()).equalsIgnoreCase("false")){
	// //@bd2a
	// HashMap tblAttrs = (HashMap)
	// configuration.getBidiAttributes(tbl.getName());
	// if ((null == tblAttrs || tblAttrs.size()==0) // Table does not have any
	// bidirectional attributes //@bd2c
	// || (null != tblAttrs && tblAttrs.containsKey("TableInherits"))) { //
	// Table inherits
	// //@bd3a Start
	// boolean disable =false;
	// if(configuration.isDisableMetadata(tbl.getName()))
	// disable = true;
	// //@bd3a End
	// configuration.setBidiAttributes(tbl.getName(), newTableAttrs);
	// //@bd3a Start
	// if(disable)
	// configuration.setDisableMetadata(tbl.getName(), true);
	// //@bd3a End
	// }
	// }//@bd2a
	// }
	// }
	// }
	// /*@bd3d Start
	// else {
	// for (int i=0; i<selectedTablesList.size(); i++) {
	// if (selectedTablesList.get(i) instanceof
	// org.eclipse.datatools.modelbase.sql.tables.Table) {
	// // Set the bidirectional options for the tables that inherits the
	// database attributes
	// // and for all tables that don't have attributes defined for them.
	// org.eclipse.datatools.modelbase.sql.tables.Table tbl =
	// (org.eclipse.datatools.modelbase.sql.tables.Table)
	// selectedTablesList.get(i);
	// if(!configuration.getMetadataInherit(tbl.getName()).equalsIgnoreCase("false")){
	// //@bd2a
	// HashMap tblAttrs = (HashMap)
	// configuration.getBidiAttributes(tbl.getName());
	// if (null != tblAttrs && tblAttrs.containsKey("TableInherits")) { // Table
	// inherits
	// configuration.setBidiAttributes(tbl.getName(), null);
	// }
	// }//@bd2a
	// }
	// }
	// }
	// */ //@bd3d End
	// }
	// }
	// }
	// //@bd1a end

	protected void validateDatabaseConnection(IConnectionProfile dbConnection, StatusInfo status) {
		if (dbConnection == null) {
			status.setError(NewRecordWizardMessages.RecordFromSqlDatabasePage_Validation_NoConnection);
		}
	}

	protected void validateTables(List selectedTables, StatusInfo status) {
		if (selectedTables == null || selectedTables.isEmpty()) {
			status.setError(NewRecordWizardMessages.RecordFromSqlDatabasePage_Validation_NoTable);
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
			connectionStatus.setError(NewRecordWizardMessages.RecordFromSqlDatabasePage_Validation_UnableToConnect);
			isConnected = false;
		} else {
			if (profile.getConnectionState() == IConnectionProfile.CONNECTED_STATE) {
				isConnected = true;
			} else {
				IStatus status = RDBConnectionUtility.connectWithPromptIfNeeded(profile, SQLPlugin.getPlugin().getSQLPromptDialogOption());
				if (!status.isOK()) {
					connectionStatus.setError(NewRecordWizardMessages.RecordFromSqlDatabasePage_Validation_UnableToConnect);
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
