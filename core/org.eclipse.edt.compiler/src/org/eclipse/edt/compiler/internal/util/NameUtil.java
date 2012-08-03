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
package org.eclipse.edt.compiler.internal.util;

import com.ibm.icu.util.StringTokenizer;

/**
 * @author twilson
 *
 */
public class NameUtil {

	public static final String NAME_DELIMITER = ".";
	private static StringTokenizer parser;
	/**
	 * 
	 */
	private NameUtil() {
	}
	
	public static String[] toStringArray(String str) {
		return toStringArray(str, NAME_DELIMITER);
	}
	
	public static String[] toStringArray(String str, String delim) {
		parser = new StringTokenizer(str, delim);
		String[] names = new String[parser.countTokens()];
		for(int i=0; i<names.length; i++) {
			names[i] = parser.nextToken();
		}
		return names;
	}
	
	public static String toString(String[] names) {
		return toString(names, NAME_DELIMITER);
	}
	public static String toString(String[] names, String delim) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < names.length; i++) {
			if (i>0) buffer.append(delim);
			buffer.append(names[i]);
		}
		return buffer.toString();
	}
	
	public static String getUnqualifiedName(String name) {
		String[] qualName = toStringArray(name);
		return qualName[qualName.length -1 ];
	}
}
