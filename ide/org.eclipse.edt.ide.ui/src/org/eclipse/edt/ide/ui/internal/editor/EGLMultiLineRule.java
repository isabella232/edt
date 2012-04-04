/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

public class EGLMultiLineRule extends MultiLineRule {

	public EGLMultiLineRule(String startSequence, String endSequence, IToken token) {
		super(startSequence, endSequence, token, (char) 0);
	}

	/**
	 * Evaluates this rules without considering any column constraints. Resumes
	 * detection, i.e. looks only for the end sequence required by this rule if the
	 * <code>resume</code> flag is set.
	 *
	 * @param scanner the character scanner to be used
	 * @param resume <code>true</code> if detection should be resumed, <code>false</code> otherwise
	 * @return the token resulting from this evaluation
	 * @since 2.0
	 */
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {

		if (resume) {

			if (endSequenceDetected(scanner))
				return fToken;

		} else {

			int c = scanner.read();
			if (Character.toLowerCase((char) c) == fStartSequence[0]) {
				if (sequenceDetected(scanner, fStartSequence, false, false)) {
					if (endSequenceDetected(scanner))
						return fToken;
				}
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	/**
	 * Returns whether the end sequence was detected. As the pattern can be considered 
	 * ended by a line delimiter, the result of this method is <code>true</code> if the 
	 * rule breaks on the end  of the line, or if the EOF character is read.
	 *
	 * @param scanner the character scanner to be used
	 * @return <code>true</code> if the end sequence has been detected
	 */
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		int c;
		char[][] delimiters = scanner.getLegalLineDelimiters();
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip the escaped character.
				scanner.read();
			} else if ((fEndSequence.length > 0) && (Character.toLowerCase((char) c) == fEndSequence[0])) {
				// Check if the specified end sequence has been found.
				if (sequenceDetected(scanner, fEndSequence, true, true))
					return true;
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for (int i = 0; i < delimiters.length; i++) {
					if (c == delimiters[i][0] && sequenceDetected(scanner, delimiters[i], true, true))
						return true;
				}
			}
		}
		if (fBreaksOnEOF)
			return true;
		scanner.unread();
		return false;
	}

	/**
	 * Returns whether the next characters to be read by the character scanner
	 * are an exact match with the given sequence. No escape characters are allowed 
	 * within the sequence. If specified the sequence is considered to be found
	 * when reading the EOF character.
	 *
	 * @param scanner the character scanner to be used
	 * @param sequence the sequence to be detected
	 * @param eofAllowed indicated whether EOF terminates the pattern
	 * @return <code>true</code> if the given sequence has been detected
	 */
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed, boolean suffixCharAllowed) {
		for (int i = 1; i < sequence.length; i++) {
			int c = scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if ((Character.toLowerCase((char) c) != sequence[i])) {
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j = i - 1; j > 0; j--)
					scanner.unread();
				return false;
			}
		}

		// If we've just detected the start sequence, peek one character ahead of it in the
		// document and see if the character before "sql" is whitespace.  If so, the token counts
		// as the beginning of the sql partition.  If not (ie, mySQL), we should not count this
		// as a match.  
		if (sequence == fStartSequence) {
			// Get the document from the scanner
			IDocument doc = ((PartitionScanner) scanner).getDocument();
			
			// get the offset of the next character
			int charOffset = ((PartitionScanner) scanner).getOffset();
			
			try {
				// back up to the offset before the "sql"
				int offsetOfCharBeforeSQL = charOffset - fStartSequence.length - 1;
				
				// if we're not at the beginning of the document, peek at the character 
				if (offsetOfCharBeforeSQL >= 0) {
					char prefix = doc.getChar(offsetOfCharBeforeSQL);
					
					// If the character isn't whitespace, don't count this as a match
					if (!Character.isWhitespace((char) prefix))
						return false;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

//RATLC01077996 - commented out to allow other characters other than whitespace after #sql{
//	For example #sql{select .... }
//		int nextChar = scanner.read();
//		if ((nextChar == ICharacterScanner.EOF) || (Character.isWhitespace((char) nextChar))) {
//			scanner.unread();
//			return true;
//		} else {
//			scanner.unread();
//			// semi colon, and closing bracket and comma are allowed for the end sequence
//			if (suffixCharAllowed && (nextChar == ';' || nextChar == '}' || nextChar == ','))
//				return true;
//			else
//				return false;
//		}

		return true;
	}

	/*
	 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
	 * @since 2.0
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (fColumn == UNDEFINED)
			return doEvaluate(scanner, resume);

		int c = scanner.read();
		scanner.unread();
		if (Character.toLowerCase((char) c) == fStartSequence[0])
			return (fColumn == scanner.getColumn() ? doEvaluate(scanner, resume) : Token.UNDEFINED);
		else
			return Token.UNDEFINED;
	}
}
