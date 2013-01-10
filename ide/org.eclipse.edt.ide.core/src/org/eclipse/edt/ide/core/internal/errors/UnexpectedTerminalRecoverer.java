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

public class UnexpectedTerminalRecoverer extends AbstractRecoverer {
	public int recoverDistance(ParseStack stack, TokenStream tokenStream) {
		if(isImportantTerminal(tokenStream.lookAhead())) return 0;
		
		stack = stack.copy();
		tokenStream = tokenStream.copy();
		
		// Ignore the next terminal
		tokenStream.deleteInput(1); 
		
		int distance = tryParseAhead(stack, tokenStream);
		
		Reporter.getInstance().unexpectedTerminalDistance(distance);
		return distance;
	}

	public void recover(ParseStack stack, TokenStream tokenStream) {
		// The tracing stuff will be improved or removed later
		Reporter.getInstance().recoverUnexpectedTerminal(tokenStream.lookAhead().text);
		
		// Report the error
		errorMessage(stack, tokenStream, tokenStream.lookAhead());
		
		// Remove the lookahead that need to be deleted
		tokenStream.deleteInput(1);
		ParseNode[] deletedInputSymbols = tokenStream.getUnprocessedTerminals();
				
		ParseNode deletedNode = chainNodes(deletedInputSymbols);

		// Create an error node to hold on to the deleted symbols
		ParseNode errorNode = new ErrorTerminalNode(ErrorNodeTypes.WS, deletedNode);
		
		// Connect the deleted symbols as whitespace
		stack.connect(errorNode); 
	}
	
	public void errorMessage(ParseStack stack, TokenStream tokenStream, TerminalNode errorTerminal) {
		String message = "\t\"" + errorTerminal.text + "\" is unexpected";
		
		ErrorMarkerCollector.instance.add(errorTerminal.offset, errorTerminal.offset + errorTerminal.text.length(), errorTerminal.line, message);
	}
}
