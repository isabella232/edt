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
package org.eclipse.edt.compiler.internal.sqltokenizer;

/**
 * @author dollar
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EGLPrimeToken {

	protected int type;
	protected String text = null;
	protected int offset;
	protected int line;
	protected int column;
	protected boolean hostVar = false;
	protected boolean whereCurrentOf = false;
	
	// clause start keywords
	public static final int SELECT = 1;
	public static final int INTO = 2;
	public static final int FROM = 3;
	public static final int VALUES = 4;
	public static final int UPDATE = 5;
	public static final int SET = 6;
	public static final int HAVING = 7;
	public static final int WHERE = 8;
	public static final int ORDER = 9;
	public static final int GROUP = 10;
	public static final int INSERT = 11;
	public static final int FOR = 12;
	public static final int BY = 13;
	public static final int OF = 14;		
	public static final int UNION = 15;
	public static final int CURRENT = 16;
	public static final int CALL = 17;
	public static final int DELETE = 18;
	
	// delimiters
	public static final int SEMI = 21;
	public static final int L_PAREN = 22;
	public static final int R_PAREN = 23;
	public static final int L_SQUARE = 24;
	public static final int R_SQUARE = 25;
	public static final int DOT = 26;
	public static final int COLON = 27;
	public static final int HOST_VAR_COLON = 28;
	public static final int BANG = 29;
	public static final int EQUAL = 30;	
	public static final int PLUS = 31;
	public static final int MINUS = 32;
	public static final int SPLAT = 33;
	public static final int SLASH = 34;
	public static final int PERCENT = 35;	
	public static final int GREATER = 36;
	public static final int GREATER_EQUAL = 37;
	public static final int LESS = 38;	
	public static final int LESS_EQUAL = 39;
	public static final int NOT_EQUAL = 40;
	public static final int OR = 41;
	public static final int SQL_OR = 42;
	public static final int AND = 43;
	public static final int SQL_AND = 44; 
	public static final int COMMA = 45;
	private int firstDelimiter = SEMI;
	private int lastDelimiter = COMMA;

	
	// Others	

	public static final int IDENTIFIER = 51;
	public static final int SQL_STRING = 52;
	public static final int REAL_NUMBER = 53;
	public static final int FLOAT_NUMBER = 54;
	public static final int INTEGER = 55;
	public static final int COLUMNS = 56;	
	public static final int EXECUTE = 57;
	public static final int DEFAULT_SELECT = 58;
	public static final int DELIMITED_NAME = 59;
	public static final int UNKNOWN_EGL = 61;	

	public static final int SQLCOMMENT = Integer.MAX_VALUE;

	/**
	 * Creates an EGLPrimeToken with text.
	 */
	public EGLPrimeToken(int tokenType, String tokenText, int tokenOffset, int tokenLine, int tokenColumn) {
		type = tokenType;
		text = tokenText;
		offset = tokenOffset;
		line = tokenLine;
		column = tokenColumn; 
	}
	
	public boolean isDelimiter() {
		if (type >= firstDelimiter 
		    && type <= lastDelimiter)
		    return true;
		else return false;    
	}
	
	public void setHostVar() {
		hostVar = true;
	}
	
	public boolean isHostVar() {
		return hostVar;
	}
	
	public void setWhereCurrentOf() {
		whereCurrentOf = true;
	}
	
	public boolean isWhereCurrentOf() {
		return whereCurrentOf;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int off) {
		offset = off;
	}
	public int getLine() {
		return line;
	}

	public void setLine(int inputLine) {
		line = inputLine;
	}
	public int getColumn() {
		return column;
	}

	public void setColumn(int inputCol) {
		column = inputCol;
	}		
	public int getType() {
		return type;
	}

	public void setType(int inType) {
		type = inType;
	}

	public String getText() {
		return text;
	}

	public void setText(String inText) {
		text = inText;
	}
	public String toString() {
		return "[\"" + getText() + "\",<" + type + ">,offset=" + offset + ",line=" + line + ",column=" + column +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

}
