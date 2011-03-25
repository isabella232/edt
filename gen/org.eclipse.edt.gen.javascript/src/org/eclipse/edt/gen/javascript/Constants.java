/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

/** Constants used by JavaScript Generation */
public interface Constants
{
	// command parameter internal names
	public static final String parameter_checkOverflow = "checkOverflow";
	
	public static final String SERIAL_VERSION_UID = "80";

	/** Type signatures for Annotations */
	public static final String ALIAS_ANNOTATION = "EGL JavaScript Gen alias";

	public static final String BOX_ANY_ANNOTATION = "EGL JavaScript Gen box Any";
	
	public static final String BOX_ANY_ELEMENTS_ANNOTATION = "EGL JavaScript Gen box Any elements";

	public static final String UNBOX_ANY_ANNOTATION = "EGL JavaScript Gen unbox Any";

	public static final String UNBOX_ANY_ELEMENTS_ANNOTATION = "EGL JavaScript Gen unbox Any elements";

	/** Fixed record generation annotations */
	public static final String APPEND_ITEMS_ANNOTATION = "EGL JavaScript Gen items needed $appendElements method";

	/** Fixed record generation annotations */
	public static final String ARRAY_ANNOTATION = "EGL JavaScript Gen item is an array flag";

	/** Item generation annotations */
	public static final String ARRAY_SIZE_ZERO_ANNOTATION = "EGL JavaScript Gen array size is 0";

	/** Item generation annotations */
	public static final String LITERAL_TYPE = "EGL JavaScript Gen Literal Assignment Type";

	/** Item generation annotations */
	public static final String LITERAL_TARGET = "EGL JavaScript Gen Literal Assignment Target";

	/** Item generation annotations */
	public static final String NEW_EXPRESSION_ANNOTATION = "EGL JavaScript Gen new expression";

	/** Type generation annotations */
	public static final String FIELD_ANNOTATION = "EGL JavaScript Gen field";

	/** Type generation annotations */
	public static final String FIELD_TYPE_DEFAULTS_ANNOTATION = "EGL JavaScript Gen field type defaults";

	/** Type generation annotations */
	public static final String FIELD_SERVICE_INOUT_PARAM_ANNOTATION = "EGL JavaScript Gen field is service inout param";

	public static final String CONTEXT_USE_EGL_OVERFLOW = "use_egl_overflow";
	// these are annotation key values
	public static final String functionArgumentTemporaryVariable = "functionArgumentTemporaryVariable";
	public static final String functionHasReturnStatement = "functionHasReturnStatement";

	/** Type generation annotations */
	public static final String TYPE_HAS_SETVALUESBLOCK = "EGL RHS NewExpression has setValuesBlock indicator";
	
	/** Statement generation annotations */
	public static final String ARRAY_ACCESS_ANNOTATION = "EGL JavaScript Gen type of array accessed";

	/** Statement generation annotations */
	public static final String EXIT_CONTINUE_ANNOTATION = "EGL JavaScript Gen exit continue";

	/** Statement generation annotations */
	public static final String HELPER_METHODS_ANNOTATION = "EGL JavaScript Gen helper methods";

	/** Statement generation annotations */
	public static final String EXPRESSION_HELPER_METHOD_ANNOTATION = "EGL JavaScript Gen expression helper method";

	/** Statement generation annotations */
	public static final String SV_EXPR_TARGET = "EGL JavaScript Gen SV expression target";

	/** Statement generation annotations */
	public static final String JAVA2EGL_DONE_ANNOTATION = "EGL JavaScript Gen convert to EGL done";

	/** Statement generation annotations */
	public static final String L_VALUE_ANNOTATION = "EGL JavaScript Gen L-value";

	/** Statement generation annotations */
	public static final String NO_EGL_PROPERTY_ANNOTATION = "EGL JavaScript No EGLProperty";

	/** Statement generation annotations */
	public static final String ARRAY_ELEMENT_TYPE_ANNOTATION = "EGL JavaScript Gen array element type";

	/** Statement generation annotations */
	public static final String NULLABLE_EXPR_ANNOTATION = "EGL JavaScript Gen nullable expression";

	/** Statement generation annotations */
	public static final String CONST_REFERENCE_PARAMETER_ANNOTATION = "EGL JavaScript Gen parameter is type const in";

	/** Statement generation annotations */
	public static final String DECIMAL_EXPR_ANNOTATION = "EGL JavaScript Gen decimal expression";
	
	/** Statement generation annotations */
	public static final String FLOAT_EXPR_ANNOTATION = "EGL JavaScript Gen float expression";
	
	/** Statement generation annotations */
	public static final String LABELS_ANNOTATION = "EGL JavaScript Gen function labels";
	
	/** Statement generation annotations */
	public static final String USE_IR_TYPE_ANNOTATION = "EGL JavaScript Gen Use IR's Type";

