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



/**
 * @author winghong
 */
public class NonTerminalInsertionRecovery extends AbstractRecovery {

    private int parseCheckDistance;
    private int missingNonTerminal;
    
    public NonTerminalInsertionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        short[] nonterminalCandidates = errorStack.getNonTerminalCandidates(errorStack.getCurrentState());
        for(int i = 0; i < nonterminalCandidates.length; i++) {
            ParseStack trialStack = errorStack.createCopy();
            trialStack.processNonTerminal(nonterminalCandidates[i]);
            
            int trialDistance = trialStack.parseCheck(tokenStream);
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
        return parseCheckDistance;
    }

    public int performRecovery() {
        // Compute the logical non terminal type to display
        int lookAheadTerminalType = tokenStream.getLookAhead().symbolType;
        ParseStack newStack = errorStack.createCopy();
        newStack.processNonTerminal(missingNonTerminal);
        int highestNonTerminalType = newStack.getHighestNonTerminal(lookAheadTerminalType);

        problemRequestor.missingNonTerminal(highestNonTerminalType, tokenStream.getLookAhead().left, tokenStream.getLookAhead().right);

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
