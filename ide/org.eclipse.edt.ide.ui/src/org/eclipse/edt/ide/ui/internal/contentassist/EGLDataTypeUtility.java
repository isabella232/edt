/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.compiler.internal.IEGLConstants;
/*
 * Comment unsupported language for edt 0.7, those will be uncommented later for edt 1.0
 */
public interface EGLDataTypeUtility {

	public final static String[] All_PREDEFINED_TYPE_STRINGS = {
	  		IEGLConstants.MIXED_ANY_STRING,
//	  		IEGLConstants.MIXED_ARRAYDICTIONARY_STRING,
//			IEGLConstants.KEYWORD_BLOB,
//			IEGLConstants.KEYWORD_CLOB,
	  		IEGLConstants.MIXED_DICTIONARY_STRING,
	  		IEGLConstants.MIXED_DATASOURCE_STRING,
	  		IEGLConstants.MIXED_SCROLLABLEDATASOURCE_STRING,
	  		IEGLConstants.MIXED_SQLDATASOURCE_STRING,
	  		IEGLConstants.MIXED_SQLRESULTSET_STRING,
	  		IEGLConstants.MIXED_SQLSTATEMENT_STRING,
	  		IEGLConstants.MIXED_HTTPPROXY_STRING,
	  		IEGLConstants.MIXED_HTTPREST_STRING,
	  		IEGLConstants.MIXED_HTTPSOAP_STRING,
	  		IEGLConstants.MIXED_IHTTP_STRING,
	  		IEGLConstants.MIXED_REQUEST_STRING,
	  		IEGLConstants.MIXED_RESPONSE_STRING,
	  		IEGLConstants.MIXED_JOB_STRING,
	  		IEGLConstants.MIXED_IRest_STRING,
	  		IEGLConstants.MIXED_MULTISTATUS_STRING,
			//birt Reports
//	  		IEGLConstants.EGL_REPORTS_BIRTREPORT,
			//Report Handler
//	  		IEGLConstants.EGL_REPORTS_REPORT,
//	  		IEGLConstants.EGL_REPORTS_REPORTDATA,
			//Console UI
//	  		IEGLConstants.EGL_CONSOLE_UI_CONSOLEFIELD,
//	  		IEGLConstants.EGL_CONSOLE_UI_MENU,
//	  		IEGLConstants.EGL_CONSOLE_UI_MENUITEM,
//	  		IEGLConstants.EGL_CONSOLE_UI_PRESENTATIONATTRIBUTES,
//	  		IEGLConstants.EGL_CONSOLE_UI_PROMPT,
//	  		IEGLConstants.EGL_CONSOLE_UI_WINDOW,
//			//DLI
//			IEGLConstants.IO_PCBRECORD_STRING,
//			IEGLConstants.ALT_PCBRECORD_STRING,
//			IEGLConstants.DB_PCBRECORD_STRING,
//			IEGLConstants.GSAM_PCBRECORD_STRING,
//			IEGLConstants.PSBDATARECORD_STRING,
	};
	
	public final static Set JASPER_REPORT_TYPE_STRINGS = new HashSet(Arrays.asList(new String[] {
//		IEGLConstants.EGL_REPORTS_REPORT,
//  		IEGLConstants.EGL_REPORTS_REPORTDATA,
	}));
	
	public final static Set BIRT_REPORT_TYPE_STRINGS = new HashSet(Arrays.asList(new String[] {
//		IEGLConstants.EGL_REPORTS_BIRTREPORT,
	}));
	
	public final static Set CONSOLE_UI_TYPE_STRINGS = new HashSet(Arrays.asList(new String[] {
//		IEGLConstants.EGL_CONSOLE_UI_CONSOLEFIELD,
//  		IEGLConstants.EGL_CONSOLE_UI_MENU,
//  		IEGLConstants.EGL_CONSOLE_UI_MENUITEM,
//  		IEGLConstants.EGL_CONSOLE_UI_PRESENTATIONATTRIBUTES,
//  		IEGLConstants.EGL_CONSOLE_UI_PROMPT,
//  		IEGLConstants.EGL_CONSOLE_UI_WINDOW,
	}));
	
	public final static Set DLI_TYPE_STRINGS = new HashSet(Arrays.asList(new String[] {
//		IEGLConstants.IO_PCBRECORD_STRING,
//		IEGLConstants.ALT_PCBRECORD_STRING,
//		IEGLConstants.DB_PCBRECORD_STRING,
//		IEGLConstants.GSAM_PCBRECORD_STRING,
//		IEGLConstants.PSBDATARECORD_STRING
	}));

