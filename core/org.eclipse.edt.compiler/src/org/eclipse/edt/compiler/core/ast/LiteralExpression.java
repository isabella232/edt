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
 * @author Dave Murray
 */
public abstract class LiteralExpression extends Expression {
	
	public static final int INTEGER_LITERAL = 0;
	public static final int DECIMAL_LITERAL = 1;
	public static final int FLOAT_LITERAL = 2;
	public static final int STRING_LITERAL = 3;
	public static final int NIL_LITERAL = 4;
	public static final int SQLSTMT_LITERAL = 5;
	public static final int ARRAY_LITERAL = 6;
	public static final int BOOLEAN_LITERAL = 7;
	public static final int HEX_LITERAL = 8;
	public static final int CHAR_LITERAL = 9;
	public static final int DBCHAR_LITERAL = 10;
	public static final int MBCHAR_LITERAL = 11;
	public static final int BYTES_LITERAL = 12;
	public static final int SMALLINT_LITERAL = 13;
	public static final int BIGINT_LITERAL = 14;
	public static final int SMALLFLOAT_LITERAL = 15;

	public LiteralExpression(int startOffset, int endOffset) {
		super(startOffset, endOffset);
	}
	
	public abstract int getLiteralKind();
	
	/**
	 * Only returns non-null for integer, decimal, float, and string literals.
	 */
	public String getValue() {
		return null;
	}
	
	protected abstract Object clone() throws CloneNotSupportedException;
	
	public String toString() {
		return getValue();
	}

}
