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
package org.eclipse.edt.mof.egl;


public interface Operation extends Function {
	String PLUS = "+";
	String MINUS = "-";
	String TIMES = "*";
	String TIMESTIMES = "**";
	String DIVIDE = "/";
	String MODULO = "\\";
	String AND = "&&";
	String OR = "||";
	String XOR = "XOR";
	String BITAND = "&";
	String BITOR = "|";
	String LEFT_SHIFT = "<<";
	String RIGHT_SHIFT_ARITHMETIC = ">>";
	String RIGHT_SHIFT_LOGICAL = ">>>";
	String CONCAT = "::";
	String NULL_CONCAT = "?:";
	String GREATER = ">";
	String LESS = "<";
	String LESS_EQUALS = "<=";
	String GREATER_EQUALS = ">=";
	String NOT = "!";
	String EQUALS = "==";
	String NOT_EQUALS = "!=";
	String IN = "IN";
	String IS = "IS";
	String ISNOT = "IS NOT";
	String LIKE = "LIKE";
	String MATCHES = "MATCHES";
	String ARRAY_ACCESS = "[]";
	String SUBSTRING = "[:";
	String DYNAMIC_ACCESS = "['";
	
	String getOpSymbol();
	void setOpSymbol(String opSymbol);
	
	boolean isWidenConversion();
	boolean isNarrowConversion();
}
