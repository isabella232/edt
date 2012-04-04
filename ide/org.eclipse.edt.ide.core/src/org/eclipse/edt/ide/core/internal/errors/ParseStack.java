/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.errors;

import java.util.List;
import java.util.Stack;

/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ParseStack implements Cloneable {
	
	private ErrorGrammar grammar = ErrorGrammar.getInstance();
	
	private Stack stack = new Stack();
	
	public ParseStack(){
		stack.push(new ParseStackEntry(grammar.getStartState(), new DummyParseNode()));
	}
	
	private boolean isTerminalOrConnector(ParseNode node) {
		// If a node is not a terminal, it is a connector iff it is whitespace
		return node.isTerminal() || node.isWhiteSpace();
	}

	/**
	 * @param subtreeRoot
	 * @return null if there is no terminal or whitespace connector in the subtree
	 */
	private NonTerminalNode findRMConnectorOrTerminalParent(NonTerminalNode subtreeRoot) {
		ParseNode[] children = subtreeRoot.children;

		// For an epsilon node, we return null
		if (children == null) {
			return null;
		}

		int position = children.length - 1;
		while (position >= 0) {
			ParseNode child = children[position];

			if(isTerminalOrConnector(child)) {
				return subtreeRoot;
			}
			else {
				// We know that child must be a nonterminal at this point
				// See whether there is a connector or whitespace node in the subtree rooted at child
				NonTerminalNode result = findRMConnectorOrTerminalParent((NonTerminalNode) child);
				if (result != null) {
					return result;
				}
				
			}

			position--;
		}

		return null;
	}
	
	
	private int findConnectorOrTerminalPos(NonTerminalNode parent) {
		ParseNode[] children = parent.children;
		for(int i = children.length - 1; i >= 0; i--) {
			if(isTerminalOrConnector(children[i])) {
				return i;
			}
		}
		
		// We assume that there is a connector or terminal child
		throw new IllegalArgumentException();
	}
	
	private void addToConnector(NonTerminalNode connector, ParseNode whitespaceNode) {
		// Create a whitespace pair holding both the original whitespace and the added whitespace
		NonTerminalNode wsPair = new NonTerminalNode(
			ErrorNodeTypes.wsPair,
			new ParseNode[] {connector.children[1], whitespaceNode}
		);
		
		connector.children[1] = wsPair; 
	}

	// TODO implement these methods
	public void connect(ParseNode whitespaceNode) {
		// Do nothing if whitespaceNode is null
		if(whitespaceNode == null) {
			return;
		}
		
		for(int distanceFromTop = 0;;distanceFromTop++) {
			ParseStackEntry stackEntry = (ParseStackEntry) stack.elementAt(stack.size() - 1 - distanceFromTop);
			ParseNode node = stackEntry.node; 
			
			// Found terminal on stack
			if(node.isTerminal()) {
				// Replace terminal on stack with connector
				stackEntry.node = createConnector((TerminalNode) node, whitespaceNode);
				return;
			}
			
			// Found connector on stack
			if(node.isWhiteSpace()) {
				// Add new whitespace in addition to the original whitespaces
				addToConnector((NonTerminalNode) node, whitespaceNode);
				return;
			}
			
			// See if we will find a parent of a connector or whitespace in the subtree rooted at node
			NonTerminalNode parent = findRMConnectorOrTerminalParent((NonTerminalNode) node);
			if(parent != null) {
				ParseNode terminalOrConnector = parent.children[findConnectorOrTerminalPos(parent)];
				
				if(terminalOrConnector.isTerminal()) {
					// Replace terminal node child with connector
					parent.children[findConnectorOrTerminalPos(parent)] = 
						createConnector((TerminalNode) terminalOrConnector, whitespaceNode);
					return;
				}
				else {
					// Add new whitespace in addition to the original whitespaces
					addToConnector((NonTerminalNode) terminalOrConnector, whitespaceNode);
					return;
				}
			}
		}
	}
	
	private NonTerminalNode createConnector(TerminalNode terminalNode, ParseNode whitespaceNode) {
		return new NonTerminalNode(ErrorNodeTypes.connector, new ParseNode[] {terminalNode, whitespaceNode});
	}
	
	public void shift(ParseNode node) {
		int shiftedState;
		
		if(node.isTerminal()) {
			TerminalNode terminalNode = (TerminalNode) node;
			shiftedState = grammar.getTerminalAction(getCurrentState(), terminalNode) - 1;
		}
		else {
			// Node must be nonterminal
			NonTerminalNode nonTerminalNode = (NonTerminalNode) node;
			shiftedState = grammar.getGotoState(getCurrentState(), nonTerminalNode);
		}
		
		if(shiftedState < 0) {
			throw new IllegalArgumentException();
		}
		
		Reporter.getInstance().shift(node, shiftedState);
		stack.push(new ParseStackEntry(shiftedState, node));
	}
	
	public void reduce(int ruleNumber) {
		Reporter.getInstance().reduce(ruleNumber);
		
		// Pop the nodes of the stack and put them in an array
		int handleSize = grammar.getHandleSize(ruleNumber);
		ParseNode[] children = handleSize == 0 ? null : new ParseNode[handleSize];
		for (int i = handleSize - 1; i >= 0; i--) {
			ParseStackEntry stackEntry = (ParseStackEntry) stack.pop();
			children[i] = stackEntry.node;
		}

		// Create the parent nonterminal node
		int nonTerminalType = grammar.getLHS(ruleNumber);
		NonTerminalNode parent = new NonTerminalNode(nonTerminalType, children);
		parent.ruleNumber = ruleNumber;
		
		// Shift the parent back on the stack
		int gotoState = grammar.getGotoState(getCurrentState(), parent);
		shift(parent);
	}
	
	public int getCurrentState() {
		ParseStackEntry stackEntry = (ParseStackEntry) stack.peek();
		return stackEntry.state; 
	}
	
	public ParseNode getTopOfStackNode() {
		ParseStackEntry stackEntry = (ParseStackEntry) stack.peek();
		return stackEntry.node; 
	}
	
	public ParseStack copy() {
		try {
			ParseStack result = (ParseStack) super.clone();
			result.stack = (Stack) stack.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		buffer.append(stack.elementAt(0).toString());
		
		for (int i = 1; i < stack.size(); i++) {
			buffer.append(' ');
			buffer.append(stack.elementAt(i).toString());
		}
		
		buffer.append(']');
		
		return buffer.toString();
	}
	
	/**
	 * @param contextDeleted
	 * @return an array of length 0 if contextDeleted is 0
	 */
	public ParseNode[] deleteContext(int contextDeleted) {
		List stackEntries = stack.subList(stack.size() - contextDeleted, stack.size());
		ParseNode[] result = new ParseNode[contextDeleted];
		for(int i = 0; i < contextDeleted; i++) {
			ParseStackEntry stackEntry = (ParseStackEntry) stackEntries.get(i);
			result[i] = stackEntry.node;
		}
		
		// Remember to actually "pop" the entries
		stackEntries.clear();
		
		return result;		
	}
	
	public int availableContext() {
		return stack.size() - 1;
	}
	
	
	public boolean isTerminalShiftable(int terminalType) {
		// Optimization: Avoid copying the stack if the terminal is immediately shiftable or 
		// immediately of error
		int action = grammar.getTerminalAction(getCurrentState(), terminalType);
		if(action > 0) {
			return true;
		}
		else if(action == 0) {
			return false;
		}
		else {
			// Make a copy of the stack, perform all the reductions and test again
			ParseStack copiedStack = this.copy();
			copiedStack.performAllReductions(terminalType);
			return copiedStack.isTerminalShiftable(terminalType);
		}
	}

	public void performAllReductions(int terminalType) {
		while(true) {
			int action = grammar.getTerminalAction(getCurrentState(), terminalType);
			
			if(action < 0) {
				// We shall never reduce by the start production because we are will not shift 
				// by EOF
				int ruleNumber = -(action) - 1;
				reduce(ruleNumber);
			}
			else {
				return;
			}
		}
	}
	
	// TODO The following is experimental
	public void performDefaultReductions() {
		while(true) {
			int defaultRule = grammar.getLRZeroReduceRule(getCurrentState());
			if(defaultRule > 0) {
				reduce(defaultRule);
			}
			else {
				break;
			}
		}
	}
	/**
	 * @return
	 */
	public Stack getStack() {
		return stack;
	}
}
