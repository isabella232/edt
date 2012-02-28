/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.errors;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author winghong
 */
public class TokenStream {
	
	private ArrayList tokenList = new ArrayList();
	private int lookaheadPos = 0;
	private int readPos = 0;
	
	private TokenStream() {
		super();
	}
	
	
	public TokenStream(String input) {
		IErrorLexer errorLexer = new ErrorLexer(new StringReader(input));

		lineTracker = new ErrorLineTracker(input);
		
		for(;;) {
			TerminalNode terminalNode = errorLexer.next();
			tokenList.add(terminalNode);
			
			if(terminalNode.terminalType == ErrorNodeTypes.EOF) break; // Escape
		}
		
		// To prevent the lookahead pos from advancing past (in the case where 
		// there is only EOF in the list, only advance if the first token is a whitespace
		if(getTerminalNodeAt(0).isWhiteSpace()) {
			advanceLookAhead();
		}
	}

	// Be careful since we can advance the lookahead pos past the end	
	private void advanceLookAhead() {
		lookaheadPos++;
		while(getTerminalNodeAt(lookaheadPos).isWhiteSpace()) {
			lookaheadPos++;
		}
	}
	
	// TODO consider returning the terminal upon shift
	// This really should be called after the whitespaces are chained 
	public void shift() {
		advanceLookAhead();
		readPos++;
	}
	
	/**
	 * Look ahead is never a whitespace
	 * @return
	 */
	public TerminalNode lookAhead() {
		return getTerminalNodeAt(lookaheadPos);
	}
	
	public TokenStream copy() {
		TokenStream result = new TokenStream();
		result.tokenList = tokenList;
		result.readPos = readPos;
		result.lookaheadPos = lookaheadPos;
		return result;
	}
	
	/**
	 * This method skips non-ws terminals
	 * @param inputSkipped
	 * @return
	 */
	public boolean deleteInput(int inputDeleted) {
		for (int i = 0; i < inputDeleted; i++) {
			if(AbstractRecoverer.isUndeletableTerminal(lookAhead())) return false; 
			advanceLookAhead();			
		}
		return true;
	}

	private TerminalNode getTerminalNodeAt(int index) {
		return (TerminalNode) tokenList.get(index);
	}
	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < readPos; i++) {
			buffer.append(getTerminalNodeAt(i));
			buffer.append(' ');
		}
		
		buffer.append(" --> ");
		for (int i = readPos; i < lookaheadPos; i++) {
			buffer.append(getTerminalNodeAt(i));
			buffer.append(' ');
		}
		
		if(readPos != lookaheadPos) {
			buffer.append(" --> ");
		}
		 
		for (int i = lookaheadPos; i < tokenList.size(); i++) {
			buffer.append(getTerminalNodeAt(i));
			buffer.append(' ');
		}
		
		return buffer.toString();
	}
	
	
	// This method basically counts the number of nonwhitespace terminals are left
	// TODO we probably don't need this method
	public int numTokensLeft() {
		int numTokensLeft = 0;
		 for (int i = lookaheadPos; i < tokenList.size(); i++) {
			if(!getTerminalNodeAt(i).isWhiteSpace()) {
				numTokensLeft++;
			}
		}
		return numTokensLeft - 1; // Because EOF would have been counted
	}
	
	public boolean hasWhitespaces() {
		return readPos != lookaheadPos;
	}


	/**
	 * TODO The reason why I need to return ParseNode is because the same array is used by the
	 * chain up method
	 * @return an array of length zero if there are no unprocessed terminals
	 */
	public ParseNode[] getUnprocessedTerminals() {
		ParseNode[] result =  (ParseNode[]) tokenList.subList(readPos, lookaheadPos).toArray(new ParseNode[0]);
		readPos = lookaheadPos;
		return result;
	}
	
	// TODO get rid of the error line tracker
	private ErrorLineTracker lineTracker;
	
	public static void main(String[] args) {
		TokenStream stream = new TokenStream(FileReaderUtil.readFile("tokens.egl"));
		stream = null;
	}

	/**
	 * Prints the line the TerminalNode belongs to
	 * @return
	 */
	public String getLine(TerminalNode terminal) {
		return lineTracker.getLine(terminal.line);
	}
	
	public TerminalNode previousNonWSTerminal(TerminalNode terminalNode) {
		int index = tokenList.indexOf(terminalNode) - 1;
		
		while(index >= 0 && (getTerminalNodeAt(index)).isWhiteSpace()) {
			index--;
		}
		
		return index < 0 ? terminalNode : getTerminalNodeAt(index); 
	}
	
	
	
	// These methods are for use of content assist
	
	private boolean skipPrefix = false;
	
	public void skipPrefix() {
		skipPrefix = true;
	}
	
	public List getPrefixNodes() {
		ArrayList result = new ArrayList();
		
		// If the token stream consists only of the EOF terminal, there is no prefix
		if(tokenList.size() == ErrorNodeTypes.EOF) {
			return result;
		}
		else {
			for (int i = readPos; i < tokenList.size() - 1; i++) {
				result.add(getTerminalNodeAt(i));
			}
		}

		return result;		
	}
	
	public String getTemplatePrefix() {
		int numTokens = tokenList.size();
		if(numTokens < 2) { // The stream is empty
			return ""; 
		}
		else {
			TerminalNode lastToken = (TerminalNode) tokenList.get(numTokens - 2);
			if(isExtensibleTerminal(lastToken.terminalType)) {
				return lastToken.text;
			}
			else if(lastToken.terminalType == ErrorNodeTypes.SQLSTMTLIT) {
				// Special case for SQL Literal
				if(lastToken.text.equalsIgnoreCase("sql")) {
					return "sql";
				}
				else {
					return "";
				}
			}
			else {
				return "";
			}
		}
	}
	
