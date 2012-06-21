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
package org.eclipse.edt.compiler.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

/**
 * @author jshavor
 *
 * This is the public interface to for all EGL keywords
 */
public class EGLKeywordHandler {

	private static String[] keywordNames =
		{
			//EGL keywords
			"absolute", //$NON-NLS-1$
			"add", //$NON-NLS-1$
			"all", //$NON-NLS-1$
			"and", //$NON-NLS-1$
			"as", //$NON-NLS-1$
			"bind", //$NON-NLS-1$
			"by", //$NON-NLS-1$
			"byName", //$NON-NLS-1$
			"byPosition", //$NON-NLS-1$
			"call", //$NON-NLS-1$
			"case", //$NON-NLS-1$
			"close", //$NON-NLS-1$
			"const", //$NON-NLS-1$
			"continue", //$NON-NLS-1$
			"constructor", //$NON-NLS-1$
			"converse", //$NON-NLS-1$
			"current", //$NON-NLS-1$
			"dataItem", //$NON-NLS-1$
			"dataTable", //$NON-NLS-1$
			"decrement", //$NON-NLS-1$
			"delegate", //$NON-NLS-1$
			"delete", //$NON-NLS-1$
			"display", //$NON-NLS-1$
			"dliCall", //$NON-NLS-1$
			"else", //$NON-NLS-1$
			"embed", //$NON-NLS-1$
			"end", //$NON-NLS-1$
			"enumeration", //$NON-NLS-1$
			"escape", //$NON-NLS-1$
			"execute", //$NON-NLS-1$
			"exit", //$NON-NLS-1$
			"extends", //$NON-NLS-1$			
			"externalType", //$NON-NLS-1$
			"false", //$NON-NLS-1$
			"field", //$NON-NLS-1$
			"first", //$NON-NLS-1$
			"for", //$NON-NLS-1$
			"forEach", //$NON-NLS-1$
			"form", //$NON-NLS-1$
			"formGroup", //$NON-NLS-1$
			"forUpdate", //$NON-NLS-1$
			"forward", //$NON-NLS-1$
			"freeSql", //$NON-NLS-1$
			"from", //$NON-NLS-1$
			"function", //$NON-NLS-1$
			"get", //$NON-NLS-1$
			"goto", //$NON-NLS-1$
			"group", //$NON-NLS-1$
			"handler", //$NON-NLS-1$
			"hold", //$NON-NLS-1$
			"if", //$NON-NLS-1$
			"implements", //$NON-NLS-1$
			"import", //$NON-NLS-1$
			"in", //$NON-NLS-1$
			"inOut", //$NON-NLS-1$
			"inparent", //$NON-NLS-1$
			"insert", //$NON-NLS-1$
			"interface", //$NON-NLS-1$
			"into", //$NON-NLS-1$
			"is", //$NON-NLS-1$
			"isa", //$NON-NLS-1$
			"label", //$NON-NLS-1$
			"languageBundle", //$NON-NLS-1$
			"last", //$NON-NLS-1$
			"library", //$NON-NLS-1$
			"like", //$NON-NLS-1$
			"matches", //$NON-NLS-1$
			"move", //$NON-NLS-1$
			"new", //$NON-NLS-1$
			"next", //$NON-NLS-1$
			"null", //$NON-NLS-1$
			"no", //$NON-NLS-1$
			"noCursor", //$NON-NLS-1$
			"not", //$NON-NLS-1$
			"of", //$NON-NLS-1$
			"onEvent", //$NON-NLS-1$
			"onException", //$NON-NLS-1$
			"open", //$NON-NLS-1$
			"openUI", //$NON-NLS-1$
			"or", //$NON-NLS-1$
			"otherwise", //$NON-NLS-1$
			"out", //$NON-NLS-1$
			"package", //$NON-NLS-1$
			"passing", //$NON-NLS-1$
			"prepare", //$NON-NLS-1$
			"previous", //$NON-NLS-1$
			"print", //$NON-NLS-1$
			"private", //$NON-NLS-1$
			"program", //$NON-NLS-1$
			"record", //$NON-NLS-1$
			"ref", //$NON-NLS-1$
			"relative", //$NON-NLS-1$
			"replace", //$NON-NLS-1$
			"return", //$NON-NLS-1$
			"returning", //$NON-NLS-1$
			"returns", //$NON-NLS-1$
			"rununit", //$NON-NLS-1$
			"scroll", //$NON-NLS-1$
			"self", //$NON-NLS-1$
			"service", //$NON-NLS-1$
			"set", //$NON-NLS-1$
			"show", //$NON-NLS-1$
			"singleRow", //$NON-NLS-1$
			"sqlnullable", //$NON-NLS-1$
			"stack", //$NON-NLS-1$
			"static", //$NON-NLS-1$
			"super", //$NON-NLS-1$
			"this", //$NON-NLS-1$
			"throw", //$NON-NLS-1$
			"to", //$NON-NLS-1$
			"transaction", //$NON-NLS-1$
			"transfer", //$NON-NLS-1$
			"true", //$NON-NLS-1$
			"try", //$NON-NLS-1$
			"type", //$NON-NLS-1$
			"update", //$NON-NLS-1$
			"url", //$NON-NLS-1$
			"use", //$NON-NLS-1$
			"using", //$NON-NLS-1$
			"usingKeys", //$NON-NLS-1$
			"usingPCB", //$NON-NLS-1$
			"when", //$NON-NLS-1$
			"where", //$NON-NLS-1$
			"while", //$NON-NLS-1$
			"with", //$NON-NLS-1$
			"withV60Compat", //$NON-NLS-1$
			"wrap", //$NON-NLS-1$
			"yes", //$NON-NLS-1$
			};


	private static HashSet keywordHashSet = new HashSet( Arrays.asList( getKeywordNamesToLowerCase() ) );

	
	/**
	 * @return
	 */
	public static String[] getKeywordNames() {
		return keywordNames;
	}
	
	
	/**
	 * return the List of EGL keyword names in lowercase
	 */
	public static String[] getKeywordNamesToLowerCase() {
		String[] lowercaseKeywordsNames = new String[keywordNames.length];
		for (int i = 0; i < lowercaseKeywordsNames.length; i++) {
			lowercaseKeywordsNames[i] = keywordNames[i].toLowerCase(Locale.ENGLISH);
		}
		return lowercaseKeywordsNames;
	}

	/**
	 * @return
	 */
	public static HashSet getKeywordHashSet() {
		return keywordHashSet;
	}

}
