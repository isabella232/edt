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

	private String FLOATLIT;

	public FloatLiteral(String FLOATLIT, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.FLOATLIT = FLOATLIT;
	}
	
	public String getValue() {
		return FLOATLIT;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return FLOAT_LITERAL;
	}
	
	public String getCanonicalString() {
		return FLOATLIT;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new FloatLiteral(new String(FLOATLIT), getOffset(), getOffset() + getLength());
	}
}
