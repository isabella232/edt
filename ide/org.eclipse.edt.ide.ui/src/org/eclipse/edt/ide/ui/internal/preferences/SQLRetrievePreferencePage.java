/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.edt.ide.sql.SQLNlsStrings;
import org.eclipse.edt.ide.sql.SQLPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.sql.ISQLPreferenceConstants;
import org.eclipse.edt.compiler.internal.IEGLConstants;

public class SQLRetrievePreferencePage extends PreferencePage implements IWorkbenchPreferencePage, Listener {
	private Button[] caseControlButtons;
	private Button[] underscoreControlButtons;
	private Button[] charTypeControlButtons;
	private Button[] nationalCharControlButtons;
    private Button retrievePrimaryKeys;
    private Button sqlPromptButton;
    private Button[] typeForDateTypesButtons;
    private Button addSqlDataCode;

	public final static String[] NAME_CASE_OPTION_BUTTON_STRINGS =
		{
			SQLNlsStrings.NameCaseOptionDoNotChangeLabel,
			SQLNlsStrings.NameCaseOptionLowercaseLabel,
			SQLNlsStrings.NameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscoreLabel
		};

	public final static String[] NAME_UNDERSCORE_OPTION_BUTTON_STRINGS =
		{
			SQLNlsStrings.NameUnderscoreOptionDoNotChangeLabel,
			SQLNlsStrings.NameUnderscoreOptionRemoveUndersoresLabel
		};
	
	public final static String[] CHARACTER_OPTION_BUTTON_STRINGS = 
		{
			SQLNlsStrings.CharacterOptionStringLabel
			//SQLNlsStrings.CharacterOptionLimitedLengthStringLabel,
			//SQLNlsStrings.CharacterOptionCharLabel,
			//SQLNlsStrings.CharacterOptionMBCharLabel,
			//SQLNlsStrings.CharacterOptionUnicodeLabel					
		};

	public final static String[] NATIONAL_CHAR_OPTION_BUTTON_STRINGS = 
	   {
		    //SQLNlsStrings.NationalCharOptionDBCharLabel,
		    //SQLNlsStrings.NationalCharOptionUnicodeLabel,
		    SQLNlsStrings.NationalCharOptionStringLabel
		    //SQLNlsStrings.NationalCharOptionLimitedStringLabel
		};

	public final static String[] TYPE_FOR_DATE_TYPES_OPTION_BUTTON_STRINGS = 
		{
		    SQLNlsStrings.DateTypesOptionDefaultLabel,
		    //SQLNlsStrings.DateTypesOptionCharLabel,
		    //SQLNlsStrings.DateTypesOptionUnicodeLabel,
		    SQLNlsStrings.DateTypesOptionStringLabel
		    //SQLNlsStrings.DateTypesOptionLimitedStringLabel
		};

