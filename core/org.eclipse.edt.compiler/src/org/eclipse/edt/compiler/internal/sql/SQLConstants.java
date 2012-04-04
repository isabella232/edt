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
package org.eclipse.edt.compiler.internal.sql;


/**
 * Insert the type's description here.
 * 
 * 
 */
public interface SQLConstants {

	// Clause keywords and types
	public final static String BY = "by"; //$NON-NLS-1$
	public final static String COLUMNS = "columns"; //$NON-NLS-1$
	public final static String DELETE = "delete"; //$NON-NLS-1$
	public final static String EXECUTE = "execute"; //$NON-NLS-1$
	public final static String FOR = "for"; //$NON-NLS-1$
	public final static String FOR_UPDATE_OF = "for update of"; //$NON-NLS-1$
	public final static String FOR_UPDATE = "for update"; //$NON-NLS-1$
	public final static String FROM = "from"; //$NON-NLS-1$
	public final static String GROUP = "group"; //$NON-NLS-1$
	public final static String GROUP_BY = "group by"; //$NON-NLS-1$
	public final static String HAVING = "having"; //$NON-NLS-1$
	public final static String INSERT = "insert"; //$NON-NLS-1$
	public final static String INSERT_INTO = "insert into"; //$NON-NLS-1$
	public final static String INTO = "into"; //$NON-NLS-1$
	public final static String OF = "of"; //$NON-NLS-1$
	public final static String ORDER = "order"; //$NON-NLS-1$
	public final static String ORDER_BY = "order by"; //$NON-NLS-1$
	public final static String SELECT = "select"; //$NON-NLS-1$
	public final static String SET = "set"; //$NON-NLS-1$
	public final static String UPDATE = "update"; //$NON-NLS-1$
	public final static String USING = "using"; //$NON-NLS-1$
	public final static String VALUES = "values"; //$NON-NLS-1$
	public final static String WHERE = "where"; //$NON-NLS-1$
    public final static String LIMITED_STRING = "limited string"; //$NON-NLS-1$
   
	// SQL I/O types 
	public final static String ADD_IO_TYPE = "add"; //$NON-NLS-1$
	public final static String CLOSE_IO_TYPE = "close"; //$NON-NLS-1$
	public final static String DELETE_IO_TYPE = "delete"; //$NON-NLS-1$
	public final static String GET_IO_TYPE = "get"; //$NON-NLS-1$
	public final static String GET_FORUPDATE_IO_TYPE = "get forUpdate"; //$NON-NLS-1$	
	public final static String REPLACE_IO_TYPE = "replace"; //$NON-NLS-1$
	public final static String GET_BY_POSITION_IO_TYPE = "get by position"; //$NON-NLS-1$
	public final static String OPEN_IO_TYPE = "open"; //$NON-NLS-1$
	public final static String OPEN_FORUPDATE_IO_TYPE = "open forUpdate"; //$NON-NLS-1$
	public final static String EXECUTE_IO_TYPE = "execute"; //$NON-NLS-1$

	public final static String[] SQL_IO_TYPE_STRINGS =
		{ ADD_IO_TYPE, CLOSE_IO_TYPE, DELETE_IO_TYPE, GET_IO_TYPE, REPLACE_IO_TYPE, OPEN_IO_TYPE, EXECUTE_IO_TYPE, };

	public final static String[] MODIFIABLE_SQL_IO_TYPE_STRINGS =
		{ ADD_IO_TYPE, GET_IO_TYPE, REPLACE_IO_TYPE, OPEN_IO_TYPE, EXECUTE_IO_TYPE, };

