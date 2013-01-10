/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.core.internal.model.document.EGLNodeNameUtility;

public class InvalidPhraseRecoverer extends AbstractRecoverer {
	int inputDeleted;
	int contextDeleted;
	int nonTerminalSubstitution;
	
	boolean performDefaultReductions = true;
	

	public int recoverDistance(ParseStack stack, TokenStream tokenStream) {

		if (performDefaultReductions) {
			stack = stack.copy();
			stack.performDefaultReductions(); 
		}		
		
		int totalDeleted = 1;
		while(true) {
			for (contextDeleted = 0; contextDeleted <= stack.availableContext() && contextDeleted <= totalDeleted; contextDeleted++) {
				inputDeleted = totalDeleted - contextDeleted;

				if(inputDeleted > tokenStream.numTokensLeft()) {
					continue;
				} 
				
				if(recoverDistance(inputDeleted, contextDeleted, stack, tokenStream) >= SUCCESS_DISTANCE) {
					return SUCCESS_DISTANCE;
				}
			}		
			totalDeleted++;
		}
	}

	private int recoverDistance(int inputDeleted, int contextDeleted, ParseStack stack, TokenStream tokenStream) {
		// Try deleting the phrase first
		if(deletePhrase(inputDeleted, contextDeleted, stack, tokenStream) >= SUCCESS_DISTANCE) return SUCCESS_DISTANCE;
		
		// Try substitution with different nonterminals
		ParseStack localStack = stack.copy();
		localStack.deleteContext(contextDeleted);
		
		// Try the different missing nonTerminals
		short[] nonTerminalCandidates = grammar.getNonTerminalCandidates(localStack.getCurrentState());
		for (int i = 0; i < nonTerminalCandidates.length; i++) {
			if(substitutePhrase(nonTerminalCandidates[i], inputDeleted, contextDeleted, stack, tokenStream) >= SUCCESS_DISTANCE) {
				return SUCCESS_DISTANCE;
			}
		}
		
		return 0;
	}
	
	private int deletePhrase(int inputDeleted, int contextDeleted, ParseStack stack, TokenStream tokenStream) {
		nonTerminalSubstitution = -1;
		
		stack = stack.copy();
		tokenStream = tokenStream.copy();
		
		stack.deleteContext(contextDeleted);
		
		if(!tokenStream.deleteInput(inputDeleted)) return 0;
		
		int distance = tryParseAhead(stack, tokenStream);
		
		return distance;
	}

	private int substitutePhrase(int ntSubstitute, int inputDeleted, int contextDeleted, ParseStack stack, TokenStream tokenStream) {
		nonTerminalSubstitution = ntSubstitute;
		
		stack = stack.copy();
		tokenStream = tokenStream.copy();
		
		stack.deleteContext(contextDeleted);
		if(!tokenStream.deleteInput(inputDeleted)) return 0;
		
		stack.shift(new ErrorNonTerminalNode(ntSubstitute));		
		
		int distance = tryParseAhead(stack, tokenStream);
		
		return distance;
	}	

	private void specialRecover(ParseStack stack, TokenStream tokenStream) {
		stack = stack.copy();
		tokenStream = tokenStream.copy();
		
		System.out.println("Stack Symbols:");
		ParseNode[] stackSymbols = stack.deleteContext(contextDeleted);
		for (int i = 0; i < stackSymbols.length; i++) {
			System.out.println("   " + ParseTreePrinter.getLabel(stackSymbols[i]));
		}
		
		System.out.println("Input Symbols:");
		ParseNode[] inputSymbols = new ParseNode[inputDeleted];
		for(int i = 0; i < inputDeleted; i++) {
			inputSymbols[i] = tokenStream.lookAhead();
			System.out.println("   " + inputSymbols[i]);
			tokenStream.shift();
		}
		
		int[] symbols = new int[] {
			ErrorNodeTypes.ID, 1,
			ErrorNodeTypes.END, 0
		};
		
	}
	
	public void recover(ParseStack stack, TokenStream tokenStream) {
		if (performDefaultReductions) {
			stack.performDefaultReductions();
		}
		
		if(nonTerminalSubstitution == ErrorNodeTypes.ErrorNode) {
			specialRecover(stack, tokenStream);
		}
		
		// Remove the stack context needs to be deleted
		ParseNode[] deletedStackSymbols = stack.deleteContext(contextDeleted);
		
		// Remove the lookahead that need to be deleted
		tokenStream.deleteInput(inputDeleted);
		ParseNode[] deletedInputSymbols = tokenStream.getUnprocessedTerminals();
		
		// Form the concatenation of the deleted symbols
		ParseNode[] deletedSymbols = new ParseNode[deletedStackSymbols.length + deletedInputSymbols.length];
		
		System.arraycopy(deletedStackSymbols, 0, deletedSymbols, 0, deletedStackSymbols.length);
		System.arraycopy(deletedInputSymbols, 0, deletedSymbols, deletedStackSymbols.length, deletedInputSymbols.length);
				
		// Chain all the deleted symbols up as a whitespace
		ParseNode deletedNode = chainNodes(deletedSymbols);

		// Perform the stack repairs based on whether it is a deletion recovery or a subsitution recovery
		if(nonTerminalSubstitution >= 0) {	// Subsitution
			// Cause the stack to shift the error node
			ParseNode substitutionNode = new ErrorNonTerminalNode(nonTerminalSubstitution, deletedNode);			
			stack.shift(substitutionNode);
		}
		else {	// Deletion
			// Create an error node to hold on to the deleted symbols
			ParseNode errorNode = new ErrorNonTerminalNode(ErrorNodeTypes.ErrorNode, deletedNode);
		
			// Connect the deleted symbols as whitespace
			stack.connect(errorNode); 
		}

		// Report the error
		TerminalNode errorTerminal = findFirstNonWSTerminal(deletedStackSymbols);
		if(errorTerminal == null) {
			errorTerminal = findFirstNonWSTerminal(deletedInputSymbols); 
		}
		
		errorMessage(stack, tokenStream, errorTerminal);
	}

	public void errorMessage(ParseStack stack, TokenStream tokenStream, TerminalNode errorTerminal) {
		TerminalNode startErrorTerminal = errorTerminal;
		TerminalNode endErrorTerminal = tokenStream.previousNonWSTerminal(tokenStream.lookAhead());
		String message = "\tThe phrase \"" + errorTerminal.text + " ... " + endErrorTerminal.text;
		if(nonTerminalSubstitution < 0) {
			message += "\" is unexpected " + contextDeleted + ":" + inputDeleted;
		}
		else {
			message += "\" is not a valid " + EGLNodeNameUtility.getNonterminalName(nonTerminalSubstitution) + " " + contextDeleted + ":" + inputDeleted;	
		}
		
		ErrorMarkerCollector.instance.add(startErrorTerminal.offset, endErrorTerminal.offset + endErrorTerminal.text.length(), startErrorTerminal.line, message);
	}
}
