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
 * @author Wing Hong Ho
 */
public class SlimParseStack extends ParseStack {
	
	protected int[] stateStack = new int[ErrorCorrectingParser.INITIAL_STACK_SIZE];
	private int stackTop;
	
	public SlimParseStack() {
		super();
	}

	public void reset(Stack realStack) {
    	// Set the stack top correctly
    	stackTop = realStack.size() - 1;
    	
    	// Copy the states from the real stack
    	for (int i = 0; i <= stackTop; i++) {
			stateStack[i] = ((Symbol) realStack.get(i)).parse_state;
		}
	}

	public void shiftStartState() {
		stateStack[0] = 0;
		stackTop = 0;
	}

	public boolean canShift(Terminal lookAhead) {
        while(true) {
            int action = get_action(stateStack[stackTop], lookAhead.symbolType);
        
            if(action > 0) {
            	stackTop += 1;
            	try {
            		stateStack[stackTop] = action - 1;
            	}
            	catch(ArrayIndexOutOfBoundsException e) {
            		enlargeStack();
            		stateStack[stackTop] = action - 1;
            	}
                return true;
            }
            else if(action < 0) {
                int lhs_sym_num = production_tab[(-action) - 1][0];
                int handle_size = production_tab[(-action) - 1][1];
                
                stackTop -= handle_size;
                int newState = get_reduce(stateStack[stackTop], lhs_sym_num);
                stackTop += 1;
                try {
                	stateStack[stackTop] = newState;
                }
                catch(ArrayIndexOutOfBoundsException e) {
            		enlargeStack();
            		stateStack[stackTop] = newState;
            	}
                
                if(lhs_sym_num == NodeTypes.$START) return true;
            }
            else {
                return false;
            }
        }
	}
	
	public boolean processLookAhead(Terminal lookAhead) {
		return true;	// Basically do nothing
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i <= stackTop; i++) {
			buffer.append(stateStack[i]);
			buffer.append(" ");
		}
		return buffer.toString();
	}

	// The following methods are not implemented in this optimization class
	public void breakRightEdge() {
		throw new UnsupportedOperationException();
	}

	public boolean canShiftNonTerminal(int nonTerminalType, int terminalType) {
		throw new UnsupportedOperationException();
	}

	public ParseStack createCopy() {
		throw new UnsupportedOperationException();
	}

	public void dumpStackUntil(int nonTerminalType, int terminalType) {
		throw new UnsupportedOperationException();
	}

	public int getConstructRoot() {
		throw new UnsupportedOperationException();
	}

	public int getConstructRoot(int terminalType) {
		throw new UnsupportedOperationException();
	}

	public int getCurrentState() {
		throw new UnsupportedOperationException();
	}

	public int getHighestNonTerminal(int lookAheadTerminalType) {
		throw new UnsupportedOperationException();
	}

	public int getStackLeftEdge() {
		throw new UnsupportedOperationException();
	}

	public int getStackRightEdge() {
		throw new UnsupportedOperationException();
	}

	public int getStackTop() {
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public int parseCheck(ITokenStream tokenStream, int maximumDistance) {
		throw new UnsupportedOperationException();
	}

	public int parseCheck(ITokenStream tokenStream) {
		throw new UnsupportedOperationException();
	}

	public void pop() {
		throw new UnsupportedOperationException();
	}

	public void processNonTerminal(int nonTerminalType) {
		throw new UnsupportedOperationException();
	}

	public Terminal undoLastTerminal() {
		throw new UnsupportedOperationException();
	}
	
	private void enlargeStack() {
    	int[] newStack = new int[stateStack.length * 2];
    	System.arraycopy(stateStack, 0, newStack, 0, stack.length);
    	stateStack = newStack;
	}
}
