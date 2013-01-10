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

import java.util.Stack;

import java_cup.runtime.Symbol;

import org.eclipse.edt.compiler.core.EGLKeywordHandler;



/**
 * @author winghong
 */
public class NonTerminalSubstitutionRecovery extends AbstractRecovery {

    private int parseCheckDistance;
    private int missingNonTerminal;
    
    public NonTerminalSubstitutionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        short[] nonterminalCandidates = errorStack.getNonTerminalCandidates(errorStack.getCurrentState());
        for(int i = 0; i < nonterminalCandidates.length; i++) {
            ParseStack trialStack = errorStack.createCopy();
            ITokenStream trialStream = tokenStream.createTokenStreamAtOffset(1);
            trialStack.processNonTerminal(nonterminalCandidates[i]);
            
            int trialDistance = trialStack.parseCheck(trialStream);
            if(trialDistance > parseCheckDistance) {
                parseCheckDistance = trialDistance;
                missingNonTerminal = nonterminalCandidates[i];
            }
        }
    }

    public float getMisspellingIndex() {
        return 0;
    }

    public int getParseCheckDistance() {
        return parseCheckDistance + 1; // TODO review whether we want to compensate for the deleted token;;
    }

    public int performRecovery() {
        // Remember offset information for error message
    	Terminal lookAhead = tokenStream.getLookAhead();
		int left = lookAhead.left;
		int right = lookAhead.right;
		int errorNodeType = tokenStream.peekLookAhead(0).symbolType;

        // Advance the token stream for both parsers
        tokenStream.advanceLookAhead();
        
        // Form the error message
        int lookAheadTerminalType = tokenStream.getLookAhead().symbolType;
        ParseStack newStack = errorStack.createCopy();
        newStack.processNonTerminal(missingNonTerminal);
        int highestNonTerminalType = newStack.getHighestNonTerminal(lookAheadTerminalType);

        if(lookAhead.symbolType == NodeTypes.EOF) {
        	Symbol lastRealSymbol = getLastRealSymbol(realStack);
        	problemRequestor.incorrectNonTerminal(highestNonTerminalType, lastRealSymbol.left, lastRealSymbol.right);
        }
        else {
        	if((NodeTypes.lvalue == highestNonTerminalType) && EGLKeywordHandler.getKeywordHashSet().contains(NodeNameUtility.getTerminalName(errorNodeType).toLowerCase())) {
            	problemRequestor.keywordAsName(errorNodeType, left, right);
            }
        	else {
        		problemRequestor.incorrectNonTerminal(highestNonTerminalType, left, right);
        	}
        }
        
        // Push the artificial non-terminal into the real stack
        int currentState = errorStack.getCurrentState();
        int gotoState = errorStack.get_reduce(currentState, highestNonTerminalType);
        Symbol nonTerminal = new Symbol(highestNonTerminalType, gotoState);
        realStack.push(nonTerminal);
        
        // Push the artificial non-terminal into the error stack
        errorStack.processNonTerminal(highestNonTerminalType);
        return realStack.size() - 1;
    }

}
