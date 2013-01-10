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
package org.eclipse.edt.gen.javascript;

import java.util.Properties;

import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


/**
 * JavaScriptAliaser 
 */
public class JavaScriptAliaser
{
	/** Special characters we must escape to get valid JavaScript identifiers */
	private static final String specialChars = "-@#";

	/** A registry for the aliases that we have to use for JavaScript keywords. */
	private static final Properties keywordCache = new Properties();

	/**
	 * A registry for the aliases that we have to use for part names, backed by
	 * the keywordCache
	 */
	private static final Properties aliasCache = new Properties( keywordCache );

	/** Maps class names that cannot be used in JavaScript to aliases. */
	private static final Properties javascriptNames = new Properties();
	
	private static final String EXTERNAL_TYPE_PREFIX = "eze$$"; //$NON-NLS-1$
	
	static
	{
		// List is taken from http://javascript.about.com/library/blreserved.htm
		keywordCache.put( "abstract", "ezekw$$abstract" );
		keywordCache.put( "as", "ezekw$$as" );
		keywordCache.put( "boolean", "ezekw$$boolean" );
		keywordCache.put( "break", "ezekw$$break" );
		keywordCache.put( "byte", "ezekw$$byte" );
		
		keywordCache.put( "case", "ezekw$$case" );
		keywordCache.put( "catch", "ezekw$$catch" );
		keywordCache.put( "char", "ezekw$$char" );
		keywordCache.put( "class", "ezekw$$class" );
		keywordCache.put( "continue", "ezekw$$continue" );
		
		keywordCache.put( "const", "ezekw$$const" );
		keywordCache.put( "debugger", "ezekw$$debugger" );
		keywordCache.put( "default", "ezekw$$default" );
		keywordCache.put( "delete", "ezekw$$delete" );
		keywordCache.put( "do", "ezekw$$do" );
		keywordCache.put( "dojo", "ezekw$$dojo" );
		keywordCache.put( "dijit", "ezekw$$dijit" );
		keywordCache.put( "dojox", "ezekw$$dojox" );
		
		keywordCache.put( "double", "ezekw$$double" );
		keywordCache.put( "else", "ezekw$$else" );
		keywordCache.put( "enum", "ezekw$$enum" );
		keywordCache.put( "export", "ezekw$$export" );
		keywordCache.put( "extends", "ezekw$$extends" );
		
		keywordCache.put( "false", "ezekw$$false" );
		keywordCache.put( "final", "ezekw$$final" );
		keywordCache.put( "finally", "ezekw$$finally" );
		keywordCache.put( "float", "ezekw$$float" );
		keywordCache.put( "for", "ezekw$$for" );
		
		keywordCache.put( "function", "ezekw$$function" );
		keywordCache.put( "goto", "ezekw$$goto" );
		keywordCache.put( "if", "ezekw$$if" );
		keywordCache.put( "implements", "ezekw$$implements" );
		keywordCache.put( "import", "ezekw$$import" );
		
		keywordCache.put( "in", "ezekw$$in" );
		keywordCache.put( "instanceof", "ezekw$$instanceof" );
		keywordCache.put( "int", "ezekw$$int" );
		keywordCache.put( "interface", "ezekw$$interface" );
		keywordCache.put( "is", "ezekw$$is" );
		
		keywordCache.put( "long", "ezekw$$long" );
		keywordCache.put( "namespace", "ezekw$$namespace" );
		keywordCache.put( "native", "ezekw$$native" );
		keywordCache.put( "new", "ezekw$$new" );
		keywordCache.put( "null", "ezekw$$null" );
		
		keywordCache.put( "package", "ezekw$$package" );
		keywordCache.put( "private", "ezekw$$private" );
		keywordCache.put( "protected", "ezekw$$protected" );
		keywordCache.put( "public", "ezekw$$public" );
		keywordCache.put( "return", "ezekw$$return" );
		
		keywordCache.put( "short", "ezekw$$short" );
		keywordCache.put( "static", "ezekw$$static" );
		keywordCache.put( "super", "ezekw$$super" );
		keywordCache.put( "switch", "ezekw$$switch" );
		keywordCache.put( "synchronized", "ezekw$$synchronized" );
		
		keywordCache.put( "this", "ezekw$$this" );
		keywordCache.put( "throw", "ezekw$$throw" );
		keywordCache.put( "throws", "ezekw$$throws" );
		keywordCache.put( "transient", "ezekw$$transient" );
		keywordCache.put( "true", "ezekw$$true" );
		
		keywordCache.put( "try", "ezekw$$try" );
		keywordCache.put( "typeof", "ezekw$$typeof" );
		keywordCache.put( "use", "ezekw$$use" );
		keywordCache.put( "var", "ezekw$$var" );
		keywordCache.put( "void", "ezekw$$void" );
		
		keywordCache.put( "volatile", "ezekw$$volatile" );
		keywordCache.put( "while", "ezekw$$while" );
		keywordCache.put( "with", "ezekw$$with" );
	}