	public final static String[] PREDEFINED_SERVICE_FUNCTION_TYPE_STRINGS = {
  			IEGLConstants.MIXED_DICTIONARY_STRING,
  			IEGLConstants.MIXED_DATASOURCE_STRING,
  			IEGLConstants.MIXED_SCROLLABLEDATASOURCE_STRING,
  			IEGLConstants.MIXED_SQLDATASOURCE_STRING,
  			IEGLConstants.MIXED_SQLRESULTSET_STRING,
  			IEGLConstants.MIXED_SQLSTATEMENT_STRING,
  			IEGLConstants.MIXED_MULTISTATUS_STRING,
			//birt Reports
//	  		IEGLConstants.EGL_REPORTS_BIRTREPORT,
//			//Report Handler
//	  		IEGLConstants.EGL_REPORTS_REPORT,
//	  		IEGLConstants.EGL_REPORTS_REPORTDATA,
//			//Console UI
//	  		IEGLConstants.EGL_CONSOLE_UI_CONSOLEFIELD,
//	  		IEGLConstants.EGL_CONSOLE_UI_MENU,
//	  		IEGLConstants.EGL_CONSOLE_UI_MENUITEM,
//	  		IEGLConstants.EGL_CONSOLE_UI_PRESENTATIONATTRIBUTES,
//	  		IEGLConstants.EGL_CONSOLE_UI_PROMPT,
//	  		IEGLConstants.EGL_CONSOLE_UI_WINDOW,
	};
	 
	public final static String[] PREDEFINED_NATIVE_LIBRARY_TYPE_STRINGS = {
	  		IEGLConstants.MIXED_ANY_STRING,
//			IEGLConstants.KEYWORD_BLOB,
//			IEGLConstants.KEYWORD_CLOB,
	};
	 
	public final static String[] PREDEFINED_DATA_TYPE_STRINGS = {
  		IEGLConstants.MIXED_ANY_STRING,
//  		IEGLConstants.MIXED_ARRAYDICTIONARY_STRING,
//		IEGLConstants.KEYWORD_BLOB,
//		IEGLConstants.KEYWORD_CLOB,
  		IEGLConstants.MIXED_DICTIONARY_STRING,
	};
	 
	public final static String[] PREDEFINED_DATAITEM_TYPE_STRINGS = {
//		IEGLConstants.KEYWORD_BLOB,
//		IEGLConstants.KEYWORD_CLOB,
	};
	 
	public final static String[] PREDEFINED_CONSOLE_FORM_DATA_TYPE_STRINGS = {
//  		IEGLConstants.MIXED_ARRAYDICTIONARY_STRING,
//		IEGLConstants.MIXED_CONSOLEFIELD_STRING,
  		IEGLConstants.MIXED_DICTIONARY_STRING,
	};
	 
	public final static String[] PREDEFINED_PSBRECORD_DATA_TYPE_STRINGS = {
//  		IEGLConstants.IO_PCBRECORD_STRING,
//		IEGLConstants.ALT_PCBRECORD_STRING,
//  		IEGLConstants.DB_PCBRECORD_STRING,
//  		IEGLConstants.GSAM_PCBRECORD_STRING,
//  		IEGLConstants.PSBDATARECORD_STRING,
	};
	 
	public final static String[] PREDEFINED_NEWABLE_TYPE_STRINGS = {
//	  		IEGLConstants.EGL_CONSOLE_UI_MENU,
//	  		IEGLConstants.EGL_CONSOLE_UI_MENUITEM,
//	  		IEGLConstants.EGL_CONSOLE_UI_PROMPT,
//	  		IEGLConstants.EGL_CONSOLE_UI_WINDOW,
	};
	 
	public final static String[] PRIMITIVE_TYPE_STRINGS = {
  		IEGLConstants.KEYWORD_BIGINT,
//		IEGLConstants.KEYWORD_BIN,
		IEGLConstants.KEYWORD_BOOLEAN,
//		IEGLConstants.KEYWORD_CHAR,
		IEGLConstants.KEYWORD_DATE,
//		IEGLConstants.KEYWORD_DBCHAR,
		IEGLConstants.KEYWORD_DECIMAL,
		IEGLConstants.KEYWORD_FLOAT,
//		IEGLConstants.KEYWORD_HEX,
		IEGLConstants.KEYWORD_INT,
//		IEGLConstants.KEYWORD_INTERVAL,
//		IEGLConstants.KEYWORD_MBCHAR,
//		IEGLConstants.KEYWORD_MONEY,
//		IEGLConstants.KEYWORD_NUM,
//		IEGLConstants.KEYWORD_NUMC,
//		IEGLConstants.KEYWORD_PACF,
		IEGLConstants.KEYWORD_NUMBER,
		IEGLConstants.KEYWORD_SMALLFLOAT,
		IEGLConstants.KEYWORD_SMALLINT,
		IEGLConstants.KEYWORD_STRING,
//		IEGLConstants.KEYWORD_TIME,
		IEGLConstants.KEYWORD_TIMESTAMP,
//		IEGLConstants.KEYWORD_UNICODE,
	};
	 
