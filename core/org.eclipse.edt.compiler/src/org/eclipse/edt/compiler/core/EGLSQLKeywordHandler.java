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
package org.eclipse.edt.compiler.core;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author fDollar
 *
 * This is the public interface to for all EGL SQL keywords
 */
public class EGLSQLKeywordHandler {

	// ***IF YOU MODIFY THIS LIST YOU NEED TO REGEN THE CONSTANTS IN IEGLConstants USING***
	// org.eclipse.edt.compiler.internal.dev.tools.EGLSQLKeywordTool
	// You also need to post a note in the Team Room so everyone is aware.
	// VG to EGL migration can't use this list, so they need to make 
	// appropriate analogous changes. 

	private static String[] sqlKeywordNames =
		{
			"absolute", //$NON-NLS-1$
			"action", //$NON-NLS-1$
			"add", //$NON-NLS-1$
			"alias", //$NON-NLS-1$
			"all", //$NON-NLS-1$
			"allocate", //$NON-NLS-1$
			"alter", //$NON-NLS-1$
			"and", //$NON-NLS-1$
			"any", //$NON-NLS-1$
			"are", //$NON-NLS-1$
			"as", //$NON-NLS-1$
			"asc", //$NON-NLS-1$
			"assertion", //$NON-NLS-1$
			"at", //$NON-NLS-1$
			"authorization", //$NON-NLS-1$
			"avg", //$NON-NLS-1$
			"begin", //$NON-NLS-1$
			"between", //$NON-NLS-1$
			"bigint", //$NON-NLS-1$
			"binaryLargeObject", //$NON-NLS-1$
			"bit", //$NON-NLS-1$
			"bit_length", //$NON-NLS-1$
			"blob", //$NON-NLS-1$
			"boolean", //$NON-NLS-1$
			"both", //$NON-NLS-1$
			"by", //$NON-NLS-1$
			"call", //$NON-NLS-1$
			"cascade", //$NON-NLS-1$
			"cascaded", //$NON-NLS-1$
			"case", //$NON-NLS-1$
			"cast", //$NON-NLS-1$
			"catalog", //$NON-NLS-1$
			"char", //$NON-NLS-1$
			"char_length", //$NON-NLS-1$
			"character", //$NON-NLS-1$
			"character_length", //$NON-NLS-1$
			"characterLargeObject", //$NON-NLS-1$
			"characterVarying", //$NON-NLS-1$
			"charLargeObject", //$NON-NLS-1$
			"charVarying", //$NON-NLS-1$
			"check", //$NON-NLS-1$
			"clob", //$NON-NLS-1$
			"close", //$NON-NLS-1$
			"coalesce", //$NON-NLS-1$
			"collate", //$NON-NLS-1$
			"collation", //$NON-NLS-1$
			"column", //$NON-NLS-1$
			"comment", //$NON-NLS-1$
			"commit", //$NON-NLS-1$
			"connect", //$NON-NLS-1$
			"connection", //$NON-NLS-1$
			"constraint", //$NON-NLS-1$
			"constraints", //$NON-NLS-1$
			"continue", //$NON-NLS-1$
			"convert", //$NON-NLS-1$
			"copy", //$NON-NLS-1$
			"corresponding", //$NON-NLS-1$
			"count", //$NON-NLS-1$
			"create", //$NON-NLS-1$
			"cross", //$NON-NLS-1$
			"current", //$NON-NLS-1$
			"current_date", //$NON-NLS-1$
			"current_time", //$NON-NLS-1$
			"current_timestamp", //$NON-NLS-1$
			"current_user", //$NON-NLS-1$
			"cursor", //$NON-NLS-1$
			"data", //$NON-NLS-1$
			"database", //$NON-NLS-1$
			"date", //$NON-NLS-1$
			"dateTime", //$NON-NLS-1$
			"day", //$NON-NLS-1$
			"deallocate", //$NON-NLS-1$
			"dec", //$NON-NLS-1$
			"decimal", //$NON-NLS-1$
			"declare", //$NON-NLS-1$
			"default", //$NON-NLS-1$
			"deferrable", //$NON-NLS-1$
			"deferred", //$NON-NLS-1$
			"delete", //$NON-NLS-1$
			"desc", //$NON-NLS-1$
			"describe", //$NON-NLS-1$
			"diagnostics", //$NON-NLS-1$
			"disconnect", //$NON-NLS-1$
			"distinct", //$NON-NLS-1$
			"domain", //$NON-NLS-1$
			"double", //$NON-NLS-1$
			"doublePrecision", //$NON-NLS-1$
			"drop", //$NON-NLS-1$
			"else", //$NON-NLS-1$
			"end", //$NON-NLS-1$
			"endExec", //$NON-NLS-1$
			"escape", //$NON-NLS-1$
			"except", //$NON-NLS-1$
			"exception", //$NON-NLS-1$
			"exec", //$NON-NLS-1$
			"execute", //$NON-NLS-1$
			"exists", //$NON-NLS-1$
			"explain", //$NON-NLS-1$
			"external", //$NON-NLS-1$
			"extract", //$NON-NLS-1$
			"false", //$NON-NLS-1$
			"fetch", //$NON-NLS-1$
			"first", //$NON-NLS-1$
			"float", //$NON-NLS-1$
			"for", //$NON-NLS-1$
			"foreign", //$NON-NLS-1$
			"found", //$NON-NLS-1$
			"from", //$NON-NLS-1$
			"full", //$NON-NLS-1$
			"get", //$NON-NLS-1$
			"getCurrentConnection", //$NON-NLS-1$
			"global", //$NON-NLS-1$
			"go", //$NON-NLS-1$
			"goto", //$NON-NLS-1$
			"graphic", //$NON-NLS-1$
			"grant", //$NON-NLS-1$
			"group", //$NON-NLS-1$
			"having", //$NON-NLS-1$
			"hour", //$NON-NLS-1$
			"identity", //$NON-NLS-1$
			"image", //$NON-NLS-1$
			"immediate", //$NON-NLS-1$
			"in", //$NON-NLS-1$
			"index", //$NON-NLS-1$
			"indicator", //$NON-NLS-1$
			"initially", //$NON-NLS-1$
			"inner", //$NON-NLS-1$
			"input", //$NON-NLS-1$
			"insensitive", //$NON-NLS-1$
			"insert", //$NON-NLS-1$
			"int", //$NON-NLS-1$
			"integer", //$NON-NLS-1$
			"intersect", //$NON-NLS-1$
			"into", //$NON-NLS-1$
			"is", //$NON-NLS-1$
			"isolation", //$NON-NLS-1$
			"join", //$NON-NLS-1$
			"key", //$NON-NLS-1$
			"language", //$NON-NLS-1$
			"last", //$NON-NLS-1$
			"leading", //$NON-NLS-1$
			"left", //$NON-NLS-1$
			"level", //$NON-NLS-1$
			"like", //$NON-NLS-1$
			"local", //$NON-NLS-1$
			"long", //$NON-NLS-1$
			"longint", //$NON-NLS-1$
			"lower", //$NON-NLS-1$
			"ltrim", //$NON-NLS-1$
			"match", //$NON-NLS-1$
			"max", //$NON-NLS-1$
			"min", //$NON-NLS-1$
			"minute", //$NON-NLS-1$
			"module", //$NON-NLS-1$
			"month", //$NON-NLS-1$
			"national", //$NON-NLS-1$
			"nationalCharacter", //$NON-NLS-1$
			"nationalCharacterLargeObject", //$NON-NLS-1$
			"nationalCharacterVarying", //$NON-NLS-1$
			"nationalCharLargeObject", //$NON-NLS-1$
			"nationalCharVarying", //$NON-NLS-1$
			"natural", //$NON-NLS-1$
			"nchar", //$NON-NLS-1$
			"ncharVarying", //$NON-NLS-1$
			"nclob", //$NON-NLS-1$
			"next", //$NON-NLS-1$
			"no", //$NON-NLS-1$
			"not", //$NON-NLS-1$
			"null", //$NON-NLS-1$
			"nullIf", //$NON-NLS-1$
			"number", //$NON-NLS-1$
			"numeric", //$NON-NLS-1$
			"octet_length", //$NON-NLS-1$
			"of", //$NON-NLS-1$
			"on", //$NON-NLS-1$
			"only", //$NON-NLS-1$
			"open", //$NON-NLS-1$
			"option", //$NON-NLS-1$
			"or", //$NON-NLS-1$
			"order", //$NON-NLS-1$
			"outer", //$NON-NLS-1$
			"output", //$NON-NLS-1$
			"overlaps", //$NON-NLS-1$
			"pad", //$NON-NLS-1$
			"partial", //$NON-NLS-1$
			"position", //$NON-NLS-1$
			"prepare", //$NON-NLS-1$
			"preserve", //$NON-NLS-1$
			"primary", //$NON-NLS-1$
			"prior", //$NON-NLS-1$
			"privileges", //$NON-NLS-1$
			"procedure", //$NON-NLS-1$
			"public", //$NON-NLS-1$
			"raw", //$NON-NLS-1$
			"read", //$NON-NLS-1$
			"real", //$NON-NLS-1$
			"references", //$NON-NLS-1$
			"relative", //$NON-NLS-1$
			"restrict", //$NON-NLS-1$
			"revoke", //$NON-NLS-1$
			"right", //$NON-NLS-1$
			"rollback", //$NON-NLS-1$
			"rows", //$NON-NLS-1$
			"rtrim", //$NON-NLS-1$
			"runtimeStatistics", //$NON-NLS-1$
			"schema", //$NON-NLS-1$
			"scroll", //$NON-NLS-1$
			"second", //$NON-NLS-1$
			"section", //$NON-NLS-1$
			"select", //$NON-NLS-1$
			"session", //$NON-NLS-1$
			"session_user", //$NON-NLS-1$
			"set", //$NON-NLS-1$
			"signal", //$NON-NLS-1$
			"size", //$NON-NLS-1$
			"smallint", //$NON-NLS-1$
			"some", //$NON-NLS-1$
			"space", //$NON-NLS-1$
			"sql", //$NON-NLS-1$
			"sqlCondition", //$NON-NLS-1$
			"sqlcode", //$NON-NLS-1$
			"sqlerror", //$NON-NLS-1$
			"sqlstate", //$NON-NLS-1$
			"substr", //$NON-NLS-1$
			"substring", //$NON-NLS-1$
			"sum", //$NON-NLS-1$
			"system_user", //$NON-NLS-1$
			"table", //$NON-NLS-1$
			"tablespace", //$NON-NLS-1$
			"temporary", //$NON-NLS-1$
			"terminate", //$NON-NLS-1$
			"then", //$NON-NLS-1$
			"time", //$NON-NLS-1$
			"timestamp", //$NON-NLS-1$
			"timezone_hour", //$NON-NLS-1$
			"timezone_minute", //$NON-NLS-1$
			"tinyint", //$NON-NLS-1$
			"to", //$NON-NLS-1$
			"trailing", //$NON-NLS-1$
			"transaction", //$NON-NLS-1$
			"translate", //$NON-NLS-1$
			"translation", //$NON-NLS-1$
			"trim", //$NON-NLS-1$
			"true", //$NON-NLS-1$
			"uncatalog", //$NON-NLS-1$
			"union", //$NON-NLS-1$
			"unique", //$NON-NLS-1$
			"unknown", //$NON-NLS-1$
			"update", //$NON-NLS-1$
			"upper", //$NON-NLS-1$
			"usage", //$NON-NLS-1$
			"user", //$NON-NLS-1$
			"using", //$NON-NLS-1$
			"values", //$NON-NLS-1$
			"varbinary", //$NON-NLS-1$
			"varchar", //$NON-NLS-1$
			"varchar2", //$NON-NLS-1$
			"vargraphic", //$NON-NLS-1$
			"varying", //$NON-NLS-1$
			"view", //$NON-NLS-1$
			"when", //$NON-NLS-1$
			"whenever", //$NON-NLS-1$
			"where", //$NON-NLS-1$
			"with", //$NON-NLS-1$
			"work", //$NON-NLS-1$
			"write", //$NON-NLS-1$
			"year", //$NON-NLS-1$
			"zone" //$NON-NLS-1$
			};


