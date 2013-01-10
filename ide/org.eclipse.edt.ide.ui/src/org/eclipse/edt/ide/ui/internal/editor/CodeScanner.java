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

/*
 * This class handles all the syntax highlighting for EGL files
 * except for the SQL sections.
 */
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.EGLKeywordHandler;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.preferences.ColorProvider;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * An EGL code scanner.
 * 
 * In the future, the tokens in this scanner will have to be updated based on changes in the
 * preferences.
 */
public class CodeScanner extends AbstractCodeScanner {

	public static String[] keywords = EGLKeywordHandler.getKeywordNamesToLowerCase();

	private ColorProvider colorProvider = null;
	/**
	 * Creates a EGL code scanner
	 */
	public CodeScanner(ColorProvider provider) {
		super();
		colorProvider = provider;
		setRules();
	}

	/*
	 * @see AbstractJavaScanner#affectsBehavior(PropertyChangeEvent)
	 */	
	public boolean affectsBehavior(PropertyChangeEvent event) {
		return event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_KEYWORD_COLOR) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_KEYWORD_BOLD) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_STRING_COLOR) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_STRING_BOLD) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_MULTI_LINE_COMMENT_BOLD) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_DEFAULT_COLOR) ||
				event.getProperty().equals(EDTUIPreferenceConstants.EDITOR_DEFAULT_BOLD);
	}

	/**
	 * Sets up the rules for different types of things we highlight
	 * in the non-SQL EGL text.  
	 */
	public void setRules() {
		Token currentToken = null;
		TextAttribute attr = null;
				
		List rules = new ArrayList();		
		
		// Add rule for single line comments.
		attr = colorProvider.getTextAttribute(ColorProvider.SINGLE_LINE_COMMENT);
		currentToken = new Token(attr);

		// Add rules for multi-line comments.
		attr = colorProvider.getTextAttribute(ColorProvider.MULTI_LINE_COMMENT);
		currentToken = new Token(attr);

		// Add rule for strings and character constants.
		attr = colorProvider.getTextAttribute(ColorProvider.LITERAL);
		currentToken = new Token(attr);
		rules.add(new SingleLineRule(CodeConstants.EGL_STRING_COMMENT, CodeConstants.EGL_STRING_COMMENT, currentToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add generic number rule.
		attr = colorProvider.getTextAttribute(ColorProvider.DEFAULT);
		currentToken = new Token(attr);
		rules.add(new NumberRule(currentToken)); //$NON-NLS-1$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new EGLWhitespaceDetector()));

		// Add rules for all keywords
		EGLWordRule wordRule = new EGLWordRule(new EGLWordDetector(), currentToken);	// currentToken still set for Default
		attr = colorProvider.getTextAttribute(ColorProvider.KEYWORD);
		currentToken = new Token(attr);
		for (int i = 0; i < keywords.length; i++) {
			wordRule.addWord((String) keywords[i], currentToken);
		}

		rules.add(wordRule);
		
		attr = colorProvider.getTextAttribute(ColorProvider.DEFAULT);
		currentToken = new Token(attr);
		setDefaultReturnToken(currentToken);

		result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
