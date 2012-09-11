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
package org.eclipse.edt.ide.ui.internal.formatting;

import java.util.Map;

import org.eclipse.edt.ide.ui.internal.formatting.ui.ProfileManager.PreferenceSettingValue;

public class CodeFormatterConstants {
		
	/**
	 * this is used to connect CategoryID and prefID 
	 */
	private static final String PREFKEY_DELIMITER = "."; //$NON-NLS-1$
	public static final String DISPLAY_TREE_DELIMITER = "."; //$NON-NLS-1$
	
	public static final String FORMATTER_CATEGORY_GENERALSETTINGS = "generalSettings"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_TABPOLICY = FORMATTER_CATEGORY_GENERALSETTINGS + ".tabPolicy"; //$NON-NLS-1$
	public static final int FORMATTER_PREF_TABPOLICY_SPACE_ONLY = 0;
	public static final int FORMATTER_PREF_TABPOLICY_TAB_ONLY = 1;
	public static final String FORMATTER_PREF_INDENT_SIZE = FORMATTER_CATEGORY_GENERALSETTINGS + ".indentationSize"; //$NON-NLS-1$
	public static final int FORMATTER_PREF_KEYWORD_NOCHANGE = 0;
	public static final int FORMATTER_PREF_KEYWORD_UPPER = 1;
	public static final int FORMATTER_PREF_KEYWORD_LOWER = 2;
	public static final int FORMATTER_PREF_KEYWORD_PREFER = 3;	
	public static final String FORMATTER_PREF_KEYWORD_CASE = FORMATTER_CATEGORY_GENERALSETTINGS + ".keywordCase";	 //$NON-NLS-1$
	
	public static final String FORMATTER_CATEGORY_BRACES = "braces"; //$NON-NLS-1$
	public static final int FORMATTER_PREF_BRACES_SAMELINE = 0;
	public static final int FORMATTER_PREF_BRACES_NEXTLINE = 1;
	public static final int FORMATTER_PREF_BRACES_NEXTLINE_INDENTED = 2;

	public static final String FORMATTER_PREF_LCURLY_POS = FORMATTER_CATEGORY_BRACES + ".openCurlyPosition"; //$NON-NLS-1$