	private static String[] sqlClauseKeywordNames =
		{
			"call", //$NON-NLS-1$
			"from", //$NON-NLS-1$
			"group", //$NON-NLS-1$
			"having", //$NON-NLS-1$
			"insert", //$NON-NLS-1$
			"order", //$NON-NLS-1$
			"select", //$NON-NLS-1$
			"set", //$NON-NLS-1$
			"union", //$NON-NLS-1$
			"update", //$NON-NLS-1$
			"values", //$NON-NLS-1$
			"where" //$NON-NLS-1$
			};


	private static Set sqlKeywordNamesLowerCase = new TreeSet();
	
	static{
		for (int i = 0; i < sqlKeywordNames.length; i++) {
			sqlKeywordNamesLowerCase.add(sqlKeywordNames[i].toLowerCase(Locale.ENGLISH));
		}
	}
	
	private static Set sqlClauseKeywordNamesLowerCase = new TreeSet();
	
	static{
		for (int i = 0; i < sqlClauseKeywordNames.length; i++) {
			sqlClauseKeywordNamesLowerCase.add(sqlClauseKeywordNames[i].toLowerCase(Locale.ENGLISH));
		}
	}
	
	/**
	 * @return
	 */
	public static String[] getSQLKeywordNames() {
		return sqlKeywordNames;
	}

