/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal;

public interface EGLUIMessageKeys {
//	1000 - 1050: used for SQL and DLI messages   
	public static final String SQL_MESSAGE_QUESTION_RETRIEVE_WILL_DELETE_ITEM_THINGS = "1000"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_SQL_VALIDATION_FAILED = "1001"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_WARNING_STATEMENT_ACTION_COMPLETED_WITH_ERRORS = "1002"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_ERROR_STATEMENT_ACTION_FAILED = "1003"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_INFO_NO_DECLARATION_FOUND = "1004"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_INFO_DUPLICATE_DECLARATIONS_FOUND = "1005"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_INFO_RECORD_PART_IN_SPECIFIED_FILE_USED = "1006"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_ERROR_RECORD_PART_REQUIRED = "1007"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_DYNAMIC_RECORD_NOT_ALLOWED = "1008"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_SQL_STATEMENT_NOT_ALLOWED_FOR_ADD_STATEMENT_WITH_DYNAMIC_ARRAY = "1009"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_ERROR_STATEMENT_ALREADY_SPECIFIED = "1010"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_SQL_STATEMENT_NOT_ALLOWED_WITH_PREPARED_STATEMENT_REFERENCE = "1011"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_ACTION_NOT_SUPPORTED_FOR_STATEMENT = "1012"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_SQL_STATEMENT_NOT_ALLOWED_FOR_EGL_STATEMENT = "1013"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_SQL_STATEMENT_ACTIONS_SUPPORTED_ONLY_FOR_EGL_SQL_STATEMENTS = "1014"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_SQL_RECORD_VARIABLE_NOT_SPECIFIED_ON_STATEMENT = "1015"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_INFO_DUPLICATE_RECORD_PARTS_FOUND = "1016"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_INFO_SQL_STATEMENT_VALIDATION_SUCCESSFUL = "1017"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_INTO_CLAUSE_ALREADY_SPECIFIED = "1018"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_INTO_CLAUSE_NOT_ALLOWED = "1019"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_NO_SQL_STATEMENT_OR_INTO_CLAUSE_TO_RESET = "1020"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_NO_SQL_STATEMENT_OR_INTO_CLAUSE_TO_REMOVE = "1021"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_ONLY_VIEW_SUPPORTED_FOR_CLOSE_STATEMENT = "1022"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_INFO_EGL_STATEMENT_MUST_BE_SYNTACTICALLY_CORRECT = "1023"; //$NON-NLS-1$
	public static final String SQL_DLI_MESSAGE_INFO_VARIABLE_OR_PART_NOT_FOUND = "1024"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_INTO_CLAUSE_ONLY_USED_FOR_SQL_SELECT = "1025"; //$NON-NLS-1$
	public static final String SQL_MESSAGE_ERROR_INTO_CLAUSE_NOT_ALLOWED_FOR_DYNAMIC_ARRAYS = "1026"; //$NON-NLS-1$	
    public static final String SQL_MESSAGE_ERROR_ACTION_FAILED = "1027";		 //$NON-NLS-1$
    public static final String SQL_MESSAGE_ERROR_PREPARE_STATEMENT_NOT_ALLOWED = "1028"; //$NON-NLS-1$
    public static final String SQL_MESSAGE_ERROR_ACTION_NOT_SUPPORTED_FOR_TABLE_NAME_HOST_VARIABLE = "1029"; //$NON-NLS-1$
    
    public static final String DLI_MESSAGE_ERROR_DLI_STATEMENT_ACTIONS_SUPPORTED_ONLY_FOR_EGL_DLI_STATEMENTS = "1030"; //$NON-NLS-1$
    public static final String DLI_MESSAGE_ERROR_NO_DLI_STATEMENT_TO_RESET = "1031"; //$NON-NLS-1$
    public static final String DLI_MESSAGE_ERROR_NO_DLI_STATEMENT_TO_REMOVE = "1032"; //$NON-NLS-1$
    public static final String DLI_MESSAGE_ERROR_IO_OBJECT_NOT_DLI_RECORD = "1033"; //$NON-NLS-1$
    public static final String DLI_MESSAGE_ERROR_DYNAMIC_RECORD_NOT_ALLOWED = "1034"; //$NON-NLS-1$
    public static final String DLI_MESSAGE_ERROR_IO_OBJECT_NOT_DLI_RECORD_OR_ARRAY_OF_DLI_SEGMENTS = "1035"; //$NON-NLS-1$
}
