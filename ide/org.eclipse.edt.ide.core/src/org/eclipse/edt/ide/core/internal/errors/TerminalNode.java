/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TerminalNode extends ParseNode {
	
	public int terminalType;
	public String text;
	public int line;
	public int column;
	public int offset;
	
	public TerminalNode(int terminalType, String text) {
		this.terminalType = terminalType;
		this.text = text;
	}
	
	public TerminalNode(int terminalType, String text, int line, int column) {
		this.terminalType = terminalType;
		this.text = text;
		this.line = line;
		this.column = column;
	}
	
	public TerminalNode(int terminalType, String text, int line, int column, int offset) {
		this.terminalType = terminalType;
		this.text = text;
		this.line = line;
		this.column = column;
		this.offset = offset;
	}

	public boolean isNonTerminal() {
		return false;
	}

	public boolean isTerminal() {
		return true;
	}
	
	public String toString() {
//		return text;
		String terminalName = EGLNodeNameUtility.getTerminalName(terminalType);
		if(terminalName.equalsIgnoreCase(text)) {
			return terminalName;	
		}
		else {
			return terminalName + " " + text;
		}
	}

	public boolean isWhiteSpace() {
		switch(terminalType) {
			case ErrorNodeTypes.WS:
//			case ErrorNodeTypes.SLCOMMENT:
//			case ErrorNodeTypes.BKCOMMENT:
//			case ErrorNodeTypes.LINE:
				return true;
			default:
				return false;
		}
	}

	public boolean isError() {
		return false;
	}

	public String getText() {
		return text;
	}
}
