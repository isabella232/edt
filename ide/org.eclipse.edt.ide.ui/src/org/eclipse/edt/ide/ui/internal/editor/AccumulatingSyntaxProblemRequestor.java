/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.NodeNameUtility;
import org.eclipse.edt.compiler.core.ast.SyntaxError;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.Problem;

public class AccumulatingSyntaxProblemRequestor implements ISyntaxErrorRequestor {

	private List problems;
	private String fileContents;
	
	public AccumulatingSyntaxProblemRequestor(String fileContents) {
        super();
        this.fileContents = fileContents;
        this.problems = new ArrayList();
    }
	
	public List getProblems() {
        return problems;
    }
	
	public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		 problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.INCORRECT_NT, getNonTerminalInserts(nonTerminalType)));
	}

	public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.INCORRECT_PHRASE, getNonTerminalInserts(nonTerminalType)));
	}

	public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.INCORRECT_PREV_NT, getNonTerminalInserts(nonTerminalType)));
	}

	public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.INCORRECT_PREV_T, getTerminalInserts(terminalType)));
	}

	public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.INCORRECT_T, getTerminalInserts(terminalType)));
	}
	
	public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.MISSING_NT, getNonTerminalInserts(nonTerminalType)));
	}

	public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.MISSING_PREV_NT, getNonTerminalInserts(nonTerminalType)));
	}

	public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.MISSING_PREV_T, getTerminalInserts(terminalType)));
	}

	public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.MISSING_SCOPE_CLOSER, getTerminalInserts(terminalType)));
	}

	public void missingTerminal(int terminalType, int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.MISSING_T, getTerminalInserts(terminalType)));
	}

	public void panicPhrase(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.PANIC_PHRASE, new String[0]));
	}
	
	public void tooManyErrors() {
		problems.add(new Problem(0, 0, IMarker.SEVERITY_ERROR, SyntaxError.TOO_MANY_ERRORS, new String[0]));
	}

	public void unexpectedPhrase(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.UNEXPECTED_PHRASE, new String[0]));		
	}

	public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.UNEXPECTED_PREV_T, new String[0]));
	}

	public void unexpectedTerminal(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.UNEXPECTED_T, new String[0]));
	}

	// Lexer Errors
	public void invalidEscapeSequence(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.INVALID_ESCAPE, new String[0]));
	}

	public void unclosedBlockComment(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.UNCLOSED_BLOCK_COMMENT, new String[0]));
	}

	public void unclosedSQL(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.UNCLOSED_SQL, new String[0]));
	}

	public void unclosedSQLCondition(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.UNCLOSED_SQLCONDITION, new String[0]));
	}

	public void unclosedString(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.UNCLOSED_STRING, new String[0]));
	}

	public void whitespaceInSQL(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_WARNING, SyntaxError.WHITESPACE_SQL, new String[0]));
	}

	public void whitespaceInSQLCondition(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_WARNING, SyntaxError.WHITESPACE_SQLCONDITION, new String[0]));
	}
	
	public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.INVALID_CHARACTER_IN_HEX_LITERAL, new String[0]));		
	}

	public void missingEndForPart(int startOffset, int endOffset) {
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, IProblemRequestor.MISSING_END, new String[0]));		
	}
	
	 private String[] getTerminalInserts(int terminalType) {
	    String terminalName = NodeNameUtility.getTerminalName(terminalType);
	    String[] inserts = new String[] {terminalName};    	
	    return inserts;
	 }
	    
	 private String[] getNonTerminalInserts(int nonTerminalType) {
		 String nonTerminalName = NodeNameUtility.getNonterminalName(nonTerminalType);
		 String[] inserts = new String[] {nonTerminalName};
		 return inserts;
	 }

	public void keywordAsName(int terminalType, int startOffset, int endOffset) {	
		problems.add(new Problem(startOffset, endOffset, IMarker.SEVERITY_ERROR, SyntaxError.KEYWORD_AS_NAME, new String[] {NodeNameUtility.getTerminalName(terminalType)}));	
	}
	
}
