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
package org.eclipse.edt.ide.ui.internal.externaltype.conversion.javatype;

public class JavaTypeConstants {
	public static final String genClass = "genClass";
	public static final String genConstructor = "genConstructor";
	public static final String genField = "genField";
	public static final String genMethod = "genMethod";
	
	public static final String JAVA_TYPE_MESSAGE_HANDLER = "javaTypeMessageHandler";
	public static final String TO_BE_GENERATED_TYPE = "toBeGeneratedType";
	public static final String ALL_CLASS_META = "allClassMeta";
	public static final String JAVA_VOID_TYPE = "void";
	public static final String UNDERSTORE_PREFIX = "_";
	
	public static final String EZE_PREFIX = "eze";
	public static final String EGL_KEYWORD_IN = "IN";
	public static final String CONTAINING_EGL_PACKAGE = "eglPackage";
	public static final String EGL_THROWS_ANNOTATION = "@Throws";
	
	public static java.util.Map<String,String> JavaToEglMapping = new java.util.HashMap<String,String>(30);
	static {
		//JavaToEglMapping.put("char", "string");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.Character", "string?");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.String", "string?");//$NON-NLS-1$
		JavaToEglMapping.put("byte", "bytes");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.Byte", "bytes?");//$NON-NLS-1$
		
		JavaToEglMapping.put("boolean", "boolean");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.Boolean", "boolean?");//$NON-NLS-1$
		JavaToEglMapping.put("short", "smallint");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.Short", "smallint?");//$NON-NLS-1$
		JavaToEglMapping.put("int", "int");//$NON-NLS-1$
		
		JavaToEglMapping.put("java.lang.Integer", "int?");//$NON-NLS-1$
		JavaToEglMapping.put("long", "bigint");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.Long", "bigint?");//$NON-NLS-1$
		JavaToEglMapping.put("java.math.BigDecimal", "decimal?");//$NON-NLS-1$
		JavaToEglMapping.put("java.math.BigInteger", "decimal?");//$NON-NLS-1$
		
		JavaToEglMapping.put("float", "smallfloat");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.Float", "smallfloat?");//$NON-NLS-1$
		JavaToEglMapping.put("double", "float");//$NON-NLS-1$
		JavaToEglMapping.put("java.lang.Double", "float?");//$NON-NLS-1$
		//JavaToEglMapping.put("java.lang.Class", "Class");//$NON-NLS-1$
		
		JavaToEglMapping.put("java.sql.Date", "date?");//$NON-NLS-1$
		JavaToEglMapping.put("java.sql.Time", "time?");//$NON-NLS-1$
		JavaToEglMapping.put("java.sql.Timestamp", "timestamp?");//$NON-NLS-1$
	}
	
	public static java.util.Set<String> EglPartNames = new java.util.HashSet<String>();
	static {
		EglPartNames.add("enumeration");//$NON-NLS-1$
		EglPartNames.add("foreach");//$NON-NLS-1$
		EglPartNames.add("handler");//$NON-NLS-1$
		EglPartNames.add("interface");//$NON-NLS-1$
		EglPartNames.add("service");//$NON-NLS-1$
		EglPartNames.add("type");//$NON-NLS-1$
		EglPartNames.add("url");//$NON-NLS-1$
	}
}
