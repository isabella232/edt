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

package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.StringTokenizer;

/**
 * Default implementation of <code>IAutoIndentStrategy</code>.
 * This strategy always copies the indentation of the previous line.
 */
public abstract class AbstractAutoIndentStrategy extends DefaultIndentLineAutoEditStrategy {

	protected boolean appendTab(IDocument d, DocumentCommand c) throws BadLocationException {
		return false;
	}
		
	/**
	 * Copies the indentation of the previous line.
	 *
	 * @param d the document to work on
	 * @param c the command to deal with
	 */
	protected void autoIndentAfterNewLine(IDocument d, DocumentCommand c) {

		if (c.offset == -1 || d.getLength() == 0)
			return;

		try {
			// find start of line
			int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
			IRegion info = d.getLineInformationOfOffset(p);
			int start = info.getOffset();

			// find white spaces
			int end = findEndOfWhiteSpace(d, start, c.offset);

			StringBuffer buf = new StringBuffer(c.text);
			if (end > start) {
				// append to input
				buf.append(d.get(start, end - start));
			}
			if (appendTab(d, c))
				buf.append('\t');

			c.text = buf.toString();

		} catch (BadLocationException excp) {
			//Stop work, but do not log error
		}
	}
	
		
	/**
	 * Returns whether the text ends with one of the given search strings.
	 */
	protected boolean endsWithDelimiter(IDocument d, String txt) {

		String[] delimiters = d.getLegalLineDelimiters();

		for (int i = 0; i < delimiters.length; i++) {
			if (txt.endsWith(delimiters[i]))
				return true;
		}

		return false;
	}
	
	/**
	 * Returns whether the text ends with one of the given search strings.
	 */
	protected boolean endsWith(IDocument d, DocumentCommand c, String value) {

		try {
			int length = value.length();
			String value1 = value.substring(0, length - 1);
			String value2 = value.substring(length - 1, length);
			return c.offset > length && isMatch(d, c, value1) && c.text.equalsIgnoreCase(value2);
		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
		return false;
	}
	protected String getIndentOfLine(IDocument d, int line) throws BadLocationException {
		if (line > -1) {
			int start = d.getLineOffset(line);
			int end = start + d.getLineLength(line) - 1;
			int whiteend = findEndOfWhiteSpace(d, start, end);
			int length = whiteend - start - 1;
			if (length < 0)
				return ""; //$NON-NLS-1$
			return d.get(start, whiteend - start - 1);
		} else {
			return ""; //$NON-NLS-1$
		}
	}
	protected boolean isMatch(IDocument d, DocumentCommand c, String keyword) throws BadLocationException {

		if (d.get(c.offset - keyword.length(), keyword.length()).equalsIgnoreCase(keyword))
			return true;

		return false;
	}
	protected void smartInsertAfterBlock(IDocument d, DocumentCommand c, int length) {
		if (c.offset == -1 || d.getLength() == 0)
			return;

		try {
			int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
			int line = d.getLineOfOffset(p);
			int start = d.getLineOffset(line);
			int whiteend = findEndOfWhiteSpace(d, start, c.offset);
			int whiteLength = whiteend - start;
			
			//check the previous line.  If there is not the same amount of whitespace, do not shift
			//because chances are the user already shifted.  Don't want to do it twice.
			if (line > 0) {
				int previousStart = d.getLineOffset(line-1);
				int previousWhiteend = findEndOfWhiteSpace(d, previousStart, c.offset);
				int previousWhiteLength = previousWhiteend - previousStart;
				
				// shift only when line does not contain any text up to the closing text
				// and the whitespace matches the previous line
				if (whiteend + length == c.offset) {
					boolean previousLineIsEndStart = previousLineIsEndStart(d, previousWhiteend);
					if ((whiteLength == previousWhiteLength && !previousLineIsEndStart) || (whiteLength == previousWhiteLength+1 && previousLineIsEndStart)) {
						// evaluate the line with the opening bracket that matches out closing text
						StringBuffer replaceText = new StringBuffer(getIndentOfLine(d, line));
						// add the rest of the current line including the just added close text
						replaceText.append(d.get(whiteend, c.offset - whiteend));
						replaceText.append(c.text);
						// modify document command
						c.length = c.offset - start;
						c.offset = start;
						c.text = replaceText.toString();
					}
				}
			}

		} catch (BadLocationException ble) {
			ble.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	private boolean previousLineIsEndStart(IDocument document, int previousWhiteend) {
		try {
			//need to handle case where
			//		function aaa()
			//      en(d) - no shift
			//also
			//		function aaa()
			//			en(d) - shifts
			String restOfDocument = document.get(previousWhiteend, document.getLength() - previousWhiteend);
			UnicodeSet delimiters = new UnicodeSet("[ \t\n\r\f(]", false);		//added ( as one of the deilimiters		
			StringTokenizer tokenizer = new StringTokenizer(restOfDocument, delimiters);
			String token = tokenizer.nextToken();
			if (token.equalsIgnoreCase(IEGLConstants.KEYWORD_DATAITEM) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_DATATABLE) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_FORM) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_FORMGROUP) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_PRIVATE) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_FUNCTION) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_HANDLER) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_INTERFACE) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_LIBRARY) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_PROGRAM) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_RECORD) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_SERVICE) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_DELEGATE) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_EXTERNALTYPE) ||

				token.equalsIgnoreCase(IEGLConstants.KEYWORD_CASE) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_ELSE) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_FOR) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_FOREACH) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_IF) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_ONEXCEPTION) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_OPENUI) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_TRY) ||
				token.equalsIgnoreCase(IEGLConstants.KEYWORD_WHILE))
					return true;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return false;
	}
}
