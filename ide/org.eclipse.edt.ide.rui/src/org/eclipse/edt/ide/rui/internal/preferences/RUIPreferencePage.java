/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.internal.HelpContextIDs;
import org.eclipse.edt.ide.rui.internal.nls.EditLocaleWizard;
import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocalesList;
import org.eclipse.edt.ide.rui.internal.nls.NewLocaleWizard;
import org.eclipse.edt.ide.rui.internal.nls.RUINlsStrings;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.preferences.AbstractPreferencePage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

public class RUIPreferencePage extends AbstractPreferencePage {
	
	Table table;
	TableViewer tableViewer;
	Button removeLocale;
	Button editLocale;

	private static final String Description_Column = "description"; //$NON-NLS-1$
	private static final String Locale_Code_Column = "code"; //$NON-NLS-1$
	private static final String Runtime_Message_Locale_Column = "runtimeLocale"; //$NON-NLS-1$
	//
	private static String[] columnNames = new String[] { 
			Description_Column,
			Locale_Code_Column,
			Runtime_Message_Locale_Column
			};
	private List allDescriptions = new ArrayList();
	private List allCodes = new ArrayList();
	private HashMap userLocaleToRuntimeLocale = new HashMap();
	private LocalesList localesList = LocalesList.getLocalesList();
	
	public RUIPreferencePage() {
		super();
	}

	protected Control createContents(Composite parent) {
		
		Composite composite = createComposite(parent, 1);
		
		createLocaleComposite(composite);
		
		loadPreferences();
		
		Dialog.applyDialogFont(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, HelpContextIDs.RUI_BASE_PREFERENCE_PAGE);
		
		return composite;
	}
	
