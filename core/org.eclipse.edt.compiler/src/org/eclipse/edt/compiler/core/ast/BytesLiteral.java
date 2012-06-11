/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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
 * BytesLiteral AST node type.
 */
public class BytesLiteral extends LiteralExpression {

	private String rawString; // 0xABCD
	private String bytes;     // ABCD

	public BytesLiteral(String rawString, String bytes, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.rawString = rawString;
		this.bytes = bytes;
	}
	
	public String getValue() {
		return bytes;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return BYTES_LITERAL;
	}
	
	public String getCanonicalString() {
		return rawString;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new BytesLiteral(rawString, bytes, getOffset(), getOffset() + getLength());
	}
}
