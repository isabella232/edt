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
package org.eclipse.edt.compiler.internal.sdk.compile;

import org.eclipse.edt.compiler.core.ast.ISyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.NodeNameUtility;
import org.eclipse.edt.compiler.core.ast.SyntaxError;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


public class SDKSyntaxErrorRequestor implements ISyntaxErrorRequestor {

	   private SDKSyntaxProblemRequestor problemRequestor;
	    
	    public SDKSyntaxErrorRequestor(SDKSyntaxProblemRequestor problemRequestor) {
	        this.problemRequestor = problemRequestor;
	    }
	    
	    private void createErrorMarker(int type, int startOffset, int endOffset, String insert) {
	    	problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, type, new String[] { insert });
	    }

	    private void createErrorMarker(int type, int startOffset, int endOffset) {
	    	problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_ERROR, type);
	    }
	    
	    private void createWarningMarker(int type, int startOffset, int endOffset) {
	    	problemRequestor.acceptProblem(startOffset, endOffset, IMarker.SEVERITY_WARNING, type);
	    }

	    // Parser Errors
		public void incorrectNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.INCORRECT_NT, startOffset, endOffset, NodeNameUtility.getNonterminalName(nonTerminalType));
		}

		public void incorrectPhrase(int nonTerminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.INCORRECT_PHRASE, startOffset, endOffset, NodeNameUtility.getNonterminalName(nonTerminalType));
		}

		public void incorrectPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.INCORRECT_PREV_NT, startOffset, endOffset, NodeNameUtility.getNonterminalName(nonTerminalType));
		}

		public void incorrectPreviousTerminal(int terminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.INCORRECT_PREV_T, startOffset, endOffset, NodeNameUtility.getTerminalName(terminalType));
		}

		public void incorrectTerminal(int terminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.INCORRECT_T, startOffset, endOffset, NodeNameUtility.getTerminalName(terminalType));
		}
		
		public void keywordAsName(int terminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.KEYWORD_AS_NAME, startOffset, endOffset, NodeNameUtility.getTerminalName(terminalType));
		}

		public void missingNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.MISSING_NT, startOffset, endOffset, NodeNameUtility.getNonterminalName(nonTerminalType));
		}

		public void missingPreviousNonTerminal(int nonTerminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.MISSING_PREV_NT, startOffset, endOffset, NodeNameUtility.getNonterminalName(nonTerminalType));
		}

		public void missingPreviousTerminal(int terminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.MISSING_PREV_T, startOffset, endOffset, NodeNameUtility.getTerminalName(terminalType));
		}

		public void missingScopeCloser(int terminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.MISSING_SCOPE_CLOSER, startOffset, endOffset, NodeNameUtility.getTerminalName(terminalType));
		}

		public void missingTerminal(int terminalType, int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.MISSING_T, startOffset, endOffset, NodeNameUtility.getTerminalName(terminalType));
		}

		public void panicPhrase(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.PANIC_PHRASE, startOffset, endOffset);
		}
		
		public void tooManyErrors() {
			createErrorMarker(SyntaxError.TOO_MANY_ERRORS, 0, 0);
		}

		public void unexpectedPhrase(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.UNEXPECTED_PHRASE, startOffset, endOffset);		
		}

		public void unexpectedPreviousTerminal(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.UNEXPECTED_PREV_T, startOffset, endOffset);
		}

		public void unexpectedTerminal(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.UNEXPECTED_T, startOffset, endOffset);
		}

		// Lexer Errors
		public void invalidEscapeSequence(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.INVALID_ESCAPE, startOffset, endOffset);
		}

		public void unclosedBlockComment(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.UNCLOSED_BLOCK_COMMENT, startOffset, endOffset);
		}

		public void unclosedSQL(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.UNCLOSED_SQL, startOffset, endOffset);
		}

		public void unclosedSQLCondition(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.UNCLOSED_SQLCONDITION, startOffset, endOffset);
		}

		public void unclosedString(int startOffset, int endOffset) {
			createErrorMarker(SyntaxError.UNCLOSED_STRING, startOffset, endOffset);
		}

		public void whitespaceInSQL(int startOffset, int endOffset) {
			createWarningMarker(SyntaxError.WHITESPACE_SQL, startOffset, endOffset);
		}

		public void whitespaceInSQLCondition(int startOffset, int endOffset) {
			createWarningMarker(SyntaxError.WHITESPACE_SQLCONDITION, startOffset, endOffset);
		}
		
		public void invalidCharacterInHexLiteral(int startOffset, int endOffset) {
			createWarningMarker(SyntaxError.INVALID_CHARACTER_IN_HEX_LITERAL, startOffset, endOffset);			
		}

		public void missingEndForPart(int startOffset, int endOffset) {
			createErrorMarker(IProblemRequestor.MISSING_END, startOffset, endOffset);			
		}

}
