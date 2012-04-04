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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;



/**
 * @author winghong
 */
public class ErrorCorrectingParser extends Parser { // TODO we are only extending parser to get access to the action object
    
    public static final int RECOVERY_SUCCESS = 5;
    public static final int INITIAL_STACK_SIZE = 128;
    
    public static final int RETURN_LINEBREAKS = 1 << 1;
    public static final int RETURN_LINE_COMMENT = 1 << 2;
    public static final int RETURN_BLOCK_COMMENT = 1 << 3;

    private ISyntaxErrorRequestor problemRequestor;
    
    private TokenStream stream;
    
    public static final int MAX_ERRORS = 100;
    private int errorsDetected;
    
    /*
     * Begin debug methods 
     */
    public ErrorCorrectingParser() {
    }
    
    public void setProblemRequestor(ISyntaxErrorRequestor problemRequestor) {
        this.problemRequestor = problemRequestor;
    }
    
    public Node parse(String source) {
        // Set up the stream that is shared by both parsers
    	Scanner scanner = new Lexer(new StringReader(source));
        stream = new TokenStream(RECOVERY_SUCCESS + AdvancedPhraseRecovery.INPUT_DELETION_LIMIT, scanner);
        return (Node) parse().value;
    }
    /*
     * End debug methods
     */
    
    public ErrorCorrectingParser(Scanner lexer) {
        stream = new TokenStream(RECOVERY_SUCCESS + AdvancedPhraseRecovery.INPUT_DELETION_LIMIT, lexer);
    }
    
    public ErrorCorrectingParser(Scanner lexer, int whitespaceMask) {
    	if(lexer instanceof Lexer) {
    		Lexer eglLexer = (Lexer) lexer;
    		eglLexer.returnBlockComments = (whitespaceMask & RETURN_BLOCK_COMMENT) != 0;
    		eglLexer.returnLineBreaks = (whitespaceMask & RETURN_LINEBREAKS) != 0;
    		eglLexer.returnLineComments = (whitespaceMask & RETURN_LINE_COMMENT) != 0;
    	}
        stream = new TokenStream(RECOVERY_SUCCESS + AdvancedPhraseRecovery.INPUT_DELETION_LIMIT, lexer);
    }
    