	public final static String[] PRIMITIVE_TYPE_NONFLEXIBLE_STRINGS = {
  		IEGLConstants.KEYWORD_BIGINT,
//		IEGLConstants.KEYWORD_BIN,
		IEGLConstants.KEYWORD_BOOLEAN,
//		IEGLConstants.KEYWORD_CHAR,
		IEGLConstants.KEYWORD_DATE,
//		IEGLConstants.KEYWORD_DBCHAR,
		IEGLConstants.KEYWORD_DECIMAL,
		IEGLConstants.KEYWORD_FLOAT,
//		IEGLConstants.KEYWORD_HEX,
		IEGLConstants.KEYWORD_INT,
//		IEGLConstants.KEYWORD_INTERVAL,
//		IEGLConstants.KEYWORD_MBCHAR,
//		IEGLConstants.KEYWORD_MONEY,
//		IEGLConstants.KEYWORD_NUM,
		IEGLConstants.KEYWORD_NUMBER,
//		IEGLConstants.KEYWORD_NUMC,
//		IEGLConstants.KEYWORD_PACF,
		IEGLConstants.KEYWORD_SMALLFLOAT,
		IEGLConstants.KEYWORD_SMALLINT,
//		IEGLConstants.KEYWORD_TIME,
		IEGLConstants.KEYWORD_TIMESTAMP,
//		IEGLConstants.KEYWORD_UNICODE,
	};
	    
	public final static String[] PRIMITIVE_TYPE_LOOSE_STRINGS = {
//  		IEGLConstants.KEYWORD_CHAR,
//		IEGLConstants.KEYWORD_DBCHAR,
//		IEGLConstants.KEYWORD_MBCHAR,
//		IEGLConstants.KEYWORD_HEX,
		IEGLConstants.KEYWORD_NUMBER,
//		IEGLConstants.KEYWORD_UNICODE
	};
	  
	public final static String[] SQL_ITEM_PRIMITIVE_TYPE_FLEXIBLE_STRINGS = {
		IEGLConstants.KEYWORD_BIGINT,
//		IEGLConstants.KEYWORD_BIN,
		IEGLConstants.KEYWORD_BOOLEAN,
//		IEGLConstants.KEYWORD_CHAR,
		IEGLConstants.KEYWORD_DATE,
//		IEGLConstants.KEYWORD_DBCHAR,
		IEGLConstants.KEYWORD_DECIMAL,
		IEGLConstants.KEYWORD_FLOAT,
//		IEGLConstants.KEYWORD_HEX,
		IEGLConstants.KEYWORD_INT,
//		IEGLConstants.KEYWORD_INTERVAL,
//		IEGLConstants.KEYWORD_MONEY,
		IEGLConstants.KEYWORD_SMALLFLOAT,
		IEGLConstants.KEYWORD_SMALLINT,
		IEGLConstants.KEYWORD_STRING,
//		IEGLConstants.KEYWORD_TIME,
		IEGLConstants.KEYWORD_TIMESTAMP,
//		IEGLConstants.KEYWORD_UNICODE,
	};
		 
	public final static String[] SQL_ITEM_PRIMITIVE_TYPE_NONFLEXIBLE_STRINGS = {
		IEGLConstants.KEYWORD_BIGINT,
//		IEGLConstants.KEYWORD_BIN,
		IEGLConstants.KEYWORD_BOOLEAN,
//		IEGLConstants.KEYWORD_CHAR,
		IEGLConstants.KEYWORD_DATE,
//		IEGLConstants.KEYWORD_DBCHAR,
		IEGLConstants.KEYWORD_DECIMAL,
		IEGLConstants.KEYWORD_FLOAT,
//		IEGLConstants.KEYWORD_HEX,
		IEGLConstants.KEYWORD_INT,
//		IEGLConstants.KEYWORD_INTERVAL,
//		IEGLConstants.KEYWORD_MONEY,
		IEGLConstants.KEYWORD_SMALLFLOAT,
		IEGLConstants.KEYWORD_SMALLINT,
//		IEGLConstants.KEYWORD_TIME,
		IEGLConstants.KEYWORD_TIMESTAMP,
//		IEGLConstants.KEYWORD_UNICODE,
	};
		 
	public final static String[] PRIMITIVE_TYPE_FORM_STRINGS = {
		IEGLConstants.KEYWORD_BIGINT,
//		IEGLConstants.KEYWORD_BIN,
		IEGLConstants.KEYWORD_BOOLEAN,
//		IEGLConstants.KEYWORD_CHAR,
		IEGLConstants.KEYWORD_DATE,
//		IEGLConstants.KEYWORD_DBCHAR,
		IEGLConstants.KEYWORD_DECIMAL,
		IEGLConstants.KEYWORD_FLOAT,
//		IEGLConstants.KEYWORD_HEX,
		IEGLConstants.KEYWORD_INT,
//		IEGLConstants.KEYWORD_MBCHAR,
//		IEGLConstants.KEYWORD_MONEY,
//		IEGLConstants.KEYWORD_NUM,
//		IEGLConstants.KEYWORD_NUMC,
//		IEGLConstants.KEYWORD_PACF,
		IEGLConstants.KEYWORD_SMALLFLOAT,
		IEGLConstants.KEYWORD_SMALLINT,
//		IEGLConstants.KEYWORD_TIME,
		IEGLConstants.KEYWORD_TIMESTAMP,
	};
}