	// Delimiters
	public final static String SINGLE_QUOTE = "'"; //$NON-NLS-1$
    public final static String SINGLE_QUOTES = "''"; //$NON-NLS-1$
	public final static String DOUBLE_QUOTE = "\""; //$NON-NLS-1$
	public final static String HOST_VARIABLE_INDICATOR = ":"; //$NON-NLS-1$
	public final static String COLUMN_IDENTIFIER = "!"; //$NON-NLS-1$
	public final static String QUALIFICATION_DELIMITER = "."; //$NON-NLS-1$
	public final static String LEFT_BRACKET = "["; //$NON-NLS-1$
	public final static String RIGHT_BRACKET = "]"; //$NON-NLS-1$
	public final static String COMMA = ","; //$NON-NLS-1$
	public final static String SEMICOLON = ";"; //$NON-NLS-1$
	public final static String COMMA_AND_SPACE = ", "; //$NON-NLS-1$
	public final static String EQUALS = " = "; //$NON-NLS-1$
	public final static String GREATER_THAN_OR_EQUAL = " >= "; //$NON-NLS-1$
	public final static String GREATER_THAN = " > "; //$NON-NLS-1$	
	public final static String CR = "\r"; //$NON-NLS-1$
	public final static String LF = "\n"; //$NON-NLS-1$
	public final static String CRLF = "\r\n"; //$NON-NLS-1$
	public final static String TAB = "\t"; //$NON-NLS-1$
	public final static String LPAREN = "("; //$NON-NLS-1$
	public final static String RPAREN = ")"; //$NON-NLS-1$
	public final static String SPACE = " "; //$NON-NLS-1$
	public final static String PARAMETER_MARKER = "?"; //$NON-NLS-1$

	// Misc. strings
	public final static String SYSTEMID = "SYSTEMID"; //$NON-NLS-1$
    public final static String SQL = "SQL"; //$NON-NLS-1$
    public final static String AND = "and"; //$NON-NLS-1$
	public final static String OR = "or"; //$NON-NLS-1$
	public final static String ASC = "asc"; //$NON-NLS-1$
	public final static String WHERE_CURRENT_OF_CLAUSE = "where current of resultSetID"; //$NON-NLS-1$
	public final static String CLOSE_STATEMENT = "close resultSetID"; //$NON-NLS-1$
	public final static String GET_BY_POSITION_STATEMENT = "fetch resultSetID using descriptor sqlda"; //$NON-NLS-1$
	public final static int FORMAT_WIDTH = 56; // in bytes
    
    //SQL DataTypes
    public final static String CHARACTER = "CHARACTER";                 //$NON-NLS-1$
    public final static String LONGVARCHAR = "LONG VARCHAR";            //$NON-NLS-1$
    public final static String LONGVAR = "LONGVAR";                     //$NON-NLS-1$
    public final static String VARGRAPHIC = "VARGRAPHIC";               //$NON-NLS-1$
    public final static String VARG = "VARG";                           //$NON-NLS-1$
    public final static String LONGVARGRAPHIC = "LONG VARGRAPHIC";      //$NON-NLS-1$
    public final static String LONGVARG = "LONGVARG";                   //$NON-NLS-1$
    public final static String TIMESTMP = "TIMESTMP";                   //$NON-NLS-1$
    public final static String DOUBLE = "DOUBLE";                       //$NON-NLS-1$
    public final static String INTEGER = "INTEGER";                     //$NON-NLS-1$
    public final static String VARCHAR = "VARCHAR";                     //$NON-NLS-1$
    public final static String GRAPHIC = "GRAPHIC";                     //$NON-NLS-1$
    public final static String DBCLOB = "DBCLOB";                       //$NON-NLS-1$
    public final static String DATALINK = "DATALINK";                   //$NON-NLS-1$
    public final static String NVARCHAR = "NVARCHAR";                   //$NON-NLS-1$
    public final static String NCHAR = "NCHAR";                         //$NON-NLS-1$
    public final static String NTEXT = "NTEXT";                         //$NON-NLS-1$
    public final static String UNIQUEIDENTIFIER = "UNIQUEIDENTIFIER";   //$NON-NLS-1$
    public final static String NCLOB = "NCLOB";                         //$NON-NLS-1$
    public final static String NVARCHAR2 = "NVARCHAR2";                 //$NON-NLS-1$
    public final static String BFILE = "BFILE";                         //$NON-NLS-1$
    public final static String IMAGE = "IMAGE";                         //$NON-NLS-1$


}
