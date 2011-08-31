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

import org.eclipse.edt.ide.ui.internal.deployment.RestBinding;
import org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SQLDatabaseBindingDetailPage extends WebBindingDetailPage {

	private SQLDatabaseBinding fSQLDatabaseBinding;
	
//    <sqlDatabaseBinding dbms="Derby" name="tony" sqlDB="localhost" sqlID="Tony" sqlJDBCDriverClass="derby.class" sqlJNDIName="jdbc/google" sqlPassword="password" sqlSchema="APP" sqlValidationConnectionURL="no"/>

	private Text fDbms;
	private Text fSqlDB;
	
	public SQLDatabaseBindingDetailPage(){
		super();
		nColumnSpan = 3;
	}
	
	protected Composite createDetailSection(Composite parent,
			FormToolkit toolkit, int sectionStyle, int columnSpan) {
		return createSection(parent, toolkit, SOAMessages.SQLDatabaseDetailSecTitle, 
				SOAMessages.SQLDatabaseDetailSecDescp, sectionStyle, columnSpan);
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createDbmsControl(toolkit, parent);
//		createBaseUriExample(toolkit, parent);
//		createSessionCookieIdControl(toolkit, parent);
//		createEnableGenerateControl(toolkit, parent);
	}
	

	private void createDbmsControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, SOAMessages.LabelDbms);
		fDbms = createTextControl(toolkit, parent);
		fDbms.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleDbmsChanged();				
			}			
		});
	}
	
	protected void HandleDbmsChanged() {
		fSQLDatabaseBinding.setDbms(fDbms.getText());		
	}
	
	private void createBaseUriExample(FormToolkit toolkit, Composite parent) {
		createSpacer( toolkit, parent, 1 );
		boolean toggleBorder = toolkit.getBorderStyle() == SWT.BORDER;
		if (toggleBorder) {
			toolkit.setBorderStyle(SWT.NONE);
		}
		Text example = toolkit.createText(parent, SOAMessages.BaseURITooltip, SWT.SINGLE|SWT.READ_ONLY); //$NON-NLS-1$;
		if (toggleBorder) {
			toolkit.setBorderStyle(SWT.BORDER);
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		gd.horizontalSpan = nColumnSpan-1;
		example.setLayoutData(gd);
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
	}	
	
//
//	private void createSessionCookieIdControl(FormToolkit toolkit,
//			Composite parent) {
//		toolkit.createLabel(parent, SOAMessages.LabelSessionCookieId);
//		fSessionCookieId = createTextControl(toolkit, parent);
//		fSessionCookieId.addModifyListener(new ModifyListener(){
//			public void modifyText(ModifyEvent e) {
//				HandleSessionCookieIdChanged();				
//			}			
//		});
//		
//	}
//
//	protected void HandleSessionCookieIdChanged() {
//		fRestBinding.setSessionCookieId(fSessionCookieId.getText());		
//	}
//

//	

//	protected void HandleNameChanged() {
//		fRestBinding.setName(fNameText.getText());
//		refreshMainTableViewer();
//	}
//	
//	protected void HandleGenCheckChanged() {
//		fRestBinding.setEnableGeneration(fGenBtn.getSelection());		
//	}

}
