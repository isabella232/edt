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
package org.eclipse.edt.compiler.core.ast;

/**
 * @author winghong
 */
public class NullProblemRequestor implements ISyntaxErrorRequestor {

	public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
	}

	public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
	}

	public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
	}

	public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
	}

	public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {
	}
	
	public void keywordAsName(int terminalType, int startOffset, int endOffset) {
	}

	public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
	}

	public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
	}

	public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
	}

	public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
	}

	public void missingTerminal(int terminalType, int startOffset, int endOffset) {
	}

	public void panicPhrase(int startOffset, int endOffset) {
	}
	
	public void tooManyErrors() {
	}

	public void unexpectedPhrase(int startOffset, int endOffset) {
	}

	public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
	}

	public void unexpectedTerminal(int startOffset, int endOffset) {
	}

	public void invalidEscapeSequence(int startOffset, int endOffset) {
	}

	public void unclosedBlockComment(int startOffset, int endOffset) {
	}

	public void unclosedSQL(int startOffset, int endOffset) {
	}

	public void unclosedSQLCondition(int startOffset, int endOffset) {
	}

	public void unclosedString(int startOffset, int endOffset) {
	}

	public void whitespaceInSQL(int startOffset, int endOffset) {
	}

	public void whitespaceInSQLCondition(int startOffset, int endOffset) {
	}
	
	public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
	}

	public void missingEndForPart(int startOffset, int endOffset) {
	}
}
