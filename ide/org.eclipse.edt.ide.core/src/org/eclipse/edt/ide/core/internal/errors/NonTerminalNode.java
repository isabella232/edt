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

public class NonTerminalNode extends ParseNode {
	public int nonTerminalType;
	public ParseNode[] children; // For an epislon nonterminla, children is null
	public int ruleNumber;

	/**
	 * @param nonterminalType
	 * @param children
	 *            - null if the node is an epsilon node
	 */
	public NonTerminalNode(int nonterminalType, ParseNode[] children) {
		this.nonTerminalType = nonterminalType;
		this.children = children;
	}

	public boolean isTerminal() {
		return false;
	}

	public boolean isNonTerminal() {
		return true;
	}

	public String toString() {
		if (children == null) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < children.length; i++) {
			String string = children[i].toString();
			buffer.append(string);
			if (string.length() > 0 && i < children.length - 1) {
				buffer.append(' ');
			}
		}
		return buffer.toString();
	}

	public boolean isWhiteSpace() {
		return nonTerminalType == ErrorNodeTypes.connector;
	}

	public boolean isError() {
		// Special case for connector nodes, connector nodes are considered in
		// error if their
		// important child is in error
		return nonTerminalType == ErrorNodeTypes.connector && children != null
				&& children[0].isError();
	}

	public String getText() {
		if (children == null) {
			return "";
		} else {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < children.length; i++) {
				buffer.append(children[i].getText());
			}
			return buffer.toString();
		}
	}
}