    public Symbol parse() {
    	// Reset the errors count and set up problem requestor
    	errorsDetected = 0;
        if(problemRequestor == null) problemRequestor = new AccumulatingSyntaxErrorRequestor();
    	
        // Set up the parse tables for local access
        production_tab = production_table();
        action_tab = action_table();
        reduce_tab = reduce_table();
        
        // Set up the action object
        CUP$Parser$actions actionObject = new CUP$Parser$actions(this);

        // Set up the scouting stack to prevent any premature reductions in the presense of errors
        SlimParseStack scoutStack = new SlimParseStack();
        scoutStack.shiftStartState();
        
        // Set up the real parse stack
        boolean[] isNonTerminal = new boolean[INITIAL_STACK_SIZE]; // Set up an array to remember whether a given symbol on the stack is a nonterminal
        Stack realStack = new Stack();
        realStack.push((new Symbol(0, 0 /* start state */)));
        int realStackTop = 0;
        isNonTerminal[0] = true;
        
        // The left most node that is artificial (i.e. in error -- created by recovery).  This is to control
        // whether we should run parse actions or not
        // Value of 0 indicates that there are no error nodes on the stack 
        int leftMostErrorNodeIndex = 0;
        
        // Perform the parsing
        for(_done_parsing = false; !_done_parsing;) {
        	// Obtain look ahead
        	Terminal lookAhead = stream.getLookAhead();
        	
        	// Test if the current lookahead can be shifted on the scouting stack
        	// If it can, then process all the actions associated with this lookahead on the real stack
        	// Otherwise, reset the scouting stack and perform recovery
        	if(scoutStack.canShift(lookAhead)) {
        		// Repeat until all the actions associated with this lookahead is performed
        		while(true) {
        		    Symbol lookAheadSymbol = stream.getLookAheadSymbol();
        		    int action = get_action(((Symbol) realStack.peek()).parse_state, lookAheadSymbol.sym);

        		    if(action > 0) {
        		    	// Parse state of -2 on a terminal node indicates that it is artificially created by error recovery
        		    	boolean isErrorNode = lookAheadSymbol.parse_state == -2;

        		    	// Perform the shift
        		    	lookAheadSymbol.parse_state = action - 1;
        		    	realStack.push(lookAheadSymbol);
        		    	realStackTop++;
        		    	
        		    	try {
        		    		isNonTerminal[realStackTop] = false;
        		    	}
        		    	catch(ArrayIndexOutOfBoundsException e) {
        		    		isNonTerminal = enlargeStack(isNonTerminal);
        		    		isNonTerminal[realStackTop] = false;
        		    	}
        		    	
        		    	// Record the new error situation if applicable
        		    	if(isErrorNode) {
        		    		leftMostErrorNodeIndex = (leftMostErrorNodeIndex == 0 ? realStackTop : Math.min(leftMostErrorNodeIndex, realStackTop));
        		    	}
        		    	
        		    	// After we have shifted, should break out of the action loop
        		    	break;
        		    }
        		    else {
        		        // Information about the current rule
        		        int nonTerminalType = production_tab[(-action) - 1][0];
        		        int handleSize = production_tab[(-action) - 1][1];
        		        Symbol nonTerminal;

        		        // We run actions normally if there are no errors nodes on stack
        		        if(leftMostErrorNodeIndex == 0) {
        		            try {
        		                nonTerminal = actionObject.CUP$Parser$do_action((-action) - 1, this, realStack, realStackTop);
        		            } catch (Exception e) {
        		                throw new RuntimeException("Parse action failed with exception", e);
        		            }
        		        }
        		        else {
        		            // Create the non-terminal symbol ourselves
        		            int left = ((Symbol) realStack.elementAt(realStackTop - handleSize + (handleSize == 0 ? 0 : 1))).left;
        		            int right = ((Symbol) realStack.peek()).right;
        		            nonTerminal = new Symbol(nonTerminalType, left, right);
        		            
        		            // Remember whether we have really nulled out an error node
        		            boolean nullable = false;
        		            
        		            // We have to simulate the actions for sequence nodes and some special opt nodes
        		            if(scoutStack.isConstructPlus(nonTerminalType)) {
        		            	nullable = true;
        		            	if(handleSize == 1) {
        		            		nonTerminal.value = new ArrayList();
        		            	}
        		            	else {
        		            		Symbol elementAt = (Symbol) realStack.elementAt(realStackTop - handleSize + 1);
        		            		Object value = (elementAt).value;
        		            		if(Collections.EMPTY_LIST == value && scoutStack.isConstructStar(elementAt.sym)) {
        		            			nonTerminal.value = new ArrayList();
        		            		}
        		            		else if(value == null) {
        		            			nonTerminal.value = new ArrayList();
        		            		}
        		            		else {
        		            			nonTerminal.value = value;
        		            		}
        		            	}
        		            }
        		            else {
        		            	nullable = true;
        		            	
        		            	switch(nonTerminalType) {
	        		            	case NodeTypes.fieldsOpt:
	        		            		nonTerminal.value = Collections.EMPTY_LIST;
	        		            	case NodeTypes.returnsOpt:
	        		            	case NodeTypes.partSubTypeOpt:
	        		            	case NodeTypes.packageDeclarationOpt:
	        		            		break;
	        		            	default:
	        		            		nullable = false;
        		            	}
        		            }

        		            // If we have nulled out a nonterminal and there are no more error nodes on stack
        		            if(nullable && realStackTop - handleSize < leftMostErrorNodeIndex) {
        		            	leftMostErrorNodeIndex = 0;
        		            }
        		        }
        		        
    		            // Pop the stack
    			        for(int i = 0; i < handleSize; i++) {
    			            realStack.pop();
    			            realStackTop--;
    			        }
    			        
    			        // Push the newly created nonterminal back to the stack
    			        nonTerminal.parse_state = get_reduce(((Symbol) realStack.peek()).parse_state, nonTerminalType);
    			        realStack.push(nonTerminal);
    			        realStackTop++;
    			        
    			        try {
    			        	isNonTerminal[realStackTop] = true;
    			        }
    			        catch(ArrayIndexOutOfBoundsException e) {
    			        	isNonTerminal = enlargeStack(isNonTerminal);
    			        	isNonTerminal[realStackTop] = true;
    			        }
    			        
    			        // The left most error node may have been absorbed into this new node
    			        // So we have may have to adjust leftMostErrorNodeIndex
    			        if(realStackTop < leftMostErrorNodeIndex) {
    			        	leftMostErrorNodeIndex = realStackTop;
    			        }
    			        
    			        // If we have reduced to $START, we should break out of the action loop
    			        if(nonTerminalType == NodeTypes.$START) break;  
        		    }
        		} // Repeat until all the actions associated with this lookahead is performed

        		// Advance look ahead
        		stream.advanceLookAhead();
        	}
        	else {
        		// Recover from the error
        		int errorNodeIndex = recover(realStack, isNonTerminal);
        		
        		// Reset the scout stack to be in sync with the real stack
        		scoutStack.reset(realStack);

        		// Check if leftMostErrorNodeIndex needs to be updated
        		if(errorNodeIndex != 0) {
		    		leftMostErrorNodeIndex = (leftMostErrorNodeIndex == 0 ? errorNodeIndex : Math.min(leftMostErrorNodeIndex, errorNodeIndex));
        		}
        		
        		// Update the realStackTop because the error recovery may modify realStack but they don't have access to this variable
        		realStackTop = realStack.size() - 1;
        		
        		// Phrase recovery and panic recovery and dump symbols from stack, it may have dumped an error node
        		if(realStackTop < leftMostErrorNodeIndex) {
        			leftMostErrorNodeIndex = 0;
        		}
        	}
        } // Perform the parsing
        	
        // Record syntax errors
        File file = (File) ((Symbol) realStack.peek()).value;
        if(problemRequestor instanceof AccumulatingSyntaxErrorRequestor) {
        	List errors = ((AccumulatingSyntaxErrorRequestor) problemRequestor).getSyntaxErrors();
        	errors.addAll(stream.getLexerErrors());
            file.setSyntaxErrors(errors);
        }
        
        // Record whitespaces
        Scanner lexer = stream.getLexer();
    	if(lexer instanceof Lexer) {
    		Lexer eglLexer = (Lexer) lexer;
    		file.blockComments = eglLexer.blockComments;
    		file.lineBreaks = eglLexer.lineBreaks;
    		file.lineComments = eglLexer.lineComments;
    	}

        // Return the value
        return (Symbol) realStack.peek();
    }

