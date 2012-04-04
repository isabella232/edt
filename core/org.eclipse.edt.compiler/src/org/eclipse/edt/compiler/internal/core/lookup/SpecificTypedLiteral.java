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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.io.Serializable;
  
public class SpecificTypedLiteral implements Serializable{
 
	public static final int StringLiteral = 1;
	public static final int HexLiteral = 2;
	public static final int CharLiteral = 3;
	public static final int DBCharLiteral = 4;
	public static final int MBCharLiteral = 5;	
	
	boolean isHex;
	int kind;
	String value;
	
	
	
	public SpecificTypedLiteral(int kind, String value, boolean isHex) {
		super();
		this.kind = kind;
		this.value = value;
		this.isHex = isHex;
	}
	public int getKind() {
		return kind;
	}
	public String getValue() {
		return value;
	}
	public boolean isHex() {
		return isHex;
	}
	
	public static Object getNonSpecificValue(Object lit) {
		if (lit == null) {
			return null;
		}
		
		if (lit instanceof SpecificTypedLiteral) {
			return lit.toString();
		}
		
		if (lit instanceof Object[]) {
			Object[] oldArr = (Object[]) lit;
			Object[] newArr = new Object[oldArr.length];
			
			for (int i = 0; i < oldArr.length; i++) {
				newArr[i] = getNonSpecificValue(oldArr[i]);
			}
			return newArr;
		}
		
		return lit;
	}

		
	public String toString() {
		return getValue();
	}
	
}
