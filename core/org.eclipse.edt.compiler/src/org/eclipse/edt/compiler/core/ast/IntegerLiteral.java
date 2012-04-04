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
 * IntegerLiteral AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class IntegerLiteral extends LiteralExpression {

	private String integer;

	public IntegerLiteral(String integer, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.integer = integer;
	}
	
	public String getValue() {
		return integer;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return INTEGER_LITERAL;
	}
	
	public String getCanonicalString() {
		return integer;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new IntegerLiteral(new String(integer), getOffset(), getOffset() + getLength());
	}
}
