/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

public interface ISyntaxErrorRequestor {

	// Parser errors
    void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset);
    void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset);

    void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset);
    void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset);

    void missingTerminal(int terminalType, int startOffset, int endOffset);
    void incorrectTerminal(int terminalType, int startOffset, int endOffset);
    void keywordAsName(int terminalType, int startOffset, int endOffset);
    void unexpectedTerminal(int startOffset, int endOffset);
    
    void missingPreviousTerminal(int terminalType, int startOffset, int endOffset);
    void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset);
    void unexpectedPreviousTerminal(int startOffset, int endOffset);
    
    void missingScopeCloser(int terminalType, int startOffset, int endOffset);
    
    void unexpectedPhrase(int startOffset, int endOffset);
    void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset);
    
    void panicPhrase(int startOffset, int endOffset);
    
    void missingEndForPart(int startOffset, int endOffset);
    
    void tooManyErrors();
    
    // Lexer errors
    void unclosedString(int startOffset, int endOffset);
    void unclosedBlockComment(int startOffset, int endOffset);
    void unclosedSQL(int startOffset, int endOffset);
    void unclosedSQLCondition(int startOffset, int endOffset);
    void invalidEscapeSequence(int startOffset, int endOffset);

    void whitespaceInSQL(int startOffset, int endOffset);
    void whitespaceInSQLCondition(int startOffset, int endOffset);
    void invalidCharacterInHexLiteral(int startOffset, int endOffset);
}
