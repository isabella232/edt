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

import org.eclipse.edt.ide.core.internal.model.document.EGLNodeNameUtility;

public class ErrorNonTerminalNode extends NonTerminalNode implements ErrorNode {
	private String errorMessage;
	
	public ErrorNonTerminalNode(int nonterminalType) {
		super(nonterminalType, null);
		errorMessage = getDefaultErrorMessage();
	}
	
	public ErrorNonTerminalNode(int nonterminalType, ParseNode errorSymbols) {
		super(nonterminalType, new ParseNode[] {errorSymbols});
		errorMessage = getDefaultErrorMessage();
	}
	
	public String toString() {
		return "<" + EGLNodeNameUtility.getNonterminalName(nonTerminalType) + ">";
	}
	
	public boolean isError() {
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	private String getDefaultErrorMessage() {
		if(children == null) {
			// This represents an insertion
			return "Missing " + EGLNodeNameUtility.getNonterminalName(nonTerminalType);
		}
		else if(nonTerminalType == ErrorNodeTypes.ErrorNode) {
			// This represents a deletion
			return "The phrase " + ParseTreePrinter.format(getText().trim()) + " is unexpected";	
		}
		else {
			// This represents a substitution
			return "The phrase " + ParseTreePrinter.format(getText().trim()) + " is not a valid "
			+ EGLNodeNameUtility.getNonterminalName(nonTerminalType);
		}
	}

	public void setErrorMessage(String string) {
		errorMessage = string;
	}
}
