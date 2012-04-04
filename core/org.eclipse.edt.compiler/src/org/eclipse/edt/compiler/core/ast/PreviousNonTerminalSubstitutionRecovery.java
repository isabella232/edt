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
package org.eclipse.edt.compiler.core.ast;

import java.util.Stack;

import java_cup.runtime.Symbol;



/**
 * @author winghong
 */
public class PreviousNonTerminalSubstitutionRecovery extends AbstractRecovery {

    private int parseCheckDistance;
    private int missingNonTerminal;
    
    public PreviousNonTerminalSubstitutionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        ParseStack previousStack = errorStack.createCopy();
        Terminal previousTerminal = previousStack.undoLastTerminal();

        if(previousTerminal == null) return;    // In some rare cases where an error has just occurred (see crash1), there is no previous terminal

        short[] nonterminalCandidates = previousStack.getNonTerminalCandidates(previousStack.getCurrentState());
        for(int i = 0; i < nonterminalCandidates.length; i++) {
            // Process the substituted nonterminal
            ParseStack trialStack = previousStack.createCopy();
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
        // Unwind the last processed terminal on the error stack
        Terminal previousTerminal = errorStack.undoLastTerminal();

        int errorStackTop = errorStack.getStackTop();
        for(int i = realStack.size(); i > errorStackTop + 1; i--) {
            realStack.pop();
        }
        ((Symbol) realStack.peek()).parse_state = errorStack.getCurrentState();
        
        // Compute the logical nonterminal substituted
        ParseStack messageStack = errorStack.createCopy();
        messageStack.processNonTerminal(missingNonTerminal);
        int highestNonTerminalType = messageStack.getHighestNonTerminal(previousTerminal.symbolType);

        problemRequestor.incorrectPreviousNonTerminal(highestNonTerminalType, previousTerminal.left, previousTerminal.right);

        // Push the artificial non-terminal into the real stack
        int currentState = errorStack.getCurrentState();
        int gotoState = errorStack.get_reduce(currentState, highestNonTerminalType);
        realStack.push(new Symbol(highestNonTerminalType, gotoState));

        // Processed the substituted nonterminal on the error stack
        errorStack.processNonTerminal(highestNonTerminalType);
        return realStack.size() - 1;
    }

}