//	public ParseNode getPrefixNode() {
//		if(tokenList.size() < 2) {
//			return null;
//		}
//		else {
//			TerminalNode lastNonEOFTerminal = getTerminalNodeAt(tokenList.size() - 2);
//			return isExtensibleTerminal(lastNonEOFTerminal.terminalType) ? lastNonEOFTerminal : null;
//		}
//	}
//
	public boolean isDone() {
		int terminalType = lookAhead().terminalType;
		
		return	terminalType == ErrorNodeTypes.EOF
			|| (skipPrefix && lookaheadPos == tokenList.size() - 2 && isExtensibleTerminal(terminalType));
	}
	
	public static boolean isExtensibleTerminal(int terminalType) {
		switch (terminalType) {
			case ErrorNodeTypes.LPAREN :
			case ErrorNodeTypes.RPAREN :
			case ErrorNodeTypes.BANG :
			case ErrorNodeTypes.COMMA :
			case ErrorNodeTypes.SEMI :
			case ErrorNodeTypes.DOT :
			case ErrorNodeTypes.COLON :
			case ErrorNodeTypes.ASSIGN :
			case ErrorNodeTypes.PLUSEQ :
			case ErrorNodeTypes.MINUSEQ :
			case ErrorNodeTypes.TIMESEQ :
			case ErrorNodeTypes.DIVEQ :
			case ErrorNodeTypes.MODULOEQ :
			case ErrorNodeTypes.AND :
			case ErrorNodeTypes.OR :
			case ErrorNodeTypes.EQ :
			case ErrorNodeTypes.NE :
			case ErrorNodeTypes.LT :
			case ErrorNodeTypes.GT :
			case ErrorNodeTypes.LE :
			case ErrorNodeTypes.GE :
			case ErrorNodeTypes.PLUS :
			case ErrorNodeTypes.MINUS :
			case ErrorNodeTypes.TIMES :
			case ErrorNodeTypes.TIMESTIMES :
			case ErrorNodeTypes.DIV :
			case ErrorNodeTypes.MODULO :
			case ErrorNodeTypes.LBRACKET :
			case ErrorNodeTypes.RBRACKET :
			case ErrorNodeTypes.LCURLY :
			case ErrorNodeTypes.RCURLY :
			case ErrorNodeTypes.AT :
			case ErrorNodeTypes.WS:
				return false;
			default :
				return true;
		}
	}
}
