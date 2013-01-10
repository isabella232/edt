/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.edt.ide.core.model.IProperty;

import org.eclipse.edt.compiler.internal.core.utils.CharOperation;

/**
 * @author twilson
 * created	Aug 29, 2003
 */
public class SourcePropertyElementInfo extends MemberElementInfo {
	
	private static char[] NO = new char[] {'n','o'};
	char[] value;
	int valueType;
	
	/**
	 * @return
	 */
	public Object getValue() {
		// TODO Handle with EGLPropertyDescriptors later
		switch (valueType) {
			case IProperty.VALUE_TYPE_BOOLEAN :
				if (value == null) return new Boolean(true);
				else if (CharOperation.fragmentEquals(NO, value, 0, false))
					return new Boolean(false);
				else
					return new Boolean(true);
			case IProperty.VALUE_TYPE_STRING :
				return new String(value);
			case IProperty.VALUE_TYPE_INT :
				try {
					return Integer.valueOf(new String(value));
				} catch (NumberFormatException e) {
					return new Integer(0);
				}
					
			default: return null;
		}

	}

	/**
	 * @param object
	 */
	public void setValue(char[] valuechars) {
		value = valuechars;
	}

	/**
	 * @return
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * @param i
	 */
	public void setValueType(int i) {
		valueType = i;
	}
}
