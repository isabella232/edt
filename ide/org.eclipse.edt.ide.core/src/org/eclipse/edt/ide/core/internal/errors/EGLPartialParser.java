/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

public class EGLPartialParser {
	// The grammar
	private ErrorGrammar grammar = ErrorGrammar.getInstance(); 
	
	// The input
	private TokenStream tokenStream;

	// The parse stack
	private ParseStack stack;
	
	public ParseStack parse(TokenStream stream) {
		ErrorMarkerCollector.instance.reset();
		
		tokenStream = stream;
		stack = new ParseStack();

		while(!tokenStream.isDone()) {
			int action = grammar.getTerminalAction(stack.getCurrentState(), tokenStream.lookAhead());
			
			if(action > 0) {
				// Before we perform any actions, we have to chain up the whitespaces
				stack.connect(chainNodes(tokenStream.getUnprocessedTerminals()));
				
				// Shift
				int shiftToState = action - 1;
				stack.shift(tokenStream.lookAhead());
				tokenStream.shift();
			}
			else if(action < 0) {
				// Before we perform any actions, we have to chain up the whitespaces
				stack.connect(chainNodes(tokenStream.getUnprocessedTerminals()));

				// Reduce
				int ruleNumber = -(action) - 1;
				
				stack.reduce(ruleNumber);

				// If we are reducing by the start rule, we are done
				if(ruleNumber == 0) {
					break;
				}
			}
			else {
				// Error
				recover();
			}
		}
		
		return stack;
	}
	
	private void recover() {
		SyntaxErrorRecoverer recoverer = new SyntaxErrorRecoverer(stack, tokenStream);
		recoverer.recover();
	}
	
	/**
	 * This method chains up the nodes by reducing them into a binary tree of wsPairs
	 * @param nodes
	 */
	private ParseNode chainNodes(ParseNode[] nodes) {
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
