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
package org.eclipse.edt.ide.sql;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SQLPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.edt.ide.sql"; //$NON-NLS-1$

	public static String[] EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS;
	public static String[] EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS;
	public static String[] NAME_CASE_OPTION_NON_MNEMONIC_STRINGS;
	public static String[] NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS;
	public static String[] TEXT_TYPE_FOR_DATE_OPTION_NON_MNEMONIC_STRINGS;

	/**
	 * Keep track of the singleton.
	 */
	protected static SQLPlugin plugin;
	
	private IProfileListener profileListener;

	/**
	 * EGLBasePlugin constructor comment.
	 * @param descriptor org.eclipse.core.runtime.IPluginDescriptor
	 */
	public SQLPlugin() {
		super();

		plugin = this;
	}

	/**
	 * Get the singleton instance.
	 */
	public static SQLPlugin getPlugin() {
		return plugin;
	}

	public boolean isCharacterOptionLimitedString() {
        String typeOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION);
        //if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[1]))
        if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]))
            return true;   
        
        return false;
    }
    
    public boolean isCharacterOptionChar() {
		String typeOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION);
		//if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[2]))
		if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]))
			return true;
		
		return false;
	}
	
	public boolean isCharacterOptionMBChar() {
		String typeOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION);
		//if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[3]))
		if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]))
			return true;
		
		return false;
	}
	
	public boolean isCharacterOptionUnicode() {
		String typeOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION);
		//if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[4]))
		if (typeOption == null || typeOption.equalsIgnoreCase(EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]))
			return true;
		
		return false;
	}

	public boolean isNationalCharOptionUnicode() {
		String typeOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION);
		//if (typeOption == null || typeOption.equalsIgnoreCase(EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[1]))
		if (typeOption == null || typeOption.equalsIgnoreCase(EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]))
			return true;
		
		return false;
	}
	
	public boolean isNationalCharOptionString() {
		String typeOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION);
		//if (typeOption == null || typeOption.equalsIgnoreCase(EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[2]))
		if (typeOption == null || typeOption.equalsIgnoreCase(EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]))
			return true;
		
		return false;
	}

	public boolean isNationalCharOptionLimitedString() {
		String typeOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION);
		//if (typeOption == null || typeOption.equalsIgnoreCase(EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[3]))
		if (typeOption == null || typeOption.equalsIgnoreCase(EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]))
			return true;
		
		return false;
	}
    
	public boolean isLowercaseItemNameCaseOption() {
		String caseOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_CASE_CONTROL_OPTION);
		if (caseOption != null && (caseOption.equalsIgnoreCase(NAME_CASE_OPTION_NON_MNEMONIC_STRINGS[1])))
			return true;
		return false;
	}

	public boolean isLowercaseNameAndUppercaseCharacterAfterUnderscoreOption() {
		String caseOption = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_CASE_CONTROL_OPTION);
		if (caseOption != null && (caseOption.equalsIgnoreCase(NAME_CASE_OPTION_NON_MNEMONIC_STRINGS[2])))
			return true;
		return false;
	}

	public boolean isRemoveUnderscoresInNameOption() {
		String underscoreOption =
			getPreferenceStore().getString(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_UNDERSCORE_CONTROL_OPTION);
		if (underscoreOption != null && (underscoreOption.equalsIgnoreCase(NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS[1])))
			return true;

		return false;
	}

	public String getTypeForDateTimeTypesOption()
	{
		// Version 7.5.1.4 had a boolean preference, but in 7.5.1.5 there are 4 possible
		// values so it's now a string.  To migrate smoothly from the old preference to
		// the new, check for the old one first.
		if ( getPreferenceStore().getBoolean( ISQLPreferenceConstants.SQL_RETRIEVE_USE_CHAR_FOR_DATE_OPTION ) ) {
			// The old preference is set to true so return CHAR.
			return IEGLConstants.KEYWORD_CHAR;
		} else if ( getPreferenceStore().contains( ISQLPreferenceConstants.SQL_RETRIEVE_USE_CHAR_FOR_DATE_OPTION ) ) {
			// The old preference is set to false so return "".
			return "";
		} else {
			// Use the new preference.
			return getPreferenceStore().getString( ISQLPreferenceConstants.SQL_RETRIEVE_USE_TEXT_TYPE_FOR_DATE_OPTION );
		}
	}
	
	public boolean getAddSqlDataCodeForDateTimeTypesOption() {
        return getPreferenceStore().getBoolean(ISQLPreferenceConstants.SQL_RETRIEVE_ADD_SQL_DATA_CODE_OPTION);
	}
	
    public boolean getRetrievePrimaryKeyOption() {
        return getPreferenceStore().getBoolean(ISQLPreferenceConstants.SQL_RETRIEVE_PRIMARY_KEY_OPTION);
    }
    
    public boolean getSQLPromptDialogOption() {
    	return getPreferenceStore().getBoolean(ISQLPreferenceConstants.SQL_PROMPT_USERID_AND_PASSWORD_OPTION);
    }

	/** 
	 * Sets default preference values. These values will be used
	 * until some preferences are actually set using Preference dialog.
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
	
		// SQL retrieve preferences
		store.setDefault(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION, EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]);
		store.setDefault(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION, EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS[0]);
		store.setDefault(ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_CASE_CONTROL_OPTION, NAME_CASE_OPTION_NON_MNEMONIC_STRINGS[0]);
		store.setDefault(
			ISQLPreferenceConstants.SQL_RETRIEVE_ITEM_NAME_UNDERSCORE_CONTROL_OPTION,
			NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS[0]);
		store.setDefault(ISQLPreferenceConstants.SQL_RETRIEVE_PRIMARY_KEY_OPTION, true);
		//store.setDefault(ISQLPreferenceConstants.SQL_RETRIEVE_COBOL_COMPATIBLE_RECORD_OPTION, false);
		store.setDefault(ISQLPreferenceConstants.SQL_PROMPT_USERID_AND_PASSWORD_OPTION, true);
		store.setDefault(ISQLPreferenceConstants.SQL_RETRIEVE_USE_TEXT_TYPE_FOR_DATE_OPTION, "" );
		store.setDefault(ISQLPreferenceConstants.SQL_RETRIEVE_ADD_SQL_DATA_CODE_OPTION, false);
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setupSQLRetrievePreferenceOutputStrings();
		
		profileListener = new IProfileListener() {
			public void profileAdded( IConnectionProfile profile ) {
				// When a profile is created, and we don't have a profile selected,
				// and the new profile is a supported type, select it.
				if ( EGLSQLUtility.getCurrentConnectionProfile() == null ) {
					getPreferenceStore().setValue(
							ISQLPreferenceConstants.SQL_CONNECTION_NAMED_CONNECTION,
							profile.getName() );
				}
			}
			public void profileDeleted( IConnectionProfile profile ) {
				// If deleting the current connection, update the preference.
				IConnectionProfile currentProfile = EGLSQLUtility.getCurrentConnectionProfile();
				if ( currentProfile != null && currentProfile.equals( profile ) )
				{
					getPreferenceStore().setToDefault( ISQLPreferenceConstants.SQL_CONNECTION_NAMED_CONNECTION );
				}
			}
			public void profileChanged( IConnectionProfile profile ) {
			}
		};
		ProfileManager.getInstance().addProfileListener(profileListener);
	}
	
	public void stop(BundleContext context) throws Exception {
		super.stop( context );
		if (profileListener != null) {
			ProfileManager.getInstance().removeProfileListener(profileListener);
		}
	}

	private void setupSQLRetrievePreferenceOutputStrings() {

		String charOptionUseString = SQLNlsStrings.CharacterOptionStringLabel;
        String charOptionUseLimitedLengthString = SQLNlsStrings.CharacterOptionLimitedLengthStringLabel;
		String charOptionUseChar = SQLNlsStrings.CharacterOptionCharLabel;
		String charOptionUseMBChar = SQLNlsStrings.CharacterOptionMBCharLabel;
		String charOptionUseUnicode = SQLNlsStrings.CharacterOptionUnicodeLabel;
		String nationalCharOptionUseDBChar = SQLNlsStrings.NationalCharOptionDBCharLabel;
		String nationalCharOptionUseUnicode = SQLNlsStrings.NationalCharOptionUnicodeLabel;
		String nationalCharOptionUseString = SQLNlsStrings.NationalCharOptionStringLabel;
		String nationalCharOptionUseLimitedString = SQLNlsStrings.NationalCharOptionLimitedStringLabel;
		String nameCaseOptionDoNotChange = SQLNlsStrings.NameCaseOptionDoNotChangeLabel;
		String nameCaseOptionLowercase = SQLNlsStrings.NameCaseOptionLowercaseLabel;
		String nameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscore = SQLNlsStrings.NameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscoreLabel;
		String nameUnderscoreOptionDoNotChange = SQLNlsStrings.NameUnderscoreOptionDoNotChangeLabel;
		String nameUnderscoreOptionRemoveUndersores = SQLNlsStrings.NameUnderscoreOptionRemoveUndersoresLabel;
		
		int i = charOptionUseString.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			charOptionUseString = new StringBuffer(charOptionUseString).deleteCharAt(i).toString();
        i = charOptionUseLimitedLengthString.indexOf("&"); //$NON-NLS-1$
        if (i >= 0)
            charOptionUseLimitedLengthString = new StringBuffer(charOptionUseLimitedLengthString).deleteCharAt(i).toString();
		i = charOptionUseChar.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			charOptionUseChar = new StringBuffer(charOptionUseChar).deleteCharAt(i).toString();
		i = charOptionUseMBChar.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			charOptionUseMBChar = new StringBuffer(charOptionUseMBChar).deleteCharAt(i).toString();
		i = charOptionUseUnicode.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			charOptionUseUnicode = new StringBuffer(charOptionUseUnicode).deleteCharAt(i).toString();
		
		i = nationalCharOptionUseDBChar.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nationalCharOptionUseDBChar = new StringBuffer(nationalCharOptionUseDBChar).deleteCharAt(i).toString();
		i = nationalCharOptionUseUnicode.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nationalCharOptionUseUnicode = new StringBuffer(nationalCharOptionUseUnicode).deleteCharAt(i).toString();
		i = nationalCharOptionUseString.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nationalCharOptionUseString = new StringBuffer(nationalCharOptionUseString).deleteCharAt(i).toString();
		i = nationalCharOptionUseLimitedString.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nationalCharOptionUseLimitedString = new StringBuffer(nationalCharOptionUseLimitedString).deleteCharAt(i).toString();
		
		i = nameCaseOptionDoNotChange.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nameCaseOptionDoNotChange = new StringBuffer(nameCaseOptionDoNotChange).deleteCharAt(i).toString();
		i = nameCaseOptionLowercase.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nameCaseOptionLowercase = new StringBuffer(nameCaseOptionLowercase).deleteCharAt(i).toString();
		i = nameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscore.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscore =
				new StringBuffer(nameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscore).deleteCharAt(i).toString();

		i = nameUnderscoreOptionDoNotChange.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nameUnderscoreOptionDoNotChange = new StringBuffer(nameUnderscoreOptionDoNotChange).deleteCharAt(i).toString();

		i = nameUnderscoreOptionRemoveUndersores.indexOf("&"); //$NON-NLS-1$
		if (i >= 0)
			nameUnderscoreOptionRemoveUndersores = new StringBuffer(nameUnderscoreOptionRemoveUndersores).deleteCharAt(i).toString();


		EGL_CHAR_OPTION_NON_MNEMONIC_STRINGS = 
			new String[] {
				charOptionUseString
				//charOptionUseLimitedLengthString,
				//charOptionUseChar,
				//charOptionUseMBChar,
				//charOptionUseUnicode
			};
		
		EGL_NATIONAL_CHAR_OPTION_NON_MNEMONIC_STRINGS =
			new String[] {
				//nationalCharOptionUseDBChar,
				//nationalCharOptionUseUnicode,
				nationalCharOptionUseString
				//nationalCharOptionUseLimitedString
			};
		
		NAME_CASE_OPTION_NON_MNEMONIC_STRINGS =
			new String[] {
				nameCaseOptionDoNotChange,
				nameCaseOptionLowercase,
				nameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscore 
			};

		NAME_UNDERSCORE_OPTION_NON_MNEMONIC_STRINGS =
			new String[] {
				nameUnderscoreOptionDoNotChange,
				nameUnderscoreOptionRemoveUndersores,
			};
		
		TEXT_TYPE_FOR_DATE_OPTION_NON_MNEMONIC_STRINGS =
			new String[] {
				"",
				//IEGLConstants.KEYWORD_CHAR,
				//IEGLConstants.KEYWORD_UNICODE,
				IEGLConstants.KEYWORD_STRING
				//SQLConstants.LIMITED_STRING
			};
	}

	/**
	 * Get the message logger.
	 */
	public Logger getLogger() {
		Logger logger = null;
		try {
			LogManager.getLogManager().readConfiguration();

			// Create a logger named 'HyadesLoggingJava14Sample':
			logger = Logger.getLogger(PLUGIN_ID);

			// Set the logger to log all messages:
			logger.setLevel(Level.ALL);
		} catch (Throwable t) {
			System.out.println("ERROR - EGL SQL Plugin - getting logger unsuccessfully!"); //$NON-NLS-1$
			System.out.println("REASON: " + t.getMessage()); //$NON-NLS-1$
		}
		return logger;
	}

	public String getConnectionJNDIName() {
		return "";	//$NON-NLS-1$
	}

	public String getSecondaryAuthenticationID() {
		return "";	//$NON-NLS-1$
	}
	
	public String getNamedConnection() {
		String conName = getPreferenceStore().getString(ISQLPreferenceConstants.SQL_CONNECTION_NAMED_CONNECTION);
		return conName;
	}
}
