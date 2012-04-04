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

import org.eclipse.osgi.util.NLS;

public class SQLNlsStrings extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.edt.ide.sql.SQLResources"; //$NON-NLS-1$

	private SQLNlsStrings() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, SQLNlsStrings.class);
	}

	// SQL Retrieve Preferences
	public static String NameCaseOptionDoNotChangeLabel;
	public static String NameCaseOptionLowercaseLabel;
	public static String NameCaseOptionLowercaseAndCapitalizeLetterAfterUnderscoreLabel;
	public static String NameUnderscoreOptionDoNotChangeLabel;
	public static String NameUnderscoreOptionRemoveUndersoresLabel;
	public static String CharacterControlLabel;
	public static String CharacterOptionCharLabel;
	public static String CharacterOptionMBCharLabel;
	public static String CharacterOptionStringLabel;
	public static String CharacterOptionLimitedLengthStringLabel;
	public static String CharacterOptionUnicodeLabel;
	public static String NationalCharControlLabel;
	public static String NationalCharOptionDBCharLabel;
	public static String NationalCharOptionUnicodeLabel;
	public static String NationalCharOptionStringLabel;
	public static String NationalCharOptionLimitedStringLabel;
	public static String NameCaseControlLabel;
	public static String NameUnderscoreControlLabel;
	public static String RetrievePrimaryKeyLabel;
	//public static String CobolCompatibleRecordLabel;
	public static String SQLUserIDAndPasswordLabel;
	public static String DateTypesControlLabel;
	public static String DateTypesOptionDefaultLabel;
	public static String DateTypesOptionCharLabel;
	public static String DateTypesOptionUnicodeLabel;
	public static String DateTypesOptionStringLabel;
	public static String DateTypesOptionLimitedStringLabel;
	public static String AddSqlDataCodeLabel;
	
	// SQL database connections preferences - used by ui plugin
	public static String SQL_CONNECTION_LABEL_GROUP;
	public static String SQL_CONNECTION_LABEL_COMBO;
	public static String SQL_CONNECTION_NEW_BUTTON;
	public static String SQL_CONNECTION_LABEL_PROPERTIES;
	public static String SQL_CONNECTION_COLUMN_PROPERTY;
	public static String SQL_CONNECTION_COLUMN_VALUE;
	public static String SQL_CONNECTION_DATABASE_PROPERTY;
	public static String SQL_CONNECTION_DBNAME_PROPERTY;
	public static String SQL_CONNECTION_JDBC_PROPERTY;
	public static String SQL_CONNECTION_LOCATION_PROPERTY;
	public static String SQL_CONNECTION_URL_PROPERTY;
	public static String SQL_CONNECTION_USER_ID_PROPERTY;
	public static String SQL_CONNECTION_USER_PASSWORD_PROPERTY;
	public static String SQL_CONNECTION_TEST_BUTTON;
	public static String SQL_CONNECTION_EDIT_BUTTON;
	public static String SQL_CONNECTION_LABEL_PASSWORD;
	public static String SQL_CONNECTION_LABEL_AUTH_ID;
	public static String SQL_CONNECTION_PROGRESS_BAR;
	public static String SQL_CONNECTION_SUCCESS_MSG;
	public static String SQL_CONNECTION_ERROR_MSG;
	public static String SQL_CONNECTION_FAILURE_MSG;
	public static String SQL_CONNECTION_ERRTITLE;
	public static String SQL_CONNECTION_DIALOG_TITLE;
	public static String SQL_CONNECTION_DONE_MSG;
	public static String SQL_CONNECTION_PROPERTIES_FOR;
	public static String SQL_CONNECTION_JNDI_PROPERTY;
	public static String SQL_BINDING_NAME_PROPERTY;
	public static String SQL_CONNECTION_DEFAULT_SCHEMA_PROPERTY;
	
	//	SQL userid and password prompt dialog
	public static String SQLUserIDAndPasswordDialogTitle;
	public static String SQLUserIDAndPasswordDialogMessage;
	public static String SQLUserIDAndPasswordDialogDatabaseLabel;
	public static String SQLUserIDAndPasswordDialogUserIDLabel;
	public static String SQLUserIDAndPasswordDialogPasswordLabel;
	public static String SQLUserIDAndPasswordDialogRememberLabel;
}
