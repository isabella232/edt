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
package org.eclipse.edt.gen.java;

public class Constants {
	private Constants() {}

	// smap constants used for debugging information
	public static final String smap_header = "SMAP\n";
	public static final String smap_stratum = "\negl\n*S egl\n*F\n";
	public static final String smap_lines = "*L\n";
	public static final String smap_trailer = "*E\n";
	public static final String smap_extensionDataTable = "*D";
	public static final String smap_extensionForm = "*F";
	public static final String smap_extensionUserLibrary = "*L";
	public static final String smap_extensionSystemLibrary = "*S";
	public static final String smap_extensionProgramParameter = "*P";
	public static final String smap_extensionTrailer = "*X\n";
	public static final String smap_attribute = "SourceDebugExtension";
	public static final String smap_fileExtension = ".eglsmap";
	public static final String smap_encoding = "UTF-8";

	// command parameter internal names
	public static final String parameter_checkOverflow = "checkOverflow";

	// these are used for generating fields with @Property and @EGLProperty
	public static final String Annotation_EGLProperty = "eglx.lang.EGLProperty";
	public static final String Annotation_Property = "eglx.lang.Property";
	public static final String Annotation_PropertyGetter = "getMethod";
	public static final String Annotation_PropertySetter = "setMethod";	
	public static final String GetterPrefix = "get";
	public static final String SetterPrefix = "set";

	// these are sub key values used as context hashmap keys
	public static final String SubKey_partBeingGenerated = "partBeingGenerated";
	public static final String SubKey_partDataTablesUsed = "partDataTablesUsed";
	public static final String SubKey_partFormsUsed = "partFormsUsed";
	public static final String SubKey_partLibrariesUsed = "partLibrariesUsed";
	public static final String SubKey_partRecordsUsed = "partRecordsUsed";
	public static final String SubKey_partTypesImported = "partTypesImported";

	// Library generation constants
	public static final String LIBRARY_PREFIX = "eze_Lib_";
	public static final String _HELPER = "_Helper";
	public static final String SERIAL_VERSION_UID = "10";

	// EGL message id's
	public static final String EGLMESSAGE_UNSUPPORTED_ELEMENT = "1000";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT = "1001";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_ANNOTATION = "1002";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_TYPE = "1003";
	public static final String EGLMESSAGE_ENCODING_ERROR = "8000";
	public static final String EGLMESSAGE_SMAPFILE_WRITE_FAILED = "9970";
	public static final String EGLMESSAGE_SMAPFILE_ENCODING_FAILED = "9971";
	public static final String EGLMESSAGE_VALIDATION_FAILED = "9980";
	public static final String EGLMESSAGE_VALIDATION_COMPLETED = "9981";
	public static final String EGLMESSAGE_GENERATION_FAILED = "9990";
	public static final String EGLMESSAGE_GENERATION_COMPLETED = "9991";
	public static final String EGLMESSAGE_GENERATION_REPORT_FAILED = "9992";
	public static final String EGLMESSAGE_EXCEPTION_OCCURED = "9998";
	public static final String EGLMESSAGE_STACK_TRACE = "9999";
}