    private boolean[] enlargeStack(boolean[] stack) {
    	boolean[] newStack = new boolean[stack.length * 2];
    	System.arraycopy(stack, 0, newStack, 0, stack.length);
    	
    	return newStack;
	}

	private int recover(Stack realStack, boolean[] isNonTerminal) {
    	// Create a full parse stack for use during recovery
    	ParseStack stack = new ParseStack(realStack, isNonTerminal);
    	
    	// Increment the error count
    	errorsDetected++;
    	
    	// Only attempt sophisticated recovery if the file do not have too many errors already
        if(errorsDetected < MAX_ERRORS) {
			// Simple Recovery
			AbstractRecovery bestRecovery;
			AbstractRecovery[] recoveries = new AbstractRecovery[] {
					new ScopeRecovery(stack, realStack, stream, problemRequestor),

					new TerminalDeletionRecovery(stack, realStack, stream, problemRequestor),

					new NonTerminalSubstitutionRecovery(stack, realStack, stream, problemRequestor),
					new TerminalSubstitutionRecovery(stack, realStack, stream, problemRequestor),

					new NonTerminalInsertionRecovery(stack, realStack, stream, problemRequestor),
					new TerminalInsertionRecovery(stack, realStack, stream, problemRequestor),

					new PreviousTerminalDeletionRecovery(stack, realStack, stream, problemRequestor),

					new PreviousNonTerminalSubstitutionRecovery(stack, realStack, stream, problemRequestor),
					new PreviousTerminalSubstitutionRecovery(stack, realStack, stream, problemRequestor),

					new PreviousNonTerminalInsertionRecovery(stack, realStack, stream, problemRequestor),
					new PreviousTerminalInsertionRecovery(stack, realStack, stream, problemRequestor), };

			bestRecovery = recoveries[0];
			for (int i = 1; i < recoveries.length; i++) {
				if (recoveries[i].getParseCheckDistance() > bestRecovery.getParseCheckDistance()) {
					bestRecovery = recoveries[i];
				}
			}

			if (bestRecovery.getParseCheckDistance() >= RECOVERY_SUCCESS) {
				int result = bestRecovery.performRecovery();
				stack.sync(isNonTerminal);
				return result;
			}

			// Phrase Recovery
			AdvancedPhraseRecovery phraseRecovery = new AdvancedPhraseRecovery(stack, realStack, stream,
					problemRequestor);
			if (phraseRecovery.getParseCheckDistance() >= RECOVERY_SUCCESS) {
				if(phraseRecovery.getNumTokensDeleted() < RECOVERY_SUCCESS) {
					int result = phraseRecovery.performRecovery();
					stack.sync(isNonTerminal);
					return result;
				}
				else {
					int result = bestRecovery.performRecovery();
					stack.sync(isNonTerminal);
					return result;
				}
			}
		}
        
        // Output too many syntax error message when the limit is busted
        if(errorsDetected == MAX_ERRORS) {
        	problemRequestor.tooManyErrors();
        }

        // Panic Recovery
        return panicRecover(stack, realStack, stream, problemRequestor);
    }
    
    private int panicRecover(ParseStack stack, Stack realStack, ITokenStream stream, ISyntaxErrorRequestor problemRequestor) {
    	// Remember the first token
    	int left = stream.getLookAhead().left;
    	int right = stream.getLookAhead().right;
    	
        // Dump the entire part
        stack.dumpStackUntil(NodeTypes.part, NodeTypes.RECORD);
        
        while(stack.getStackTop() != realStack.size() - 1) {
            realStack.pop();
        }
        
        // Delete the input tokens until we can parse RECOVERY_SUCCESS tokens ahead
        while(true) {
            ParseStack trialStack = stack.createCopy();
            
            if(trialStack.parseCheck(stream, RECOVERY_SUCCESS) == RECOVERY_SUCCESS) {
                break;
            }
            
            stream.advanceLookAhead();
        }
        
        // Only report the panic error if we haven't reported too many already 
        if(errorsDetected < MAX_ERRORS) {
        	problemRequestor.panicPhrase(left, right);
        }
        
        // Panic recoveries do not create artificial nodes
        return 0;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(stack.toString());
        buffer.append("\n");
        buffer.append(stream.toString());
        return buffer.toString();
    }
    
}
