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
package org.eclipse.edt.compiler.internal.core.validation.name;

/**
 * @author dollar
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EGLNameToken {

	protected int type;
	protected String text = null;
	protected int offset;
	protected int line;
	protected int column;
	protected boolean subscript = false;
	
	// delimiters
	private int firstDelimiter = L_SQUARE;
	public static final int L_SQUARE = 1;
	public static final int R_SQUARE = 2;
	public static final int DOT = 3;
	public static final int COMMA = 4;
	public static final int COLON = 5;
	private int lastDelimiter = COLON;

	// Others	
	public static final int IDENTIFIER = 6;
	public static final int REAL_NUMBER = 7;
	public static final int INTEGER = 8;
	public static final int FLOAT_NUMBER = 9;
	public static final int QUOTED_STRING = 10;
	
	public static final int UNKNOWN_EGL = 11;	

	public static final int COMMENT = Integer.MAX_VALUE;

	/**
	 * Creates an EGLNameToken with text.
	 */
	public EGLNameToken(int tokenType, String tokenText, int tokenOffset, int tokenLine, int tokenColumn) {
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


