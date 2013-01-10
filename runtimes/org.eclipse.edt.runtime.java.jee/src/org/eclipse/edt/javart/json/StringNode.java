/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.json;

import eglx.lang.AnyException;

public class StringNode extends ValueNode {

	java.lang.String jsonValue;

	java.lang.String javaValue;

	//
	public StringNode(java.lang.String value, boolean json) {
		super();
		if (json) {
			this.jsonValue = value;
		} else {
			this.javaValue = value;
		}
	}
	
	public StringNode(byte[] value, boolean json) {
		super();
		if (json) {
			this.jsonValue = new String(value);
		} else {
			this.javaValue = new String(value);
		}
	}
	
	public StringNode(java.util.Calendar value, boolean json) {
		super();
		if (json) {
			this.jsonValue = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(value.getTime());
		} else {
			this.javaValue = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(value.getTime());
		}
	}

	public void accept(JsonVisitor visitor)  throws AnyException{
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			visitChildren(visitor);
		}
		visitor.endVisit(this);
	}

	public void visitChildren(JsonVisitor visitor) {
	}

	public java.lang.String getJavaValue() {
		if (javaValue == null) {
			javaValue = convertJsonToJava(jsonValue);
		}
		return javaValue;
	}

	public java.lang.String getJsonValue() {
		if (jsonValue == null) {
			jsonValue = convertJavaToJson(javaValue);
		}
		return jsonValue;
	}

	public static java.lang.String convertJavaToJson(java.lang.String value) {
		StringBuilder inBuf = new StringBuilder(value);
		StringBuilder outBuf = new StringBuilder();
		char currentChar;
		for( int idx = 0; idx < inBuf.length(); idx++ )
		{
			currentChar = inBuf.charAt( idx );
			switch(currentChar)
			{
				case('\\'):
					outBuf.append( "\\\\" );
					break;
				case('\"'):
					outBuf.append( "\\\"" );
					break;
				case('/'):
					outBuf.append( "\\/" );
					break;
				case('\b'):
					outBuf.append( "\\b" );
					break;
				case('\f'):
					outBuf.append( "\\f" );
					break;
				case('\n'):
					outBuf.append( "\\n" );
					break;
				case('\r'):
					outBuf.append( "\\r" );
					break;
				case('\t'):
					outBuf.append( "\\t" );
					break;
				default:
					if( Character.isISOControl( currentChar ) )
					{
						String controlEsc = "0000";
						controlEsc += Integer.toHexString( currentChar );
						outBuf.append( "\\u" );
						outBuf.append( controlEsc.substring( controlEsc.length() - 4 ) );
					}
					else
					{
						outBuf.append( currentChar );
					}
					break;
			}
		}
		return outBuf.toString();
	}

	public static java.lang.String convertJsonToJava(java.lang.String value) {
		String str = replaceUnicodeEscapes(value);
		StringBuilder inBuf = new StringBuilder(str);
		StringBuilder outBuf = new StringBuilder();
		char currentChar;
		for( int idx = 0; idx < inBuf.length(); idx++ )
		{
			currentChar = inBuf.charAt( idx );
			if( currentChar == '\\' && ++idx < inBuf.length() )
			{
				//look at the next char to see if it's special
				currentChar = inBuf.charAt( idx );
				switch(currentChar)
				{
					case('\\'):
						outBuf.append( '\\' );
						break;
					case('\"'):
						outBuf.append( '\"' );
						break;
					case('/'):
						outBuf.append( '/' );
						break;
					case('b'):
						outBuf.append( '\b' );
						break;
					case('f'):
						outBuf.append( '\f' );
						break;
					case('n'):
						outBuf.append( '\n' );
						break;
					case('r'):
						outBuf.append( '\r' );
						break;
					case('t'):
						outBuf.append( '\t' );
						break;
					default:
						outBuf.append( '\\' );
						outBuf.append( currentChar );
						break;
				}
			}
			else
			{
				outBuf.append( currentChar );
			}
		}
		return outBuf.toString();
	}
	
	private static String replaceUnicodeEscapes(String str) {
		int index = str.indexOf("\\u");
		if (index < 0) {
			return str;
		}
		
		StringBuilder buff = new StringBuilder();
		
		char previousChar = 'x';
		if (index > 0) {
			buff.append(str.substring(0, index));
			previousChar = str.charAt(index - 1);
		}
		
		for (int i = index; i < str.length(); i++) {
			if ((i < str.length() - 5) && (previousChar != '\\') && (str.charAt(i) == '\\') && (str.charAt(i + 1) == 'u')) {				
				String hex = str.substring(i+2, i+6);
				int hexInt = Integer.parseInt(hex, 16);
				buff.append((char)hexInt);
				i = i + 5;
				previousChar = 'x';
			}
			else {
				previousChar = str.charAt(i);
				buff.append(previousChar);
			}
		}
		
		return buff.toString();
		
	}

	public java.lang.String toJson() {
		return "\"" + getJsonValue() + "\"";
	}
	
	public String toString() {
		return getJavaValue();
	}
	
	public String toJava() {
		return getJavaValue();
	}

	public java.util.Calendar toCalendar() {
		java.util.Calendar calendar = java.util.Calendar.getInstance(); 
		calendar.setTime(new java.text.SimpleDateFormat("yyyyMMddHHmmss").parse(getJavaValue(),new java.text.ParsePosition(0)));
		return calendar;
	}
}
