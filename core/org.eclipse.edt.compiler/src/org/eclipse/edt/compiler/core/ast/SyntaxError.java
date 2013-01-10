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
 * @author Wing Hong Ho
 */
public class SyntaxError {
	
	// Parser Errors
	public static final int MISSING_NT = 2100;
	public static final int INCORRECT_NT = 2101;
	public static final int MISSING_PREV_NT = 2102;
	public static final int INCORRECT_PREV_NT = 2103;
	public static final int MISSING_T = 2104;
	public static final int INCORRECT_T = 2105;
	public static final int UNEXPECTED_T = 2106;
	public static final int MISSING_PREV_T = 2107;
	public static final int INCORRECT_PREV_T = 2108;
	public static final int UNEXPECTED_PREV_T = 2109;
	public static final int MISSING_SCOPE_CLOSER = 2110;
	public static final int UNEXPECTED_PHRASE = 2111;
	public static final int INCORRECT_PHRASE = 2112;
	public static final int PANIC_PHRASE = 2113;
	public static final int TOO_MANY_ERRORS = 2114;
	public static final int KEYWORD_AS_NAME = 3019;
	
	// Lexer Errors
	public static final int UNCLOSED_STRING = 2200;
	public static final int UNCLOSED_BLOCK_COMMENT = 2201;
	public static final int UNCLOSED_SQL = 2202;
	public static final int UNCLOSED_SQLCONDITION = 2203;
	public static final int INVALID_ESCAPE = 2205;
	public static final int WHITESPACE_SQL = 2206;
	public static final int WHITESPACE_SQLCONDITION = 2207;
	public static final int INVALID_CHARACTER_IN_HEX_LITERAL = 2209;
	
	// Severities
	public static final int ERROR = 0;
	public static final int WARNING = 1;
	
	public int type;
	public int startOffset;
	public int endOffset;
	public int[] symbolTypes;
	public int severity;

	public SyntaxError(int type, int startOffset, int endOffset, int[] symbolTypes, int severity) {
		this.type = type;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.symbolTypes = symbolTypes;
		this.severity = severity;
	}

	public SyntaxError(int type, int startOffset, int endOffset, int[] symbolTypes) {
		this(type, startOffset, endOffset, symbolTypes, ERROR);
	}

	public SyntaxError(int type, int startOffset, int endOffset, int severity) {
		this(type, startOffset, endOffset, null, severity);
	}

	public SyntaxError(int type, int startOffset, int endOffset) {
		this(type, startOffset, endOffset, null, ERROR);
	}
}
