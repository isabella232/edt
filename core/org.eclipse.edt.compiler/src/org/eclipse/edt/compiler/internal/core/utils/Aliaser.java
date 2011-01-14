/*******************************************************************************
 * Copyright © 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.utils;

import java.util.Properties;

/**
 * This is a copy of the Java Aliaser since we cannot have a dependency of the Java Plugin 
 */
public class Aliaser
{
	/**
	 * A registry for the aliases that we have to use for
	 * Java's keywords.
	 */
	private static final Properties keywordCache = new Properties();

	/**
	 * A registry for the aliases that we have to use for
	 * part names, backed by the keywordCache
	 */
	private static final Properties aliasCache = new Properties( keywordCache );

	static
	{
		// To prevent compilation errors, insert aliases for
		// Java's keywords.
		keywordCache.put( "abstract", "abstract_" );
		keywordCache.put( "assert", "assert_" );
		keywordCache.put( "boolean", "boolean_" );
		keywordCache.put( "break", "break_" );
		keywordCache.put( "byte", "byte_" );
		keywordCache.put( "case", "case_" );
		keywordCache.put( "catch", "catch_" );
		keywordCache.put( "char", "char_" );
		keywordCache.put( "class", "class_" );
		keywordCache.put( "const", "const_" );
		keywordCache.put( "continue", "continue_" );
		keywordCache.put( "default", "default_" );
		keywordCache.put( "do", "do_" );
		keywordCache.put( "double", "double_" );
		keywordCache.put( "else", "else_" );
		keywordCache.put( "extends", "extends_" );
		keywordCache.put( "false", "false_" );
		keywordCache.put( "final", "final_" );
		keywordCache.put( "finally", "finally_" );
		keywordCache.put( "float", "float_" );
		keywordCache.put( "for", "for_" );
		keywordCache.put( "goto", "goto_" );
		keywordCache.put( "if", "if_" );
		keywordCache.put( "implements", "implements_" );
		keywordCache.put( "import", "import_" );
		keywordCache.put( "instanceof", "instanceof_" );
		keywordCache.put( "int", "int_" );
		keywordCache.put( "interface", "interface_" );
		keywordCache.put( "long", "long_" );
		keywordCache.put( "native", "native_" );
		keywordCache.put( "new", "new_" );
		keywordCache.put( "null", "null_" );
		keywordCache.put( "package", "package_" );
		keywordCache.put( "private", "private_" );
		keywordCache.put( "protected", "protected_" );
		keywordCache.put( "public", "public_" );
		keywordCache.put( "return", "return_" );
		keywordCache.put( "short", "short_" );
		keywordCache.put( "static", "static_" );
		keywordCache.put( "strictfp", "strictfp_" );
		keywordCache.put( "super", "super_" );
		keywordCache.put( "switch", "switch_" );
		keywordCache.put( "synchronized", "synchronized_" );
		keywordCache.put( "this", "this_" );
		keywordCache.put( "throw", "throw_" );
		keywordCache.put( "throws", "throws_" );
		keywordCache.put( "transient", "transient_" );
		keywordCache.put( "true", "true_" );
		keywordCache.put( "try", "try_" );
		keywordCache.put( "void", "void_" );
		keywordCache.put( "volatile", "volatile_" );
		keywordCache.put( "while", "while_" );
	}
	
	/**
	 * Maps names of classes in the java.lang package to their aliases.
	 */
	private static final Properties javaLangNames = new Properties();
	
