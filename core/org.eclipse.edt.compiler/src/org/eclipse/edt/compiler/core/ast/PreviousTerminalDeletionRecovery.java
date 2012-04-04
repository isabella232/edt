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
public class PreviousTerminalDeletionRecovery extends AbstractRecovery {

    private int parseCheckDistance;

    public PreviousTerminalDeletionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        // Undo the last terminal
        ParseStack trialStack = errorStack.createCopy();
        Terminal previousTerminal = trialStack.undoLastTerminal();

        parseCheckDistance = previousTerminal == null ? 0 : trialStack.parseCheck(tokenStream) + 1; // + 1 to compensate for the deleted token
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
        int errorStackTop = errorStack.getStackTop();
        for(int i = realStack.size(); i > errorStackTop + 1; i--) {
            realStack.pop();
        }
        ((Symbol) realStack.peek()).parse_state = errorStack.getCurrentState();

        // Report the problem
        problemRequestor.unexpectedPreviousTerminal(previousTerminal.left, previousTerminal.right);
        
        return 0;  // Previous Terminal Deletion does not affect contents of nodes
    }

}
