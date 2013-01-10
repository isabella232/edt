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

import org.eclipse.edt.compiler.core.ast.NodeNameUtility;

/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ParseTreePrinter {
	private static final String INDENT = "  ";

	public static void printError(ParseNode node) {
		if(node.isError()) {
			ErrorNode errorNode = (ErrorNode) node;
			System.out.println(errorNode.getErrorMessage());
		}
		else if(node.isNonTerminal()) {
			NonTerminalNode nonTerminalNode = (NonTerminalNode) node;
			ParseNode[] children = nonTerminalNode.children;
			
			if(children != null) {
				for (int i = 0; i < children.length; i++) {
					printError(children[i]);
				}
			}
		}
	}

	public static void print(ParseNode node) {
		print(node, "");
	}

	private static void print(ParseNode node, String indent) {
		System.out.print(indent);
		if (node.isTerminal()) {
			System.out.println(getTerminalLabel((TerminalNode)node) + " " + getTerminalText((TerminalNode) node));
		} else {
			NonTerminalNode nonTerminalNode = (NonTerminalNode) node;
			System.out.println(getNonTerminalLabel(nonTerminalNode));

			ParseNode[] children = nonTerminalNode.children;
			if (children != null) {
				indent = indent + INDENT;
				for (int i = 0; i < children.length; i++) {
					print(children[i], indent);
				}
			}
		}
	}

	public static String getTerminalLabel(TerminalNode terminalNode) {
		String label = NodeNameUtility.getTerminalName(terminalNode.terminalType); 
		return terminalNode.isError() ? "Error " + label : label;  
	}
	
	public static String getTerminalText(TerminalNode terminalNode) {
		return format(terminalNode.getText());
	}
	
	public static String format(String string) {
		StringBuffer buffer = new StringBuffer(string.length() * 2);
		buffer.append('"');
		for (int i = 0; i < string.length(); i++) {
			switch (string.charAt(i)) {
				case '\r' :
					buffer.append("\\r");
					break;
				case '\n' :
					buffer.append("\\n");
					break;
				case '\t' :
					buffer.append("\\t");
					break;
				default :
					buffer.append(string.charAt(i)); 
			}
		}
		buffer.append('"');
		return buffer.toString();
	}
	
	public static String getLabel(ParseNode node) {
		return node.isTerminal() ? getTerminalLabel((TerminalNode) node) : getNonTerminalLabel((NonTerminalNode) node);
	}

	public static String getNonTerminalLabel(NonTerminalNode nonTerminalNode) {
		String label;
		if(nonTerminalNode.isWhiteSpace()) {
			String childLabel = getLabel(nonTerminalNode.children[0]);
			label =  childLabel + "-connector";
		}
		else { 
			label = NodeNameUtility.getNonterminalName(nonTerminalNode.nonTerminalType);
		}
		
		return nonTerminalNode.isError() ? "Error " + label : label;
	}
}
