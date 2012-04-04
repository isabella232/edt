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

public interface ISQLPreferenceConstants {

	public static final String PREFIX = SQLPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

	// SQL database connections preference constants
	public static final String SQL_CONNECTION_NAMED_CONNECTION = PREFIX + "connectionNamedConnection"; //$NON-NLS-1$

	// SQL retrieve preference constants
	public static final String SQL_RETRIEVE_ITEM_CHAR_CONTROL_OPTION = PREFIX + "charOption"; //$NON-NLS-1$
	public static final String SQL_RETRIEVE_ITEM_NATIONAL_CHAR_CONTROL_OPTION = PREFIX + "nationalCharOption"; //$NON-NLS-1$
	public static final String SQL_RETRIEVE_ITEM_NAME_CASE_CONTROL_OPTION = PREFIX + "caseOption"; //$NON-NLS-1$	
	public static final String SQL_RETRIEVE_ITEM_NAME_UNDERSCORE_CONTROL_OPTION = PREFIX + "underscoreOption"; //$NON-NLS-1$
    public static final String SQL_RETRIEVE_PRIMARY_KEY_OPTION = PREFIX + "retrievePrimaryKeyOption"; //$NON-NLS-1$
    public static final String SQL_RETRIEVE_USE_CHAR_FOR_DATE_OPTION = PREFIX + "charForDateOption"; //$NON-NLS-1$
    public static final String SQL_RETRIEVE_USE_TEXT_TYPE_FOR_DATE_OPTION = PREFIX + "textTypeForDateOption"; //$NON-NLS-1$
    public static final String SQL_RETRIEVE_ADD_SQL_DATA_CODE_OPTION = PREFIX + "addSqlDataCodeOption"; //$NON-NLS-1$
    public static final String SQL_PROMPT_USERID_AND_PASSWORD_OPTION = PREFIX + "promptOption"; //$NON-NLS-1$
}

