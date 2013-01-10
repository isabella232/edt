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

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


public class AccumulatingSyntaxErrorMessageRequestor implements ISyntaxErrorRequestor {
    private Map /*<SyntaxError>, <String>*/ syntaxErrors = new Hashtable();		//list of syntax error messages 
    
    public Map getSyntaxErrors() {
        return syntaxErrors;
    }
    
    private String[] getTerminalInserts(int terminalType)
    {
    	String terminalName = NodeNameUtility.getTerminalName(terminalType);
    	String[] inserts = new String[] {terminalName};    	
    	return inserts;
    }
    
    private String[] getNonTerminalInserts(int nonTerminalType)
    {
    	String nonTerminalName = NodeNameUtility.getNonterminalName(nonTerminalType);
    	String[] inserts = new String[] {nonTerminalName};
    	return inserts;
    }

    // Parser errors
    public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	
    	syntaxErrors.put(new SyntaxError(SyntaxError.INCORRECT_NT, startOffset, endOffset, new int[] { nonTerminalType }),     	
    					 DefaultProblemRequestor.getMessageFromBundle(SyntaxError.INCORRECT_NT, getNonTerminalInserts(nonTerminalType)));
    }

    public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.INCORRECT_PHRASE, startOffset, endOffset, new int[] { nonTerminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.INCORRECT_PHRASE, getNonTerminalInserts(nonTerminalType)));
    }

    public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.INCORRECT_PREV_NT, startOffset, endOffset, new int[] { nonTerminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.INCORRECT_PREV_NT, getNonTerminalInserts(nonTerminalType)));
    }

    public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.INCORRECT_PREV_T, startOffset, endOffset, new int[] { terminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.INCORRECT_PREV_T, getTerminalInserts(terminalType)));
    }

    public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.INCORRECT_T, startOffset, endOffset, new int[] { terminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.INCORRECT_T, getTerminalInserts(terminalType)));    	
    }
    
    public void keywordAsName(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.KEYWORD_AS_NAME, startOffset, endOffset, new int[] {terminalType}),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.KEYWORD_AS_NAME, getTerminalInserts(terminalType)));    	
    }

    public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.MISSING_NT, startOffset, endOffset, new int[] { nonTerminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.MISSING_NT, getNonTerminalInserts(nonTerminalType)));
    }

    public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.MISSING_PREV_NT, startOffset, endOffset, new int[] { nonTerminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.MISSING_PREV_NT, getNonTerminalInserts(nonTerminalType)));
    }

    public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.MISSING_PREV_T, startOffset, endOffset, new int[] { terminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.MISSING_PREV_T, getTerminalInserts(terminalType)));
    }

    public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.MISSING_SCOPE_CLOSER, startOffset, endOffset, new int[] { terminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.MISSING_SCOPE_CLOSER, getTerminalInserts(terminalType)));
    }

    public void missingTerminal(int terminalType, int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.MISSING_T, startOffset, endOffset, new int[] { terminalType }),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.MISSING_T, getTerminalInserts(terminalType)));
    }

    public void unexpectedPhrase(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.UNEXPECTED_PHRASE, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.UNEXPECTED_PHRASE, new String[0]));
    }

    public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.UNEXPECTED_PREV_T, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.UNEXPECTED_PREV_T, new String[0]));
    }

    public void unexpectedTerminal(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.UNEXPECTED_T, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.UNEXPECTED_T, new String[0]));
    }

    public void panicPhrase(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.PANIC_PHRASE, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.PANIC_PHRASE, new String[0]));
    }
    
    public void tooManyErrors() {
    	syntaxErrors.put(new SyntaxError(SyntaxError.TOO_MANY_ERRORS, 0, 0),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.TOO_MANY_ERRORS, new String[0]));
	}

	// Lexer errors
	public void invalidEscapeSequence(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.INVALID_ESCAPE, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.INVALID_ESCAPE, new String[0]));
	}

	public void unclosedBlockComment(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.UNCLOSED_BLOCK_COMMENT, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.UNCLOSED_BLOCK_COMMENT, new String[0]));
	}

	public void unclosedSQL(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.UNCLOSED_SQL, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.UNCLOSED_SQL, new String[0]));
	}

	public void unclosedSQLCondition(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.UNCLOSED_SQL, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.UNCLOSED_SQL, new String[0]));
	}

	public void unclosedString(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.UNCLOSED_STRING, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.UNCLOSED_STRING, new String[0]));
	}

	public void whitespaceInSQL(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.WHITESPACE_SQL, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.WHITESPACE_SQL, new String[0]));
	}

	public void whitespaceInSQLCondition(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.WHITESPACE_SQLCONDITION, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.WHITESPACE_SQLCONDITION, new String[0]));
	}
	
	public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
    	syntaxErrors.put(new SyntaxError(SyntaxError.INVALID_CHARACTER_IN_HEX_LITERAL, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(SyntaxError.INVALID_CHARACTER_IN_HEX_LITERAL, new String[0]));
	}

	public void missingEndForPart(int startOffset, int endOffset) {
		syntaxErrors.put(new SyntaxError(IProblemRequestor.MISSING_END, startOffset, endOffset),
    			DefaultProblemRequestor.getMessageFromBundle(IProblemRequestor.MISSING_END, new String[0]));		
	}
}
