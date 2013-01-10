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

public class InvalidTerminalRecoverer extends AbstractRecoverer {
	TerminalNode bestCandidate;
	int bestDistance = -1;
	
	public int recoverDistance(ParseStack stack, TokenStream tokenStream) {
		// Reset the recovery distance
		bestCandidate = null;
		bestDistance = -1;

		// Try the different missing terminals
		short[] terminalCandidates = grammar.getTerminalCandidates(stack.getCurrentState());
		for (int i = 0; i < terminalCandidates.length; i++) {
			int curDistance = recoveryDistance(terminalCandidates[i], stack, tokenStream);
			if(curDistance > bestDistance) {
				bestCandidate = new ErrorTerminalNode(terminalCandidates[i]);
				bestDistance = curDistance;
			}
		}
		
		return bestDistance;
	}
	
	private int recoveryDistance(int candidate, ParseStack stack, TokenStream tokenStream) {
		if(isImportantTerminal(tokenStream.lookAhead())) return 0;
		
		stack = stack.copy();
		tokenStream = tokenStream.copy();
		
		tokenStream.deleteInput(1);		
		processTerminal(stack, new ErrorTerminalNode(candidate));

		int distance = tryParseAhead(stack, tokenStream);
		Reporter.getInstance().invalidTerminalDistance(candidate, distance);
		
		return distance;
	}
	
	public void recover(ParseStack stack, TokenStream tokenStream) {
		// The tracing stuff will be improved or removed later
		Reporter.getInstance().recoverInvalidTerminal(tokenStream.lookAhead().text, bestCandidate.terminalType);
		
		// Report the error
		errorMessage(stack, tokenStream, tokenStream.lookAhead());

		// Remove the lookahead that need to be deleted
		tokenStream.deleteInput(1);
		ParseNode[] substitutedSymbols = tokenStream.getUnprocessedTerminals();

		// Figure out what the highest symbol is
		ParseStack tempStack = stack.copy();
		processTerminal(tempStack, bestCandidate);
		ParseNode highestSymbol = getHighestSymbol(tempStack, tokenStream.lookAhead());
		
		// First we need to perform all the reductions
		performAllReductions(stack, bestCandidate);
		
		// Get all the substituted symbols chained up
		ParseNode substitutedNode = chainNodes(substitutedSymbols);
		
		// Attach the substitued symbols to the error node (the type of error nodes depend
		// on whether there is any chain-reduction on the substituted symbol
		ParseNode errorNode;
		if(highestSymbol.isTerminal()) {
			TerminalNode hsTerminal = (TerminalNode) highestSymbol;
			errorNode = new ErrorTerminalNode(hsTerminal.terminalType, substitutedNode);
		}
		else {
			NonTerminalNode hsNonTerminal = (NonTerminalNode) highestSymbol;
			errorNode = new ErrorNonTerminalNode(hsNonTerminal.nonTerminalType, substitutedNode);
		}
		
		// Shift the error node
		stack.shift(errorNode);
	}

	public void errorMessage(ParseStack stack, TokenStream tokenStream, TerminalNode errorTerminal) {
		ParseStack tempStack = stack.copy();
		processTerminal(tempStack, bestCandidate);
		TokenStream tempTokenStream = tokenStream.copy();
		tempTokenStream.deleteInput(1);
		String highestSymbolName = getHighestSymbolName(tempStack, tempTokenStream.lookAhead());

		String message; 
		if(highestSymbolName.toUpperCase().equals(highestSymbolName)) {
			message = "\t\"" + errorTerminal + "\" is unexpected, expecting " + EGLNodeNameUtility.getTerminalName(bestCandidate.terminalType) + " instead.";
		}
		else {
			message = "\t\"" + errorTerminal + "\" is an invalid " + highestSymbolName;
		}
		
		ErrorMarkerCollector.instance.add(errorTerminal.offset, errorTerminal.offset + errorTerminal.text.length(), errorTerminal.line, message);
	}

}
