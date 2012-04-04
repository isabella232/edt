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
public class AdvancedPhraseRecovery extends AbstractRecovery {

    public static final int INPUT_DELETION_LIMIT = 15;
    
    private RecoveryConfiguration bestConfiguration;
    
    private static class RecoveryConfiguration {
        
        private int stackDeleted;
        private int inputDeleted;
        private int nonTerminalSubstitution;
        
        private int parseCheckDistance;
        
        public RecoveryConfiguration(int stackDeleted, int inputDeleted, int nonTerminalSubstitution, int parseCheckDistance) {
            this.stackDeleted = stackDeleted;
            this.inputDeleted = inputDeleted;
            this.nonTerminalSubstitution = nonTerminalSubstitution;
            this.parseCheckDistance = parseCheckDistance;
        }
        
        public boolean isBetterThan(RecoveryConfiguration other) {
            if(this.parseCheckDistance < ErrorCorrectingParser.RECOVERY_SUCCESS) return false;
            if(other == null) return true;
            
            // The number of tokens deleted is the most import thing
            int thisTotal = this.inputDeleted + this.stackDeleted;
            int otherTotal = other.inputDeleted + other.stackDeleted;
            
            if(thisTotal < otherTotal) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    
    public AdvancedPhraseRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super(errorStack, realStack, tokenStream, problemRequestor);
    }

    protected void performTrial() {
        ParseStack poppingStack = errorStack.createCopy();
        for(int stackDeleted = 0; !poppingStack.isEmpty(); stackDeleted++) {
            // Perform the trials on this popped stack
            performTrial(poppingStack, stackDeleted);
            
            // Pop one more of the stack symbols from the parse stack
            poppingStack.pop();
            poppingStack.breakRightEdge();
        }
    }
    
    protected void performTrial(ParseStack poppedStack, int stackDeleted) {
        for(int inputDeleted = 0; inputDeleted < INPUT_DELETION_LIMIT; inputDeleted++) {
            // Set up the stream
            ITokenStream trialStream = tokenStream.createTokenStreamAtOffset(inputDeleted);
            
            // Try nonterminal substitution first
            short[] nonTerminalCandidates = poppedStack.getNonTerminalCandidates(poppedStack.getCurrentState());
            for(int i = 0; i < nonTerminalCandidates.length; i++) {
                int nonTerminalCandidate = nonTerminalCandidates[i];
                ParseStack trialStack = poppedStack.createCopy();
                trialStack.processNonTerminal(nonTerminalCandidate);

                int parseCheckDistance = trialStack.parseCheck(trialStream);
                RecoveryConfiguration configuration = new RecoveryConfiguration(stackDeleted, inputDeleted, nonTerminalCandidate, parseCheckDistance);
                
                if(configuration.isBetterThan(bestConfiguration)) {
                    bestConfiguration = configuration;
                }
                
                // If this is already a successful recovery, no other recovery of this stack depth will be better 
                // since they will delete more input tokens
                if(parseCheckDistance > ErrorCorrectingParser.RECOVERY_SUCCESS) {
                    return;
                }
            }
            
            // Try just deleting the tokens
            {
                ParseStack trialStack = poppedStack.createCopy();
                
                int parseCheckDistance = trialStack.parseCheck(trialStream);
                RecoveryConfiguration configuration = new RecoveryConfiguration(stackDeleted, inputDeleted, -1, parseCheckDistance);

                if(configuration.isBetterThan(bestConfiguration)) {
                    bestConfiguration = configuration;
                }
                
                // If this is already a successful recovery, no other recovery of this stack depth will be better 
                // since they will delete more input tokens
                if(parseCheckDistance > ErrorCorrectingParser.RECOVERY_SUCCESS) {
                    return;
                }
            }
        }
    }

    public float getMisspellingIndex() {
        return 0;
    }

    public int getParseCheckDistance() {
        return bestConfiguration == null ? 0 : bestConfiguration.parseCheckDistance;
    }
    
    public int getNumTokensDeleted() {
        return bestConfiguration == null ? 0 : bestConfiguration.inputDeleted;
    }

    public int performRecovery() {
        // Remember the region that is deleted
        int deleteStart = tokenStream.getLookAhead().left;
        int deleteEnd = errorStack.getStackRightEdge();
        
        // Pop the error stack and real stack
        for(int i = 0; i < bestConfiguration.stackDeleted; i++) {
            // Remember the region that is deleted
            deleteStart = errorStack.getStackLeftEdge();
            
            // Actually the pop the stacks
            errorStack.pop();
            realStack.pop();
            
            // Remember to break the right edge (see Crash6.egl)
            errorStack.breakRightEdge();
            ((Symbol) realStack.peek()).parse_state = errorStack.getCurrentState();	// Use the error stack as a hint -- not that break right edge currently doesn't alter state size
        }
        
        // Advance the real token stream -- affects both parsers
        for(int i = 0; i < bestConfiguration.inputDeleted; i++) {
            deleteEnd = tokenStream.getLookAhead().right;
            tokenStream.advanceLookAhead();
        }
        
        if(bestConfiguration.nonTerminalSubstitution != -1) {
            // Push the artificial non-terminal into the real stack
            int currentState = errorStack.getCurrentState();
            int gotoState = errorStack.get_reduce(currentState, bestConfiguration.nonTerminalSubstitution);
            if(gotoState != -1) {
	            Symbol nonTerminal = new Symbol(bestConfiguration.nonTerminalSubstitution, gotoState);
	            realStack.push(nonTerminal);
	            
	            // Push the substituted non-terminal into the error stack
	            errorStack.processNonTerminal(bestConfiguration.nonTerminalSubstitution);
	            
	            // Report problem
	            problemRequestor.incorrectPhrase(bestConfiguration.nonTerminalSubstitution, deleteStart, deleteEnd);
	
	            // The nonterminal is artificially created
	            return realStack.size() - 1;
            }
            else {
            	problemRequestor.unexpectedPhrase(deleteStart, deleteEnd);
                return 0;
            }
        }
        else {
            problemRequestor.unexpectedPhrase(deleteStart, deleteEnd);
            return 0;
        }
    }

}
