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
package org.eclipse.edt.compiler.internal;

/**
 * @author dollar
 *
 * This is the public interface to for all EGL mnemonics for property
 * values and statements.  These are NOT keywords.
 */
public class EGLMnemonicHandler {
	// ***IF YOU MODIFY THIS LIST YOU NEED TO REGEN THE CONSTANTS IN IEGLConstants USING***
	// org.eclipse.edt.compiler.internal.dev.tools.EGLMnemonicTool
	//
		private static String[] mnemonicNames =
		{
			"across",                   //$NON-NLS-1$
			"after_Delete",             //$NON-NLS-1$
			"after_Field",              //$NON-NLS-1$
			"after_Insert",             //$NON-NLS-1$
			"after_Openui",             //$NON-NLS-1$
			"after_Row",                //$NON-NLS-1$
			"aibtdli",					//$NON-NLS-1$
			"aix",                      //$NON-NLS-1$
			"alarm",                    //$NON-NLS-1$
			"askip", 					//$NON-NLS-1$
			"autoCommit",               //$NON-NLS-1$
			"automatic",                //$NON-NLS-1$
			"before_Delete",            //$NON-NLS-1$
			"before_Field",             //$NON-NLS-1$
			"before_Insert",            //$NON-NLS-1$
			"before_Openui",            //$NON-NLS-1$
			"before_Row",               //$NON-NLS-1$
			"black",                    //$NON-NLS-1$						
			"blanks",                   //$NON-NLS-1$
			"blink",                    //$NON-NLS-1$
			"blue",                     //$NON-NLS-1$
			"bold",                     //$NON-NLS-1$
			"bottom",					//$NON-NLS-1$
			"box",                      //$NON-NLS-1$
			"button",                   //$NON-NLS-1$
			"byInsertion",			    //$NON-NLS-1$
			"byKey",					//$NON-NLS-1$
			"bypass",                   //$NON-NLS-1$
			"cbltdli",					//$NON-NLS-1$
			"center", 		            //$NON-NLS-1$
			"character",				//$NON-NLS-1$
			"color",                    //$NON-NLS-1$
			"compress",  				//$NON-NLS-1$
			"commentLine", 				//$NON-NLS-1$
			"conditional",              //$NON-NLS-1$
			"csv",         		        //$NON-NLS-1$
			"cursor",                   //$NON-NLS-1$
			"cyan",                     //$NON-NLS-1$
			"db2TimeStampFormat",       //$NON-NLS-1$
			"defaultCase",				//$NON-NLS-1$
			"defaultDateFormat",        //$NON-NLS-1$
			"defaultHighlight",			//$NON-NLS-1$
			"defaultIntensity",			//$NON-NLS-1$
			"defaultTimeFormat",		//$NON-NLS-1$
			"defaultTimeStampFormat", 	//$NON-NLS-1$
			"dim",                      //$NON-NLS-1$
			"d1a",                      //$NON-NLS-1$
			"d1c",                      //$NON-NLS-1$
			"d1e",                      //$NON-NLS-1$
			"d2a",                      //$NON-NLS-1$
			"d2c",                      //$NON-NLS-1$
			"d2e",                      //$NON-NLS-1$
			"dAll",                     //$NON-NLS-1$
			"data",                     //$NON-NLS-1$
			"databaseConnection",       //$NON-NLS-1$
			"datasource_database_Connection", //$NON-NLS-1$
			"datasource_sql_statement", //$NON-NLS-1$
			"datasource_report_data",   //$NON-NLS-1$
			"DB",                       //$NON-NLS-1$
			"dCurrent",                 //$NON-NLS-1$
			"deadLock",                 //$NON-NLS-1$
			"debug",                    //$NON-NLS-1$
			"defaultColor",             //$NON-NLS-1$
			"disc",                     //$NON-NLS-1$
			"doubleByte",               //$NON-NLS-1$
			"down",                     //$NON-NLS-1$
			"duplicate",                //$NON-NLS-1$
			"empty",                    //$NON-NLS-1$			
			"endOfFile",                //$NON-NLS-1$
			"enter",                    //$NON-NLS-1$
			"errorLine",                //$NON-NLS-1$
			"eurDateFormat",            //$NON-NLS-1$
			"eurTimeFormat",			//$NON-NLS-1$
			"explicit",                 //$NON-NLS-1$
			"export_html",              //$NON-NLS-1$
			"export_pdf",               //$NON-NLS-1$
			"export_text",              //$NON-NLS-1$
			"export_xml",               //$NON-NLS-1$
			"fileNotAvailable",         //$NON-NLS-1$
			"fileNotFound",             //$NON-NLS-1$
			"form",                     //$NON-NLS-1$
			"formLine",                 //$NON-NLS-1$
			"full",                     //$NON-NLS-1$
			"green",                    //$NON-NLS-1$
			"GSAM",                     //$NON-NLS-1$
			"hardIoError",              //$NON-NLS-1$
			"hidden",   	            //$NON-NLS-1$
			"highlight",   	            //$NON-NLS-1$
			"hpux",                     //$NON-NLS-1$
			"html",                     //$NON-NLS-1$
			"hyperlink",                //$NON-NLS-1$
			"I4GL",                     //$NON-NLS-1$
			"imsbmp",                   //$NON-NLS-1$
			"imsvs",                    //$NON-NLS-1$
			"index",                    //$NON-NLS-1$
			"initial",                  //$NON-NLS-1$	
			"initialAttributes",        //$NON-NLS-1$
			"input",                    //$NON-NLS-1$					
			"inputOutput",              //$NON-NLS-1$					
			"intensity",                //$NON-NLS-1$
			"invalidFormat",            //$NON-NLS-1$
			"invisible",                //$NON-NLS-1$
			"ioError",                  //$NON-NLS-1$
			"iseriesc",                 //$NON-NLS-1$
			"iseriesj",                 //$NON-NLS-1$
			"isoDateFormat",            //$NON-NLS-1$
			"isoTimeFormat", 			//$NON-NLS-1$
			"jisDateFormat",            //$NON-NLS-1$
			"jisTimeFormat", 			//$NON-NLS-1$
			"l",                        //$NON-NLS-1$
			"library",					//$NON-NLS-1$
			"leading",                  //$NON-NLS-1$
			"left",                     //$NON-NLS-1$
			"linux",                    //$NON-NLS-1$
			"local",					//$NON-NLS-1$
			"lower",					//$NON-NLS-1$
			"magenta",                  //$NON-NLS-1$
			"main",                     //$NON-NLS-1$
			"masked",                   //$NON-NLS-1$			
			"menu_Action",              //$NON-NLS-1$
			"messageLine",              //$NON-NLS-1$
			"menuLine",                 //$NON-NLS-1$			
			"modified",                 //$NON-NLS-1$
			"no",                       //$NON-NLS-1$
			"noAutoCommit",             //$NON-NLS-1$
			"noCommit",					//$NON-NLS-1$
			"noRecordFound",            //$NON-NLS-1$
			"normal",                   //$NON-NLS-1$			
			"nohighlight",              //$NON-NLS-1$
			"none",                     //$NON-NLS-1$
			"noOutline",				//$NON-NLS-1$
			"normalIntensity",          //$NON-NLS-1$
			"null",                     //$NON-NLS-1$
			"nullFill",					//$NON-NLS-1$
			"numeric",                  //$NON-NLS-1$
			"odbcTimeStampFormat",      //$NON-NLS-1$
			"on_Key",                   //$NON-NLS-1$
			"output",                   //$NON-NLS-1$
			"pakey",                    //$NON-NLS-1$
			"parens",                   //$NON-NLS-1$
			"pdf",                      //$NON-NLS-1$
			"pfkey",                    //$NON-NLS-1$
			"position",                 //$NON-NLS-1$			
			"programLink",              //$NON-NLS-1$
			"promptLine",               //$NON-NLS-1$
			"protect",                  //$NON-NLS-1$
			"r",                        //$NON-NLS-1$
			"readCommitted",            //$NON-NLS-1$
			"readUncommitted",          //$NON-NLS-1$			
			"red",                      //$NON-NLS-1$
			"repeatableRead",           //$NON-NLS-1$
			"reportData",               //$NON-NLS-1$
			"request",                  //$NON-NLS-1$
			"reverse",                  //$NON-NLS-1$
			"right",                    //$NON-NLS-1$
			"secret",                   //$NON-NLS-1$			
			"serializableTransaction",  //$NON-NLS-1$
			"session",                  //$NON-NLS-1$
			"set",                      //$NON-NLS-1$
			"singleByte",               //$NON-NLS-1$
			"skip",                     //$NON-NLS-1$
			"softIoError",              //$NON-NLS-1$
			"solaris",                  //$NON-NLS-1$
			"sqlStatement",             //$NON-NLS-1$
			"submit",                   //$NON-NLS-1$					
			"submitBypass",             //$NON-NLS-1$					
			"systemGregorianDateFormat",//$NON-NLS-1$
			"systemJulianDateFormat",   //$NON-NLS-1$
			"table",                    //$NON-NLS-1$
			"tcpip",                    //$NON-NLS-1$
			"text",                     //$NON-NLS-1$
			"top", 						//$NON-NLS-1$
			"TP", 						//$NON-NLS-1$
			"trailing",                 //$NON-NLS-1$
			"trunc",                    //$NON-NLS-1$
			"twoPhase",                 //$NON-NLS-1$
			"type1",                    //$NON-NLS-1$
			"type2",                    //$NON-NLS-1$
			"uiForm",       	        //$NON-NLS-1$
			"underline",                //$NON-NLS-1$
			"unique",                   //$NON-NLS-1$
			"unprotect",                //$NON-NLS-1$
			"upper",					//$NON-NLS-1$
			"usaDateFormat",            //$NON-NLS-1$
			"usaTimeFormat", 			//$NON-NLS-1$
			"uss",                      //$NON-NLS-1$
			"value",                    //$NON-NLS-1$
			"vsebatch",                 //$NON-NLS-1$
			"vsecics",                  //$NON-NLS-1$
			"white",                    //$NON-NLS-1$
			"win",                      //$NON-NLS-1$
			"word",						//$NON-NLS-1$
			"xml",                      //$NON-NLS-1$
			"yellow",                   //$NON-NLS-1$
			"yes",                      //$NON-NLS-1$
			"zosbatch",     	        //$NON-NLS-1$
			"zoscics",                  //$NON-NLS-1$


		};
	/**
	 * @return
	 */
	public static String[] getMnemonicNames() {
		return mnemonicNames;
	}

	/**
	 * return the List of EGL mnemonic names in lowercase
	 */
	public static String[] getMnemonicNamesToLowerCase() {
		String[] lowercaseMnemonicNames = new String[mnemonicNames.length];
		for (int i = 0; i < lowercaseMnemonicNames.length; i++) {
			lowercaseMnemonicNames[i] = mnemonicNames[i].toLowerCase();
		}
		return lowercaseMnemonicNames;
	}		
		
}
