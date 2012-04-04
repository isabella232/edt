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
package org.eclipse.edt.ide.ui.templates.parts;

public class DecimalType extends Type {
	int length = 31;
	int decimals = 0;
	
	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getDecimals() {
		return decimals;
	}
	
	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}
	
	public String getName() {
		int tempLength = getLength();
		int tempDecimals = getDecimals();
		
		if (tempLength > 32) {
			tempLength = 32;
			
			tempDecimals = tempDecimals - (getLength() - tempLength);
			
			if (tempDecimals < 0) {
				tempDecimals = 0;
			}
		}
		
		return "decimal(" + tempLength + ((tempDecimals>0)?"," + tempDecimals:"") + ")";
	}
	
	public Type compareTo(Type type) {
		if (type instanceof DecimalType) {
			DecimalType you = (DecimalType) type;
			int maxSig = getMax(getSig(), you.getSig());
			int maxDec = getMax(getDecimals(), you.getDecimals());
			
			DecimalType dec = new DecimalType();
			dec.setDecimals(maxDec);
			dec.setLength(maxSig + maxDec);
			return dec;			
		} else {
			return super.compareTo(type);
		}
	}
	
	private int getSig() {
		return getLength() - getDecimals();
	}
	
	private int getMax(int i, int j) {
		if (i > j) {
			return i;
		}
		return j;
	}
	
	public Object clone() {
		DecimalType type = new DecimalType();
		type.length = this.length;
		type.decimals = this.decimals;
		type.name = this.name;
		type.isNullable = this.isNullable;
		type.setAnnotations(this.getAnnotations());
		return type;
	}
}
