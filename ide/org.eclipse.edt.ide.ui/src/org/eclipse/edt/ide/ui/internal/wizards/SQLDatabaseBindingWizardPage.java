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

import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.wizards.BindingSQLDatabaseConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class SQLDatabaseBindingWizardPage extends EGLDDBindingWizardPage {
	public static final String WIZPAGENAME_SQLDatabaseBindingWizardPage = "WIZPAGENAME_SQLDatabaseBindingWizardPage"; //$NON-NLS-1$
																		   
//	private StringDialogField fSessionCookieId;
//	private Button fBtnPreserveRequestHeaders;
//	private RestBindingNameFieldAdapter adapter = new RestBindingNameFieldAdapter();
	
//	private class RestBindingNameFieldAdapter implements IStringBrowseButtonFieldAdapter{
//
//		public void dialogFieldChanged(DialogField field) {
//			if(field == fNameField)
//				HandleBindingNameChanged(getBindingRestConfiguration());
//			
//		}
//
//		public void changeControlPressed(DialogField field) {
//			HandleRestBindingNameBrowsePressed();			
//		}
//		
//	}
	
	public SQLDatabaseBindingWizardPage(String pageName){
		super(pageName);
		setTitle(NewWizardMessages.TitleAddSQLDatabaseBinding);
		setDescription(NewWizardMessages.DescAddSQLDatabaseBinding);
		//TODO check this
		nColumns = 4;
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.MODULE_SQLDATABASEBINDING);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);

		setControl(composite);
		Dialog.applyDialogFont(parent);
	}
	

	private BindingSQLDatabaseConfiguration getConfiguration(){
		return (BindingSQLDatabaseConfiguration)((EGLPartWizard)getWizard()).getConfiguration(getName());
	}
	
	protected BindingSQLDatabaseConfiguration getBindingSQLDatabaseConfiguration(){
		return (BindingSQLDatabaseConfiguration)((EGLPartWizard)getWizard()).getConfiguration(SQLDatabaseBindingWizardPage.WIZPAGENAME_SQLDatabaseBindingWizardPage);
	}
}
