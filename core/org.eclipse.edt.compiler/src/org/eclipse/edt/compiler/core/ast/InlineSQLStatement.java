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
 * OnExceptionBlock AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class InlineSQLStatement extends Node {
	
	private static final int POST_DELIMITER_LENGTH = 1;
	
	private String sqlStmt;
	private int openingBraceOffset;

	public InlineSQLStatement(String sqlStmt, int openingBraceOffset, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.sqlStmt = sqlStmt;
		this.openingBraceOffset = openingBraceOffset;
	}
	
	public String getValue() {
		// We need to strip off the "#sql{" at the beginning and the "}" at the end
		String fulltext = sqlStmt;
		return fulltext.substring(openingBraceOffset, fulltext.length() - POST_DELIMITER_LENGTH).trim();
	}
	
	public int getValueOffset() {
	    String text = sqlStmt;
	    int i = openingBraceOffset;
	    while(Character.isWhitespace(text.charAt(i))) {
	        i++;
	    }
	    return i + getOffset();
	}
	
	public int getOpeningBraceOffset() {
	    return getOffset() + openingBraceOffset;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new InlineSQLStatement(sqlStmt, openingBraceOffset, getOffset(), getOffset() + getLength());
	}
}
