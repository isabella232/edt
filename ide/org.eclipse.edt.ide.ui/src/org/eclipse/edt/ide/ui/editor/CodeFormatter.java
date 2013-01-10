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
package org.eclipse.edt.ide.ui.editor;

import java.io.BufferedReader;
import java.io.StringReader;

import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;


public abstract class CodeFormatter {

	protected File getFileAST(IDocument doc){
		if(doc instanceof IEGLDocument)
		{
			IEGLDocument egldoc = (IEGLDocument)doc;
			return egldoc.getNewModelEGLFile();
		}
		else{	//parse the content to FileAST tree
			ErrorCorrectingParser newParser;			
			int whitespacemask = ErrorCorrectingParser.RETURN_BLOCK_COMMENT | ErrorCorrectingParser.RETURN_LINE_COMMENT | ErrorCorrectingParser.RETURN_LINEBREAKS;
			// TODO commented out EGLVAGCompatibilitySetting
//	       	if(EGLVAGCompatibilitySetting.isVAGCompatibility())
//	       		newParser = new ErrorCorrectingParser(new VAGLexer(new BufferedReader(new StringReader(doc.get()))), whitespacemask);
//	       	else
	       		newParser = new ErrorCorrectingParser(new Lexer(new BufferedReader(new StringReader(doc.get()))), whitespacemask);		
			return (File)(newParser.parse().value);			
		}
	}

	/** 
	 * Format <code>source</code>,
	 * and returns a text edit that correspond to the difference between the given string and the formatted string.
	 * <p>It returns null if the given string cannot be formatted due to syntax errors</p>
	 * 
	 * <p>If the offset position is matching a whitespace, the result can include whitespaces. It would be up to the
	 * caller to get rid of preceeding whitespaces.</p>
	 * 
	 * @param offset the given offset to start recording the edits (inclusive).
	 * @param length the given length to stop recording the edits (exclusive).
	 * @param indentationLevel the initial indentation level, used 
	 *      to shift left/right the entire source fragment. An initial indentation
	 *      level of zero or below has no effect.
	 * @param lineSeparator the line separator to use in formatted source,
	 *     if set to <code>null</code>, then the platform default one will be used.
	 * @param syntxErrRequestor if there is any syntax error in the passing document, format will return null 
	 * 							caller can decicde what to do with the syntax errors
	 * 							pass in null if caller doesn't care to know about the syntax error
	 * 
	 * @return the text edit
	 * @throws IllegalArgumentException if offset is lower than 0, length is lower than 0 or
	 * length is greater than source length.
	 */
	public abstract TextEdit format(IDocument document, int offset, int length, int indentationLevel, String lineSeparator, ISyntaxErrorRequestor syntxErrRequestor);
	
}
