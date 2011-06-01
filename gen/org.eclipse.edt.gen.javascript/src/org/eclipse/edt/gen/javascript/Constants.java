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

	// these are annotation key values used as context hashmap keys
	public static final String Annotation_partBeingGenerated = "partBeingGenerated";
	public static final String Annotation_partDataTablesUsed = "partDataTablesUsed";
	public static final String Annotation_partFormsUsed = "partFormsUsed";
	public static final String Annotation_partLibrariesUsed = "partLibrariesUsed";
	public static final String Annotation_partRecordsUsed = "partRecordsUsed";

	// these are annotations key values, related to the values in the IRs
	public static final String Annotation_EGLProperty = "egl.core.eglproperty";

	// command parameter internal names
	public static final String SERIAL_VERSION_UID = "80";

	// Commonly used package names
	public static final String JSRT_EGL_NAMESPACE = "egl.";
	public static final String JSRT_STRLIB_PKG = "egl.egl.core.StrLib['$inst'].";
	public static final String JSRT_DTTMLIB_PKG = "egl.egl.core.DateTimeLib['$inst'].";

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
}
