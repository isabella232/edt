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
public class TerminalInsertionRecovery extends AbstractRecovery {

    private int parseCheckDistance;
    private int missingTerminal;
    
    public TerminalInsertionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        short[] terminalCandidates = errorStack.getTerminalCandidates(errorStack.getCurrentState());
        for(int i = 0; i < terminalCandidates.length; i++) {
        	// Not a very good fix:
        	// Sometimes the recovery will keep inserting or substituting LCURLY
        	// because a lot of times, it will then be able to parse 3 tokens
        	// This causes too many nested property settings and may eventually
        	// lead to a stack overflow
        	if(terminalCandidates[i] == NodeTypes.LCURLY) {
        		continue;
        	}

            Terminal terminal = new Terminal(terminalCandidates[i], -1, -1, -1);
            ParseStack trialStack = errorStack.createCopy();
            trialStack.processLookAhead(terminal);
            
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
        // Remember offset information for error message
        int errorOffset = tokenStream.getLookAhead().left;

        // Report error
        problemRequestor.missingTerminal(missingTerminal, tokenStream.getLookAhead().left, tokenStream.getLookAhead().right);

        // Compute construct root after the terminal has been inserted
        
        Terminal terminal = new Terminal(missingTerminal, errorOffset, errorOffset, -1);
        Symbol symbol = new Symbol(missingTerminal, errorOffset, errorOffset);
        symbol.parse_state = -2;
        tokenStream.rollBack(terminal, symbol);
        
        // Return 0 for the error node position.  The true error node position will be
        // determined when the main parser shifts the artificially created terminal since 
        // its parse_state is -2
        return 0;
    }

}
