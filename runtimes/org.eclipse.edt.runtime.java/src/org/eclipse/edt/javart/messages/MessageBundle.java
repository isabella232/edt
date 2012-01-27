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
// NLS_ENCODING=UTF-8
// NLS_MESSAGEFORMAT_ALL
package org.eclipse.edt.javart.messages;

import java.util.ListResourceBundle;

/**
 * The resource bundle containing the messages.
 */
public class MessageBundle extends ListResourceBundle
{
	/**
	 * The array containing the messages.
	 */
	static final Object[][] contents =
	{
		{ Message.DYNAMIC_ACCESS_FAILED, "A field named {0} cannot be found in {1}." },
		{ Message.SOA_E_WS_PROXY_INVALID_HTTP_EXCEPTION, "The URL ''{0}'' used to invoke the service is invalid. It does not contain the http:// protocol." },
		{ Message.SOA_E_WS_PROXY_INVALID_URL_EXCEPTION, "The URL ''{0}'' used to invoke the service is invalid. {1}" },
		{ Message.SOA_E_WS_PROXY_EMPTY_URL_EXCEPTION, "The URL used to invoke the service is blank. Specify a URL that points to the service." },
		{ Message.SOA_E_JSON_FIELD_TYPE_EXCEPTION, "JSON conversion cannot be performed on field:{0} in {1}." },
		{ Message.SOA_E_JSON_TYPE_EXCEPTION, "JSON conversion cannot be performed on a value of type {0}." },
		{ Message.MDY_ERROR, "The DateTimeLib.mdy function cannot convert the values {0}, {1}, and {2} into a month, day, and year." },
		{ Message.CONVERSION_ERROR, "The value {0} of type {1} cannot be converted to the type {2}." },
		{ Message.CAUGHT_JAVA_EXCEPTION, "{0}" },
		{ Message.SQL_EXCEPTION_CAUGHT, "{0}: [sqlstate:{1}][sqlcode:{2}]" },
		{ Message.LIST_INDEX_OUT_OF_BOUNDS, "List index {0} is out of bounds. The list''s size is {1}." },
		{ Message.INDEX_OUT_OF_BOUNDS, "The index value {0} is out of bounds." },
		{ Message.INVALID_SUBSTRING_INDEX, "Invalid substring indices {0}:{1}." },
		{ Message.EXCEPTION_IN_DELEGATE_INVOKE, "Invoking function {0} with a delegate failed due to {1}" },
		{ Message.EXCEPTION_IN_DELEGATE_GET, "Creation of a delegate for function {0} failed due to {1}" },
		{ Message.RUN_COMMAND_FAILED, "An error occurred running the {0} command.  Error: {1}." },
		{ Message.NULL_NOT_ALLOWED, "A null value was used." },
		{ Message.NO_FIELD_IN_TIMESTAMP, "Function {0} can only be called on timestamps whose pattern includes ''{1}''." },
		{ Message.INVALID_MATCH_PATTERN, "The string-matching pattern {0} is invalid." },
		{ Message.NEGATIVE_SIZE, "The size value {0} is less than zero." },
		{ Message.SOA_E_WS_PROXY_PARMETERS_JSON2EGL, "An exception occurred while converting from JSON. parameter:{0}, json:{1}" },
		{ Message.SOA_E_WS_PROXY_COMMUNICATION, "An exception occurred while communicating with the service. URL:{0}" },
		{ Message.SOA_E_EGL_SERVICE_INVOCATION, "An error occurred while trying to invoke function:{0} on EGL Service:{1}." },
		{ Message.SOA_E_WS_SERVICE, "An exception occurred during a service call. Service:{0}, Function:{0}" },
		{ Message.SOA_E_FUNCTION_NOT_FOUND, "The function {0} was not found on service {1}"},
		{ Message.SOA_E_LOAD_LOCAL_SERVICE, "An error occurred while loading the {0} service." },
		{ Message.SOA_E_WS_REST_BAD_CONTENT, "The request could not be converted to a service call. The received request was ''{0}''. " },
		{ Message.SOA_E_WS_PROXY_UNIDENTIFIED, "An exception occurred while calling a service." },
		{ Message.SOA_E_WS_REST_NO_SERVICE, "No REST-RPC service was found. URL:{0}" },
		{ Message.SOA_E_WS_REST_WRONG_HTTP_FUNCTION, "An HTTP {0} was recieved. EGL RPC service only operate with an HTTP.POST."},
		{ Message.XML2EGL_ERROR, "An error occurred while converting from XML string {0}. Error: {1}" },
		{ Message.EGL2XML_ERROR, "An error occurred while converting to XML. Parameter: {0} Error: {1}" },
		{ Message.SOA_E_WS_HTTP_NO_READ_TIMEOUT, "The environments HttpURLConnection does not support timeout." },
		{ Message.CREATE_OBJECT_FAILED, "An error occurred while creating an object of the {0} type. The following error occurred: {1}." },
		{ Message.PROPERTIES_FILE_MISSING, "The {0} properties file could not be loaded." },
		{ Message.UNHANDLED_EXCEPTION, "An unhandled error occurred. Error: {0}." },
		{ Message.SOA_E_WS_PROXY_SERVICE_TIMEOUT, "The service invocation timed out. exception: Connection read time out error. url:{0}" },
		{ Message.SOA_E_WS_REST_NO_RESPONSE, "No response was received from the service. URL:{0}" },
		{ Message.VALUE_OUT_OF_RANGE, "The value {0} is not within the valid range of {1} to {2}." },
		{ Message.MISSING_RESOURCE_FILE_NAME, "No file name was provided to get resource." },
		{ Message.RESOURCE_FILE_NOT_FOUND, "The file ({0}) could not be found." },
		{ Message.ERROR_PARSING_RESOURCE_FILE, "An Exception occurred while parsing the file ({0}). exception:{1}" },
		{ Message.ERROR_RESOURCE_FACTORY_NOT_FOUND, "Factory not found for binding name: {0}, type:{1}, binding property file:{2}." },
		{ Message.ERROR_NO_RESOURCE_IMPLEMENTATION, "There is no resource implementation for binding name: {0}, type:{1}, binding property file:{2}." },
		{ Message.ERROR_RESOURCE_BINDING_NOT_FOUND, "No resource binding found for for binding name: {0}, binding property file:{1}." },
		{ Message.ERROR_RESOURCE_IMPLEMENTATION_EXCEPTION, "An exception occurred while getting th eresource implementation for binding found for for binding name: {0}, type:{1}, binding property file:{2}." },
		{ Message.RESOURCE_URI_EXCEPTION, "The URI {0} is not a valid URI." },
	};
	
	/**
	 * Returns the contents of this message bundle.
	 *
	 * @return the contents of this message bundle.
	 * @see #contents
	 * @see java.util.ListResourceBundle#getContents()
	 */
	protected Object[][] getContents()
	{
		return contents;
	}
}
