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
 * FloatLiteral AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class FloatLiteral extends LiteralExpression {

	private final int type;
	private final String value;

	public FloatLiteral(int type, String value, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.type = type;
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return type;
	}
	
	public String getCanonicalString() {
		return value + (type == SMALLFLOAT_LITERAL ? "f" : "F");
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new FloatLiteral(type, new String(value), getOffset(), getOffset() + getLength());
	}
}
