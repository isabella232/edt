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
 * StringLiteral AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class StringLiteral extends LiteralExpression {

	private String rawString;
	private String stringValue;
	private boolean isHex;

	public StringLiteral(String rawString, String stringValue, boolean isHex, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		this.rawString = rawString;
		this.stringValue = stringValue;
		this.isHex = isHex;
	}
	
	@Override
	public String getValue() {
		return stringValue;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public boolean isHex() {
		return isHex;
	}
	
	@Override
	public int getLiteralKind() {
		return STRING_LITERAL;
	}
	
	@Override
	public String getCanonicalString() {
		return rawString;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new StringLiteral(rawString, stringValue, isHex, getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return rawString;
	}
}
