/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript;

public class Constants {
	private Constants() {}

	// command parameter internal names
	public static final String parameter_checkOverflow = "checkOverflow";
	public static final String parameter_projectPaths = "projectPaths";

	// these are sub key values used as context hashmap keys
	public static final String SubKey_partBeingGenerated = "partBeingGenerated";
	public static final String SubKey_partDataTablesUsed = "partDataTablesUsed";
	public static final String SubKey_partFormsUsed = "partFormsUsed";
	public static final String SubKey_partLibrariesUsed = "partLibrariesUsed";
	public static final String SubKey_partRecordsUsed = "partRecordsUsed";
	public static final String SubKey_simpleContent = "simpleContent";
	public static final String SubKey_isaSignature = "isaSignature";
	public static final String SubKey_recordToAnyAssignment = "recordToAnyAssignment";
	public static final String SubKey_isInList = "isInList";
	
	public static final String EXPR_LHS = "EXPR_LHS";
	
	public static final String DONT_UNBOX = "DONT_UNBOX";
	
	public static final String QUALIFIER_ALIAS = "QUALIFIER_ALIAS";


	// command parameter internal names
	public static final String SERIAL_VERSION_UID = "10";

	// Commonly used package names
	public static final String JSRT_EGL_NAMESPACE = "egl.";
	public static final String JSRT_STRLIB_PKG = "egl.eglx.lang.StrLib['$inst'].";
	public static final String JSRT_DTTMLIB_PKG = "egl.eglx.lang.DateTimeLib.";
	public static final String JSRT_XMLLIB_PKG = "egl.eglx.xml.XMLLib['$inst'].";
	
	// these are annotations key values, related to the values in the IRs
	public static final String Annotation_EGLProperty = "eglx.lang.EGLProperty";	
	public static final String Annotation_Property = "eglx.lang.Property";
	public static final String Annotation_JavaScriptObject = "eglx.javascript.JavaScriptObject";
	public static final String Annotation_PropertyGetter = "getMethod";
	public static final String Annotation_PropertySetter = "setMethod";
	public static final String RUI_PROPERTIES_LIBRARY = "eglx.ui.rui.RUIPropertiesLibrary";
	public static final String PROPERTIES_FOLDER_NAME = "properties"; //$NON-NLS-1$
	public static final String WEB_CONTENT_FOLDER_NAME = "WebContent"; //$NON-NLS-1$	
	
	public static final String GetterPrefix = "get";
	public static final String SetterPrefix = "set";

	// EGL message id's
	public static final String EGLMESSAGE_UNSUPPORTED_ELEMENT = "1000";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT = "1001";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_ANNOTATION = "1002";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_TYPE = "1003";
	public static final String EGLMESSAGE_ENCODING_ERROR = "8000";
	public static final String EGLMESSAGE_VALIDATION_FAILED = "9980";
	public static final String EGLMESSAGE_VALIDATION_COMPLETED = "9981";
	public static final String EGLMESSAGE_GENERATION_FAILED = "9990";
	public static final String EGLMESSAGE_GENERATION_COMPLETED = "9991";
	public static final String EGLMESSAGE_GENERATION_REPORT_FAILED = "9992";
	public static final String EGLMESSAGE_EXCEPTION_OCCURED = "9998";
	public static final String EGLMESSAGE_STACK_TRACE = "9999";
	
	public final static String RUI_HANDLER = "eglx.ui.rui.RUIHandler";
	public final static String RUI_WIDGET = "eglx.ui.rui.RUIWidget";
	public final static String JACASCRIPT_OBJECT = "eglx.javascript.JavaScriptObject";
	public final static String EXTERNALTYPE_RELATIVE_PATH = "relativePath";
	public final static String EXTERNALTYPE_EXTERNAL_NAME = "externalName";
}
