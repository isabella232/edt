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
public class PreviousTerminalSubstitutionRecovery extends AbstractRecovery {

    private int parseCheckDistance;
    private int substitutionTerminal;

    public PreviousTerminalSubstitutionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        ParseStack previousStack = errorStack.createCopy();
        Terminal previousTerminal = previousStack.undoLastTerminal();

        if(previousTerminal == null) return;    // In some rare cases where an error has just occurred (see crash1), there is no previous terminal
        
        short[] terminalCandidates = previousStack.getTerminalCandidates(previousStack.getCurrentState());
        for(int i = 0; i < terminalCandidates.length; i++) {
            Terminal terminal = new Terminal(terminalCandidates[i], -1, -1, -1);
            ParseStack trialStack = previousStack.createCopy();
            trialStack.processLookAhead(terminal);
            
            int trialDistance = trialStack.parseCheck(tokenStream);
            if(trialDistance > parseCheckDistance) {
                parseCheckDistance = trialDistance;
                substitutionTerminal = terminalCandidates[i];
            }
        }
    }

    public float getMisspellingIndex() {
        return 0;
    }

    public int getParseCheckDistance() {
        return parseCheckDistance - 1;  // TODO the minus one is to favor corrections on the erorr terminal
    }

    public int performRecovery() {
        // Unwind the last processed terminal on the error stack
        Terminal previousTerminal = errorStack.undoLastTerminal();

        // Unwind the last processed terminal by using the error stack as a hint
        // So that we don't have to repeat all the logic
        int errorStackTop = errorStack.getStackTop();
        for(int i = realStack.size(); i > errorStackTop + 1; i--) {
            realStack.pop();
        }
        ((Symbol) realStack.peek()).parse_state = errorStack.getCurrentState();

        // Process the substitution token
        Terminal terminal = new Terminal(substitutionTerminal, previousTerminal.left, previousTerminal.right, -1);
        Symbol symbol = new Symbol(substitutionTerminal, previousTerminal.left, previousTerminal.right);
        symbol.parse_state = -2;
        tokenStream.rollBack(terminal, symbol);
        
        // Report problem
        problemRequestor.incorrectPreviousTerminal(substitutionTerminal, previousTerminal.left, previousTerminal.right);

        // Return 0 for the error node position.  The true error node position will be
        // determined when the main parser shifts the artificially created terminal since 
        // its parse_state is -2
        return 0;
    }

}
