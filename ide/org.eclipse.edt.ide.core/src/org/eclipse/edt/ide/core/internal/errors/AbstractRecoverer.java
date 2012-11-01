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

import java.util.Stack;

import org.eclipse.edt.ide.core.internal.model.document.EGLNodeNameUtility;


/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class AbstractRecoverer {
	public static final int SUCCESS_DISTANCE = 3;
	
	protected ErrorGrammar grammar = ErrorGrammar.getInstance();

	public abstract int recoverDistance(ParseStack stack, TokenStream tokenStream);
	public abstract void recover(ParseStack stack, TokenStream tokenStream);
	public abstract void errorMessage(ParseStack stack, TokenStream tokenStream, TerminalNode errorTerminal);

	// TODO Move grammatical information into Grammar file
	protected static int[] IMPORTANT_TERMINALS = {
		ErrorNodeTypes.RECORD,
		ErrorNodeTypes.PROGRAM,
		ErrorNodeTypes.FUNCTION,
		ErrorNodeTypes.RPAREN,
		ErrorNodeTypes.END
	};
	
	protected static int[] UNDELETABLE_TERMINALS = {
		ErrorNodeTypes.RECORD,
		ErrorNodeTypes.PROGRAM,
		ErrorNodeTypes.FUNCTION,
		ErrorNodeTypes.EOF
	};
	
	protected static int[] SCOPE_CLOSERS = {
		ErrorNodeTypes.RPAREN,
		ErrorNodeTypes.END
	};

	public static boolean isUndeletableTerminal(TerminalNode terminalNode) {
		return isInList(terminalNode.terminalType, UNDELETABLE_TERMINALS);
		
	}

	private static boolean isInList(int number, int[] list) {
		for (int i = 0; i < list.length; i++) {
			if(list[i] == number) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isImportantTerminal(TerminalNode terminalNode) {
		return isInList(terminalNode.terminalType, IMPORTANT_TERMINALS);
	}
	
	protected static boolean isScopeCloser(TerminalNode terminalNode) {
		return isInList(terminalNode.terminalType, SCOPE_CLOSERS);
	}
	
	protected static boolean isScopeCloser(int terminalType) {
		return isInList(terminalType, SCOPE_CLOSERS);
	}
	
	protected int tryParseAhead(ParseStack stack, TokenStream tokenStream) {
		// See how far we can parse
		int shifted = 0;
		while(true) {
			int action = grammar.getTerminalAction(stack.getCurrentState(), tokenStream.lookAhead());
			
			if(action > 0) {
				// If we are shifting an important terminal, we are done
				if(isImportantTerminal(tokenStream.lookAhead())) {
					return Integer.MAX_VALUE;
				}
				
				// Shift
				stack.shift(tokenStream.lookAhead());
				tokenStream.shift();
				shifted++;
			}
			else if(action < 0) {
				// Reduce
				int ruleNumber = -(action) - 1;
				
				// If we are reducing by the start rule, we return "infinity"
				if(ruleNumber == 0) {
					return Integer.MAX_VALUE;
				}
				stack.reduce(ruleNumber);
				
//				// If we are creating a "significant" node, we return infinity
//				int nonTerminal = grammar.getLHS(ruleNumber);
//				if(Character.isUpperCase(ErrorNodeTypesConverter.getNonterminalTokenForInt(nonTerminal).charAt(0))) {
//					return Integer.MAX_VALUE;
//				}
			}
			else {
				return shifted;
			}
		}
	}
	
	protected boolean processTerminal(ParseStack stack, TerminalNode terminalNode) {
		// We should not try to process EOF
		if(terminalNode.terminalType == ErrorNodeTypes.EOF) {
			throw new IllegalArgumentException();
		}
		
		// First perform all reductions possible
		performAllReductions(stack, terminalNode);
		
		// Now see whether we can shift this terminal
		int action = grammar.getTerminalAction(stack.getCurrentState(), terminalNode);

		if(action > 0) {
			// We can shift
			stack.shift(terminalNode);
			return true;		
		}
		else {
			// We cannot shift
			return false;
		}
	}
	
	protected void performAllReductions(ParseStack stack, TerminalNode terminalNode) {
		while(true) {
			int action = grammar.getTerminalAction(stack.getCurrentState(), terminalNode);
			
			if(action < 0) {
				// We shall never reduce by the start production because we are will not shift 
				// by EOF
				int ruleNumber = -(action) - 1;
				stack.reduce(ruleNumber);
			}
			else {
				return;
			}
		}
	}

	/**
	 * EGL Normally, the highest symbol of a given terminal is the nonterminal closest to the root of 
	 * the tree that chain-derives the terminal.
	 * 
	 * This is not optimal for our use.  For example, the rule of a primitive type is below:
	 * 
	 *     primitiveType -> PRIMITIVE precision (where prevision is nullable)
	 * 
	 * If the user have a test case like "DataItem MyDataItem cha End", we would like to say that 
	 * "cha" is not a valid primitive type.
	 * 
	 * As a result, we are extending the meaning of the highest symbol of a given terminal to the 
	 * nonterminal closest to the root that have the terminal as its sole yield.
	 */	
	protected ParseNode getHighestSymbol(ParseStack stack, TerminalNode lookahead) {
		int poppable = 1;
		while(true) {
			int action = grammar.getTerminalAction(stack.getCurrentState(), lookahead);
			
			if(action > 0) {
				// We are about to shift, so the highest node is on the top of the stack
				break;
			}
			else if(action < 0) {
				int ruleNumber = -(action) - 1;
				
				int handleSize = grammar.getHandleSize(ruleNumber);  

				if(handleSize > poppable) break;

				stack.reduce(ruleNumber);
				poppable = poppable - handleSize + 1;
			}
			else {
				// Error situations should not happen
				break;
			}
		}

		stack.deleteContext(poppable - 1);		
		
		return stack.getTopOfStackNode();
	}

	protected String getHighestSymbolName(ParseStack stack, TerminalNode lookahead) {
		ParseNode highestNode = getHighestSymbol(stack, lookahead);
		
		if(highestNode.isTerminal()) {
			TerminalNode terminalNode = (TerminalNode) highestNode;
			return EGLNodeNameUtility.getTerminalName(terminalNode.terminalType);
		}
		else {
			NonTerminalNode nonTerminalNode = (NonTerminalNode) highestNode;
			return EGLNodeNameUtility.getNonterminalName(nonTerminalNode.nonTerminalType);
		}
	}
	
	protected TerminalNode findFirstNonWSTerminal(ParseNode[] nodes) {
		Stack nodeStack = new Stack();
		
		// Delete the contexts except for the last one
		for (int i = nodes.length - 1; i >= 0; i--) {
			nodeStack.push(nodes[i]);
		}
		
		// Find the first terminal among these entries
		while(!nodeStack.isEmpty()) {
			ParseNode parseNode = (ParseNode) nodeStack.pop();

			if(parseNode.isTerminal()) {
				if(parseNode.isWhiteSpace()) {
					continue;
				}
				else {
					return (TerminalNode) parseNode;
				}
			}

			// We know now that parseNode is a non terminal			
			NonTerminalNode nonTerminalNode = (NonTerminalNode) parseNode;
			if(nonTerminalNode.children != null) {
				for (int i = nonTerminalNode.children.length - 1; i >= 0; i--) {
					nodeStack.push(nonTerminalNode.children[i]);
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * This method chains up the nodes by reducing them into a binary tree of wsPairs
	 * TODO this method perhaps shouldn't be here
	 * @param nodes
	 */
	protected ParseNode chainNodes(ParseNode[] nodes) {
		int size = nodes.length;
		while(size > 1) {
			// Pair up all the whitespaces node
			for (int i = 0; i < size / 2; i++) {
				nodes[i] = new NonTerminalNode(
					ErrorNodeTypes.wsPair,
					new ParseNode[] {nodes[i*2], nodes[i*2+1]}
				);
			}
			
			// Deal with the odd one out and set up the new size
			if(size % 2 > 0) {
				nodes[size / 2] = nodes[size - 1];
				size = size / 2 + 1;
			}
			else {
				size = size / 2;
			}
		}
		
		// Return null if the argument array is empty
		return size > 0 ? nodes[0] : null;
	}
}