	public static final String FORMATTER_CATEGORY_WS = "whiteSpace"; //$NON-NLS-1$
	public static final String FROMATTER_PREF_WS_SORTBY = FORMATTER_CATEGORY_WS + ".sortBy"; //$NON-NLS-1$
	public static final int FORMATTER_PREF_WS_SORTBY_WSPOSITION = 0;
	public static final int FORMATTER_PREF_WS_SORTBY_EGLSYNTAX = 1;
	//after ,
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_DATADECL = FORMATTER_CATEGORY_WS + ".afterComma.dataDecl"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_SETSTMT = FORMATTER_CATEGORY_WS + ".afterComma.setStatement"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_ENUM = FORMATTER_CATEGORY_WS + ".afterComma.enumeration"; //$NON-NLS-1$
    public static final String FORMATTER_PREF_WS_AFTER_COMMA_FUNCPARAMS = FORMATTER_CATEGORY_WS + ".afterComma.funcParms"; //$NON-NLS-1$
    public static final String FORMATTER_PREF_WS_AFTER_COMMA_PGMPARAMS = FORMATTER_CATEGORY_WS + ".afterComma.pgmParms"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_WHEN = FORMATTER_CATEGORY_WS + ".afterComma.when"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_FOREACH = FORMATTER_CATEGORY_WS + ".afterComma.forEach"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_EXPRS = FORMATTER_CATEGORY_WS + ".afterComma.exprList"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_ARRAY = FORMATTER_CATEGORY_WS + ".afterComma.array"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_FUNCINVOC = FORMATTER_CATEGORY_WS + ".afterComma.funcInvoc"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_CALLSTMT = FORMATTER_CATEGORY_WS + ".afterComma.callStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_NAMEDTYPE = FORMATTER_CATEGORY_WS + ".afterComma.namedType";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_SETTINGS = FORMATTER_CATEGORY_WS + ".afterComma.settings"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_IMPL = FORMATTER_CATEGORY_WS + ".afterComma.implClause"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COMMA_USESTMT = FORMATTER_CATEGORY_WS + ".afterComma.useStatement"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LCURLY_SETTINGS = FORMATTER_CATEGORY_WS + ".afterLCurly.settings"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LBRACKET_ARRAY = FORMATTER_CATEGORY_WS + ".afterLBracket.array"; //$NON-NLS-1$
	//after (
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_FUNCPARMS = FORMATTER_CATEGORY_WS + ".afterLParen.funcParms"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_PGMPARAMS = FORMATTER_CATEGORY_WS + ".afterLParen.pgmParms"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_RETURN = FORMATTER_CATEGORY_WS + ".afterLParen.return";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_IF = FORMATTER_CATEGORY_WS + ".afterLParen.if"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_WHILE = FORMATTER_CATEGORY_WS + ".afterLParen.while";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_FOR = FORMATTER_CATEGORY_WS + ".afterLParen.for"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_FOREACH = FORMATTER_CATEGORY_WS + ".afterLParen.forEach"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_ONEVENT = FORMATTER_CATEGORY_WS + ".afterLParen.onEvent";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_CALLSTMT = FORMATTER_CATEGORY_WS + ".afterLParen.callStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_GETBYPOS = FORMATTER_CATEGORY_WS + ".afterLParen.getByPos"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_WHEN = FORMATTER_CATEGORY_WS + ".afterLParen.when";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_ONEXCEPTION = FORMATTER_CATEGORY_WS + ".afterLParen.onException"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_NAMEDTYPE = FORMATTER_CATEGORY_WS + ".afterLParen.namedType";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_CASE = FORMATTER_CATEGORY_WS + ".afterLParen.case";
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_EXIT = FORMATTER_CATEGORY_WS + ".afterLParen.exit";
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_PARENTEXPR = FORMATTER_CATEGORY_WS + ".afterLParen.parentExpr"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_LPAREN_FUNCINVOC = FORMATTER_CATEGORY_WS + ".afterLParen.funcInvoc";	 //$NON-NLS-1$
	//before ,
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_DATADECL = FORMATTER_CATEGORY_WS + ".beforeComma.dataDecl"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_SETSTMT = FORMATTER_CATEGORY_WS + ".beforeComma.setStatement"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_ENUM = FORMATTER_CATEGORY_WS + ".beforeComma.enumeration"; //$NON-NLS-1$
    public static final String FORMATTER_PREF_WS_BEFORE_COMMA_FUNCPARAMS = FORMATTER_CATEGORY_WS + ".beforeComma.funcParms"; //$NON-NLS-1$
    public static final String FORMATTER_PREF_WS_BEFORE_COMMA_PGMPARAMS = FORMATTER_CATEGORY_WS + ".beforeComma.pgmParms";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_WHEN = FORMATTER_CATEGORY_WS + ".beforeComma.when"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_FOREACH = FORMATTER_CATEGORY_WS + ".beforeComma.forEach"; //$NON-NLS-1$	
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_EXPRS = FORMATTER_CATEGORY_WS + ".beforeComma.exprList"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_ARRAY = FORMATTER_CATEGORY_WS + ".beforeComma.array"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_FUNCINVOC = FORMATTER_CATEGORY_WS + ".beforeComma.funcInvoc"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_CALLSTMT = FORMATTER_CATEGORY_WS + ".beforeComma.callStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_NAMEDTYPE = FORMATTER_CATEGORY_WS + ".beforeComma.namedType";		 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_SETTINGS = FORMATTER_CATEGORY_WS + ".beforeComma.settings"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_IMPL = FORMATTER_CATEGORY_WS + ".beforeComma.implClause"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COMMA_USESTMT = FORMATTER_CATEGORY_WS + ".beforeComma.useStatement"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_SEMI_STMT = FORMATTER_CATEGORY_WS + ".beforeSemicolon.statements"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS = FORMATTER_CATEGORY_WS + ".beforeLCurly.settings"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RCURLY_SETTINGS = FORMATTER_CATEGORY_WS + ".beforeRCurly.settings"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LBRACKET_ARRAY = FORMATTER_CATEGORY_WS + ".beforeLBracket.array"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RBRACKET_ARRAY = FORMATTER_CATEGORY_WS + ".beforeRBracket.array"; //$NON-NLS-1$
	//before (
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_FUNCPARMS = FORMATTER_CATEGORY_WS + ".beforeLParen.funcParms"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_PGMPARAMS = FORMATTER_CATEGORY_WS + ".beforeLParen.pgmParms"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_RETURN = FORMATTER_CATEGORY_WS + ".beforeLParen.return";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_IF = FORMATTER_CATEGORY_WS + ".beforeLParen.if"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_WHILE = FORMATTER_CATEGORY_WS + ".beforeLParen.while";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_FOR = FORMATTER_CATEGORY_WS + ".beforeLParen.for"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_FOREACH = FORMATTER_CATEGORY_WS + ".beforeLParen.forEach"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_ONEVENT = FORMATTER_CATEGORY_WS + ".beforeLParen.onEvent";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_CALLSTMT = FORMATTER_CATEGORY_WS + ".beforeLParen.callStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_GETBYPOS = FORMATTER_CATEGORY_WS + ".beforeLParen.getByPos"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_WHEN = FORMATTER_CATEGORY_WS + ".beforeLParen.when";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_ONEXCEPTION = FORMATTER_CATEGORY_WS + ".beforeLParen.onException"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_NAMEDTYPE = FORMATTER_CATEGORY_WS + ".beforeLParen.namedType";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_CASE = FORMATTER_CATEGORY_WS + ".beforeLParen.case";
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_EXIT = FORMATTER_CATEGORY_WS + ".beforeLParen.exit";
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_PARENTEXPR = FORMATTER_CATEGORY_WS + ".beforeLParen.parentExpr"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_LPAREN_FUNCINVOC = FORMATTER_CATEGORY_WS + ".beforeLParen.funcInvoc";	 //$NON-NLS-1$
	//before )
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_FUNCPARMS = FORMATTER_CATEGORY_WS + ".beforeRParen.funcParms"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_PGMPARAMS = FORMATTER_CATEGORY_WS + ".beforeRParen.pgmParms"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_RETURN = FORMATTER_CATEGORY_WS + ".beforeRParen.return";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_IF = FORMATTER_CATEGORY_WS + ".beforeRParen.if"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_WHILE = FORMATTER_CATEGORY_WS + ".beforeRParen.while";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_FOR = FORMATTER_CATEGORY_WS + ".beforeRParen.for"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_FOREACH = FORMATTER_CATEGORY_WS + ".beforeRParen.forEach"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_ONEVENT = FORMATTER_CATEGORY_WS + ".beforeRParen.onEvent";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_CALLSTMT = FORMATTER_CATEGORY_WS + ".beforeRParen.callStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_GETBYPOS = FORMATTER_CATEGORY_WS + ".beforeRParen.getByPos"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_WHEN = FORMATTER_CATEGORY_WS + ".beforeRParen.when";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_ONEXCEPTION = FORMATTER_CATEGORY_WS + ".beforeRParen.onException"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_NAMEDTYPE = FORMATTER_CATEGORY_WS + ".beforeRParen.namedType";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_CASE = FORMATTER_CATEGORY_WS + ".beforeRParen.case";
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_EXIT = FORMATTER_CATEGORY_WS + ".beforeRParen.exit";
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_PARENTEXPR = FORMATTER_CATEGORY_WS + ".beforeRParen.parentExpr"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_RPAREN_FUNCINVOC = FORMATTER_CATEGORY_WS + ".beforeRParen.funcInvoc";	 //$NON-NLS-1$
	
