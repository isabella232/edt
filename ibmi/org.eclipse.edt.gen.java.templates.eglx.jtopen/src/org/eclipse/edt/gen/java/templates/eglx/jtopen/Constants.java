/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

public interface Constants {
	//Gen methods
	static final String genArrayResize = "genArrayResize";
	static final String genDecimals = "genDecimals";
	static final String genLength = "genLength";
	static final String genPattern = "genPattern";
	static final String genHelperClassName = "genHelperClassName"; 
	static final String genHelperClass = "genHelperClass"; 
	static final String genAS400Type = "genAS400Type"; 
	static final String genAS400TypeConstructorOptions = "genAS400TypeConstructorOptions"; 
	static final String getFunctionAccess = "getFunctionAccess";
	static final String genUsingClause = "genUsingClause";
	static final String getUsingArgument = "getUsingArgument";
	
	//signatures
	static final String signature_IBMiProgram = "eglx.jtopen.annotations.IBMiProgram";
	static final String signature_ExternalName = "eglx.lang.ExternalName";
	static final String signature_IBMiConnection = "eglx.jtopen.IBMiConnection";
	static final String signature_AS400Array = "eglx.jtopen.annotations.AS400Array";
	static final String signature_AS400Bin1 = "eglx.jtopen.annotations.AS400Bin1";
	static final String signature_AS400Bin2 = "eglx.jtopen.annotations.AS400Bin2";
	static final String signature_AS400Bin4 = "eglx.jtopen.annotations.AS400Bin4";
	static final String signature_AS400Bin8 = "eglx.jtopen.annotations.AS400Bin8";
	static final String signature_AS400Date = "eglx.jtopen.annotations.AS400Date";
	static final String signature_AS400DecimalFloat = "eglx.jtopen.annotations.AS400DecimalFloat";
	static final String signature_AS400DecimalPacked = "eglx.jtopen.annotations.AS400DecimalPacked";
	static final String signature_AS400DecimalZoned = "eglx.jtopen.annotations.AS400DecimalZoned";
	static final String signature_AS400Float4 = "eglx.jtopen.annotations.AS400Float4";
	static final String signature_AS400Float8 = "eglx.jtopen.annotations.AS400Float8";
	static final String signature_AS400Text = "eglx.jtopen.annotations.AS400Text";
	static final String signature_AS400Time = "eglx.jtopen.annotations.AS400Time";
	static final String signature_AS400Timestamp = "eglx.jtopen.annotations.AS400Timestamp";
	static final String signature_AS400UnsignedBin1 = "eglx.jtopen.annotations.AS400UnsignedBin1";
	static final String signature_AS400UnsignedBin2 = "eglx.jtopen.annotations.AS400UnsignedBin2";
	static final String signature_AS400UnsignedBin4 = "eglx.jtopen.annotations.AS400UnsignedBin4";
	
	//Annotation fields
	static final String subKey_parameterAnnotations = "parameterAnnotations";
	static final String subKey_ibmiFormat = "ibmiFormat";
	static final String subKey_ibmiSeparatorChar = "ibmiSeparatorChar";
	static final String subKey_eglPattern = "eglPattern";
	static final String subKey_connectionResource = "connectionResourceBindingURI";
	static final String subKey_isServiceProgram = "isServiceProgram";
	static final String subKey_libraryName = "libraryName";
	static final String subKey_programName = "programName";
	public static final String subKey_encoding = "encoding";
	public static final String subKey_timeZoneID = "ibmiTimezoneID";
	public static final String subKey_length = "length";
	public static final String subKey_preserveTrailingSpaces = "preserveTrailingSpaces";
	public static final String subKey_decimals = "decimals";
	public static final String subKey_elementCount = "elementCount";
	public static final String subKey_validElementCountVariable = "returnCountVariable";
	public static final String subKey_elementTypeAS400Annotation = "elementTypeAS400Annotation";
	static final String subKey_ibmiGeneratedHelpers = "ibmiGeneratedHelpers";
	static final String subKey_ibmiAnnotations = "ibmiAnnotations";
	
	//Misc constants
	public static final String as400ConnectionName = "ezeIBMiConn";
	public static final String HELPER_SUFFIX = "_IBMiStructure";
	public static final String HELPER_PREFIX = "Eze_";
	public static final String FUNCTION_HELPER_SUFFIX = "_IBMiProxy";
	public static final String FUNCTION_HELPER_PREFIX = "eze_";
}
