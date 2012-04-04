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
package org.eclipse.edt.compiler.core.ast;

/**
 * NumericSpecPrimitiveType AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class NumericSpecPrimitiveType extends PrimitiveType {

	private String[] numericPrimitiveSpecOpt;

	public NumericSpecPrimitiveType(Primitive prim, String[] numericPrimitiveSpecOpt, int startOffset, int endOffset) {
		super(prim, startOffset, endOffset);
		
		this.numericPrimitiveSpecOpt = numericPrimitiveSpecOpt;
	}
	
	public boolean hasPrimLength() {
		return numericPrimitiveSpecOpt != null;
	}

	public boolean hasPrimDecimals() {
		return numericPrimitiveSpecOpt != null && numericPrimitiveSpecOpt.length == 2;
	}

	public boolean hasPrimPattern() {
		return false;
	}

	public String getPrimLength() {
		return numericPrimitiveSpecOpt[0];
	}

	public String getPrimDecimals() {
		return numericPrimitiveSpecOpt[1];
	}

	public String getPrimPattern() {
		return null;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		String[] newNumericPrimitiveSpecOpt = numericPrimitiveSpecOpt != null ? new String[numericPrimitiveSpecOpt.length] : null;
		
		if(newNumericPrimitiveSpecOpt != null){
			for (int i = 0; i < numericPrimitiveSpecOpt.length; i++) {
				newNumericPrimitiveSpecOpt[i] = new String(numericPrimitiveSpecOpt[i]);
			}
		}
		
		return new NumericSpecPrimitiveType(getPrimitive(), newNumericPrimitiveSpecOpt, getOffset(), getOffset() + getLength());
	}
}