	/** Commonly used package names */
	public static final String JSRT_PKG = "egl.egl.jsrt.";

	/** Commonly used package names */
	public static final String JSRT_MATH_PKG = "egl.egl.core.$MathLib.";
	
	/** Commonly used package names */
	public static final String JSRT_STRLIB_PKG = "egl.egl.core.$StrLib.";
	
	/** Commonly used package names */
	public static final String JSRT_SYSLIB_PKG = "egl.egl.core.$SysLib.";
	
	public static final String JSRT_SERVICELIB_PKG = "egl.egl.core.$ServiceLib.";
	
	public static final String JSRT_XMLLIB_PKG = "egl.egl.core.$XMLLib.";
	
	/** Commonly used package names */
	public static final String JSRT_DATETIME_PKG = "egl.egl.core.$DateTimeLib.";

	/** Function generation constants */
	public static final String $FUNC = "$func_";

	/** Function generation constants */
	public static final String $RESULT = "$result";

	/** Library generation constants */
	public static final String _LIB = "_Lib";

	/** Service generation constants */
	public static final String BIND_XML_FILE_SUFFIX = "-bnd.xml";

	/** Service generation constants */
	public static final String DEPLOYMENT_DESCRIPTOR_EXTENSION = ".egldd";

	/** Service generation constants */
	public static final String _SERVICE_IMPL = "_Impl";

	/** Service generation constants */
	public static final String _SERVICE_PROXY = "_Proxy";

	/** Service generation constants */
	public static final String _SERVICE_BINDING_IMPL = "BindingImpl";

	/** Service generation constants */
	public static final String _INTERFACE = "_Intf";

	/** Service generation constants */
	public static final String _SERVICE_PARAMETER = "_ws";

	/** Service generation constants */
	public static final String _SERVICE_HOLDER = ".value";

	/** Service generation constants */
	public static final String SERVICE_PARAMETER_NAME_ANNOTATION = "EGL JavaScript Gen service parameter name annotation";

	/** Service generation constants */
	public static final String SERVICE_EGL_ARRAY_NAME_PREFIX_ANNOTATION = "EGL JavaScript Gen service EGL Array name annotation";

	/** Service generation constants */
	public static final String SERVICE_JAVA_ARRAY_ANNOTATION = "EGL JavaScript Gen java array annotation";

	/** Service generation constants */
	public static final String SERVICE_WSDL_ELEMENT_ANNOTATION = "EGL JavaScript Gen WSDL element annotation ";

	/** Service generation constants */
	public static final String SERVICE_WSDL_ISHEX_ANNOTATION = "EGL JavaScript Gen wsdl is hex type annotation element";

	/** Service generation constants */
	public static final String SERVICE_BINDING_ANNOTATION = "EGL JavaScript Gen service binding type annotation element";

	/** Service generation constants */
	public static final String SERVICE_PROTOCOL_ANNOTATION = "EGL JavaScript Gen service protocol annotation element";

	/** Service generation constants */
	public static final String STRUCTURED_FIELD_NAME_PREFIX = "EGL JavaScript Gen Structured Field Name Prefix";

	/** Service generation constants */
	public static final String DEPLOYMENT_DESCRIPTOR_ANNOTATION = "EGL JavaScript Gen Deloyment descriptor annotation";

	/** Record generation constants */
	public static final String EZE = "Eze";

	/** Record generation constants */
	public static final String EZE_ELEMENT = "ezeElement";

	/** Record generation constants */
	public static final String EZE_INDEX = "ezeIndex";

	/** Record generation constants */
	public static final String EZE_MAX_BUFFER_OFFSET = "ezeMaxBufferOffset";

	/** Record generation constants */
	public static final String EZE_OFFSET = "ezeOffset";

	/** Record generation constants */
	public static final String FIELD_INFORMATION_ANNOTATION = "EGL JavaScript Gen field information annotation";
	
	public static final String SERVICE_CALLBACK = "EGL RUI Service Callback";
	
	public static final String SERVICE_ERRORCALLBACK = "EGL RUI Service errorCallback";
	
	public static final String SERVICE_TIMEOUT = "EGL RUI Service timeout";	
	
	public static final String WEB_CONTENT_FOLDER_NAME = "WebContent";
	
	public static final String ARRAY_NULL_CHECK = "RUI Array Null Check Indicator";
	
	public static final String ARRAY_INDEX_CHECK = "RUI Array Index Check Indicator";
	
	public static final String USES_SERVICELIB_BINDSERVICE_FUNCTION = "Uses ServiceLib.bindService function";
	
	public static final String SERVICELIB_BINDSERVICE_SIGNATURE = "egl.core.ServiceLib#bindService";
	
	public static final String TEMP_VAR_ANNOTATION = "EGL temp variable";	

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
