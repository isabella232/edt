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
public class ParseStack extends ParseTable {
    
    protected StackSymbol[] stack = new StackSymbol[ErrorCorrectingParser.INITIAL_STACK_SIZE];
    private int stackTop;
    
    public ParseStack() {
        super();
    }
    
    public ParseStack(Stack realStack, boolean[] isNonTerminal) {
    	// Set the stack top correctly
    	stackTop = realStack.size() - 1;
    	
    	// Create all the nodes by inspecting the old nodes
    	Symbol[] symbols = (Symbol[]) realStack.toArray(new Symbol[realStack.size()]);
    	for (int i = 0; i < symbols.length; i++) {
			Symbol symbol = symbols[i];
			if(isNonTerminal[i]) {
				try {
					stack[i] = new NonTerminal(symbol.sym, symbol.left, symbol.right, symbol.parse_state);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					stack[i] = new NonTerminal(symbol.sym, symbol.left, symbol.right, symbol.parse_state);
				}
			}
			else {
				try {
					stack[i] = new Terminal(symbol.sym, symbol.left, symbol.right, symbol.parse_state);
				}
				catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					stack[i] = new Terminal(symbol.sym, symbol.left, symbol.right, symbol.parse_state);
				}
			}
		}
    }
    
    public void sync(boolean[] isNonTerminal) {
    	for (int i = 0; i <= stackTop; i++) {
			isNonTerminal[i] = stack[i].isNonTerminal();
		}
    }
    
    public void shiftStartState() {
        stack[stackTop] = new NonTerminal(0, 0, 0, 0);
    }
    
    public ParseStack createCopy() {
        ParseStack clone = new ParseStack();
        
        clone.stack = new StackSymbol[this.stack.length];
        System.arraycopy(this.stack, 0, clone.stack, 0, this.stack.length);
        
        clone.stackTop = this.stackTop;
        
        return clone;
    }
    
    public boolean processLookAhead(Terminal lookAhead) {
        while(true) {
            int action = get_action(stack[stackTop].parseState, lookAhead.symbolType);
        
            if(action > 0) {
                lookAhead.parseState = action - 1;
                stackTop += 1;
                try {
                	stack[stackTop] = lookAhead;
                }	            
				catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					stack[stackTop] = lookAhead;
				}
                return true;
            }
            else if(action < 0) {
                int lhs_sym_num = production_tab[(-action) - 1][0];
                int handle_size = production_tab[(-action) - 1][1];
                
                int right = stack[stackTop].right;
                stackTop -= handle_size;

                int left = handle_size == 0 ? stack[stackTop].right : stack[stackTop + 1].left;
                
                int newState = get_reduce(stack[stackTop].parseState, lhs_sym_num);
                
                NonTerminal nonTerminal = new NonTerminal(lhs_sym_num, left, right, newState);
                stackTop += 1;
                try {
                	stack[stackTop] = nonTerminal;
                }
                catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					stack[stackTop] = nonTerminal;
				}
                
                if(lhs_sym_num == NodeTypes.$START) return true;   // TODO If we have created the $START symbol, we are done
            }
            else {
                return false;
            }
        }
    }
    
    public int getHighestNonTerminal(int lookAheadTerminalType) {
        
        // Somestimes a scope closer is missing after the nonterminal is inserted (because parse check is free to
        // append any number of scope closers), the lookAheadTerminal may induce an error
        // Here, we are heuristically trying different scope closers until one that has action
        // This may not be the same scope closer chosen during the real recovery because an exhausted parse check is conducted.
        // However, this shouldn't cause a problem as scope closers do not induce different reduce actions in the EGL language
        // as of current.  This may not be true for all languages or for EGL in the future
        if(get_action(stack[stackTop].parseState, lookAheadTerminalType) == 0) {
            for(int i = 0; i < ParseTable.SCOPE_CLOSERS.length; i++) {
                if(get_action(stack[stackTop].parseState, ParseTable.SCOPE_CLOSERS[i]) != 0) {
                    lookAheadTerminalType = ParseTable.SCOPE_CLOSERS[i];
                    break;
                }
            }
        }
        
        while(true) {
            int action = get_action(stack[stackTop].parseState, lookAheadTerminalType);
        
            if(action > 0) {
                return stack[stackTop].symbolType;
            }
            else if(action < 0) {
                int lhs_sym_num = production_tab[(-action) - 1][0];
                int handle_size = production_tab[(-action) - 1][1];
                
                if(handle_size != 1) {
                    return stack[stackTop].symbolType;
                }
                
                int right = stack[stackTop].right;
                stackTop -= handle_size;

                int left;
                try {
                	left = handle_size == 0 ? stack[stackTop].right : stack[stackTop + 1].left;
                }
                catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					left = handle_size == 0 ? stack[stackTop].right : stack[stackTop + 1].left;
				}
                
                int newState = get_reduce(stack[stackTop].parseState, lhs_sym_num);
                
                NonTerminal nonTerminal = new NonTerminal(lhs_sym_num, left, right, newState);
                stackTop += 1;
                try {
                	stack[stackTop] = nonTerminal;
                }
                catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					stack[stackTop] = nonTerminal;
				}
            }
            else {
            	// Defect 1105037
            	// The following throw statement was intended as a fail-fast mechanism and is stricter than necessary.
            	// In really, the lookAheadTerminal really isn't guaranteed to shift eventually without error because 
            	// the lookAheadTerminal is heuristically guessed by the first block in this method (see its comments).
            	// 
            	// Instead, we should return the stack top when an error is discovered because that is the furthest we can 
            	// reduce on the stack
            	//
                // throw new RuntimeException("Can only call if lookAheadTerminal is guaranteed to shift eventually without error");
                return stack[stackTop].symbolType;
            }
        }
    }
    
    public boolean canShiftNonTerminal(int nonTerminalType, int terminalType) {
        while(true) {
            int currentState = stack[stackTop].parseState;
            
            // Check to see in this state whether we can shift the non terminal
            if(get_reduce(currentState, nonTerminalType) != -1) {
                return true;
            }

            // Process the current action and check again
            int action = get_action(stack[stackTop].parseState, terminalType);
            
            if(action > 0) {
                Terminal terminal = new Terminal(terminalType, -1 ,-1, action - 1); // TODO shouldn't need to create the terminal in the future
                stackTop += 1;
                try {
                	stack[stackTop] = terminal;
                }
                catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					stack[stackTop] = terminal;
				}
            }
            else if(action < 0) {
                int lhs_sym_num = production_tab[(-action) - 1][0];
                int handle_size = production_tab[(-action) - 1][1];
                
                int right = stack[stackTop].right;
                stackTop -= handle_size;

                int left = handle_size == 0 ? stack[stackTop].right : stack[stackTop + 1].left;
                
                int newState = get_reduce(stack[stackTop].parseState, lhs_sym_num);
                
                NonTerminal nonTerminal = new NonTerminal(lhs_sym_num, left, right, newState);
                stackTop += 1;
                try {
                	stack[stackTop] = nonTerminal;
                }
                catch(ArrayIndexOutOfBoundsException e) {
					enlargeStack();
					stack[stackTop] = nonTerminal;
				}
            }
            else {
                return false;
            }
        }
    }
    
    public boolean canShift(Terminal lookAhead) {
        return this.createCopy().processLookAhead(lookAhead);
    }
    
    public Terminal undoLastTerminal() {
        // Return null if the stack top is a non terminal
        if(stack[stackTop].isNonTerminal()) return null;
        
        // Obtain the last terminal from the stack
        // Note that this terminal is shared on all the stacks, so we must clone it
        Terminal terminal = (Terminal) stack[stackTop--];
        terminal = new Terminal(terminal.symbolType, terminal.left, terminal.right, terminal.parseState);
        
        // Remove all epsilon nodes
        while(stackTop != 0 && stack[stackTop].left == stack[stackTop].right) {
            stackTop--;
        }
        
        breakRightEdge();   // TODO study whether we need to break the right edge
        
        return terminal;
    }
    
    public int getCurrentState() {
        return stack[stackTop].parseState;
    }
    
    public int parseCheck(ITokenStream tokenStream) {
        return parseCheck(tokenStream, tokenStream.getCacheCapcity());
    }
    
    public int parseCheck(ITokenStream tokenStream, int maximumDistance) {
        int distance = 0;
        
        // Check if the stack is done already
        if(tokenStream.peekLookAhead(0).symbolType == NodeTypes.EOF && stack[stackTop].isTerminal() && stack[stackTop].symbolType == NodeTypes.EOF) {
            return maximumDistance;
        }
        
        // Process lookaheads until they are exhausted or another error is hit
        while(distance < maximumDistance && canShift(tokenStream.peekLookAhead(distance))) {
            processLookAhead(tokenStream.peekLookAhead(distance++));
            
            // If we can shift EOF, obviously we succeeded
            if(tokenStream.peekLookAhead(distance - 1).symbolType == NodeTypes.EOF) {
                return maximumDistance;
            }
        }
        
        if(distance == maximumDistance) {
            return distance;   // Can parse all of the look aheads
        }
        else {
            // Try different scope closers with the remaining lookaheads
            ITokenStream newTokenStream = tokenStream.createTokenStreamAtOffset(distance);
            
            int furthestDistance = 0;
            for(int i = 0; i < ParseTable.SCOPE_CLOSERS.length; i++) {
                Terminal scopeCloser = new Terminal(ParseTable.SCOPE_CLOSERS[i], -1, -1, -1);
                if(canShift(scopeCloser)) {
                    ParseStack newStack = this.createCopy();
                    newStack.processLookAhead(scopeCloser);
                    int newDistance = newStack.parseCheck(newTokenStream, maximumDistance - distance) - 2;  // TODO The minus 2 is the penalty for having to close the scope
                    if(newDistance > furthestDistance) {
                        furthestDistance = newDistance;
                    }
                }
            }
            
            return distance + furthestDistance;
        }
    }
    
    public void processNonTerminal(int nonTerminalType) {
        int currentState = getCurrentState();
        int gotoState = get_reduce(currentState, nonTerminalType);
        NonTerminal nonTerminal = new NonTerminal(nonTerminalType, stack[stackTop].right, stack[stackTop].right, gotoState);
        stackTop += 1;
        try {
        	stack[stackTop] = nonTerminal;
        }
        catch(ArrayIndexOutOfBoundsException e) {
			enlargeStack();
			stack[stackTop] = nonTerminal;
		}
    }
    
    public void breakRightEdge() {
        StackSymbol symbol = stack[stackTop];
        
        if(symbol.isTerminal()) return;
        
        NonTerminal nonTerminal = (NonTerminal) symbol;
        if(nonTerminal.symbolType == NodeTypes.stmt_star) {
            // TODO do more break right edges
            int newState = get_reduce(stack[stackTop -1].parseState, NodeTypes.stmt_plus);
            stack[stackTop] = new NonTerminal(NodeTypes.stmt_plus, nonTerminal.left, nonTerminal.right, newState);
        }
    }
    
    public void dumpStackUntil(int nonTerminalType, int terminalType) {
        while(stackTop > 0) {
            int currentState = stack[stackTop].parseState;
            
            if(get_reduce(currentState, nonTerminalType) != -1) {
                return;
            }
            
            if(get_action(currentState, terminalType) != 0) {
                if(createCopy().canShiftNonTerminal(nonTerminalType, terminalType)) {
                    return;
                }
            }
            
            stackTop--;
        }
    }
    
    public int getStackRightEdge() {
        return stack[stackTop].right;
    }
    
    /**
     * This is for generating error messages.  The stack must have been popped before this can be called
     */
    public int getStackLeftEdge() {
        return stack[stackTop].left;
    }
    
    public void pop() {
        stackTop--;
    }
    
    public boolean isEmpty() {
        return stackTop == 0;
    }
    
    public int getStackTop() {
        return stackTop;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i <= stackTop; i++) {
            buffer.append(stack[i].toString());
            buffer.append(" ");
        }
        return buffer.toString();
    }
    
    private void enlargeStack() {
    	StackSymbol[] newStack = new StackSymbol[stack.length * 2];
    	System.arraycopy(stack, 0, newStack, 0, stack.length);
    	stack = newStack;
	}
}
