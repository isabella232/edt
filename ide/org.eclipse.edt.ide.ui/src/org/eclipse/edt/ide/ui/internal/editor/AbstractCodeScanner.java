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

import java.util.Locale;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public abstract class AbstractCodeScanner extends BufferedRuleBasedScanner {

	// Holds the rules for the scanner
	protected static IRule[] result;	
		
	/**
	 * An EGL aware white space detector.
	 */
	class EGLWhitespaceDetector implements IWhitespaceDetector {
		public boolean isWhitespace(char c) {
			return Character.isWhitespace(c);
		}
	}
	
	/**
	 * An EGL aware word detector.  
	 * Need to override evaluate() to be case insensitive - added toLowerCase()
	 */
	class EGLWordRule extends WordRule {
		public EGLWordRule(IWordDetector detector) {
			super(detector);
		}
		public EGLWordRule(IWordDetector detector, IToken defaultToken) {
			super(detector, defaultToken);
		}
		/*
		 * @see IRule#evaluate(ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			StringBuffer eglBuffer = new StringBuffer();
			int c = scanner.read();
			if (fDetector.isWordStart((char) c)) {
				if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {
						eglBuffer.setLength(0);
					do {
						eglBuffer.append((char) c);
						c = scanner.read();
					} while (c != ICharacterScanner.EOF && fDetector.isWordPart((char) c));
					scanner.unread();
					IToken token = (IToken) fWords.get(eglBuffer.toString().toLowerCase( Locale.ENGLISH ));
					if (token != null)
						return token;

					if (fDefaultToken.isUndefined())
						unreadBuffer(scanner);
						return fDefaultToken;
				}
			}
			scanner.unread();
			return Token.UNDEFINED;
		}
	}
	
	/**
	 * An EGL aware word detector.
	 */
	class EGLWordDetector implements IWordDetector {
		public boolean isWordPart(char c) {
			//In EGL '-' '#' and '@' is a legal character for a name
			return Character.isJavaIdentifierPart(c) || c == '-' || c == '#' || c== '@';
		}

		public boolean isWordStart(char c) {
			return Character.isJavaIdentifierStart(c);
		}
	}
}
