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

	// these are annotations key values, related to the values in the IRs
	public static final String Annotation_EGLProperty = "egl.core.eglproperty";

	// command parameter internal names
	public static final String SERIAL_VERSION_UID = "80";

	// Commonly used package names
	public static final String JSRT_DATETIME_PKG = "egl.egl.core.$DateTimeLib.";

	// EGL message id's
	public static final String EGLMESSAGE_UNSUPPORTED_ELEMENT = "1000";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT = "1001";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_ANNOTATION = "1002";
	public static final String EGLMESSAGE_MISSING_TEMPLATE_FOR_TYPE = "1003";
	public static final String EGLMESSAGE_VALIDATION_FAILED = "9980";
	public static final String EGLMESSAGE_VALIDATION_COMPLETED = "9981";
	public static final String EGLMESSAGE_GENERATION_FAILED = "9990";
	public static final String EGLMESSAGE_GENERATION_COMPLETED = "9991";
	public static final String EGLMESSAGE_EXCEPTION_OCCURED = "9998";
	public static final String EGLMESSAGE_STACK_TRACE = "9999";
}
