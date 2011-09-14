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
package org.eclipse.edt.ide.sql;

public class SQLConstants {
	//EGL Supported Vendor Types
	public static final int DB2UDB = 1;
	public static final int DB2UDBAS400 = 2;
	public static final int DB2UDBOS390 = 3;
	public static final int INFORMIX = 4;
	public static final int ORACLE = 5;
	public static final int MSSQLSERVER = 6;
	public static final int DERBY = 7;
	public static final int GENERIC = 8;
	public static final int DB2UDBVSE = 9;

	public static final String DB2UDB_NAME = "DB2 UDB";					//$NON-NLS-1$
	public static final String DB2UDBAS400_NAME = "DB2 UDB iSeries"; 	//$NON-NLS-1$
	public static final String DB2UDBOS390_NAME = "DB2 UDB zSeries";	//$NON-NLS-1$
	public static final String DB2UDBVSE_NAME = "DB2 UDB zVSE";			//$NON-NLS-1$
	public static final String INFORMIX_NAME = "Informix";				//$NON-NLS-1$
	public static final String ORACLE_NAME = "Oracle";					//$NON-NLS-1$
	public static final String MSSQLSERVER_NAME = "SQL Server";			//$NON-NLS-1$
	public static final String CLOUDSCAPE_NAME = "Cloudscape";			//$NON-NLS-1$
	public static final String DERBY_NAME = "Derby";					//$NON-NLS-1$
	public static final String MYSQL_NAME = "MySql";					//$NON-NLS-1$
	public static final String SYBASE_NAME = "Sybase";					//$NON-NLS-1$
	public static final String GENERIC_JDBC_NAME = "Generic JDBC";		//$NON-NLS-1$
	public static final String DB2_ALIAS_NAME = "DB2 ALIAS";			//$NON-NLS-1$
	
	public static final String DB2UDB_V_7_2= "V7.2";				//$NON-NLS-1$
	
	public static final String EGL_CUSTOM_PROPERTIES = "EGL_CUSTOM_PROPERTIES";
	public static final String EGL_SECONDARY_ID_CUSTOM_PROPERTY = "EGL_SECONDARY_ID";	//$NON-NLS-1$
	public static final String EGL_DB_PASSWORD_CUSTOM_PROPERTY = "EGL_DB_PASSWORD";		//$NON-NLS-1$
		
	// Clause keywords and types
	public static final String BY = "by"; //$NON-NLS-1$
	public static final String COLUMNS = "columns"; //$NON-NLS-1$
	public static final String DELETE_FROM = "delete from"; //$NON-NLS-1$
	public static final String EXECUTE = "execute"; //$NON-NLS-1$
	public static final String FOR = "for"; //$NON-NLS-1$
	public static final String FOR_UPDATE_OF = "for update of"; //$NON-NLS-1$
	public static final String FROM = "from"; //$NON-NLS-1$
	public static final String GROUP = "group"; //$NON-NLS-1$
	public static final String GROUP_BY = "group by"; //$NON-NLS-1$
	public static final String HAVING = "having"; //$NON-NLS-1$
	public static final String INSERT = "insert"; //$NON-NLS-1$
	public static final String INSERT_INTO = "insert into"; //$NON-NLS-1$
	public static final String INTO = "into"; //$NON-NLS-1$
	public static final String OF = "of"; //$NON-NLS-1$
	public static final String ORDER = "order"; //$NON-NLS-1$
	public static final String ORDER_BY = "order by"; //$NON-NLS-1$
	public static final String SELECT = "select"; //$NON-NLS-1$
	public static final String SET = "set"; //$NON-NLS-1$
	public static final String UPDATE = "update"; //$NON-NLS-1$
	public static final String VALUES = "values"; //$NON-NLS-1$
	public static final String WHERE = "where"; //$NON-NLS-1$
    public static final String LIMITED_STRING = "limited string"; //$NON-NLS-1$
   
