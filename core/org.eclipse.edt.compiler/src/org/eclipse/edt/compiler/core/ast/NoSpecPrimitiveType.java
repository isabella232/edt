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
 * NoSpecPrimitiveType AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class NoSpecPrimitiveType extends PrimitiveType {

	public NoSpecPrimitiveType(Primitive prim, int startOffset, int endOffset) {
		super(prim, startOffset, endOffset);
	}

	public boolean hasPrimLength() {
		return false;
	}

	public boolean hasPrimDecimals() {
		return false;
	}

	public boolean hasPrimPattern() {
		return false;
	}

	public String getPrimLength() {
		return null;
	}

	public String getPrimDecimals() {
		return null;
	}

	public String getPrimPattern() {
		return null;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new NoSpecPrimitiveType(getPrimitive(), getOffset(), getOffset() + getLength());
	}
}
