/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
 * DecimalLiteral AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class DecimalLiteral extends LiteralExpression {

	private String DECIMALLIT;

	public DecimalLiteral(String DECIMALLIT, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.DECIMALLIT = DECIMALLIT;
	}
	
	public String getValue() {
		return DECIMALLIT;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return DECIMAL_LITERAL;
	}
	
	public String getCanonicalString() {
		return DECIMALLIT;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new DecimalLiteral(new String(DECIMALLIT), getOffset(), getOffset() + getLength());
	}
}