	public static final String FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT = FORMATTER_CATEGORY_WS + ".beforeOperator.assignment"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_OP_BINARY = FORMATTER_CATEGORY_WS + ".beforeOperator.binary"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT = FORMATTER_CATEGORY_WS + ".afterOperator.assignment"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_OP_UNARY = FORMATTER_CATEGORY_WS + ".afterOperator.unary"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_OP_BINARY = FORMATTER_CATEGORY_WS + ".afterOperator.binary"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_QUESTION_FIELDS = FORMATTER_CATEGORY_WS + ".beforeQuestion.nullableFields"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_QUESTION_PARMS = FORMATTER_CATEGORY_WS + ".beforeQuestion.nullableParameters"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_QUESTION_RETURNS = FORMATTER_CATEGORY_WS + ".beforeQuestion.nullableReturns"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COLON_SUBSTRING = FORMATTER_CATEGORY_WS + ".beforeColon.subString"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COLON_LABELSTMT = FORMATTER_CATEGORY_WS + ".beforeColon.labelStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_BEFORE_COLON_ONEVENT = FORMATTER_CATEGORY_WS + ".beforeColon.onEvent"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COLON_SUBSTRING = FORMATTER_CATEGORY_WS + ".afterColon.subString"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WS_AFTER_COLON_ONEVENT = FORMATTER_CATEGORY_WS + ".afterColon.onEvent"; //$NON-NLS-1$
	
	public static final String FORMATTER_CATEGORY_BLANKLINES = "blankLines"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_BLANKLINES_BEFORE_PKG = FORMATTER_CATEGORY_BLANKLINES + ".beforePkg"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_BLANKLINES_BEFORE_IMPORT = FORMATTER_CATEGORY_BLANKLINES + ".beforeImport"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL = FORMATTER_CATEGORY_BLANKLINES + ".beforePartDecl"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_BLANKLINES_BEFORE_PARTDATADECL = FORMATTER_CATEGORY_BLANKLINES + ".beforePartDataDecl"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_BLANKLINES_BEFORE_NESTEDFUNC = FORMATTER_CATEGORY_BLANKLINES + ".beforeNestedFunction"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_BLANKLINES_BEFORE_NESTEDFORM = FORMATTER_CATEGORY_BLANKLINES + ".beforeNestedForm"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_BLANKLINES_KEEP_EXISTING = FORMATTER_CATEGORY_BLANKLINES + ".preservExisting"; //$NON-NLS-1$
		
