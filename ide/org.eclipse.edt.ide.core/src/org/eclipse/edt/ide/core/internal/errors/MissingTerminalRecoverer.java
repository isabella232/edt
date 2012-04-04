/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

public class MissingTerminalRecoverer extends AbstractRecoverer {
	int bestDistance;
	TerminalNode bestCandidate;
	
	public int recoverDistance(ParseStack stack, TokenStream tokenStream) {
		// Reset the recovery distance
		bestCandidate = null;
		bestDistance = -1;

		// Try the different missing terminals
		short[] terminalCandidates = grammar.getTerminalCandidates(stack.getCurrentState());
		for (int i = 0; i < terminalCandidates.length; i++) {
			TerminalNode curCandidate = new ErrorTerminalNode(terminalCandidates[i]);
			int curDistance = recoveryDistance(curCandidate, stack, tokenStream);
			if(curDistance > bestDistance) {
				bestCandidate = curCandidate;
				bestDistance = curDistance;
			}
		}
		
		return bestDistance;
	}
	
	private int recoveryDistance(TerminalNode candidate, ParseStack stack, TokenStream tokenStream) {
		stack = stack.copy();
		tokenStream = tokenStream.copy();

		boolean shifted = processTerminal(stack, candidate);

		int distance = tryParseAhead(stack, tokenStream);
		Reporter.getInstance().missingTerminalDistance(candidate.terminalType, distance);
		
		return distance;
	}
	
	public void recover(ParseStack stack, TokenStream tokenStream) {
		Reporter.getInstance().recoverMissingTerminal(bestCandidate.terminalType);
		
		// First we need to perform all the reductions
		performAllReductions(stack, bestCandidate);
		
		// Figure out what the highest symbol is
		ParseStack tempStack = stack.copy();
		processTerminal(tempStack, bestCandidate);
		ParseNode highestSymbol = getHighestSymbol(tempStack, tokenStream.lookAhead());
		
		// Shift either an error terminal node or an error nonterminal node depending on 
		// what the highest symbol is
		ParseNode errorNode;
		if(highestSymbol.isTerminal()) {
			TerminalNode hsTerminal = (TerminalNode) highestSymbol;
			errorNode = new ErrorTerminalNode(hsTerminal.terminalType);
		}
		else {
			NonTerminalNode hsNonTerminal = (NonTerminalNode) highestSymbol;
			errorNode = new ErrorNonTerminalNode(hsNonTerminal.nonTerminalType);
		}
		
		stack.shift(errorNode); 

		// Report the error
		errorMessage(stack, tokenStream, tokenStream.lookAhead());
	}
	
	public void errorMessage(ParseStack stack, TokenStream tokenStream, TerminalNode errorTerminal) {
		String message = "\tMissing " + getHighestSymbolName(stack.copy(), tokenStream.lookAhead());
		
		ErrorMarkerCollector.instance.add(errorTerminal.offset, errorTerminal.offset + errorTerminal.text.length(), errorTerminal.line, message);
	}
}
