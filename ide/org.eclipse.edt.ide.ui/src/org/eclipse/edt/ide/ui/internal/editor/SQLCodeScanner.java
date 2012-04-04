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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.EGLSQLKeywordHandler;
import org.eclipse.edt.ide.ui.internal.preferences.ColorProvider;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class SQLCodeScanner extends AbstractCodeScanner {

	public static String[] keywords = EGLSQLKeywordHandler.getSQLKeywordNamesToLowerCase();

	private ColorProvider colorProvider = null;
	
	/**
	 * Creates a EGL code scanner
	 */
	public SQLCodeScanner(ColorProvider provider) {
		super();
		colorProvider = provider;
		setRules();
	}

	/**
	 * Sets up the rules for different types of things we highlight
	 * in the SQL EGL text.  Here are the differences between SQL and
	 * the rest of the EGL file.
	 *  1. SQL doesn't support multi-line comments or system words,
	 *     so they aren't included.  
	 *  2. SQL comments begin with -- instead of //.  
	 *  3. SQL literals start with single quote instead of double quote.    
	 */
	public void setRules() {
		Token currentToken = null;
		TextAttribute attr = null;
		
//		ColorProvider.other = new Token(ColorProvider.DEFAULT);
//		ColorProvider.keyword = new Token(ColorProvider.KEYWORD);
//		ColorProvider.singleLineComment = new Token(ColorProvider.SINGLE_LINE_COMMENT);
//		//		ColorProvider.multiLineComment = new Token(ColorProvider.MULTI_LINE_COMMENT);
//		ColorProvider.literal = new Token(ColorProvider.LITERAL);
//		//		ColorProvider.systemWord = new Token(ColorProvider.SYSTEM_WORD);

		List rules = new ArrayList();

		// Jeff 10-24 Changed to use new method of getting Tokens

		// Add rule for SQL single line comments.
		attr = colorProvider.getTextAttribute(ColorProvider.SINGLE_LINE_COMMENT);
		currentToken = new Token(attr);
		rules.add(new EndOfLineRule(CodeConstants.EGL_SQL_SINGLE_LINE_COMMENT, currentToken)); //$NON-NLS-1$

		// //Add rules for multi-line comments.
//		attr = colorProvider.getTextAttribute(ColorProvider.MULTI_LINE_COMMENT);
//		currentToken = new Token(attr);
		// rules.add(new MultiLineRule("/*", "*/", currentToken)); //$NON-NLS-1$ //$NON-NLS-2$

		// Add rule for strings and character constants.
		attr = colorProvider.getTextAttribute(ColorProvider.LITERAL);
		currentToken = new Token(attr);
		rules.add(new SingleLineRule(CodeConstants.EGL_SQL_STRING_COMMENT, CodeConstants.EGL_SQL_STRING_COMMENT, currentToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add generic number rule.
		attr = colorProvider.getTextAttribute(ColorProvider.DEFAULT);
		currentToken = new Token(attr);
		rules.add(new NumberRule(currentToken));

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new EGLWhitespaceDetector()));

		// Add rules for all keywords
		EGLWordRule wordRule = new EGLWordRule(new EGLWordDetector(), currentToken);	// currentToken still set for Default
		attr = colorProvider.getTextAttribute(ColorProvider.KEYWORD);
		currentToken = new Token(attr);
		for (int i = 0; i < keywords.length; i++) {
			wordRule.addWord((String) keywords[i], currentToken);
		}

		//		attr = colorProvider.getTextAttribute(ColorProvider.SYSTEM_WORD);
		//		currentToken = new Token(attr);
		//		for (int i = 0; i < systemWords.size(); i++) {
		//			wordRule.addWord((String) systemWords.get(i), currentToken);
		//		}

		rules.add(wordRule);
		attr = colorProvider.getTextAttribute(ColorProvider.DEFAULT);
		currentToken = new Token(attr);
		setDefaultReturnToken(currentToken);

		result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