	static
	{
		// Initialize javascriptNames
		javascriptNames.put( "class", "className" );
	}

	/**
	 * Adds an alias for a special character to the buffer. The alias is the
	 * escape char followed by the four-digit hex representation of the Unicode
	 * character.
	 * 
	 * @param buffer
	 *            the buffer to write the alias to.
	 * @param c
	 *            the character to be aliased.
	 * @param escapeChar
	 *            the escape character.
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
	 * Certain function names defined in EGL types (such as String.length) cannot be implemented as-is because they
	 * conflict with the runtime language's existing (and un-overridable) definitions.   This will check for these
	 * special cases and return a Function object as appropriate.
	 * 
	 * @param f
	 * @return
	 */
	public static Function getAlias(Context ctx, Function f){
		Function result = f;
		
		try {
			if ((f.getContainer() instanceof Type)) {
				Type type = ((Type)f.getContainer()).getClassifier();
				
				if (TypeUtils.isTextType(type)) {
					if (f.getCaseSensitiveName().equals("length")) {
						result = (Function) f.clone();
						result.setName("textLen");
					}
				}
			}
			
			
			
			/* TODO sbg The logic below is part of https://bugs.eclipse.org/bugs/show_bug.cgi?id=358329, however,
			 * it should eventually be removed when we implement 
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=359315
			 */

			// TODO sbg As written, this will cause overloads to overwrite method renames (above) 
			{
				String overloaded = CommonUtilities.isOverloaded(ctx, f);
				if (overloaded != null){
					result = (Function) f.clone();
					result.setName(overloaded);
				}
			}
		}
		catch (Exception e) {
			result = f;
		}
		
		return result;
	}	

	/**
	 * Returns an alias for a part name, using '_' as the escape character.
	 * 
	 * @param partName
	 * @return either an alias for the part name, or the original part name if
	 *         it doesn't need an alias.
	 */
	public static String getAlias( String partName )
	{
		return getAlias( partName, '_' );
	}