	private void createLocaleComposite(Composite parent) {
		Group localeGroup = createGroup(parent, 2);
		localeGroup.setText(RUINlsStrings.RUIDeployPreferencePage_Locales_that_the_handler_will_suppo_);
		
		// Create the table 
		createTable(localeGroup);
		
		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new LocalesContentProvider(this.localesList, this.tableViewer));
		tableViewer.setLabelProvider(new LocalesLabelProvider());
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
                editLocale();
			}
		});
		
		Composite buttonArea = new Composite(localeGroup, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = 0;
		buttonArea.setLayout(layout);
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonArea.setLayoutData(data);
		
		Button addLocale = new Button(buttonArea, SWT.PUSH);
		addLocale.setText(RUINlsStrings.RUIDeployPreferencePage_1);
		addLocale.addSelectionListener(new SelectionAdapter() {
	       	
			public void widgetSelected(SelectionEvent e) {
				addLocale();
			}
		});
		setButtonLayoutData(addLocale);
		
		// Edit button
		this.editLocale = new Button(buttonArea, SWT.PUSH);
		this.editLocale.setText(RUINlsStrings.RUIDeployPreferencePage_edit_button);
		this.editLocale.setEnabled(false);
		this.editLocale.addSelectionListener(new SelectionAdapter() {
	       	
			//	Edit the selection and refresh the view
			public void widgetSelected(SelectionEvent e) {
				editLocale();				
			}
		});
		setButtonLayoutData(editLocale);
		
		// Remove button
		this.removeLocale = new Button(buttonArea, SWT.PUSH);
		this.removeLocale.setText(RUINlsStrings.RUIDeployPreferencePage_2);
		this.removeLocale.setEnabled(false);
		this.removeLocale.addSelectionListener(new SelectionAdapter() {
	       	
			//	Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e) {
				deleteLocale();				
			}
		});
		setButtonLayoutData(removeLocale);
	}
	
	private void addLocale() {
		Locale locale = new Locale();
		NewLocaleWizard wizard = new NewLocaleWizard(locale, allCodes, allDescriptions, userLocaleToRuntimeLocale);
        WizardDialog dialog = new WizardDialog(getShell(), wizard);
        int rc = dialog.open();
        if (rc == Window.OK) {
        	String code = locale.getCode();
        	String runtimeCode = locale.getRuntimeLocaleCode();
        	
			localesList.addLocale(locale);
			allDescriptions.add(locale.getDescription());
			allCodes.add(locale.getCode());
			if (!userLocaleToRuntimeLocale.containsKey(code)) {
				List runtimeLocales = new ArrayList();
				runtimeLocales.add(runtimeCode);
				userLocaleToRuntimeLocale.put(code, runtimeLocales);
			} else {
				List runtimeLocales = (List)userLocaleToRuntimeLocale.get(code);
				if (!runtimeLocales.contains(runtimeCode)) {
					runtimeLocales.add(runtimeCode);
				}
			}
        }
        Collections.sort(localesList.getLocales(), localesList.new SortIt());
        tableViewer.refresh(true, true); 
	}
	
	private void deleteLocale() {
		Locale locale = null;
		IStructuredSelection selection = ((IStructuredSelection) 
				tableViewer.getSelection());
		Iterator iterator = ((IStructuredSelection)selection).iterator();
		while (iterator.hasNext()) {
			if (localesList.getLocales().size() == 1)
				break;
			Object obj = iterator.next();
			if (obj instanceof Locale) {
				locale = (Locale)obj;
				localesList.removeLocale(locale);
				allDescriptions.remove(locale.getDescription());
				allCodes.remove(locale.getCode());
			}
		} 
		tableViewer.refresh();
		updateButtons();
	}
	
	private void editLocale() {
		Locale oldLocale = (Locale) ((IStructuredSelection) 
				tableViewer.getSelection()).getFirstElement();
		if (oldLocale != null) {
			String oldCode = oldLocale.getCode();
	        String oldDescription = oldLocale.getDescription();
	        String oldRuntimeLocaleCode = oldLocale.getRuntimeLocaleCode();
			
			Locale newLocale = new Locale(oldCode, oldDescription, oldRuntimeLocaleCode);
			EditLocaleWizard wizard = new EditLocaleWizard(newLocale, allCodes, allDescriptions, userLocaleToRuntimeLocale);
	        WizardDialog dialog = new WizardDialog(getShell(), wizard);

	        int rc = dialog.open();
	        if (rc == Window.OK) {
	        	// Remove the old locale
		        localesList.removeLocale(oldLocale);
	        	
	        	// Add the new locale
				localesList.addLocale(newLocale);
	        	
	        	String code = newLocale.getCode();
	        	String description = newLocale.getDescription();
	        	String runtimeCode = newLocale.getRuntimeLocaleCode();
	        	
	        	if (!code.equalsIgnoreCase(oldCode)) {
	        		allCodes.remove(oldCode);
	        		allCodes.add(code);
	        	}
	        	if (!description.equalsIgnoreCase(oldDescription)) {
	        		allDescriptions.remove(oldDescription);
	        		allDescriptions.add(description);
	        	}
	        	if (!runtimeCode.equalsIgnoreCase(oldRuntimeLocaleCode)) {
					if (!userLocaleToRuntimeLocale.containsKey(code)) {
						List runtimeLocales = new ArrayList();
						runtimeLocales.add(runtimeCode);
						userLocaleToRuntimeLocale.put(code, runtimeLocales);
					} else {
						List runtimeLocales = (List)userLocaleToRuntimeLocale.get(code);
						if (!runtimeLocales.contains(runtimeCode)) {
							runtimeLocales.add(runtimeCode);
						}
					}
	        	}   	        	
	        } 
	        Collections.sort(localesList.getLocales(), localesList.new SortIt());
	        tableViewer.refresh(); 
		} 
	}
	
	private void createTable(Composite parent) {
		int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
		 SWT.FULL_SELECTION ;

		table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 260;
		table.setLayoutData(gridData);		
			
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		table.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL && table.getSelection().length > 0) {
					deleteLocale();
				}
				
			}
			
		});
		
		// 1st column Locale Description
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(RUINlsStrings.RUIDeployPreferencePage_4);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new LocaleViewerSorter(LocaleViewerSorter.DESCRIPTION));
			}
		});
		
		// 2nd column Locale Code
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(RUINlsStrings.RUIDeployPreferencePage_5);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by locale code when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new LocaleViewerSorter(LocaleViewerSorter.CODE));
			}
		});
		
		// 3rd column Runtime Messages Locale Code
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(RUINlsStrings.RUIDeployPreferencePage_6);
		column.setWidth(200);
		// Add listener to column so tasks are sorted by runtime locale description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new LocaleViewerSorter(LocaleViewerSorter.RUNTIME));
			}
		});
		
		table.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				updateButtons();
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
//		CellEditor[] editors = new CellEditor[columnNames.length];

		// Column 1 : Description (Free text)
