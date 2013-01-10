/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.generators;



public class RestServiceUtilities 
{
	static final String HTTP_METHOD_GET = "GET";
	static final String HTTP_METHOD_POST = "POST";
	static final String HTTP_METHOD_DELETE = "DELETE";
	static final String HTTP_RESPONSE_TIMEOUT = "EGL_TIMEOUT";
	static final String PROXY_SERVLET = "EGL Rich UI Proxy";
	static final String SERVICE_SERVLET = "EGL REST Service servlet";
	public static String JSON_RPC_POST_METHOD_ID = RestServiceUtilities.HTTP_POST_METHOD;
	static final String CONTENT_TYPE_KEY = "Content-Type";
	static final String CONTENT_TYPE_VALUE_TEXT_HTML = "text/html";
	static final String CONTENT_TYPE_VALUE_APPLICATION_JSON = "application/json";
	static final String CONTENT_TYPE_VALUE_APPLICATION_XML = "application/xml";
	static final String CONTENT_TYPE_VALUE_APPLICATION_FORMDATA = "application/x-www-form-urlencoded";
	static final String CONTENT_TYPE_VALUE_CHARSET_UTF8 = "; charset=UTF-8";
	public static int HTTP_STATUS_FAILED = 500;
	public static String HTTP_STATUS_MSG_FAILED = "FAILED";
	public static int HTTP_STATUS_OK = 200;
	public static String HTTP_STATUS_MSG_OK = "OK";
	public static final String URI_MAPPING_FILE_SUFFIX = "-uri.xml";
	static String JSON_RPC_ERROR_ID = "error";
	private static String JSON_RPC_ERROR_NAME_ID = "name";
	static String JSON_RPC_ERROR_NAME_VALUE = "JSONRPCError";
	public static final String SERVICE_MAPPING_ELEM = "servicemapping";
	public static final String SERVICES_MAPPINGS_ELEM = "servicemappings";
	public static final String CONTEXT_ROOT_ELEM = "contextroot";
	public static final String CLASSPATH_ELEM = "classpath";
	public static final String URI_ELEM = "uri";
	public static final String CLASSNAME_ATTR = "classname";
	public static final String STATEFUL_SERVICE_ATTR = "stateful";
	public static final String HOST_PROGRAM_SERVICE_ATTR = "hostProgramService";
	public static final String TRUE_VALUE = "true";
	private static final String EGL_FUNCTION_ATTR = "eglfunction";
	public static final String HTTP_FUNCTION_ATTR = "httpmethod";
	public static final String IN_ENCODING_ATTR = "in-encoding";
	public static final String OUT_ENCODING_ATTR = "out-encoding";
	public static final String HTTP_POST_METHOD = "POST";
	private static String JSON_RPC_ERROR_MESSAGE_ID = "message";
	private static String JSON_RPC_ERROR_CODE_ID = "code";
	static String CONTENT_TYPE_TEXT_ID = "text/plain;charset=utf-8";
	static String UTF8 = "UTF-8";
	private static String HTTP_AUTHENTICATION_ID = "Authorization";
	private static String HTTP_AUTHENTICATION_USER_ID = "egl_user";
	private static String HTTP_AUTHENTICATION_PWD_ID = "egl_pwd";
	private static String CUSTOM_EGLSOAP_REQUEST_HEADER = "CUSTOMEGLSOAPREQUESTHEADER";
	public static String CUSTOM_EGLSOAP_RESPONSE_HEADER = "CUSTOMEGLSOAPRESPONSEHEADER";
	public static final int FORMAT_NONE = 0;
	public static final int FORMAT_XML = 1;
	public static final int FORMAT_JSON = 2;
	public static final int FORMAT_FORM = 3;


}
