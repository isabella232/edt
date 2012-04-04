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
public class PreviousTerminalInsertionRecovery extends AbstractRecovery {

    private int parseCheckDistance;
    private int missingTerminal;
    
    public PreviousTerminalInsertionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        ParseStack previousStack = errorStack.createCopy();
        Terminal previousTerminal = previousStack.undoLastTerminal();
        
        if(previousTerminal == null) return;    // In some rare cases where an error has just occurred (see crash1), there is no previous terminal
        
        short[] terminalCandidates = previousStack.getTerminalCandidates(previousStack.getCurrentState());
        for(int i = 0; i < terminalCandidates.length; i++) {
            Terminal terminal = new Terminal(terminalCandidates[i], -1, -1, -1);
            
            // Process the inserted terminal
            ParseStack trialStack = previousStack.createCopy();
            trialStack.processLookAhead(terminal);
            
            // Process the previous terminal
            if(trialStack.canShift(previousTerminal)) {
                trialStack.processLookAhead(previousTerminal);
            }
            else {
                continue;   // Some times the previous terminal is not actually in the follow set of the subsituted nonterminal;
            }
            
            // Parse check
            int trialDistance = trialStack.parseCheck(tokenStream);
            if(trialDistance > parseCheckDistance) {
                parseCheckDistance = trialDistance;
                missingTerminal = terminalCandidates[i];
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

        // Unwind the last processed terminal by using the error stack as a hint
        // So that we don't have to repeat all the logic
        Symbol previousSymbol = (Symbol) realStack.peek();
        int errorStackTop = errorStack.getStackTop();
        for(int i = realStack.size(); i > errorStackTop + 1; i--) {
            realStack.pop();
        }
        ((Symbol) realStack.peek()).parse_state = errorStack.getCurrentState();

        // Roll back the previous terminal
        tokenStream.rollBack(previousTerminal, previousSymbol);
        
        // "Roll back" the inserted terminal
        // TODO Get the offsets right
        Symbol symbol = new Symbol(missingTerminal);
        symbol.parse_state = -2;
        tokenStream.rollBack(new Terminal(missingTerminal, -1, -1, -1), symbol);
        
        // Report the error
        problemRequestor.missingPreviousTerminal(missingTerminal, previousTerminal.left, previousTerminal.right);

        // Return 0 for the error node position.  The true error node position will be
        // determined when the main parser shifts the artificially created terminal since 
        // its parse_state is -2
        return 0;
    }

}