//		TextCellEditor textEditor = new TextCellEditor(table);
//		((Text) textEditor.getControl()).setTextLimit(60);
//		editors[0] = textEditor;
		
		// Column 2 : Locale Code (Free text)
//		textEditor = new TextCellEditor(table);
//		((Text) textEditor.getControl()).setTextLimit(60);
//		editors[1] = textEditor;
		
		// COlumn 3 : Runtime Message Locale ( Combo )
//		editors[2] = new ComboBoxCellEditor(table, LocaleUtility.getRuntimeDescriptionsArray(), SWT.READ_ONLY);

		// Assign the cell editors to the viewer 
//		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
//		cellModifier = new CellModifier(this);
//		tableViewer.setCellModifier(cellModifier);

	}

	protected void initializeValues() {
		super.initializeValues();
		/**
		 * now the handler locales list
		 */
		buildLocaleConvienenceData();
		tableViewer.setInput(this.localesList);
	}

	/**
	 * 
	 */
	private void buildLocaleConvienenceData() {
		/**
		 * initialize stores
		 */
		this.allDescriptions.clear();
		this.allCodes.clear();
		this.userLocaleToRuntimeLocale.clear();
		
		for (Iterator iterator = this.localesList.getLocales().iterator(); iterator.hasNext();) {
			Locale locale = (Locale) iterator.next();
			String code = locale.getCode();
			String runtimeCode = locale.getRuntimeLocaleCode();
			this.allDescriptions.add(locale.getDescription());
			this.allCodes.add(code);
			if (!this.userLocaleToRuntimeLocale.containsKey(code)) {
				List runtimeLocales = new ArrayList();
				runtimeLocales.add(runtimeCode);
				this.userLocaleToRuntimeLocale.put(code, runtimeLocales);
			} else {
				List runtimeLocales = (List)this.userLocaleToRuntimeLocale.get(code);
				if (!runtimeLocales.contains(runtimeCode)) {
					runtimeLocales.add(runtimeCode);
				}
			}
		}
	}
	
	protected void enableValues() {
		super.enableValues();
	}

	protected void storeValues() {
		getEGLBasePreferenceStore().setValue(IRUIPreferenceConstants.RUI_DEFAULT_LOCALES, this.localesList.toString());
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	private IPreferenceStore getEGLBasePreferenceStore() {
		return EGLBasePlugin.getPlugin().getPreferenceStore();
	}
	
	public boolean performOk() {
		return super.performOk();
	}
	
	/**
	 * Return the column names in a collection
	 * 
	 * @return List  containing column names
	 */
	public List getColumnNames() {
		return Arrays.asList(columnNames);
	}
	
	protected void performDefaults() {
		/**
		 * set the locale list and default locale back to default values
		 */
		super.performDefaults();
		
		this.table.setRedraw(false); // turn redraw off to stop flashing
		localesList.defaultTheLocalesList();
		buildLocaleConvienenceData();
		this.tableViewer.refresh();
		this.table.setRedraw(true);
	}

	public LocalesList getLocalesList() {
		return localesList;
	}
	
	/**
	 * Updates the buttons.
	 */
	protected void updateButtons() {
		IStructuredSelection selection= (IStructuredSelection) tableViewer.getSelection();
		int selectionCount= selection.size();

		editLocale.setEnabled(selectionCount == 1);
		removeLocale.setEnabled(selectionCount > 0 && localesList.getLocales().size() > 1);
	}
}
