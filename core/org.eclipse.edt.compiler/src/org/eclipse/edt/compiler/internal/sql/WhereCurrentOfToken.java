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
package org.eclipse.edt.compiler.internal.sql;

import org.eclipse.edt.compiler.internal.sqltokenizer.EGLPrimeToken;


/**
 * @author dollar
 *
 */
public class WhereCurrentOfToken extends Token {
	private String resultSetIdentifier= ""; //$NON-NLS-1$
	/**
	 * StringToken constructor comment.
	 */
	public WhereCurrentOfToken() {
		super();
	}
	/**
	 * StringToken constructor comment.
	 * @param string java.lang.String
	 */
	public WhereCurrentOfToken(String string) {
		super(trim(string));
		if (this.string.length() == 0) {
			this.string = " "; //$NON-NLS-1$
		}
		setStartOffset(0);
		setEndOffset(string.length()-1);
	}
	
	/**
	 * StringToken constructor comment.
	 * @param EGLPrimeToken
	 */
	public WhereCurrentOfToken(EGLPrimeToken token) {
		string = token.getText();
		setStartOffset(token.getOffset());
		setEndOffset(token.getOffset()+ token.getText().length());
		
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (6/14/2001 12:25:55 PM)
	 * @param token org.eclipse.edt.compiler.internal.compiler.sql.StringToken
	 */
	public void append(StringToken token) {
		StringBuffer buff = new StringBuffer(string);
		if (buff.toString().equalsIgnoreCase("where current of")) //$NON-NLS-1$
			resultSetIdentifier = token.string;		
		if (getEndOffset() != token.getStartOffset())
			buff.append(" ");    // tokens had a space on input,maintain one.
		buff.append(token.string);
		string = buff.toString();
		setEndOffset(token.getStartOffset()+ token.string.length());
	}	

	public String getResultSetIdentifier(){
		return resultSetIdentifier;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (8/28/2001 11:36:20 AM)
	 * @return java.lang.String
	 * @param string java.lang.String
	 */
	public static String trim(String string) {

		int len = string.length();
		int st = 0;
		char[] val = string.toCharArray();

		while ((st < len) && (val[st] <= ' ')) {
			st++;
		}
		while ((st < len) && (val[len - 1] <= ' ')) {
			len--;
		}

		String pre = ""; //$NON-NLS-1$
		String post = ""; //$NON-NLS-1$
		if (st > 0) {
			pre = " "; //$NON-NLS-1$
		}
		if (len < string.length()) {
			post = " "; //$NON-NLS-1$
		}
		StringBuffer buff = new StringBuffer();
		buff.append(pre);
		buff.append(string.substring(st, len));
		buff.append(post);

		return buff.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.compiler.internal.compiler.sql.Token#getSQLString()
	 */
	public String getSQLString() {
		return null;
	}

	public boolean isWhereCurrentOfToken() {
		return true;
	}
}


