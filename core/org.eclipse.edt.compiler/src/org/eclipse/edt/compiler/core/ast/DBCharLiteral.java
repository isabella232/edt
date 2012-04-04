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
public class DBCharLiteral extends LiteralExpression {

	private String rawString;
	private String stringValue;
	private boolean isHex;

	public DBCharLiteral(String rawString, String stringValue, boolean isHex, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		this.rawString = rawString;
		this.stringValue = stringValue;
		this.isHex = isHex;
	}
	
	public String getValue() {
		return stringValue;
	}
			
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public boolean isHex() {
		return isHex;
	}
	
	public int getLiteralKind() {
		return DBCHAR_LITERAL;
	}
	
	public String getCanonicalString() {
		return rawString;
	}

	protected Object clone() throws CloneNotSupportedException {
		return new DBCharLiteral(rawString, stringValue, isHex, getOffset(), getOffset() + getLength());
	}
}