	/**
	 * Returns an alias for a part name. Aliases are only different from the
	 * name when the name is a JavaScript keyword or it contains the characters
	 * '-', '@', or '#'. JavaScript keywords are aliased with a "ezekw$$" prefix.
	 * Invalid characters are aliased by replacing them with a string made from
	 * the escape character plus the Unicode value of the aliased character in
	 * hex. For example, if the escape character is '_', the alias for HELLO is
	 * HELLO, the alias for throw is ezekw$$throw, and the alias for rice-a-roni
	 * is rice_002da_002droni.
	 * 
	 * @param partName
	 *            the name of the part.
	 * @param escapeChar
	 *            the escape character to use.
	 * @return either an alias for the part name, or the part name if it doesn't
	 *         need an alias.
	 */
	public static String getAlias( String partName, char escapeChar )
	{
		// First check our cache of names so we don't have to examine each
		// character in the part name every time.
		String alias = JavaScriptAliaser.aliasCache.getProperty( partName );

		if ( alias != null )
		{
			return alias;
		}

		// We have to examine the part name to make an alias for it.
		char[] chars = partName.toCharArray();
		int start = 0;
		StringBuffer buffer = null;

		for ( int i = 0; i < chars.length; i++ )
		{
			if ( chars[ i ] != escapeChar && specialChars.indexOf( chars[ i ] ) >= 0 )
			{
				if ( buffer == null )
				{
					// Make a buffer to hold the alias in.
					buffer = new StringBuffer( chars.length + 16 );
				}

				// Put the characters that don't need to be aliased into the
				// buffer.
				buffer.append( chars, start, i - start );

				// Put the alias for this character into the buffer.
				JavaScriptAliaser.addCharacterAlias( buffer, chars[ i ], escapeChar );

				start = i + 1;
			}
		}

		// Save the value in the cache and return it.
		if ( buffer == null )
		{
			// No alias was needed.
			alias = partName;
		}
		else
		{
			// An alias was needed.
			buffer.append( chars, start, chars.length - start );
			alias = buffer.toString();
		}

		JavaScriptAliaser.aliasCache.put( partName, alias );

		return alias;
	}
	
	public static String getAliasForExternalType( String partName ){
		return EXTERNAL_TYPE_PREFIX + partName;		
	}

	/**
	 * This is the same as getAlias, with one more step. The value it returns will
	 * not be one of the special names we don't want to reuse.
	 * 
	 * @param partName
	 *            the name of the part.
	 * @return either an alias for the part name, or the part name if it doesn't
	 *         need an alias.
	 */
	public static String getJavascriptSafeAlias( String partName )
	{
		String alias = JavaScriptAliaser.getAlias( partName );

		return JavaScriptAliaser.javascriptNames.getProperty( alias, alias );
	}

	/**
	 * Returns whether name is a JavaScript keyword that we should use an alias
	 * for.
	 * 
	 * @param name
	 *            the name to check.
	 * @return true if so
	 */
	public static boolean isJavaScriptKeyword( String name )
	{
		return JavaScriptAliaser.keywordCache.containsKey( name );
	}

	/**
	 * Construct a String made from packageName, lowercased, with each part
	 * aliased.
	 * 
	 * @param packageName
	 * @return The massaged package name.
	 */
	public static String packageNameAlias( String packageName )
	{
		String unAliased = packageName.toLowerCase();
		String aliased = "";

		int dotIndex = unAliased.indexOf( '.' );

		if ( dotIndex == -1 )
		{
			aliased = getJavascriptSafeAlias( unAliased );
		}
		else
		{
			while ( dotIndex != -1 )
			{
				String piece = unAliased.substring( 0, dotIndex );

				aliased += getJavascriptSafeAlias( piece ) + ".";

				unAliased = unAliased.substring( dotIndex + 1 );
				dotIndex = unAliased.indexOf( '.' );
			}

			aliased = aliased + getJavascriptSafeAlias( unAliased );
		}

		return aliased;
	}

	/**
	 * Return the package name for a directory path, names lowercased with each
	 * folder separated by the specified separator character.
	 * 
	 * @param pkg
	 *            array of folder names
	 * @param separator
	 *            Character separator
	 * @return Package name
	 */
	public static String packageNameAlias( String[] pkg, char separator)
	{
		return packageNameAlias( pkg, separator, true );
	}
	public static String packageNameAlias( String[] pkg, char separator , boolean toLowerCase)
	{
		StringBuffer buff = new StringBuffer();

		for ( int i = 0; i < pkg.length; i++ )
		{
			if ( i > 0 )
			{
				buff.append( separator );
			}

			String alias = getJavascriptSafeAlias( pkg[ i ] );
			if (toLowerCase) 
				alias = alias.toLowerCase();
			
			buff.append( alias );
		}

		return buff.toString();
	}
}
