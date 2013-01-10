/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal;

import java.util.Arrays;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.osgi.util.TextProcessor;

public class Strings {

	/**
	 * Tells whether we have to use the {@link TextProcessor}
	 * <p>
	 * XXX: This is a performance optimization needed due to https://bugs.eclipse.org/bugs/show_bug.cgi?id=227713
	 * </p>
	 * @since 3.4
	 */
	private static final boolean USE_TEXT_PROCESSOR;
	static {
		String testString= "args : String[]"; //$NON-NLS-1$
		USE_TEXT_PROCESSOR= testString != TextProcessor.process(testString);
	}
	
	
	private Strings(){}
	
	/**
	 * Indent char is a space char but not a line delimiters.
	 * <code>== Character.isWhitespace(ch) && ch != '\n' && ch != '\r'</code>
	 */
	private static boolean isIndentChar(char ch) {
		return Character.isWhitespace(ch) && !isLineDelimiterChar(ch);
	}
	
	/**
	 * tests if a char is lower case. Fix for 26529 
	 */
	public static boolean isLowerCase(char ch) {
		return Character.toLowerCase(ch) == ch;
	}	
	
	/**
	 * Line delimiter chars are  '\n' and '\r'.
	 */
	private static boolean isLineDelimiterChar(char ch) {
		return ch == '\n' || ch == '\r';
	}	

	/**
	 * Converts the given string into an array of lines. The lines 
	 * don't contain any line delimiter characters.
	 *
	 * @return the string converted into an array of strings. Returns <code>
	 * 	null</code> if the input string can't be converted in an array of lines.
	 */
	public static String[] convertIntoLines(String input) {
		try {
			ILineTracker tracker= new DefaultLineTracker();
			tracker.set(input);
			int size= tracker.getNumberOfLines();
			String result[]= new String[size];
			for (int i= 0; i < size; i++) {
				IRegion region= tracker.getLineInformation(i);
				int offset= region.getOffset();
				result[i]= input.substring(offset, offset + region.getLength());
			}
			return result;
		} catch (BadLocationException e) {
			return null;
		}
	}

