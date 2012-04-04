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
package org.eclipse.edt.javart.util;

import java.util.Properties;

public class JavaAliaser {
	/**
	 * A registry for the aliases that we have to use for Java's keywords.
	 */
	private static final Properties keywordCache = new Properties();

	static {
		// To prevent compilation errors, insert aliases for Java's keywords.
		// This list of keywords is correct as of Java 7.  For completeness
		// we include identifiers that are keywords in EGL too, plus the literals
		// true, false, and null.
		keywordCache.put("abstract", "eze_abstract");
		keywordCache.put("assert", "eze_assert");
		keywordCache.put("boolean", "eze_boolean");
		keywordCache.put("break", "eze_break");
		keywordCache.put("byte", "eze_byte");
		keywordCache.put("case", "eze_case");
		keywordCache.put("catch", "eze_catch");
		keywordCache.put("char", "eze_char");
		keywordCache.put("class", "eze_class");
		keywordCache.put("const", "eze_const");
		keywordCache.put("continue", "eze_continue");
		keywordCache.put("default", "eze_default");
		keywordCache.put("do", "eze_do");
		keywordCache.put("double", "eze_double");
		keywordCache.put("else", "eze_else");
		keywordCache.put("enum", "eze_enum");
		keywordCache.put("extends", "eze_extends");
		keywordCache.put("false", "eze_false");
		keywordCache.put("final", "eze_final");
		keywordCache.put("finally", "eze_finally");
		keywordCache.put("float", "eze_float");
		keywordCache.put("for", "eze_for");
		keywordCache.put("goto", "eze_goto");
		keywordCache.put("if", "eze_if");
		keywordCache.put("implements", "eze_implements");
		keywordCache.put("import", "eze_import");
		keywordCache.put("instanceof", "eze_instanceof");
		keywordCache.put("int", "eze_int");
		keywordCache.put("interface", "eze_interface");
		keywordCache.put("long", "eze_long");
		keywordCache.put("native", "eze_native");
		keywordCache.put("new", "eze_new");
		keywordCache.put("null", "eze_null");
		keywordCache.put("package", "eze_package");
		keywordCache.put("private", "eze_private");
		keywordCache.put("protected", "eze_protected");
		keywordCache.put("public", "eze_public");
		keywordCache.put("return", "eze_return");
		keywordCache.put("short", "eze_short");
		keywordCache.put("static", "eze_static");
		keywordCache.put("strictfp", "eze_strictfp");
		keywordCache.put("super", "eze_super");
		keywordCache.put("switch", "eze_switch");
		keywordCache.put("synchronized", "eze_synchronized");
		keywordCache.put("this", "eze_this");
		keywordCache.put("throw", "eze_throw");
		keywordCache.put("throws", "eze_throws");
		keywordCache.put("transient", "eze_transient");
		keywordCache.put("true", "eze_true");
		keywordCache.put("try", "eze_try");
		keywordCache.put("void", "eze_void");
		keywordCache.put("volatile", "eze_volatile");
		keywordCache.put("while", "eze_while");
	}

	/**
	 * Returns true if name is a Java keyword that we should use an alias for.
	 * @param name the name to check.
	 * @return true if name is a Java keyword that we should use an alias for.
	 */
	public static boolean isJavaKeyword(String name) {
		return keywordCache.containsKey(name);
	}

	/**
	 * Returns an alias for the part name. Aliases are only different from the name when the name is a Java keyword. Java
	 * keywords are aliased by prefixing the keyword with eze_.
	 * @param partName the name of the part.
	 * @return either an alias for the part name, or the part name if it doesn't need an alias.
	 */
	public static String getAlias(String partName) {
		// check our cache of names
		return keywordCache.getProperty(partName, partName);
	}

	/**
	 * Return the package name in lowercase with folders separated by a separator character.
	 * @param pkg String array of folder names
	 * @param separator Character separator
	 * @return Legal Java gen package name
	 */
	public static String packageNameAlias(String[] pkg, char separator) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < pkg.length; i++) {
			if (i > 0)
				buff.append(separator);
			buff.append(getAlias(pkg[i]).toLowerCase());
		}
		return buff.toString();
	}

	/**
	 * @return a String made from packageName, lowercased, with each part aliased.
	 */
	public static String packageNameAlias(String packageName) {
		String unAliased = packageName.toLowerCase();
		String aliased = "";
		int dotIndex = unAliased.indexOf('.');
		if (dotIndex == -1)
			aliased = getAlias(unAliased);
		else {
			while (dotIndex != -1) {
				String piece = unAliased.substring(0, dotIndex);
				aliased = (aliased.length() == 0 ? aliased : aliased + '.') + getAlias(piece);
				unAliased = unAliased.substring(dotIndex + 1);
				dotIndex = unAliased.indexOf('.');
			}
			aliased = aliased + '.' + getAlias(unAliased);
		}
		return aliased;
	}

	public static boolean isValidJavaIdentifier(String str, boolean validateNotKeyword) {
		if (str == null)
			return false;
		if (str == "")
			return true;
		if (validateNotKeyword) {
			if (isJavaKeyword(str))
				return false;
		}
		if (!Character.isJavaIdentifierStart(str.charAt(0)))
			return false;
		for (int i = 1; i < str.length(); i++) {
			if (!Character.isJavaIdentifierPart(str.charAt(i)))
				return false;
		}
		return true;
	}
}
