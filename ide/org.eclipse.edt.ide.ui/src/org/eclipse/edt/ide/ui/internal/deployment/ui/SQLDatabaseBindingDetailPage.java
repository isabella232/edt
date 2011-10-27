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
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.datatools.connectivity.ui.PingJob;
import org.eclipse.edt.compiler.internal.util.Encoder;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Parameters;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SQLDatabaseBindingDetailPage extends WebBindingDetailPage {

	private Binding fSQLDatabaseBinding;
	
	private Button fUseURI;
	private Text fUri;
	
	private Button fUseDefinedInfo;
	private Text fDbms;
	private Text fSqlDB;
	private Text fUserId;
	private Text fSqlJDBCDriverClass;
	private Text fSqlPassword;
	private Text fDefaultSchema;
	private Text fConnLocation;
//	private Text fSqlValidationConnectionURL;
	
	private static final int indent = 20;
	
	protected Button btnPing;
	
	private List<Control> uriControls;
	private List<Control> definedControls;
	
	public SQLDatabaseBindingDetailPage(){
		super();
		nColumnSpan = 3;
		this.uriControls = new ArrayList<Control>();
		this.definedControls = new ArrayList<Control>();
	}
	
	protected Composite createDetailSection(Composite parent,
			FormToolkit toolkit, int sectionStyle, int columnSpan) {
		return createSection(parent, toolkit, SOAMessages.SQLDatabaseBindingDetailSecTitle, 
				SOAMessages.SQLDatabaseBindingDetailSecDescp, sectionStyle, columnSpan);
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createURIControls(toolkit, parent);
		createSQLControls(toolkit, parent);
	}
	
	private void createURIControls(FormToolkit toolkit, Composite parent) {
		int borderStyle = toolkit.getBorderStyle();
		uriControls.clear();
		
		fUseURI = toolkit.createButton(parent, SOAMessages.SQLDatabaseBindingUseURILabel, SWT.RADIO);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan;
		fUseURI.setLayoutData(gd);
		fUseURI.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (Control c : uriControls) {
					c.setEnabled(true);
				}
				for (Control c : definedControls) {
					c.setEnabled(false);
				}
				fSQLDatabaseBinding.setUseURI(true);
			}
		});
		
		Label l = toolkit.createLabel(parent, SOAMessages.LabelURI);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		uriControls.add(l);
		fUri = createTextControl(toolkit, parent);
		fUri.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setUri(fUri.getText());
			}			
		});
		uriControls.add(fUri);
		
		// At least on Linux, creating a text in the form draws a border no matter what. Workaround is to put it inside a composite.
		// In order to keep things aligned, we put just the individual Text controls in composites.
		toolkit.setBorderStyle(SWT.NULL);
		l = toolkit.createLabel(parent, SOAMessages.LabelJNDIExample);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		uriControls.add(l);
		Composite exampleComposite = toolkit.createComposite(parent);
		exampleComposite.setFont(parent.getFont());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan - 1;
		exampleComposite.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		exampleComposite.setLayout(layout);
		uriControls.add(exampleComposite);
		Text example = toolkit.createText(exampleComposite, "jndi://jdbc/SAMPLE", SWT.READ_ONLY|SWT.SINGLE); //$NON-NLS-1$
		example.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		uriControls.add(example);
		
		l = toolkit.createLabel(parent, SOAMessages.LabelWorkspaceExample);
		gd = new GridData();
		gd.horizontalIndent = indent;
		l.setLayoutData(gd);
		uriControls.add(l);
		exampleComposite = toolkit.createComposite(parent);
		exampleComposite.setFont(parent.getFont());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan - 1;
		exampleComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		exampleComposite.setLayout(layout);
		uriControls.add(exampleComposite);
		example = toolkit.createText(exampleComposite, "workspace://myConnectionProfile", SWT.READ_ONLY|SWT.SINGLE); //$NON-NLS-1$
		example.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		uriControls.add(example);
		toolkit.setBorderStyle(borderStyle); // reset previous style
		
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
				for (Control c : definedControls) {
					c.setEnabled(true);
				}
				for (Control c : uriControls) {
					c.setEnabled(false);
				}
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

			public void widgetSelected(SelectionEvent e) {
				testConnection();
			}
		});
		GridData pingGD = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.FILL_HORIZONTAL);
		pingGD.horizontalSpan = 3;
		btnPing.setLayoutData(pingGD);
		btnPing.setText(SOAMessages.SQLDatabaseBindingDetailPageTestConnection); //$NON-NLS-1$
		definedControls.add(btnPing);
	}
	
	protected void testConnection() {
		String profileName = fDbms.getText();
		
		//String profileProviderID = "org.eclipse.datatools.connectivity.db.derby.embedded.connectionProfile";
		if(profileName != null) {
			 String profileProviderID = EGLSQLUtility.getConnectionProviderProfile(profileName);
			 String profileDescription = ""; //$NON-NLS-1$
			 String parentProfile = ""; //$NON-NLS-1$
			 boolean isAutoConnect = false;
			 ConnectionProfile profile = new ConnectionProfile(profileName, profileDescription,
						profileProviderID, parentProfile, isAutoConnect);
				
				profile.setBaseProperties(getConnectionProfileProperties());

				//getShell().getDisplay()
				BusyIndicator.showWhile( Display.getCurrent(), 
				        createTestConnectionRunnable( profile ) );	
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
		if (ssel.size() == 1)
			fSQLDatabaseBinding = (Binding) ssel.getFirstElement();
		else
			fSQLDatabaseBinding = null;
		update();
	}

	protected void update() {
		fNameText.setText(fSQLDatabaseBinding.getName() == null ? "" : fSQLDatabaseBinding.getName()); //$NON-NLS-1$
		
		String uri = fSQLDatabaseBinding.getUri();
		if (uri != null) {
			fUri.setText(fSQLDatabaseBinding.getUri());
		}
		
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
		}
		
		if (fSQLDatabaseBinding.isUseURI()) {
			fUseURI.setSelection(true);
			fUseDefinedInfo.setSelection(false);
			
			for (Control c : definedControls) {
				c.setEnabled(false);
			}
		}
		else {
			fUseURI.setSelection(false);
			fUseDefinedInfo.setSelection(true);
			
			for (Control c : uriControls) {
				c.setEnabled(false);
			}
		}
	}	
	
	protected void HandleNameChanged() {
		fSQLDatabaseBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
}