	static
	{
		// Initialize javaLangNames.
		javaLangNames.put( "AbstractMethodError", "AbstractMethodError_" );
		javaLangNames.put( "ArithmeticException", "ArithmeticException_" );
		javaLangNames.put( "ArrayIndexOutOfBoundsException", "ArrayIndexOutOfBoundsException_" );
		javaLangNames.put( "ArrayStoreException", "ArrayStoreException_" );
		javaLangNames.put( "AssertionError", "AssertionError_" );
		javaLangNames.put( "Boolean", "Boolean_" );
		javaLangNames.put( "Byte", "Byte_" );
		javaLangNames.put( "CharSequence", "CharSequence_" );
		javaLangNames.put( "Character", "Character_" );
		javaLangNames.put( "Class", "Class_" );
		javaLangNames.put( "ClassCastException", "ClassCastException_" );
		javaLangNames.put( "ClassCircularityError", "ClassCircularityError_" );
		javaLangNames.put( "ClassFormatError", "ClassFormatError_" );
		javaLangNames.put( "ClassLoader", "ClassLoader_" );
		javaLangNames.put( "ClassNotFoundException", "ClassNotFoundException_" );
		javaLangNames.put( "CloneNotSupportedException", "CloneNotSupportedException_" );
		javaLangNames.put( "Cloneable", "Cloneable_" );
		javaLangNames.put( "Comparable", "Comparable_" );
		javaLangNames.put( "Compiler", "Compiler_" );
		javaLangNames.put( "Double", "Double_" );
		javaLangNames.put( "Error", "Error_" );
		javaLangNames.put( "Exception", "Exception_" );
		javaLangNames.put( "ExceptionInInitializerError", "ExceptionInInitializerError_" );
		javaLangNames.put( "Float", "Float_" );
		javaLangNames.put( "IllegalAccessError", "IllegalAccessError_" );
		javaLangNames.put( "IllegalAccessException", "IllegalAccessException_" );
		javaLangNames.put( "IllegalArgumentException", "IllegalArgumentException_" );
		javaLangNames.put( "IllegalMonitorStateException", "IllegalMonitorStateException_" );
		javaLangNames.put( "IllegalStateException", "IllegalStateException_" );
		javaLangNames.put( "IllegalThreadStateException", "IllegalThreadStateException_" );
		javaLangNames.put( "IncompatibleClassChangeError", "IncompatibleClassChangeError_" );
		javaLangNames.put( "IndexOutOfBoundsException", "IndexOutOfBoundsException_" );
		javaLangNames.put( "InheritableThreadLocal", "InheritableThreadLocal_" );
		javaLangNames.put( "InstantiationError", "InstantiationError_" );
		javaLangNames.put( "InstantiationException", "InstantiationException_" );
		javaLangNames.put( "Integer", "Integer_" );
		javaLangNames.put( "InternalError", "InternalError_" );
		javaLangNames.put( "InterruptedException", "InterruptedException_" );
		javaLangNames.put( "LinkageError", "LinkageError_" );
		javaLangNames.put( "Long", "Long_" );
		javaLangNames.put( "Math", "Math_" );
		javaLangNames.put( "NegativeArraySizeException", "NegativeArraySizeException_" );
		javaLangNames.put( "NoClassDefFoundError", "NoClassDefFoundError_" );
		javaLangNames.put( "NoSuchFieldError", "NoSuchFieldError_" );
		javaLangNames.put( "NoSuchFieldException", "NoSuchFieldException_" );
		javaLangNames.put( "NoSuchMethodError", "NoSuchMethodError_" );
		javaLangNames.put( "NoSuchMethodException", "NoSuchMethodException_" );
		javaLangNames.put( "NullPointerException", "NullPointerException_" );
		javaLangNames.put( "Number", "Number_" );
		javaLangNames.put( "NumberFormatException", "NumberFormatException_" );
		javaLangNames.put( "Object", "Object_" );
		javaLangNames.put( "OutOfMemoryError", "OutOfMemoryError_" );
		javaLangNames.put( "Package", "Package_" );
		javaLangNames.put( "Process", "Process_" );
		javaLangNames.put( "Runnable", "Runnable_" );
		javaLangNames.put( "Runtime", "Runtime_" );
		javaLangNames.put( "RuntimeException", "RuntimeException_" );
		javaLangNames.put( "RuntimePermission", "RuntimePermission_" );
		javaLangNames.put( "SecurityException", "SecurityException_" );
		javaLangNames.put( "SecurityManager", "SecurityManager_" );
		javaLangNames.put( "Short", "Short_" );
		javaLangNames.put( "StackOverflowError", "StackOverflowError_" );
		javaLangNames.put( "StackTraceElement", "StackTraceElement_" );
		javaLangNames.put( "StrictMath", "StrictMath_" );
		javaLangNames.put( "String", "String_" );
		javaLangNames.put( "StringBuffer", "StringBuffer_" );
		javaLangNames.put( "StringIndexOutOfBoundsException", "StringIndexOutOfBoundsException_" );
		javaLangNames.put( "System", "System_" );
		javaLangNames.put( "Thread", "Thread_" );
		javaLangNames.put( "ThreadDeath", "ThreadDeath_" );
		javaLangNames.put( "ThreadGroup", "ThreadGroup_" );
		javaLangNames.put( "ThreadLocal", "ThreadLocal_" );
		javaLangNames.put( "Throwable", "Throwable_" );
		javaLangNames.put( "UnknownError", "UnknownError_" );
		javaLangNames.put( "UnsatisfiedLinkError", "UnsatisfiedLinkError_" );
		javaLangNames.put( "UnsupportedClassVersionError", "UnsupportedClassVersionError_" );
		javaLangNames.put( "UnsupportedOperationException", "UnsupportedOperationException_" );
		javaLangNames.put( "VerifyError", "VerifyError_" );
		javaLangNames.put( "VirtualMachineError", "VirtualMachineError_" );
		javaLangNames.put( "Void", "Void_" );
	}
	
