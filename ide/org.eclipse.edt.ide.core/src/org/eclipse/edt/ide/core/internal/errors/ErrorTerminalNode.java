/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

public class ErrorTerminalNode extends TerminalNode implements ErrorNode {
	private String errorMessage;
	private ParseNode errorTerminal;
	
	public ErrorTerminalNode(int terminalType) {
		super(terminalType, null);
		errorMessage = getDefaultErrorMessage();
	}
	
	public ErrorTerminalNode(int terminalType, ParseNode errorTerminal) {
		super(terminalType, null);
		this.errorTerminal = errorTerminal;  
		errorMessage = getDefaultErrorMessage();
	}
	
	public String toString() {
		return "<" + EGLNodeNameUtility.getTerminalName(terminalType) + ">";
	}
	
	public boolean isError() {
		return true;
	}

	private String getDefaultErrorMessage() {
		String text = getText().trim() + " is unexpected";
		
		if(errorTerminal == null) {
			// This represents an insertion
			return "Missing " + EGLNodeNameUtility.getTerminalName(terminalType);
		}
		else if(terminalType == ErrorNodeTypes.WS) {
			// This represents a deletion
			return text; 	
		}
		else {
			// This represents a substitution
			return text + ", expecting " + EGLNodeNameUtility.getTerminalName(terminalType) + " instead";
		}
	}

	public void setErrorMessage(String string) {
		errorMessage = string;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public String getText() {
		return errorTerminal == null ? "" : errorTerminal.getText();
	}
}
