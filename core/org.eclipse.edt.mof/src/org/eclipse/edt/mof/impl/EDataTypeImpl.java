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
package org.eclipse.edt.mof.impl;

import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.utils.EList;


public class EDataTypeImpl extends EClassifierImpl implements EDataType {
	private static int Slot_defaultValueString = 0;
	private static int Slot_javaClassName = 1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + EClassifierImpl.totalSlots();
	}
	
	static { 
		int offset = EClassifierImpl.totalSlots();
		Slot_defaultValueString += offset;
		Slot_javaClassName += offset;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object getDefaultValue() {
		String typeName = getJavaClassName();
		if (typeName == null || typeName.equals(""))
			return null;
		else if (typeName.equals(EDataType.EDataType_String))
			return "";
		else if (typeName.equals(EDataType.EDataType_Int32)) 
			return 0;
		else if (typeName.equals(EDataType.EDataType_Boolean))
			return Boolean.FALSE;
		else if (typeName.equals(EDataType.EDataType_List)) {
			return new EList();
		}
		return null;
//		return getDefaultValueString() == null
//			? null
//			: getEFactory().createFromString(this, getDefaultValueString());
			
	}
	
	@Override
	public String getDefaultValueString() {
		return (String)slotGet(Slot_defaultValueString);
	}

	@Override
	public String getJavaClassName() {
		return (String)slotGet(Slot_javaClassName);
	}

	@Override
	public void setDefaultValueString(String value) {
		slotSet(Slot_defaultValueString, value);
	}

	@Override
	public void setJavaClassName(String javaName) {
		slotSet(Slot_javaClassName, javaName);
	}

}