	/**
	 * Adds an alias for the character to the buffer.  The
	 * alias is the escape char followed by the four-digit hex
	 * representation of the Unicode character.
	 *
	 * @param buffer  the buffer to write the alias to.
	 * @param c       the character to be aliased.
	 * @param escapeChar the escape character.
	 */
	private static void addCharacterAlias( StringBuffer buffer, char c, char escapeChar )
	{
		buffer.append( escapeChar );
		String hex = Integer.toHexString( c );
		
		// For VG parts, uppercase it.
		if ( escapeChar == 'x' )
		{
			hex = hex.toUpperCase();
		}

		// Make sure we get four digits.
		if ( c < '\u0010' )
		{
			buffer.append( "000" );
		}
		else if ( c < '\u0100' )
		{
			buffer.append( "00" );
		}
		else if ( c < '\u1000' )
		{
			buffer.append( '0' );
		}
		buffer.append( hex );
	}

	/**
	 * Returns true if name is a Java keyword that we should use an alias for.
	 * 
	 * @param name  the name to check.
	 * @return true if name is a Java keyword that we should use an alias for.
	 */
	public static boolean isJavaKeyword( String name )
	{
		return Aliaser.keywordCache.containsKey( name );
	}

	/**
	 * Returns an alias for the part name.  Aliases are only different
	 * from the name when the name is a Java keyword or it contains the 
	 * characters $, -, @, _, or #.  Java keywords are aliased by adding a 
	 * dollar sign at the end of the name.  Invalid characters are aliased
	 * by replacing them with a string made from the escape character plus the 
	 * Unicode value of the aliased character in hex.  For example, if the
	 * escape character is '_', the alias for HELLO is HELLO, the alias for throws
	 * is throws_, the alias for sand$ is sand_0024, and the alias for rice-a-roni
	 * is rice_002da_002droni.
	 *
	 * @param partName   the name of the part.
	 * @param escapeChar the escape character to use.
	 * @return either an alias for the part name, or the part
	 *                  name if it doesn't need an alias.
	 */
	public static String getAlias( String partName, char escapeChar )
	{
		// First check our cache of names so we don't have to
		// examine each character in the part name every time.
		String alias = Aliaser.aliasCache.getProperty( partName );
		if ( alias != null )
		{
			return alias;
		}
	
		// We have to examine the part name to make an alias
		// for it.
		char[] chars = partName.toCharArray();
		int start = 0;
		StringBuffer buffer = null;
		for ( int i = 0; i < chars.length; i++ )
		{
			if ( chars[ i ] == '$' || chars[ i ] == '-' || chars[ i ] == '_' ||
			     chars[ i ] == '@' || chars[ i ] == '#' )
			{
				if ( buffer == null )
				{
					// Make a buffer to hold the alias in.
					buffer = new StringBuffer( chars.length + 16 );
				}
	
				// Put the characters that don't need to be
				// aliased into the buffer.
				buffer.append( chars, start, i - start );
	
				// Put the hex for this character into the
				// buffer.
				Aliaser.addCharacterAlias( buffer, chars[ i ], escapeChar );
				start = i + 1;
			}
		}
	
		// Save the value in the cache and return it.
		if ( buffer == null )
		{
			// No alias was needed.
			Aliaser.aliasCache.put( partName, partName );
			return partName;
		}
		else
		{
			// An alias was needed.
			buffer.append( chars, start, chars.length - start );
			alias = buffer.toString();
			Aliaser.aliasCache.put( partName, alias );
			return alias;
		}
	}

