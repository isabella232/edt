/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal.preferences;

import java.util.Arrays;
import java.util.List;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.rui.internal.HelpContextIDs;
import org.eclipse.edt.ide.ui.internal.preferences.AbstractPreferencePage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

/**
 *
 */
public class RUIDeployPreferencePage extends AbstractPreferencePage {

	public static final String EGL_RUI_DEPLOY_DEFAULT_HANDLER_LOCALES = "eglRUIDeployDefaultHandlerLocales"; //$NON-NLS-1$
	
	Table table;
	TableViewer tableViewer;
	
	// Set the table column property names
	private static final String Default_Locale_Column = "default"; //$NON-NLS-1$
	private static final String Description_Column = "description"; //$NON-NLS-1$
	private static final String Locale_Code_Column = "code"; //$NON-NLS-1$
	private static final String Runtime_Message_Locale_Column = "runtimeLocale"; //$NON-NLS-1$
	// Set column names
	private static String[] columnNames = new String[] { 
			Default_Locale_Column, 
			Description_Column,
			Locale_Code_Column,
			Runtime_Message_Locale_Column
			};
	private CellModifier cellModifier;
	private HandlerLocalesList handlerLocalesList = new HandlerLocalesList();
//	private Button changeGenModeButton;

	/**
	 * 
	 */
	public RUIDeployPreferencePage() {
	}
	
	protected Control createContents(Composite parent) {
		
		Composite composite = createComposite(parent, 1);
		
//		Group promptGroup = createGroup(composite, 1);
//		promptGroup.setText(Messages.RUIDeployPreferencePage_1);
		
//		changeGenModeButton = new Button(promptGroup, SWT.CHECK);
//		changeGenModeButton.setText(Messages.RUIDeployPreferencePage_2);

		Group localeGroup = createGroup(composite, 2);
		localeGroup.setText(Messages.RUIDeployPreferencePage_0);
		
		Label x = new Label(localeGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 3;
		x.setLayoutData(layoutData);
		
		Label tableTitle = new Label(localeGroup, SWT.NONE);
		tableTitle.setText(Messages.RUIDeployPreferencePage_Locales_that_the_handler_will_suppo_);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		tableTitle.setLayoutData(layoutData);
		
		// Create the table 
		createTable(localeGroup);
		
		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new HandlerLocalesContentProvider(this.handlerLocalesList, this.tableViewer));
		tableViewer.setLabelProvider(new HandlerLocalesLabelProvider());
		
		loadPreferences();
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, HelpContextIDs.RUI_DEPLOYMENT_PREFERENCE_PAGE);
		
		return composite;
	}

	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
		 SWT.FULL_SELECTION |SWT.HIDE_SELECTION;

		table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 260;
		gridData.horizontalSpan = 2;
		table.setLayoutData(gridData);		
			
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no effect!!
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText(Messages.RUIDeployPreferencePage_3);
		column.setWidth(150);
	
		// 2nd column Locale Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(Messages.RUIDeployPreferencePage_4);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
//TODO - EDT				tableViewer.setSorter(new LocaleViewerSorter(LocaleViewerSorter.DESCRIPTION));
			}
		});
		
		// 3rd column Locale Code
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(Messages.RUIDeployPreferencePage_5);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by locale code when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
//TODO - EDT				tableViewer.setSorter(new LocaleViewerSorter(LocaleViewerSorter.CODE));
			}
		});
		
		// 4th column Runtime messages locale code
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(Messages.Globalization_7);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by runtime locale description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
//TODO - EDT				tableViewer.setSorter(new LocaleViewerSorter(LocaleViewerSorter.RUNTIME));
			}
		});

	}	
	
	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[1];

		// Column 1 : Selected (Checkbox)
		editors[0] = new CheckboxCellEditor(table);

		// Assign the cell editors to the viewer 
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		cellModifier = new CellModifier(this);
		tableViewer.setCellModifier(cellModifier);

	}
	
	/**
	 * Return the column names in a collection
	 * 
	 * @return List  containing column names
	 */
	public List getColumnNames() {
		return Arrays.asList(columnNames);
	}
	
	/**
	 * Return the LocalesList
	 */
	public HandlerLocalesList getHandlerLocalesList() {
		return handlerLocalesList;	
	}
	
	protected boolean loadPreferences() {
//		String value = getEGLBasePreferenceStore().getString(IRUIPreferenceConstants.PREFERENCE_PROMPT_DEPLOYMENT_GENERATION_MODE);
//		if (value.toLowerCase().equals("true")) { //$NON-NLS-1$
//			changeGenModeButton.setSelection(true);
//		}
		
		/**
		 * now the handler locales list
		 * First load up the list of locales that is defined on the main RUI pref page
		 * then match the default info with this list
		 */
		
		handlerLocalesList.buildLocalesList();
		tableViewer.setInput(handlerLocalesList);
		
		return true;
	}
	
	protected void storeValues() {
		getEGLBasePreferenceStore().setValue(EGL_RUI_DEPLOY_DEFAULT_HANDLER_LOCALES, handlerLocalesList.toString());
//		getEGLBasePreferenceStore().setValue(IRUIPreferenceConstants.PREFERENCE_PROMPT_DEPLOYMENT_GENERATION_MODE, new Boolean(changeGenModeButton.getSelection()).toString());
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return getEGLBasePreferenceStore();
	}
	
	private IPreferenceStore getEGLBasePreferenceStore() {
		return EGLBasePlugin.getPlugin().getPreferenceStore();
	}
	
	protected void performDefaults() {
//		getEGLBasePreferenceStore().setValue(IRUIPreferenceConstants.PREFERENCE_PROMPT_DEPLOYMENT_GENERATION_MODE, "true"); //$NON-NLS-1$
//		changeGenModeButton.setSelection(true);
		
		handlerLocalesList.defaultTheLocalesList();
		this.tableViewer.refresh();
		
		super.performDefaults();
	}
	
	public void dispose() {
		this.handlerLocalesList.dispose();
		super.dispose();
	}

}