	// SQL I/O types 
	public static final String ADD_IO_TYPE = "add"; //$NON-NLS-1$
	public static final String CLOSE_IO_TYPE = "close"; //$NON-NLS-1$
	public static final String DELETE_IO_TYPE = "delete"; //$NON-NLS-1$
	public static final String GET_IO_TYPE = "get"; //$NON-NLS-1$
	public static final String GET_FORUPDATE_IO_TYPE = "get forUpdate"; //$NON-NLS-1$	
	public static final String REPLACE_IO_TYPE = "replace"; //$NON-NLS-1$
	public static final String GET_BY_POSITION_IO_TYPE = "get by position"; //$NON-NLS-1$
	public static final String OPEN_IO_TYPE = "open"; //$NON-NLS-1$
	public static final String OPEN_FORUPDATE_IO_TYPE = "open forUpdate"; //$NON-NLS-1$
	public static final String EXECUTE_IO_TYPE = "execute"; //$NON-NLS-1$

	public static final String[] SQL_IO_TYPE_STRINGS =
		{ ADD_IO_TYPE, CLOSE_IO_TYPE, DELETE_IO_TYPE, GET_IO_TYPE, REPLACE_IO_TYPE, OPEN_IO_TYPE, EXECUTE_IO_TYPE, };

	public static final String[] MODIFIABLE_SQL_IO_TYPE_STRINGS =
		{ ADD_IO_TYPE, GET_IO_TYPE, REPLACE_IO_TYPE, OPEN_IO_TYPE, EXECUTE_IO_TYPE, };

	// Delimiters
	public static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$
    public static final String SINGLE_QUOTES = "''"; //$NON-NLS-1$
	public static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$
	public static final String HOST_VARIABLE_INDICATOR = ":"; //$NON-NLS-1$
	public static final String COLUMN_IDENTIFIER = "!"; //$NON-NLS-1$
	public static final String QUALIFICATION_DELIMITER = "."; //$NON-NLS-1$
	public static final String LEFT_BRACKET = "["; //$NON-NLS-1$
	public static final String RIGHT_BRACKET = "]"; //$NON-NLS-1$
	public static final String COMMA = ","; //$NON-NLS-1$
	public static final String COMMA_AND_SPACE = ", "; //$NON-NLS-1$
	public static final String EQUALS = " = "; //$NON-NLS-1$
	public static final String GREATER_THAN_OR_EQUAL = " >= "; //$NON-NLS-1$
	public static final String GREATER_THAN = " > "; //$NON-NLS-1$	
	public static final String CR = "\r"; //$NON-NLS-1$
	public static final String LF = "\n"; //$NON-NLS-1$
	public static final String CRLF = "\r\n"; //$NON-NLS-1$
	public static final String TAB = "\t"; //$NON-NLS-1$
	public static final String LPAREN = "("; //$NON-NLS-1$
	public static final String RPAREN = ")"; //$NON-NLS-1$
	public static final String SPACE = " "; //$NON-NLS-1$
	public static final String PARAMETER_MARKER = "?"; //$NON-NLS-1$

	// Misc. strings
	public static final String SYSTEMID = "SYSTEMID"; //$NON-NLS-1$
    public static final String SQL = "SQL"; //$NON-NLS-1$
    public static final String AND = "and"; //$NON-NLS-1$
	public static final String OR = "or"; //$NON-NLS-1$
	public static final String ASC = "asc"; //$NON-NLS-1$
	public static final String WHERE_CURRENT_OF_CLAUSE = "where current of resultSetID"; //$NON-NLS-1$
	public static final String CLOSE_STATEMENT = "close resultSetID"; //$NON-NLS-1$
	public static final String GET_BY_POSITION_STATEMENT = "fetch resultSetID using descriptor sqlda"; //$NON-NLS-1$
	public static final int FORMAT_WIDTH = 56; // in bytes
	
	public static final int DATABASE_PROFILE_PROPERTY_LENGTH =10;//$NON-NLS-1$
	public static final String DATABASE_DEFAULT_SCHEMA_ID="org.eclipse.datatools.connectivity.db.defaultSchema";
}
