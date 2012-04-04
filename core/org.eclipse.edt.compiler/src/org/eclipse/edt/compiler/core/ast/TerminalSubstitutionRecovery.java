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

import org.eclipse.edt.compiler.core.EGLKeywordHandler;



/**
 * @author winghong
 */
public class TerminalSubstitutionRecovery extends AbstractRecovery {

    private int parseCheckDistance;
    private int substitutionTerminal;

    public TerminalSubstitutionRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
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
            ITokenStream trialStream = tokenStream.createTokenStreamAtOffset(1);
            trialStack.processLookAhead(terminal);
            
            int trialDistance = trialStack.parseCheck(trialStream);
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
        return parseCheckDistance + 1; // TODO review whether we want to compensate for the deleted token;
    }

    public int performRecovery() {
        // Remember offset information for error message
        int errorStart = tokenStream.getLookAhead().left;
        int errorEnd = tokenStream.getLookAhead().right;
        int errorNodeType = tokenStream.peekLookAhead(0).symbolType;
                
        // Substitute the terminal on the token stream
        Terminal terminal = new Terminal(substitutionTerminal, errorStart, errorEnd, -1);
        Symbol symbol = new Symbol(substitutionTerminal, errorStart, errorEnd);
        symbol.parse_state = -2;
        tokenStream.advanceLookAhead();
        tokenStream.rollBack(terminal, symbol);
        
        //Report error        
        if((NodeTypes.ID == terminal.symbolType || NodeTypes.TIMES == terminal.symbolType) &&
           (EGLKeywordHandler.getKeywordHashSet().contains(NodeNameUtility.getTerminalName(errorNodeType).toLowerCase())
           	|| NodeTypes.PRIMITIVE == errorNodeType
           	|| NodeTypes.NUMERICPRIMITIVE == errorNodeType
           	|| NodeTypes.TIMESTAMPINTERVALPRIMITIVE == errorNodeType
           	|| NodeTypes.CHARPRIMITIVE == errorNodeType) &&
           errorNodeType != NodeTypes.STRING) {
        	problemRequestor.keywordAsName(errorNodeType, errorStart, errorEnd);
        }
        else {
        	problemRequestor.incorrectTerminal(substitutionTerminal, errorStart, errorEnd);
        }
        
        // Return 0 for the error node position.  The true error node position will be
        // determined when the main parser shifts the artificially created terminal since 
        // its parse_state is -2
        return 0;
    }

}
