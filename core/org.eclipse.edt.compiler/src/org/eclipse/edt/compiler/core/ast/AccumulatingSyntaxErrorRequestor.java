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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * @author winghong
 */
public class AccumulatingSyntaxErrorRequestor implements ISyntaxErrorRequestor {
    
    private List syntaxErrors = new ArrayList();
    
    public List getSyntaxErrors() {
        return syntaxErrors;
    }
    
    // Parser errors
    public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.INCORRECT_NT, startOffset, endOffset, new int[] { nonTerminalType }));
    }

    public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.INCORRECT_PHRASE, startOffset, endOffset, new int[] { nonTerminalType }));
    }

    public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.INCORRECT_PREV_NT, startOffset, endOffset, new int[] { nonTerminalType }));
    }

    public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.INCORRECT_PREV_T, startOffset, endOffset, new int[] { terminalType }));
    }

    public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.INCORRECT_T, startOffset, endOffset, new int[] { terminalType }));
    }
    
    public void keywordAsName(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.KEYWORD_AS_NAME, startOffset, endOffset, new int[] {terminalType}));    	
    }

    public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.MISSING_NT, startOffset, endOffset, new int[] { nonTerminalType }));
    }

    public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.MISSING_PREV_NT, startOffset, endOffset, new int[] { nonTerminalType }));
    }

    public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.MISSING_PREV_T, startOffset, endOffset, new int[] { terminalType }));
    }

    public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.MISSING_SCOPE_CLOSER, startOffset, endOffset, new int[] { terminalType }));
    }

    public void missingTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.MISSING_T, startOffset, endOffset, new int[] { terminalType }));
    }

    public void unexpectedPhrase(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.UNEXPECTED_PHRASE, startOffset, endOffset));
    }

    public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.UNEXPECTED_PREV_T, startOffset, endOffset));
    }

    public void unexpectedTerminal(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.UNEXPECTED_T, startOffset, endOffset));
    }

    public void panicPhrase(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.PANIC_PHRASE, startOffset, endOffset));
    }
    
    public void tooManyErrors() {
    	syntaxErrors.add(new SyntaxError(SyntaxError.TOO_MANY_ERRORS, 0, 0));
	}

	// Lexer errors
	public void invalidEscapeSequence(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.INVALID_ESCAPE, startOffset, endOffset));
	}

	public void unclosedBlockComment(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.UNCLOSED_BLOCK_COMMENT, startOffset, endOffset));
	}

	public void unclosedSQL(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.UNCLOSED_SQL, startOffset, endOffset));
	}

	public void unclosedSQLCondition(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.UNCLOSED_SQL, startOffset, endOffset));
	}

	public void unclosedString(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.UNCLOSED_STRING, startOffset, endOffset));
	}

	public void whitespaceInSQL(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.WHITESPACE_SQL, startOffset, endOffset));
	}

	public void whitespaceInSQLCondition(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.WHITESPACE_SQLCONDITION, startOffset, endOffset));
	}
	
	public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
    	syntaxErrors.add(new SyntaxError(SyntaxError.INVALID_CHARACTER_IN_HEX_LITERAL, startOffset, endOffset));
	}
	
	public void missingEndForPart(int startOffset, int endOffset) {
		syntaxErrors.add(new SyntaxError(IProblemRequestor.MISSING_END, startOffset, endOffset));		
	}
    
}
