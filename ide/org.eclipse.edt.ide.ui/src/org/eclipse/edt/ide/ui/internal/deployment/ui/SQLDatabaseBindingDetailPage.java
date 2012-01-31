/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener1;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.datatools.connectivity.ui.PingJob;
import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SQLDatabaseBindingDetailPage extends WebBindingDetailPage implements IProfileListener1 {
	
	private static final int indent = 20;
	
	private Binding fSQLDatabaseBinding;
	
	private Button fUseWorkspace;
	private Button fUseJndi;
	private Button fUseDefinedInfo;
	private Button fDeployAsJndi;
	private Button btnPing;
	private Combo workspaceCombo;
	private Text fJndiName;
	private Text fJndiUser;
	private Text fJndiPassword;
	private Text fDeployAsJndiName;
	private Text fDbms;
	private Text fSqlDB;
	private Text fUserId;
	private Text fSqlJDBCDriverClass;
	private Text fSqlPassword;
	private Text fDefaultSchema;
	private Text fConnLocation;
//	private Text fSqlValidationConnectionURL;
	
	private List<Control> workspaceControls;
	private List<Control> jndiControls;
	private List<Control> definedControls;
	private List<Control> deployAsJndiControls;
	
	/**
	 * When adding a "<does not exist>" profile to the combo, we need to know the actual profile name.
	 * Since this is a translated string we can't just substring the combo's value.
	 */
	private String nonExistantProfile;
	
	public SQLDatabaseBindingDetailPage() {
		super();
		nColumnSpan = 3;
		this.workspaceControls = new ArrayList<Control>();
		this.jndiControls = new ArrayList<Control>();
		this.definedControls = new ArrayList<Control>();
		this.deployAsJndiControls = new ArrayList<Control>();
	}
	
	protected Composite createDetailSection(Composite parent,
			FormToolkit toolkit, int sectionStyle, int columnSpan) {
		return createSection(parent, toolkit, SOAMessages.SQLDatabaseBindingDetailSecTitle, 
				SOAMessages.SQLDatabaseBindingDetailSecDescp, sectionStyle, columnSpan);
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createWorkspaceControls(toolkit, parent);
		createSQLControls(toolkit, parent);
		createJNDIControls(toolkit, parent);
		createDeployAsJNDIControls(toolkit, parent);
		
		ProfileManager.getInstance().addProfileListener(this);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		ProfileManager.getInstance().removeProfileListener(this);
	}
	
	private void createWorkspaceControls(FormToolkit toolkit, Composite parent) {
		workspaceControls.clear();
		
		fUseWorkspace = toolkit.createButton(parent, SOAMessages.LabelSqlUseWorkspace, SWT.RADIO);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan;
		fUseWorkspace.setLayoutData(gd);
		fUseWorkspace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				workspaceUriSelected();
				fSQLDatabaseBinding.setUseURI(true);
				setWorkspaceUriInModel();
			}
		});
		
		workspaceCombo = new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
		toolkit.adapt(workspaceCombo);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan - 1;
		gd.horizontalIndent = indent;
		workspaceCombo.setLayoutData(gd);
		workspaceControls.add(workspaceCombo);
		workspaceCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setWorkspaceUriInModel();
			}
		});
		
		Button newConnection = toolkit.createButton(parent, SOAMessages.NewLabel, SWT.PUSH);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalIndent = indent;
		newConnection.setLayoutData(gd);
		workspaceControls.add(newConnection);
		newConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IConnectionProfile newProfile = EGLSQLUtility.createNewProfile();
				if (newProfile != null) {
					fSQLDatabaseBinding.setUri("workspace://" + newProfile.getName());
					updateCombo();
				}
			}
		});
		
		createSpacer(toolkit, parent, nColumnSpan);
	}
	
	private void createSQLControls(FormToolkit toolkit, Composite parent) {
		definedControls.clear();
		
		fUseDefinedInfo = toolkit.createButton(parent, SOAMessages.SqlDatabaseBindingSpecifyInfoLabel, SWT.RADIO);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan;
		fUseDefinedInfo.setLayoutData(gd);
		fUseDefinedInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				userDefinedSelected();
				fSQLDatabaseBinding.setUseURI(false);
			}
		});
		
		Label l = toolkit.createLabel(parent, SOAMessages.LabelDbms);
		definedControls.add(l);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		fDbms = createTextControl(toolkit, parent);
		fDbms.setEditable(false);
		fDbms.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_dbms, fDbms.getText());
			}			
		});
		definedControls.add(fDbms);
		
		l = toolkit.createLabel(parent, SOAMessages.LabelSqlJDBCDriverClass);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		definedControls.add(l);
		fSqlJDBCDriverClass = createTextControl(toolkit, parent);
		fSqlJDBCDriverClass.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlJDBCDriverClass, fSqlJDBCDriverClass.getText());
			}			
		});
		definedControls.add(fSqlJDBCDriverClass);

		l = toolkit.createLabel(parent, SOAMessages.LabelSqlValidationConnectionURL);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		definedControls.add(l);
		fSqlDB = createTextControl(toolkit, parent);
		fSqlDB.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlDB, fSqlDB.getText());
			}			
		});
		definedControls.add(fSqlDB);

		l = toolkit.createLabel(parent, SOAMessages.LabelSqlID);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		definedControls.add(l);
		fUserId = createTextControl(toolkit, parent);
		fUserId.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlID, fUserId.getText());
			}			
		});
		definedControls.add(fUserId);

		l = toolkit.createLabel(parent, SOAMessages.LabelSqlPassword);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		definedControls.add(l);
		fSqlPassword = createTextControl(toolkit, parent);
		fSqlPassword.setEchoChar('*'); //$NON-NLS-1$
		fSqlPassword.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlPassword, Encoder.encode(fSqlPassword.getText()));
			}			
		});
		definedControls.add(fSqlPassword);

		l = toolkit.createLabel(parent, SOAMessages.LabelSqlSchema);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		definedControls.add(l);
		fDefaultSchema = createTextControl(toolkit, parent);
		fDefaultSchema.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlSchema, fDefaultSchema.getText());
			}			
		});
		definedControls.add(fDefaultSchema);

		l = toolkit.createLabel(parent, SOAMessages.LabelSqlJarLists);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		definedControls.add(l);
		fConnLocation = createTextControl(toolkit, parent);
		fConnLocation.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jarList, fConnLocation.getText());
			}
		});
		definedControls.add(fConnLocation);

		/*
		l = toolkit.createLabel(parent, SOAMessages.LabelSqlValidationConnectionURL);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		definedControls.add(l);
		fSqlValidationConnectionURL = createTextControl(toolkit, parent);
		fSqlValidationConnectionURL.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlValidationConnectionURL(fSqlValidationConnectionURL.getText());
			}
		});
		definedControls.add(fSqlValidationConnectionURL);
		*/
		
		btnPing = new Button(parent, SWT.NONE);
		btnPing.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testConnection();
			}
		});
		GridData pingGD = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.FILL_HORIZONTAL);
		pingGD.horizontalSpan = 3;
		btnPing.setLayoutData(pingGD);
		btnPing.setText(SOAMessages.SQLDatabaseBindingDetailPageTestConnection); //$NON-NLS-1$
		definedControls.add(btnPing);
		
		createSpacer(toolkit, parent, nColumnSpan);
	}
	
	private void createJNDIControls(FormToolkit toolkit, Composite parent) {
		jndiControls.clear();
		
		fUseJndi = toolkit.createButton(parent, SOAMessages.LabelSqlUseJndi, SWT.RADIO);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan;
		fUseJndi.setLayoutData(gd);
		fUseJndi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				jndiUriSelected();
				fSQLDatabaseBinding.setUseURI(true);
				fSQLDatabaseBinding.setUri("jndi://" + fJndiName.getText());
			}
		});
		
		Label l = toolkit.createLabel(parent, SOAMessages.LabelSqlJndiName);
		jndiControls.add(l);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		fJndiName = createTextControl(toolkit, parent);
		fJndiName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setUri("jndi://" + fJndiName.getText());
			}			
		});
		jndiControls.add(fJndiName);
		
		l = toolkit.createLabel(parent, SOAMessages.LabelSqlJndiUser);
		jndiControls.add(l);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		fJndiUser = createTextControl(toolkit, parent);
		fJndiUser.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jndiUser, fJndiUser.getText());
			}			
		});
		jndiControls.add(fJndiUser);
		
		l = toolkit.createLabel(parent, SOAMessages.LabelSqlJndiPassword);
		jndiControls.add(l);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		fJndiPassword = createTextControl(toolkit, parent);
		fJndiPassword.setEchoChar('*'); //$NON-NLS-1$
		fJndiPassword.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jndiPassword, fJndiPassword.getText());
			}			
		});
		jndiControls.add(fJndiPassword);
		
		createSpacer(toolkit, parent, nColumnSpan);
	}
	
	private void createDeployAsJNDIControls(FormToolkit toolkit, Composite parent) {
		deployAsJndiControls.clear();
		
		fDeployAsJndi = toolkit.createButton(parent, SOAMessages.LabelSqlDeployAsJndi, SWT.CHECK|SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan;
		fDeployAsJndi.setLayoutData(gd);
		fDeployAsJndi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDeployAsControls();
			}
		});
		
		Label l = toolkit.createLabel(parent, SOAMessages.LabelSqlDeployAsJndiDesc);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan;
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		deployAsJndiControls.add(l);
		
		l = toolkit.createLabel(parent, SOAMessages.LabelSqlJndiName);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		deployAsJndiControls.add(l);
		fDeployAsJndiName = createTextControl(toolkit, parent);
		fDeployAsJndiName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fSQLDatabaseBinding), SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jndiName, fDeployAsJndiName.getText());
			}			
		});
		deployAsJndiControls.add(fDeployAsJndiName);
	}
	
	protected void testConnection() {
		String vendorName = fDbms.getText();
		
		String profileProviderID = null;
		if(vendorName != null && vendorName.length() > 0) {
			profileProviderID = EGLSQLUtility.getConnectionProviderProfile(vendorName);
		}
		
		// The data tools 'test connection' operation will throw an NPE if there is no provider ID.
		if (profileProviderID != null) {
			String profileDescription = ""; //$NON-NLS-1$
			String parentProfile = ""; //$NON-NLS-1$
			boolean isAutoConnect = false;
			ConnectionProfile profile = new ConnectionProfile(vendorName, profileDescription,
						profileProviderID, parentProfile, isAutoConnect);
				
			profile.setBaseProperties(getConnectionProfileProperties());

			BusyIndicator.showWhile( Display.getCurrent(), createTestConnectionRunnable( profile ) );	
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), SOAMessages.SQLDataBaseBindingTestConnectionNoVendorTitle,
					NLS.bind(SOAMessages.SQLDataBaseBindingTestConnectionNoVendorMsg, vendorName == null ? "" : vendorName));
		}
	}
	
	private Properties getConnectionProfileProperties() {
		Properties copy = new Properties();
		
		copy.put("org.eclipse.datatools.connectivity.db.connectionProperties", ""); //$NON-NLS-1$ //$NON-NLS-2$
		copy.put("org.eclipse.datatools.connectivity.db.savePWD", false); //$NON-NLS-1$
		if(fConnLocation.getText() != null) {
			copy.put("jarList", fConnLocation.getText().trim()); //$NON-NLS-1$
		}
		
		copy.put("org.eclipse.datatools.connectivity.db.username", fUserId.getText()); //$NON-NLS-1$
		copy.put("org.eclipse.datatools.connectivity.db.password", fSqlPassword.getText()); //$NON-NLS-1$
		copy.put("org.eclipse.datatools.connectivity.db.driverClass", fSqlJDBCDriverClass.getText()); //$NON-NLS-1$
		
		//copy.put("org.eclipse.datatools.connectivity.db.databaseName", fSqlDB.getText());
		if(fSqlDB.getText() != null) {
			copy.put("org.eclipse.datatools.connectivity.db.URL", fSqlDB.getText().trim()); //$NON-NLS-1$
		}
		
		return copy;
	}
	
	protected Runnable createTestConnectionRunnable( final IConnectionProfile profile ) {
        final Job pingJob = new PingJob( Display.getCurrent().getActiveShell(), profile );
        pingJob.schedule();
        return new Runnable() {
            public void run() {
                try {
                    pingJob.join();
                }
                catch (InterruptedException e) {
                }
            }
        };
	}
	
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() == 1) {
			fSQLDatabaseBinding = (Binding) ssel.getFirstElement();
		}
		else {
			fSQLDatabaseBinding = null;
		}
		update();
	}

	protected void update() {
		fNameText.setText(fSQLDatabaseBinding.getName() == null ? "" : fSQLDatabaseBinding.getName()); //$NON-NLS-1$
		
		Parameters params = fSQLDatabaseBinding.getParameters();
		if (params != null) {
			String dbms = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_dbms);
			if (dbms != null) {
				fDbms.setText(dbms);
			}
			String sqldb = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlDB);
			if (sqldb != null) {
				fSqlDB.setText(sqldb);
			}
			String sqlid = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlID);
			if (sqlid != null) {
				fUserId.setText(sqlid);
			}
			String sqljdbcdriver = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlJDBCDriverClass);
			if (sqljdbcdriver != null) {
				fSqlJDBCDriverClass.setText(sqljdbcdriver);
			}
			String sqlpassword = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlPassword);
			if (sqlpassword != null && sqlpassword.trim().length() > 0 && Encoder.isEncoded(sqlpassword)) {
				fSqlPassword.setText(Encoder.decode(sqlpassword));
			} else if (sqlpassword != null) {
				fSqlPassword.setText(sqlpassword);
			}
			String sqlschema = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlSchema);
			if (sqlschema != null) {
				fDefaultSchema.setText(sqlschema);
			}
			
			String jarLists = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jarList);
			if(jarLists != null) {
				fConnLocation.setText(jarLists);
			}
			