	/**
	 * return the List of SQL keyword names in lowercase
	 */
	public static String[] getSQLKeywordNamesToLowerCase() {
		return (String[])sqlKeywordNamesLowerCase.toArray(new String[sqlKeywordNamesLowerCase.size()]);
	}

	/**
	 * return the List of SQL keyword names in lowercase
	 */
	public static Set getSQLKeywordNamesToLowerCaseAsSet() {
		return sqlKeywordNamesLowerCase;
	}

	/**
	 * @return
	 */
	public static String[] getSQLClauseKeywordNames() {
		return sqlClauseKeywordNames;
	}

	/**
	 * return the List of SQL keyword names in lowercase
	 */
	public static String[] getSQLClauseKeywordNamesToLowerCase() {
		return (String[])sqlClauseKeywordNamesLowerCase.toArray(new String[sqlClauseKeywordNamesLowerCase.size()]);
	}
	
	/**
	 * return the List of SQL keyword names in lowercase
	 */
	public static Set getSQLClauseKeywordNamesToLowerCaseAsSet() {
		return sqlClauseKeywordNamesLowerCase;
	}

	/**
	 * return the List of SQL keyword names as a comma separated string
	 */
	public static String getSQLClauseKeywordNamesCommaSeparatedString() {
		return commaSeparatedString(getSQLClauseKeywordNamesToLowerCase());
	}
	
	private static String commaSeparatedString (String[] validStrings){
		StringBuffer newStr = new StringBuffer(""); //$NON-NLS-1$
	
		if ( validStrings.length > 0 )
		{ // add first token
			newStr.append(validStrings[0]);	
		}
	
		//now loop to handle rest of strings
		for (int ii= 1; ii<validStrings.length; ii++)
		{
			newStr.append(", "); //$NON-NLS-1$
			newStr.append(validStrings[ii]);
		}		

		return newStr.toString();
	}
}
