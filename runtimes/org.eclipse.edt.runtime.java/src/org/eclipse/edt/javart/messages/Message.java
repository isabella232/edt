/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.messages;

/**
 * Message defines constants for message IDs.  Each ID is an 8-character String
 * of the form EGLnnnnx, where the n's are the digits of the message number and
 * the x is E, I, or W (for Error, Information, or Warning).
 */
public class Message
{
	public static final String DYNAMIC_ACCESS_FAILED = "EGL0001E";
	public static final String MDY_ERROR = "EGL0006E";
	public static final String CONVERSION_ERROR = "EGL0007E";
	public static final String CAUGHT_JAVA_EXCEPTION = "EGL0008E";
	public static final String SQL_EXCEPTION_CAUGHT = "EGL0009E";
	public static final String LIST_INDEX_OUT_OF_BOUNDS = "EGL0010E";
	public static final String INDEX_OUT_OF_BOUNDS = "EGL0011E";
	public static final String INVALID_SUBSTRING_INDEX = "EGL0012E";
	public static final String EXCEPTION_IN_DELEGATE_INVOKE = "EGL0013E";
	public static final String EXCEPTION_IN_DELEGATE_GET = "EGL0014E";
	public static final String RUN_COMMAND_FAILED = "EGL0015E";	
	public static final String NULL_NOT_ALLOWED = "EGL0016E";
	public static final String NO_FIELD_IN_TIMESTAMP = "EGL0017E";
	public static final String INVALID_MATCH_PATTERN = "EGL0018E";
	public static final String NEGATIVE_SIZE = "EGL0019E";
	public static final String SOA_E_WS_PROXY_INVALID_HTTP_EXCEPTION = "EGL0002E";
	public static final String SOA_E_WS_PROXY_INVALID_URL_EXCEPTION = "EGL0003E";
	public static final String SOA_E_WS_PROXY_EMPTY_URL_EXCEPTION = "EGL0004E";
	public static final String SOA_E_JSON_TYPE_EXCEPTION = "EGL0005E";
	public static final String SOA_E_JSON_FIELD_TYPE_EXCEPTION = "EGL0020E";
	public static final String SOA_E_WS_PROXY_PARMETERS_JSON2EGL = "EGL0021E";
	public static final String SOA_E_WS_PROXY_COMMUNICATION = "EGL0022E";
	public static final String SOA_E_EGL_SERVICE_INVOCATION = "EGL0023E";
	public static final String SOA_E_WS_SERVICE = "EGL0024E";
	public static final String SOA_E_FUNCTION_NOT_FOUND = "EGL0025E";
	public static final String SOA_E_LOAD_LOCAL_SERVICE = "EGL0026E";
	public static final String SOA_E_WS_REST_BAD_CONTENT = "EGL0027E";
	public static final String SOA_E_WS_PROXY_UNIDENTIFIED = "EGL0028E";
	public static final String SOA_E_WS_REST_NO_SERVICE = "EGL0029E";
	public static final String SOA_E_WS_REST_WRONG_HTTP_FUNCTION = "EGL0030E";
	public static final String XML2EGL_ERROR = "EGL0031E";
	public static final String EGL2XML_ERROR = "EGL0032E";
	public static final String SOA_E_WS_HTTP_NO_READ_TIMEOUT = "EGL0033E";
	public static final String CREATE_OBJECT_FAILED = "EGL0034E";
	public static final String PROPERTIES_FILE_MISSING = "EGL0035E";
	public static final String UNHANDLED_EXCEPTION = "EGL0036E";
	public static final String SOA_E_WS_PROXY_SERVICE_TIMEOUT = "EGL0037E";
	public static final String SOA_E_WS_REST_NO_RESPONSE = "EGL0038E";
	public static final String VALUE_OUT_OF_RANGE = "EGL0039E";
	public static final String MISSING_RESOURCE_FILE_NAME = "EGL0040E";
	public static final String RESOURCE_FILE_NOT_FOUND = "EGL0041E";
	public static final String ERROR_PARSING_RESOURCE_FILE = "EGL0042E";
	public static final String ERROR_RESOURCE_FACTORY_NOT_FOUND = "EGL0043E";
	public static final String ERROR_RESOURCE_BINDING_NOT_FOUND = "EGL0044E";
	public static final String ERROR_NO_RESOURCE_IMPLEMENTATION = "EGL0045E";
	public static final String ERROR_RESOURCE_IMPLEMENTATION_EXCEPTION = "EGL0046E";
	public static final String RESOURCE_URI_EXCEPTION = "EGL0047E";
	public static final String RESOURCE_NO_PROCESSOR = "EGL0048E";
	public static final String MISSING_DEFAULT_DD = "EGL0049E";
}