	public static final String FORMATTER_CATEGORY_NEWLINES = "newLines"; //$NON-NLS-1$
	
	public static final String FORMATTER_CATEGORY_WRAPPING = "lineWrapping"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_MAX_LEN = FORMATTER_CATEGORY_WRAPPING + ".maxLineWidth"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_NUMINDENTS = FORMATTER_CATEGORY_WRAPPING + ".numOfIndent4WrappedLines"; //$NON-NLS-1$
	public static final int FORMATTER_PREF_WRAP_POLICY_NOCHANGE = 0;
	public static final int FORMATTER_PREF_WRAP_POLICY_NOWRAP = 1;
	public static final int FORMATTER_PREF_WRAP_POLICY_NECESSARY = 2;	
	public static final int FORMATTER_PREF_WRAP_POLICY_ONEPERLINE_FORCE = 3;
	public static final int FORMATTER_PREF_WRAP_POLICY_ONEPERLINE_NECESSARY = 4;
	public static final String FORMATTER_PREF_WRAP_SETTINGSBLOCK = FORMATTER_CATEGORY_WRAPPING + ".settingsBlock"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_INITEXPR = FORMATTER_CATEGORY_WRAPPING + ".initExpr"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_BINARYEXPR = FORMATTER_CATEGORY_WRAPPING + ".binaryExpr"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_USESTMT = FORMATTER_CATEGORY_WRAPPING + ".useStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_MOVESTMT = FORMATTER_CATEGORY_WRAPPING + ".moveStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_FORSTMT = FORMATTER_CATEGORY_WRAPPING + ".forStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_CALLSTMT = FORMATTER_CATEGORY_WRAPPING + ".callStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_IOSTMT = FORMATTER_CATEGORY_WRAPPING + ".ioStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_SETSTMT = FORMATTER_CATEGORY_WRAPPING + ".setStmt"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_VAR_DELC = FORMATTER_CATEGORY_WRAPPING + ".varDelcaration"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_PARAMS = FORMATTER_CATEGORY_WRAPPING + ".parameters"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_EXPRS =  FORMATTER_CATEGORY_WRAPPING + ".exprs"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_ARRAY = FORMATTER_CATEGORY_WRAPPING + ".array"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_ARGS = FORMATTER_CATEGORY_WRAPPING + ".args"; //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_IMPL = FORMATTER_CATEGORY_WRAPPING + ".implClause";	 //$NON-NLS-1$
	public static final String FORMATTER_PREF_WRAP_ENUM = FORMATTER_CATEGORY_WRAPPING + ".enumFields"; //$NON-NLS-1$
		
	/**
	 * 
	 * @param key
	 * @return string[] - element 0 is category ID
	 * 					  element 1 is preference ID
	 */
	public static String[] getCategoryIDnPrefID(String key){
		String[] result = new String[]{"", ""}; //$NON-NLS-1$ //$NON-NLS-2$
		int dotIndex = key.indexOf(PREFKEY_DELIMITER);
		if(dotIndex != -1){	//if found dot
			result[0] = key.substring(0, dotIndex);
			result[1] = key.substring(dotIndex+1);
		}
		return result;
	}
	
	public static String getPreferenceSettingKey(String categoryID, String prefID){
		return categoryID + PREFKEY_DELIMITER + prefID;
	}
	
	private static PreferenceSettingValue getPreferenceSettingValue(Map preferenceSetting, String preferenceKey){
		Object objVal = preferenceSetting.get(preferenceKey);
		if(objVal != null)
			return (PreferenceSettingValue)objVal;
		return null;
	}
	
	public static boolean getBooleanPreferenceSetting(Map preferenceSetting, String preferenceKey){

		PreferenceSettingValue val = getPreferenceSettingValue(preferenceSetting, preferenceKey);
		if(val!=null)
			return Boolean.parseBoolean(val.getCurrentValue());
		
		return false;
	}
	
	public static int getIntPreferenceSetting(Map preferenceSetting, String preferenceKey){
		PreferenceSettingValue val = getPreferenceSettingValue(preferenceSetting, preferenceKey);
		if(val!=null)
			return Integer.parseInt(val.getCurrentValue());
		return 0;
	}
	
	public static int getEnumPreferenceSetting(Map preferenceSetting, String preferenceKey){
		return getIntPreferenceSetting(preferenceSetting, preferenceKey);
	}
	

}
