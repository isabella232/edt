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
 * CharacterSpecPrimitiveType AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class CharacterSpecPrimitiveType extends PrimitiveType {

	private String charPrimitiveSpecOpt;

	public CharacterSpecPrimitiveType(Primitive prim, String charPrimitiveSpecOpt, int startOffset, int endOffset) {
		super(prim, startOffset, endOffset);
		
		this.charPrimitiveSpecOpt = charPrimitiveSpecOpt;
	}
	
	public boolean hasPrimLength() {
		return charPrimitiveSpecOpt != null;
	}

	public boolean hasPrimDecimals() {
		return false;
	}

	public boolean hasPrimPattern() {
		return false;
	}

	public String getPrimLength() {
		return charPrimitiveSpecOpt;
	}

	public String getPrimDecimals() {
		return null;
	}

	public String getPrimPattern() {
		return null;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		String newCharPrimitiveSpecOpt = charPrimitiveSpecOpt != null ? new String(charPrimitiveSpecOpt) : null;
		
		return new CharacterSpecPrimitiveType(getPrimitive(), newCharPrimitiveSpecOpt, getOffset(), getOffset() + getLength());
	}
}