	/**
	 * Returns <code>true</code> if the given string only consists of
	 * white spaces according to Java. If the string is empty, <code>true
	 * </code> is returned.
	 * 
	 * @return <code>true</code> if the string only consists of white
	 * 	spaces; otherwise <code>false</code> is returned
	 * 
	 * @see java.lang.Character#isWhitespace(char)
	 */
	public static boolean containsOnlyWhitespaces(String s) {
		int size= s.length();
		for (int i= 0; i < size; i++) {
			if (!Character.isWhitespace(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	/**
	 * Removes leading tabs and spaces from the given string. If the string
	 * doesn't contain any leading tabs or spaces then the string itself is 
	 * returned.
	 */
	private static String trimLeadingTabsAndSpaces(String line) {
		int size= line.length();
		int start= size;
		for (int i= 0; i < size; i++) {
			char c= line.charAt(i);
			if (!isIndentChar(c)) {
				start= i;
				break;
			}
		}
		if (start == 0)
			return line;
		else if (start == size)
			return ""; //$NON-NLS-1$
		else
			return line.substring(start);
	}
	
	/**
	 * Returns the indent of the given string in indentation units. Odd spaces
	 * are not counted.
	 * 
	 * @param line the text line
	 * @param tabWidth the width of the '\t' character in space equivalents
	 * @param indentWidth the width of one indentation unit in space equivalents
	 * @since 3.1
	 */
	public static int computeIndentUnits(String line, int tabWidth, int indentWidth) {
		if (indentWidth == 0)
			return -1;
		int visualLength= measureIndentLength(line, tabWidth);
		return visualLength / indentWidth;
	}
	
	/**
	 * Computes the visual length of the indentation of a
	 * <code>CharSequence</code>, counting a tab character as the size until
	 * the next tab stop and every other whitespace character as one.
	 * 
	 * @param line the string to measure the indent of
	 * @param tabSize the visual size of a tab in space equivalents
	 * @return the visual length of the indentation of <code>line</code>
	 * @since 3.1
	 */
	private static int measureIndentLength(CharSequence line, int tabSize) {
		int length= 0;
		int max= line.length();
		for (int i= 0; i < max; i++) {
			char ch= line.charAt(i);
			if (ch == '\t') {
				int reminder= length % tabSize;
				length += tabSize - reminder;
			} else if (isIndentChar(ch)) {
				length++;
			} else {
				return length;
			}
		}
		return length;
	}

	/**
	 * Removes the given number of indents from the line. Asserts that the given line 
	 * has the requested number of indents. If <code>indentsToRemove <= 0</code>
	 * the line is returned.
	 * 
	 * @since 3.1
	 */
	private static String trimIndent(String line, int indentsToRemove, int tabWidth, int indentWidth) {
		if (line == null || indentsToRemove <= 0)
			return line;

		final int spaceEquivalentsToRemove= indentsToRemove * indentWidth;
		
		int start= 0;
		int spaceEquivalents= 0;
		int size= line.length();
		String prefix= null;
		for (int i= 0; i < size; i++) {
			char c= line.charAt(i);
			if (c == '\t') {
				int remainder= spaceEquivalents % tabWidth;
				spaceEquivalents += tabWidth - remainder;
			} else if (isIndentChar(c)) {
				spaceEquivalents++;
			} else {
				// Assert.isTrue(false, "Line does not have requested number of indents"); //$NON-NLS-1$
				start= i;
				break; 
			}
			if (spaceEquivalents == spaceEquivalentsToRemove) {
				start= i + 1;
				break;
			}
			if (spaceEquivalents > spaceEquivalentsToRemove) {
				// can happen if tabSize > indentSize, e.g tabsize==8, indent==4, indentsToRemove==1, line prefixed with one tab
				// this implements the third option
				start= i + 1; // remove the tab
				// and add the missing spaces
				char[] missing= new char[spaceEquivalents - spaceEquivalentsToRemove];
				Arrays.fill(missing, ' ');
				prefix= new String(missing);
				break;
			}
		}
		String trimmed;
		if (start == size)
			trimmed= ""; //$NON-NLS-1$
		else
			trimmed= line.substring(start);
		
		if (prefix == null)
			return trimmed;
		return prefix + trimmed;
	}

	/**
	 * Removes the common number of indents from all lines. If a line
	 * only consists out of white space it is ignored. If <code>
	 * considerFirstLine</code> is false the first line will be ignored.
	 * 
	 * @param project the java project from which to get the formatter
	 *        preferences, or <code>null</code> for global preferences
	 * @since 3.1
	 */
	public static void trimIndentation(String[] lines, IJavaProject project, boolean considerFirstLine) {
		trimIndentation(lines, CodeFormatterUtil.getTabWidth(project), CodeFormatterUtil.getIndentWidth(project), considerFirstLine);
	}
	
	/**
	 * Removes the common number of indents from all lines. If a line
	 * only consists out of white space it is ignored. If <code>
	 * considerFirstLine</code> is false the first line will be ignored.
	 * @since 3.1
	 */
	private static void trimIndentation(String[] lines, int tabWidth, int indentWidth, boolean considerFirstLine) {
		String[] toDo= new String[lines.length];
		// find indentation common to all lines
		int minIndent= Integer.MAX_VALUE; // very large
		for (int i= considerFirstLine ? 0 : 1; i < lines.length; i++) {
			String line= lines[i];
			if (containsOnlyWhitespaces(line))
				continue;
			toDo[i]= line;
			int indent= computeIndentUnits(line, tabWidth, indentWidth);
			if (indent < minIndent) {
				minIndent= indent;
			}
		}
		
		if (minIndent > 0) {
			// remove this indent from all lines
			for (int i= considerFirstLine ? 0 : 1; i < toDo.length; i++) {
				String s= toDo[i];
				if (s != null)
					lines[i]= trimIndent(s, minIndent, tabWidth, indentWidth);
				else {
					String line= lines[i];
					int indent= computeIndentUnits(line, tabWidth, indentWidth);
					if (indent > minIndent)
						lines[i]= trimIndent(line, minIndent, tabWidth, indentWidth);
					else
						lines[i]= trimLeadingTabsAndSpaces(line);
				}
			}
		}
	}
	
	/**
	 * Concatenate the given strings into one strings using the passed line delimiter as a
	 * delimiter. No delimiter is added to the last line.
	 */
	public static String concatenate(String[] lines, String delimiter) {
		StringBuffer buffer= new StringBuffer();
		for (int i= 0; i < lines.length; i++) {
			if (i > 0)
				buffer.append(delimiter);
			buffer.append(lines[i]);
		}
		return buffer.toString();
	}
	
	public static String removeMnemonicIndicator(String string) {
		return LegacyActionTools.removeMnemonics(string);
	}
	
	/**
	 * Adds special marks so that that the given styled string is readable in a BIDI environment.
	 * <p>
	 * XXX: Styles are currently erased by this method, see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=227559
	 * </p>
	 * 
	 * @param styledString the styled string
	 * @return the processed styled string
	 * @since 3.4
	 */
	public static StyledString markLTR(StyledString styledString) {
		if (!USE_TEXT_PROCESSOR)
			return styledString;
		
		String string= TextProcessor.process(styledString.getString());
		return new StyledString(string);
	}
	
	/**
	 * Adds special marks so that that the given styled string is readable in a BIDI environment.
	 * <p>
	 * XXX: Styles are currently erased by this method, see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=227559
	 * </p>
	 * 
	 * @param styledString the styled string
	 * @param additionalDelimiters the additional delimiters
	 * @return the processed styled string
	 * @since 3.4
	 */
	public static StyledString markLTR(StyledString styledString, String additionalDelimiters) {
		if (!USE_TEXT_PROCESSOR)
			return styledString;
		
		String string= TextProcessor.process(styledString.getString(), TextProcessor.getDefaultDelimiters() + additionalDelimiters);
		return new StyledString(string);
	}
	
	/**
	 * Adds special marks so that that the given string is readable in a BIDI environment.
	 * 
	 * @param string the string
	 * @return the processed styled string
	 * @since 3.4
	 */
	public static String markLTR(String string) {
		if (!USE_TEXT_PROCESSOR)
			return string;
		
		return TextProcessor.process(string);
	}
	
	/**
	 * Adds special marks so that that the given string is readable in a BIDI environment.
	 * 
	 * @param string the string
	 * @param additionalDelimiters the additional delimiters
	 * @return the processed styled string
	 * @since 3.4
	 */
	public static String markLTR(String string, String additionalDelimiters) {
		if (!USE_TEXT_PROCESSOR)
			return string;
		
		return TextProcessor.process(string, TextProcessor.getDefaultDelimiters() + additionalDelimiters);
	}
	

}