	/**
	 * Returns an alias for the part name with '_' as the escape character.
	 * @param  partName
	 * @return either an alias for the part name, or the part
	 *                  name if it doesn't need an alias.
	 */
	public static String getAlias( String partName )
	{
		return( getAlias( partName, '_' ) );
	}

	/**
	 * This is the same as getAlias, with one more step.  The value it returns
	 * will not be the name of any class in the java.lang package.
	 * 
	 * @param partName  the name of the part.
	 * @return either an alias for the part name, or the part
	 *                  name if it doesn't need an alias.
	 */
	public static String getJavaSafeAlias( String partName )
	{
		String alias = Aliaser.getAlias( partName );
		return Aliaser.javaLangNames.getProperty( alias, alias );
	}
	
	/**
	 * Return the package name in lowercase with folders separated by 
	 * a separator character.
	 * @param pkg		String array of folder names
	 * @param separator Character separator
	 * @return		Legal Java gen package name
	 */
	public static String packageNameAlias(String[] pkg, char separator)
	{
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<pkg.length; i++) 
		{
			if (i>0)
			{
				buff.append(separator);
			}	
			buff.append(getJavaSafeAlias(pkg[i]).toLowerCase());
		}		
		return buff.toString();
	}

	/**
	 * @return a String made from packageName, lowercased, with each part aliased. 
	 */
	public static String packageNameAlias( String packageName )
	{
		String unAliased = packageName.toLowerCase();
		String aliased = "";
		int dotIndex = unAliased.indexOf( '.' );
		if ( dotIndex == -1 )
		{
			aliased = getJavaSafeAlias( unAliased );
		}
		else
		{
			while ( dotIndex != -1 )
			{
				String piece = unAliased.substring( 0, dotIndex );
				aliased = 
					( aliased.length() == 0 ? aliased : aliased + '.' )
					+ getJavaSafeAlias( piece );
				unAliased = unAliased.substring( dotIndex + 1 );
				dotIndex = unAliased.indexOf( '.' );
			}
			aliased = 
				aliased + '.' + getJavaSafeAlias( unAliased );
		}
		
		return aliased;
	}
	
	public static boolean isValidJavaIdentifier(String str, boolean validateNotKeyword) {
		if (str == null) {
			return false;
		}
		if (str == "") {
			return true;
		}
		if (validateNotKeyword) {
			if (isJavaKeyword(str)) {
				return false;
			}
		}
		
		if (!Character.isJavaIdentifierStart(str.charAt(0))) {
			return false;
		}
		
		for (int i = 1; i < str.length(); i++) {
			if (!Character.isJavaIdentifierPart(str.charAt(i))) {
				return false;
			}
		}
		return true;

	}
		
}
