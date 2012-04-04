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

import org.eclipse.edt.ide.core.internal.model.document.EGLNodeNameUtility;

public class MissingNonTerminalRecoverer extends AbstractRecoverer {
	int bestCandidate;
	int bestDistance;
	
	public int recoverDistance(ParseStack stack, TokenStream tokenStream) {
		// Reset the recovery distance
		bestCandidate = -1;
		bestDistance = -1;

		// Try the different missing nonTerminals
		short[] nonTerminalCandidates = grammar.getNonTerminalCandidates(stack.getCurrentState());
		for (int i = 0; i < nonTerminalCandidates.length; i++) {
			int curDistance = recoveryDistance(nonTerminalCandidates[i], stack, tokenStream);
			if(curDistance > bestDistance) {
				bestCandidate = nonTerminalCandidates[i];
				bestDistance = curDistance;
			}
		}
		
		return bestDistance;
	}
	
	private int recoveryDistance(int candidate, ParseStack stack, TokenStream tokenStream) {
		stack = stack.copy();
		tokenStream = tokenStream.copy();

		stack.shift(new ErrorNonTerminalNode(candidate));

		int distance = tryParseAhead(stack, tokenStream);
		Reporter.getInstance().missingNonTerminalDistance(candidate, distance);
		
		return distance;
	}
	
	public void recover(ParseStack stack, TokenStream tokenStream) {
		Reporter.getInstance().recoverMissingNonTerminal(bestCandidate);
		stack.shift(new ErrorNonTerminalNode(bestCandidate));
		errorMessage(stack, tokenStream, tokenStream.lookAhead());		
	}

	public void errorMessage(ParseStack stack, TokenStream tokenStream, TerminalNode errorTerminal) {
		String message = "\tMissing " + EGLNodeNameUtility.getNonterminalName(bestCandidate);
		
		ErrorMarkerCollector.instance.add(errorTerminal.offset, errorTerminal.offset + errorTerminal.text.length(), errorTerminal.line, message);
	}

}
