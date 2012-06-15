/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
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
	
	//signatures
	static final String signature_IBMiProgram = "eglx.jtopen.annotations.IBMiProgram";
	static final String signature_IBMiConnection = "eglx.jtopen.IBMiConnection";
	static final String signature_StructArray = "eglx.jtopen.annotations.StructArray";
	static final String signature_StructBin1 = "eglx.jtopen.annotations.StructBin1";
	static final String signature_StructBin2 = "eglx.jtopen.annotations.StructBin2";
	static final String signature_StructBin4 = "eglx.jtopen.annotations.StructBin4";
	static final String signature_StructBin8 = "eglx.jtopen.annotations.StructBin8";
	static final String signature_StructByteArray = "eglx.jtopen.annotations.StructByteArray";
	static final String signature_StructDate = "eglx.jtopen.annotations.StructDate";
	static final String signature_StructDecFloat = "eglx.jtopen.annotations.StructDecFloat";
	static final String signature_StructPackedDecimal = "eglx.jtopen.annotations.StructPackedDecimal";
	static final String signature_StructZonedDecimal = "eglx.jtopen.annotations.StructZonedDecimal";
	static final String signature_StructFloat4 = "eglx.jtopen.annotations.StructFloat4";
	static final String signature_StructFloat8 = "eglx.jtopen.annotations.StructFloat8";
	static final String signature_StructText = "eglx.jtopen.annotations.StructText";
	static final String signature_StructTime = "eglx.jtopen.annotations.StructTime";
	static final String signature_StructTimestamp = "eglx.jtopen.annotations.StructTimestamp";
	static final String signature_StructUnsignedBin1 = "eglx.jtopen.annotations.StructUnsignedBin1";
	static final String signature_StructUnsignedBin2 = "eglx.jtopen.annotations.StructUnsignedBin2";
	static final String signature_StructUnsignedBin4 = "eglx.jtopen.annotations.StructUnsignedBin4";
	static final String signature_StructUnsignedBin8 = "eglx.jtopen.annotations.StructUnsignedBin8";
	
	//Annotation fields
	static final String subKey_parameterAnnotations = "parameterAnnotations";
	static final String subKey_ibmiFormat = "ibmiFormat";
	static final String subKey_ibmiSeparatorChar = "ibmiSeparatorChar";
	static final String subKey_eglPattern = "eglPattern";
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
	public static final String subKey_elementTypeAnnotation = "elementTypeAnnotation";
	static final String subKey_ibmiGeneratedHelpers = "ibmiGeneratedHelpers";
	static final String subKey_ibmiAnnotations = "ibmiAnnotations";
	static final String subKey_realFunctionName = "realFunctionName";
	
	//Misc constants
	public static final String as400ConnectionName = "ezeIBMiConn";
	public static final String HELPER_SUFFIX = "_IBMiStructure";
	public static final String HELPER_PREFIX = "Eze_";
	public static final String FUNCTION_HELPER_SUFFIX = "_IBMiProxy";
	public static final String FUNCTION_HELPER_PREFIX = "eze_";
	public static final String IBMI_RUNTIME_CONTAINER_ID = "org.eclipse.edt.ide.jtopen.ibmiContainer";
}
