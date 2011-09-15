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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ConnectionProfile;
import org.eclipse.datatools.connectivity.ui.PingJob;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import java.util.Properties;
import org.eclipse.edt.compiler.internal.util.Encoder;

public class SQLDatabaseBindingDetailPage extends WebBindingDetailPage {

	private SQLDatabaseBinding fSQLDatabaseBinding;
	
	private Text fDbms;
	private Text fSqlDB;
	private Text fUserId;
	private Text fSqlJDBCDriverClass;
	private Text fSqlJNDIName;
	private Text fSqlPassword;
	private Text fDefaultSchema;
	private Text fConnLocation;
	//private Text fSqlValidationConnectionURL;
	
	protected Button btnPing;
	
	public SQLDatabaseBindingDetailPage(){
		super();
		nColumnSpan = 3;
	}
	
	protected Composite createDetailSection(Composite parent,
			FormToolkit toolkit, int sectionStyle, int columnSpan) {
		return createSection(parent, toolkit, SOAMessages.SQLDatabaseBindingDetailSecTitle, 
				SOAMessages.SQLDatabaseBindingDetailSecDescp, sectionStyle, columnSpan);
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createSQLControls(toolkit, parent);
	}
	

	private void createSQLControls(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, SOAMessages.LabelDbms);
		fDbms = createTextControl(toolkit, parent);
		fDbms.setEditable(false);
		fDbms.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setDbms(fDbms.getText());		
			}			
		});
		
		toolkit.createLabel(parent, SOAMessages.LabelSqlJDBCDriverClass);
		fSqlJDBCDriverClass = createTextControl(toolkit, parent);
		fSqlJDBCDriverClass.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlJDBCDriverClass(fSqlJDBCDriverClass.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlValidationConnectionURL);
		fSqlDB = createTextControl(toolkit, parent);
		fSqlDB.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlDB(fSqlDB.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlID);
		fUserId = createTextControl(toolkit, parent);
		fUserId.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlID(fUserId.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlPassword);
		fSqlPassword = createTextControl(toolkit, parent);
		fSqlPassword.setEchoChar('*'); //$NON-NLS-1$
		fSqlPassword.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlPassword(Encoder.encode(fSqlPassword.getText()));		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlSchema);
		fDefaultSchema = createTextControl(toolkit, parent);
		fDefaultSchema.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlSchema(fDefaultSchema.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlJNDIName);
		fSqlJNDIName = createTextControl(toolkit, parent);
		fSqlJNDIName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlJNDIName(fSqlJNDIName.getText());		
			}			
		});
		
		toolkit.createLabel(parent, SOAMessages.LabelSqlJarLists);
		fConnLocation = createTextControl(toolkit, parent);
		fConnLocation.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setJarList(fConnLocation.getText());
			}			
		});

		
		/*toolkit.createLabel(parent, SOAMessages.LabelSqlValidationConnectionURL);
		fSqlValidationConnectionURL = createTextControl(toolkit, parent);
		fSqlValidationConnectionURL.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlValidationConnectionURL(fSqlValidationConnectionURL.getText());		
			}			
		});*/
		
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
	}
	
	protected void testConnection() {
		String profileName = fDbms.getText();
		
		//String profileProviderID = "org.eclipse.datatools.connectivity.db.derby.embedded.connectionProfile";
		if(profileName != null) {
			 String[] profiles = profileName.split(" ");
			 String profileProviderID = EGLSQLUtility.getDBProviderID(profiles[0]);
			 String profileDescription = "";
			 String parentProfile = "";
			 boolean isAutoConnect = false;
			 ConnectionProfile profile = new ConnectionProfile(profileName, profileDescription,
						profileProviderID, parentProfile, isAutoConnect);
				
				profile.setBaseProperties(getProfileProperties(profiles));

				//getShell().getDisplay()
				BusyIndicator.showWhile( Display.getCurrent(), 
				        createTestConnectionRunnable( profile ) );	
		}
	}
	
	private Properties getProfileProperties(String[] profiles) {
		Properties copy = new Properties();
		
		copy.put("org.eclipse.datatools.connectivity.db.connectionProperties", "");
		copy.put("org.eclipse.datatools.connectivity.db.savePWD", false);
		if(fConnLocation.getText() != null) {
			copy.put("jarList", fConnLocation.getText().trim());
		}
		
		copy.put("org.eclipse.datatools.connectivity.db.username", fUserId.getText());
		copy.put("org.eclipse.datatools.connectivity.db.password", fSqlPassword.getText());
		copy.put("org.eclipse.datatools.connectivity.db.driverClass", fSqlJDBCDriverClass.getText());
		
		//copy.put("org.eclipse.datatools.connectivity.db.databaseName", fSqlDB.getText());
		if(fSqlDB.getText() != null) {
			copy.put("org.eclipse.datatools.connectivity.db.URL", fSqlDB.getText().trim());
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
			fSQLDatabaseBinding = (SQLDatabaseBinding) ssel.getFirstElement();
		else
			fSQLDatabaseBinding = null;
		update();
	}

	protected void update() {
		fNameText.setText(fSQLDatabaseBinding.getName() == null ? "" : fSQLDatabaseBinding.getName()); //$NON-NLS-1$
		String dbms = fSQLDatabaseBinding.getDbms();
		if (dbms != null)
			fDbms.setText(dbms);
		String sqldb = fSQLDatabaseBinding.getSqlDB();
		if (sqldb != null)
			fSqlDB.setText(sqldb);
		String sqlid = fSQLDatabaseBinding.getSqlID();
		if (sqlid != null)
			fUserId.setText(sqlid);
		String sqljdbcdriver = fSQLDatabaseBinding.getSqlJDBCDriverClass();
		if (sqljdbcdriver != null)
			fSqlJDBCDriverClass.setText(sqljdbcdriver);
		String sqljndiname = fSQLDatabaseBinding.getSqlJNDIName();
		if (sqljndiname != null)
			fSqlJNDIName.setText(sqljndiname);
		String sqlpassword = fSQLDatabaseBinding.getSqlPassword();
		if (sqlpassword != null && sqlpassword.trim().length() > 0 && Encoder.isEncoded(sqlpassword)) {
			fSqlPassword.setText(Encoder.decode(sqlpassword));
		} else if (sqlpassword != null) {
			fSqlPassword.setText(sqlpassword);
		}
		String sqlschema = fSQLDatabaseBinding.getSqlSchema();
		if (sqlschema != null)
			fDefaultSchema.setText(sqlschema);
		
		String jarLists = fSQLDatabaseBinding.getJarList();
		if(jarLists != null)
			fConnLocation.setText(jarLists);
		
		/*String sqlvalidation = fSQLDatabaseBinding.getSqlValidationConnectionURL();
		if (sqlvalidation != null)
			fSqlValidationConnectionURL.setText(sqlvalidation);*/
	}	
	
	protected void HandleNameChanged() {
		fSQLDatabaseBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
}
