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
 * CharLiteral AST node type.
 * 
 * @author David Murray
 */
public class HexLiteral extends LiteralExpression {

	private String rawString;
	private String stringValue;

	public HexLiteral(String rawString, String stringValue, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		this.rawString = rawString;
		this.stringValue = stringValue;
	}
	
	public String getValue() {
		return stringValue;
	}
			
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return HEX_LITERAL;
	}
	
	public String getCanonicalString() {
		return rawString;
	}

	protected Object clone() throws CloneNotSupportedException {
		return new HexLiteral(rawString, stringValue, getOffset(), getOffset() + getLength());
	}
}
