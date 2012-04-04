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
public class ScopeRecovery extends AbstractRecovery {
    
    private int parseCheckDistance;
    private int scopeCloserIndex;
    
    public ScopeRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        for(int i = 0; i < ParseTable.SCOPE_CLOSERS.length; i++) {
            Terminal scopeCloser = new Terminal(ParseTable.SCOPE_CLOSERS[i], -1, -1, -1);
            if(errorStack.canShift(scopeCloser)) {
                ParseStack newStack = errorStack.createCopy();
                newStack.processLookAhead(scopeCloser);
                int distance = newStack.parseCheck(tokenStream);
                if(distance > parseCheckDistance) {
                    parseCheckDistance = distance;
                    scopeCloserIndex = i;
                }
            }
        }
    }

    public float getMisspellingIndex() {
        return 1;
    }

    public int getParseCheckDistance() {
        return parseCheckDistance - 2;  // Closing scope incurs a penalty of -2
    }

    public int performRecovery() {
    	Terminal lookAhead = tokenStream.getLookAhead();
		int left = lookAhead.left;
		int right = lookAhead.right;		
    	
        Terminal scopeCloser = new Terminal(ParseTable.SCOPE_CLOSERS[scopeCloserIndex], left, left, -1);
        Symbol scopeCloserSymbol = new Symbol(ParseTable.SCOPE_CLOSERS[scopeCloserIndex], left, left);
        tokenStream.rollBack(scopeCloser, scopeCloserSymbol);
        
        if(lookAhead.symbolType == NodeTypes.EOF && scopeCloser.symbolType == NodeTypes.END) {
        	Symbol lastRealSymbol = getLastRealSymbol(realStack);
        	problemRequestor.missingEndForPart(lastRealSymbol.left, lastRealSymbol.right);
        }
        else {
        	problemRequestor.missingScopeCloser(scopeCloser.symbolType, left, right);
        }
        
        return 0;  // Scope recoveries don't affect the contents of the nodes
    }
}
