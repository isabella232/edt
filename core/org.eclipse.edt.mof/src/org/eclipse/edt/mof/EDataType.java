/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof;


public interface EDataType extends EClassifier {
	
	final String EDataType_JavaObject = "java.lang.Object";
	final String EDataType_String = "java.lang.String";
	final String EDataType_Boolean = "java.lang.Boolean";
	final String EDataType_Int32 = "java.lang.Integer";
	final String EDataType_Float = "java.lang.Float";
	final String EDataType_Decimal = "java.math.BigDecimal";
	final String EDataType_List = "java.util.List";

	String getDefaultValueString();
	void setDefaultValueString(String value);
	
	Object getDefaultValue();
	
	String getJavaClassName();
	void setJavaClassName(String javaName);
	
}