//			String sqlvalidation = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_sqlValidationConnectionURL);
//			if (sqlvalidation != null)
//				fSqlValidationConnectionURL.setText(sqlvalidation);
			
			String jndiName = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jndiName);
			if(jndiName != null) {
				fDeployAsJndiName.setText(jndiName);
			}
			
			String jndiUser = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jndiUser);
			if(jndiUser != null) {
				fJndiUser.setText(jndiUser);
			}
			
			String jndiPass = EGLDDRootHelper.getParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_jndiPassword);
			if(jndiPass != null) {
				fJndiPassword.setText(jndiPass);
			}
			
			fDeployAsJndi.setSelection(EGLDDRootHelper.getBooleanParameterValue(params, SQLDatabaseBinding.ATTRIBUTE_BINDING_SQL_deployAsJndi));
		}
		
		updateDeployAsControls(); // it's possible this doesn't get invoked below
		
		String uri = fSQLDatabaseBinding.getUri();
		if (uri != null) {
			if (uri.startsWith("workspace://")) {
				if (fSQLDatabaseBinding.isUseURI()) {
					fUseWorkspace.setSelection(true);
					workspaceUriSelected();
				}
			}
			else if (uri.startsWith("jndi://")) {
				fJndiName.setText(uri.substring(7));
				if (fSQLDatabaseBinding.isUseURI()) {
					fUseJndi.setSelection(true);
					jndiUriSelected();
				}
			}
		}
			
		if (!fSQLDatabaseBinding.isUseURI()) {
			fUseDefinedInfo.setSelection(true);
			userDefinedSelected();
		}
		
		updateCombo();
	}
	
	protected void HandleNameChanged() {
		fSQLDatabaseBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
	
	protected void setWorkspaceUriInModel() {
		String selection = workspaceCombo.getText();
		if (nonExistantProfile != null && selection.equals(NLS.bind(SOAMessages.SqlConnectionProfileDoesntExist, nonExistantProfile))) {
			selection = nonExistantProfile;
		}
		fSQLDatabaseBinding.setUri("workspace://" + selection);
	}
	
	protected void updateCombo() {
		nonExistantProfile = null;
		
		IConnectionProfile[] profiles = ProfileManager.getInstance().getProfiles();
		List <String> names = new ArrayList<String>(profiles.length + 1);
		for (int i = 0; i < profiles.length; i++) {
			names.add(profiles[i].getName());
		}
		
		int selectionIndex = -1;
		String nameToSelect = null;
		String uri = fSQLDatabaseBinding.getUri();
		if (uri != null && uri.startsWith("workspace://")) {
			nameToSelect = uri.substring(12);
		}
		
		if (nameToSelect != null) {
			for (int i = 0; i < names.size(); i++) {
				if (nameToSelect.equals(names.get(i))) {
					selectionIndex = i;
					break;
				}
			}
			if (selectionIndex == -1) {
				nonExistantProfile = nameToSelect;;
				names.add(NLS.bind(SOAMessages.SqlConnectionProfileDoesntExist, nameToSelect));
				selectionIndex = names.size() - 1;
			}
		}
		
		workspaceCombo.setItems(names.toArray(new String[names.size()]));
		if (selectionIndex > -1) {
			workspaceCombo.select(selectionIndex);
		}
		else if (names.size() > 0) {
			workspaceCombo.select(0);
		}
	}
	
	private void updateDeployAsControls() {
		boolean state = fDeployAsJndi.getSelection() && fDeployAsJndi.getEnabled();
		for (Control c : deployAsJndiControls) {
			c.setEnabled(state);
		}
	}
	
	private void workspaceUriSelected() {
		for (Control c : definedControls) {
			c.setEnabled(false);
		}
		for (Control c : jndiControls) {
			c.setEnabled(false);
		}
		for (Control c : workspaceControls) {
			c.setEnabled(true);
		}
		
		fDeployAsJndi.setEnabled(true);
		updateDeployAsControls();
	}
	
	private void jndiUriSelected() {
		for (Control c : definedControls) {
			c.setEnabled(false);
		}
		for (Control c : jndiControls) {
			c.setEnabled(true);
		}
		for (Control c : workspaceControls) {
			c.setEnabled(false);
		}
		
		fDeployAsJndi.setEnabled(false);
		updateDeployAsControls();
	}
	
	private void userDefinedSelected() {
		for (Control c : definedControls) {
			c.setEnabled(true);
		}
		for (Control c : jndiControls) {
			c.setEnabled(false);
		}
		for (Control c : workspaceControls) {
			c.setEnabled(false);
		}
		
		fDeployAsJndi.setEnabled(true);
		updateDeployAsControls();
	}
	
	@Override
	public void profileAdded(IConnectionProfile profile) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateCombo();
			}
		});
	}
	
	@Override
	public void profileDeleted(IConnectionProfile profile) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateCombo();
			}
		});
	}
	
	@Override
	public void profileChanged(IConnectionProfile profile) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateCombo();
			}
		});
	}
	
	@Override
	public void profileChanged(final IConnectionProfile profile, final String oldName, String oldDesc, Boolean oldAutoConnect) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// If the model's workspace profile is what changed, update the model.
				String uri = fSQLDatabaseBinding.getUri();
				if (uri != null && uri.startsWith("workspace://")) {
					String name = uri.substring(12);
					if (name.equals(oldName)) {
						fSQLDatabaseBinding.setUri("workspace://" + profile.getName());
					}
				}
				updateCombo();
			}
		});
	}
}
