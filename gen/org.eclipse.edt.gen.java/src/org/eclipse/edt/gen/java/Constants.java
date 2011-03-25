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
package org.eclipse.edt.gen.java;

public class Constants {
	private Constants() {}

	// smap constants used for debugging information
	public static final String smap_header = "SMAP\n";
	public static final String smap_stratum = "\nJava\n*S egl\n*F\n";
	public static final String smap_lines = "\n*L\n";
	public static final String smap_trailer = "*E\n";
	public static final String smap_attribute = "SourceDebugExtension";
	public static final String smap_fileextension = ".eglsmap";
	public static final String smap_encoding = "UTF-8";

	// command parameter internal names
	public static final String parameter_checkOverflow = "checkOverflow";

	// these are annotation key values used as context hashmap keys
	public static final String Annotation_functionArgumentTemporaryVariable = "functionArgumentTemporaryVariable";
	public static final String Annotation_functionHasReturnStatement = "functionHasReturnStatement";
	public static final String Annotation_partLibrariesUsed = "partLibrariesUsed";
	public static final String Annotation_partTypesImported = "partTypesImported";
	// these are annotations key values, related to the values in the IRs
	public static final String Annotation_EGLProperty = "egl.core.eglproperty";

	public static final String REDEFINED_ANNOTATION = "EGL Java Gen redefined record flag";
	public static final String IS_OVERLAY_ANNOTATION = "EGL Java Gen overlay class";

	// Library generation constants
	public static final String LIBRARY_PREFIX = "eze_Lib_";
	public static final String _HELPER = "_Helper";
	public static final String SERIAL_VERSION_UID = "80";

	// EGL message id's
	public static final String EGLMESSAGE_UNSUPPORTED_ELEMENT = "1000";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT = "1001";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_ANNOTATION = "1002";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_TYPE = "1003";
	public static final String EGLMESSAGE_SMAPFILE_WRITE_FAILED = "9970";
	public static final String EGLMESSAGE_SMAPFILE_ENCODING_FAILED = "9971";
	public static final String EGLMESSAGE_VALIDATION_FAILED = "9980";
	public static final String EGLMESSAGE_VALIDATION_COMPLETED = "9981";
	public static final String EGLMESSAGE_GENERATION_FAILED = "9990";
	public static final String EGLMESSAGE_GENERATION_COMPLETED = "9991";
	public static final String EGLMESSAGE_EXCEPTION_OCCURED = "9998";
	public static final String EGLMESSAGE_STACK_TRACE = "9999";
}