	protected Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);
		
		//GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		return composite;
	}

	/**
	 * @return com.ibm.swt.widgets.Control
	 * @param parent com.ibm.swt.widgets.Composite
	 */
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IUIHelpConstants.SQL_RETRIEVE_PREFERENCES_CONTEXT);

		Composite composite = createComposite(parent, 1);
		//createDataItemCharTypeControlOptionsGroup(composite);
		//createDataItemNationalCharControlOptionsGroup(composite);
		createDataItemNameControlOptionsGroup(composite);

		createTypeForDateTypesControlOptionsGroup(composite);

		// Add Retrieve Primary Keys and Exclude System Schemas button.
		retrievePrimaryKeys = new Button(composite, SWT.CHECK);
        retrievePrimaryKeys.setText(SQLNlsStrings.RetrievePrimaryKeyLabel);
        
        sqlPromptButton = new Button(composite, SWT.CHECK);
        sqlPromptButton.setText(SQLNlsStrings.SQLUserIDAndPasswordLabel);
        	
		initializeValues();
		Dialog.applyDialogFont(parent);
		return composite;
	}

	/**
	 * Creates UI portions to collect char information for SQL retrieve.
	 */
	protected void createDataItemCharTypeControlOptionsGroup(Composite parent) {
		Group eglCharControlGroup = createGroup(parent, 1);
		eglCharControlGroup.setText(SQLNlsStrings.CharacterControlLabel);
		Composite eglCharControlComposite = createComposite(eglCharControlGroup, 1);
		
		charTypeControlButtons = new Button[CHARACTER_OPTION_BUTTON_STRINGS.length];
		for (int i = 0; i < CHARACTER_OPTION_BUTTON_STRINGS.length; i++) {
			charTypeControlButtons[i] = new Button(eglCharControlComposite, SWT.RADIO);
			charTypeControlButtons[i].setText(CHARACTER_OPTION_BUTTON_STRINGS[i]);
		}
	}
	
	/**
	 * Creates UI portions to collect national character information for SQL retrieve.
	 */
	protected void createDataItemNationalCharControlOptionsGroup(Composite parent) {	
		Group nationalCharControlGroup = createGroup(parent, 1);
		nationalCharControlGroup.setText(SQLNlsStrings.NationalCharControlLabel);
		Composite nationalCharControlComposite = createComposite(nationalCharControlGroup, 1);
		
		nationalCharControlButtons = new Button[NATIONAL_CHAR_OPTION_BUTTON_STRINGS.length];
		for (int i = 0; i < NATIONAL_CHAR_OPTION_BUTTON_STRINGS.length; i++) {
			nationalCharControlButtons[i] = new Button(nationalCharControlComposite, SWT.RADIO);
			nationalCharControlButtons[i].setText(NATIONAL_CHAR_OPTION_BUTTON_STRINGS[i]);
		}
	}
	
	/**
	 * Creates UI portions to collect data item name control information for SQL retrieve.
	 */
	protected void createDataItemNameControlOptionsGroup(Composite parent) {
		Group caseControlGroup = createGroup(parent, 1);
		caseControlGroup.setText(SQLNlsStrings.NameCaseControlLabel);
		Composite caseControlComposite = createComposite(caseControlGroup, 1);

		caseControlButtons = new Button[NAME_CASE_OPTION_BUTTON_STRINGS.length];
		for (int i = 0; i < NAME_CASE_OPTION_BUTTON_STRINGS.length; i++) {
			caseControlButtons[i] = new Button(caseControlComposite, SWT.RADIO);
			caseControlButtons[i].setText(NAME_CASE_OPTION_BUTTON_STRINGS[i]);
		}

		Group underscoreControlGroup = createGroup(parent, 1);
		underscoreControlGroup.setText(SQLNlsStrings.NameUnderscoreControlLabel);
		Composite underscoreControlComposite = createComposite(underscoreControlGroup, 1);
		
		underscoreControlButtons = new Button[NAME_UNDERSCORE_OPTION_BUTTON_STRINGS.length];
		for (int i = 0; i < NAME_UNDERSCORE_OPTION_BUTTON_STRINGS.length; i++) {
			underscoreControlButtons[i] = new Button(underscoreControlComposite, SWT.RADIO);
			underscoreControlButtons[i].setText(NAME_UNDERSCORE_OPTION_BUTTON_STRINGS[i]);
		}
	}

	/**
	 * Creates UI portions to collect date-time type information for SQL retrieve.
	 */
	protected void createTypeForDateTypesControlOptionsGroup(Composite parent) {	
		Group group = createGroup(parent, 1);
		group.setText(SQLNlsStrings.DateTypesControlLabel);
		Composite composite = createComposite(group, 1);
		
		typeForDateTypesButtons = new Button[TYPE_FOR_DATE_TYPES_OPTION_BUTTON_STRINGS.length];
		for (int i = 0; i < TYPE_FOR_DATE_TYPES_OPTION_BUTTON_STRINGS.length; i++) {
			typeForDateTypesButtons[i] = new Button(composite, SWT.RADIO);
			typeForDateTypesButtons[i].setText(TYPE_FOR_DATE_TYPES_OPTION_BUTTON_STRINGS[i]);
		}
		
		Label spacer = new Label(composite, SWT.LEFT);
		spacer.setText(" ");
		addSqlDataCode = new Button(composite, SWT.CHECK);
		addSqlDataCode.setText(SQLNlsStrings.AddSqlDataCodeLabel);
		typeForDateTypesButtons[0].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( e.getSource() == typeForDateTypesButtons[ 0 ] ) {
					addSqlDataCode.setEnabled( !typeForDateTypesButtons[ 0 ].getSelection() );
				}
			}
		});
	}
	
	protected Group createGroup(Composite parent, int numColumns) {
		Group group = new Group(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		group.setLayout(layout);

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		group.setLayoutData(data);

		return group;
	}

	protected boolean determinePageCompletion() {
		setErrorMessage(null);
		setValid(true);

		return isValid();
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return SQLPlugin.getPlugin().getPreferenceStore();
	}

	public void handleEvent(Event e) {	
		determinePageCompletion();
	}
	/**
	 * Initializes this preference page for the given workbench.
	 * <p>
	 * This method is called automatically as the preference page is being created
	 * and initialized. Clients must not call this method.
	 * </p>
	 *
	 * @param workbench the workbench
	 *  
	 */
	public void init(org.eclipse.ui.IWorkbench workbench) {}

	/**
	 * Initializes states of the controls using default values
	 * in the preference store.
	 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();

		String option = store.getDefaultString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION);
		/*for (int i = 0; i < SQLPlugin.EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (i == 0 || i == 1) {
				charTypeControlButtons[i].setEnabled(true);
			}
			if (SQLPlugin.EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				charTypeControlButtons[i].setSelection(true);
			else
				charTypeControlButtons[i].setSelection(false);
		}*/
		
		option = store.getDefaultString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION);
		/*for (int i = 0; i < SQLPlugin.EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (i == 2 || i == 3) {
				nationalCharControlButtons[i].setEnabled(true);
			}
			if (SQLPlugin.EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				nationalCharControlButtons[i].setSelection(true);
			else
				nationalCharControlButtons[i].setSelection(false);
		}*/
		
		option = store.getDefaultString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_CASE_CONTROL_OPTION);
		for (int i = 0; i < SQLPlugin.NAME_CASE_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.NAME_CASE_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				caseControlButtons[i].setSelection(true);
			else
				caseControlButtons[i].setSelection(false);
		}

		option = store.getDefaultString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_UNDERSCORE_CONTROL_OPTION);
		for (int i = 0; i < SQLPlugin.NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				underscoreControlButtons[i].setSelection(true);
			else
				underscoreControlButtons[i].setSelection(false);
		}
	
		retrievePrimaryKeys.setSelection(store.getDefaultBoolean(ISQLPreferenceConstants.SQL_RETRIEVE_PRIMARY_KEY_OPTION));
		sqlPromptButton.setSelection(store.getDefaultBoolean(ISQLPreferenceConstants.SQL_PROMPT_USERID_AND_PASSWORD_OPTION));
		addSqlDataCode.setSelection(store.getDefaultBoolean(ISQLPreferenceConstants.SQL_RETRIEVE_ADD_SQL_DATA_CODE_OPTION));
		addSqlDataCode.setEnabled( false );

		// Version 7.5.1.4 had a boolean preference, but in 7.5.1.5 there are 4 possible
		// values.  To migrate smoothly from the old preference to the new, check if the old
		// preference is set.
		if ( store.getBoolean( ISQLPreferenceConstants.SQL_RETRIEVE_USE_CHAR_FOR_DATE_OPTION ) ) {
			option = IEGLConstants.KEYWORD_CHAR;
		} else {
			option = store.getDefaultString( ISQLPreferenceConstants.SQL_RETRIEVE_USE_TEXT_TYPE_FOR_DATE_OPTION );
		}
		for (int i = 0; i < SQLPlugin.TEXT_TYPE_FOR_DATE_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.TEXT_TYPE_FOR_DATE_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				typeForDateTypesButtons[i].setSelection(true);
			else
				typeForDateTypesButtons[i].setSelection(false);
		}
	}
	
	/**
	 * Initializes states of the controls from the preference store.
	 */
	private void initializeValues() {
		IPreferenceStore store = getPreferenceStore();
		boolean selectionSet = false;

		String option = store.getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION);
		/*for (int i = 0; i < SQLPlugin.EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				charTypeControlButtons[i].setSelection(true);
			else
				charTypeControlButtons[i].setSelection(false);
		}*/
		
		option = store.getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION);
		/*for (int i = 0; i < SQLPlugin.EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				nationalCharControlButtons[i].setSelection(true);
			else
				nationalCharControlButtons[i].setSelection(false);
		}*/
		
		option = store.getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_CASE_CONTROL_OPTION);
		for (int i = 0; i < SQLPlugin.NAME_CASE_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.NAME_CASE_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				caseControlButtons[i].setSelection(true);
			else
				caseControlButtons[i].setSelection(false);
		}

		option = store.getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_UNDERSCORE_CONTROL_OPTION);
		for (int i = 0; i < SQLPlugin.NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option)) {
				underscoreControlButtons[i].setSelection(true);
				selectionSet = true;
			} else
				underscoreControlButtons[i].setSelection(false);
		}
		if (!selectionSet) {
			underscoreControlButtons[0].setSelection(true);
		}
		
		retrievePrimaryKeys.setSelection(store.getBoolean(ISQLPreferenceConstants.SQL_RETRIEVE_PRIMARY_KEY_OPTION));
		sqlPromptButton.setSelection(store.getBoolean(ISQLPreferenceConstants.SQL_PROMPT_USERID_AND_PASSWORD_OPTION));
		addSqlDataCode.setSelection(store.getBoolean(ISQLPreferenceConstants.SQL_RETRIEVE_ADD_SQL_DATA_CODE_OPTION));
		
		if ( store.getBoolean( ISQLPreferenceConstants.SQL_RETRIEVE_USE_CHAR_FOR_DATE_OPTION ) ) {
			// The old preference is set to true, so use char.
			option = IEGLConstants.KEYWORD_CHAR;
		} else if ( store.contains( ISQLPreferenceConstants.SQL_RETRIEVE_USE_CHAR_FOR_DATE_OPTION ) ) {
			// The old preference is set to false, so use "".
			option = "";
		} else {
			// The old preference is not set, so use the new preference.
			option = store.getString( ISQLPreferenceConstants.SQL_RETRIEVE_USE_TEXT_TYPE_FOR_DATE_OPTION );
		}
		
		for (int i = 0; i < SQLPlugin.TEXT_TYPE_FOR_DATE_OPTION_NON_MNEMONIC_STRINGS.length; i++) {
			if (SQLPlugin.TEXT_TYPE_FOR_DATE_OPTION_NON_MNEMONIC_STRINGS[i].equalsIgnoreCase(option))
				typeForDateTypesButtons[i].setSelection(true);
			else
				typeForDateTypesButtons[i].setSelection(false);
		}
		
		addSqlDataCode.setEnabled( !typeForDateTypesButtons[ 0 ].getSelection() );
	}

	protected void performDefaults() {
		super.performDefaults();
		initializeDefaults();
	}

	public boolean performOk() {

		if (!determinePageCompletion())
			return false;

		storeValues();

		return true;
	}

	/**
	 * Stores the values of the controls back to the preference store.
	 */
	private void storeValues() {
		IPreferenceStore store = getPreferenceStore();

		if(charTypeControlButtons != null) {
			for (int i = 0; i < charTypeControlButtons.length; i++) {
				if (charTypeControlButtons[i].getSelection() == true) {
					store.setValue(
						ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION,
						SQLPlugin.EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[i]);
					break;
				}
			}
		} else {
			store.setValue(
					ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION,
					SQLPlugin.EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]);
		}
		
		if(nationalCharControlButtons != null) {
			for (int i = 0; i < nationalCharControlButtons.length; i++) {
				if (nationalCharControlButtons[i].getSelection() == true) {
					store.setValue(
						ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION,
						SQLPlugin.EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[i]);
					break;
				}
			}
		} else {
			store.setValue(
					ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION,
					SQLPlugin.EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]);
		}
		
				
		for (int i = 0; i < caseControlButtons.length; i++) {
			if (caseControlButtons[i].getSelection() == true) {
				store.setValue(
					ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_CASE_CONTROL_OPTION,
					SQLPlugin.NAME_CASE_OPTION_NON_MNEMONIC_STRINGS[i]);
				break;
			}
		}

		for (int i = 0; i < underscoreControlButtons.length; i++) {
			if (underscoreControlButtons[i].getSelection() == true) {
				store.setValue(
					ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_UNDERSCORE_CONTROL_OPTION,
					SQLPlugin.NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS[i]);
				break;
			}
		}
		
        store.setValue(ISQLPreferenceConstants.SQL_RETRIEVE_PRIMARY_KEY_OPTION, retrievePrimaryKeys.getSelection());
        store.setValue(ISQLPreferenceConstants.SQL_PROMPT_USERID_AND_PASSWORD_OPTION, sqlPromptButton.getSelection());
        store.setValue(ISQLPreferenceConstants.SQL_RETRIEVE_ADD_SQL_DATA_CODE_OPTION, addSqlDataCode.getSelection());
        
		for (int i = 0; i < typeForDateTypesButtons.length; i++) {
			if (typeForDateTypesButtons[i].getSelection()) {
				store.setValue(
					ISQLPreferenceConstants.SQL_RETRIEVE_USE_TEXT_TYPE_FOR_DATE_OPTION,
					SQLPlugin.TEXT_TYPE_FOR_DATE_OPTION_NON_MNEMONIC_STRINGS[i]);
				break;
			}
		}
        // This preference was used in 7.5.1.4 but replaced in 7.5.1.5 by SQL_RETRIEVE_USE_TEXT_TYPE_FOR_DATE_OPTION.
        store.setValue(ISQLPreferenceConstants.SQL_RETRIEVE_USE_CHAR_FOR_DATE_OPTION, false);
	}
}
