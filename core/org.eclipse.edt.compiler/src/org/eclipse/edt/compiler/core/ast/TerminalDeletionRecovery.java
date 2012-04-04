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

/**
 * @author winghong
 */
public class TerminalDeletionRecovery extends AbstractRecovery {

    private int parseCheckDistance;

    public TerminalDeletionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        ParseStack trialStack = errorStack.createCopy();
        ITokenStream trialStream = tokenStream.createTokenStreamAtOffset(1);
        parseCheckDistance = trialStack.parseCheck(trialStream) + 1; // TODO review whether we want to compensate for the deleted token
    }

    public float getMisspellingIndex() {
        return 0;
    }

    public int getParseCheckDistance() {
        return parseCheckDistance;
    }

    public int performRecovery() {
        problemRequestor.unexpectedTerminal(tokenStream.getLookAhead().left, tokenStream.getLookAhead().right);
        tokenStream.advanceLookAhead();
        
        return 0;  // Terminal deletions don't affect contents of nodes
    }

}
