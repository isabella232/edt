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

import org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SQLDatabaseBindingDetailPage extends WebBindingDetailPage {

	private SQLDatabaseBinding fSQLDatabaseBinding;
	
	private Text fDbms;
	private Text fSqlDB;
	private Text fSqlID;
	private Text fSqlJDBCDriverClass;
	private Text fSqlJNDIName;
	private Text fSqlPassword;
	private Text fSqlSchema;
	private Text fSqlValidationConnectionURL;
	
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
		fDbms.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setDbms(fDbms.getText());		
			}			
		});
		
		toolkit.createLabel(parent, SOAMessages.LabelSqlDB);
		fSqlDB = createTextControl(toolkit, parent);
		fSqlDB.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlDB(fSqlDB.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlID);
		fSqlID = createTextControl(toolkit, parent);
		fSqlID.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlID(fSqlID.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlJDBCDriverClass);
		fSqlJDBCDriverClass = createTextControl(toolkit, parent);
		fSqlJDBCDriverClass.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlJDBCDriverClass(fSqlJDBCDriverClass.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlPassword);
		fSqlPassword = createTextControl(toolkit, parent);
		fSqlPassword.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlPassword(fSqlPassword.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlSchema);
		fSqlSchema = createTextControl(toolkit, parent);
		fSqlSchema.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlSchema(fSqlSchema.getText());		
			}			
		});

		toolkit.createLabel(parent, SOAMessages.LabelSqlJNDIName);
		fSqlJNDIName = createTextControl(toolkit, parent);
		fSqlJNDIName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlJNDIName(fSqlJNDIName.getText());		
			}			
		});

		
		toolkit.createLabel(parent, SOAMessages.LabelSqlValidationConnectionURL);
		fSqlValidationConnectionURL = createTextControl(toolkit, parent);
		fSqlValidationConnectionURL.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				fSQLDatabaseBinding.setSqlValidationConnectionURL(fSqlValidationConnectionURL.getText());		
			}			
		});
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
			fSqlID.setText(sqlid);
		String sqljdbcdriver = fSQLDatabaseBinding.getSqlJDBCDriverClass();
		if (sqljdbcdriver != null)
			fSqlJDBCDriverClass.setText(sqljdbcdriver);
		String sqljndiname = fSQLDatabaseBinding.getSqlJNDIName();
		if (sqljndiname != null)
			fSqlJNDIName.setText(sqljndiname);
		String sqlpassword = fSQLDatabaseBinding.getSqlPassword();
		if (sqlpassword != null)
			fSqlPassword.setText(sqlpassword);
		String sqlschema = fSQLDatabaseBinding.getSqlSchema();
		if (sqlschema != null)
			fSqlSchema.setText(sqlschema);
		String sqlvalidation = fSQLDatabaseBinding.getSqlValidationConnectionURL();
		if (sqlvalidation != null)
			fSqlValidationConnectionURL.setText(sqlvalidation);
	}	
	
	protected void HandleNameChanged() {
		fSQLDatabaseBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
}
