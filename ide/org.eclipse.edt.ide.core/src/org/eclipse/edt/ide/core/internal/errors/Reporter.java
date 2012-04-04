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

import org.eclipse.edt.ide.core.internal.model.document.EGLNodeNameUtility;

/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Reporter {
	private static Reporter instance = new Reporter();
	public boolean reportRecoveryTrials = false;
	public boolean reportRecovery = false;
	public boolean reportParseProgress = false; 
	
	private Reporter() {
		super();
	}
	
	
	public static Reporter getInstance() {
		return instance;
	}

	public void shift(ParseNode node, int state) {
		if(!reportParseProgress) return;
		
		if(node.isTerminal()) {
			TerminalNode terminalNode = (TerminalNode) node;
			System.err.println("Shift " + terminalNode.text + " into state: " + state);
		}
		else if(node.isNonTerminal()){
			NonTerminalNode nonTerminalNode = (NonTerminalNode) node;
			System.err.println("Shift " + EGLNodeNameUtility.getNonterminalName(nonTerminalNode.nonTerminalType) + " into state: " + state);
		}
	}
	
	public void reduce(int ruleNumber) {
		if(!reportParseProgress) return;
		System.err.println("Reduce using rule: " + ruleNumber);
	}
	
	public void unexpectedTerminalDistance(int distance) {
		if(!reportRecoveryTrials) return;
		System.out.println("Unexpected Terminal: " + distance); 
	}
	
	public void missingTerminalDistance(int terminal, int distance) {
		if(!reportRecoveryTrials) return;
		System.out.println("Missing terminal: " + EGLNodeNameUtility.getTerminalName(terminal) + " distance :" + distance);
	}
	
	public void invalidTerminalDistance(int terminal, int distance) {
		if(!reportRecoveryTrials) return;
		System.out.println("Invalid terminal: " + EGLNodeNameUtility.getTerminalName(terminal) + " distance :" + distance);
	}
	
	public void missingNonTerminalDistance(int nonTerminal, int distance) {
		if(!reportRecoveryTrials) return;
		System.out.println("Missing nonTerminal: " + EGLNodeNameUtility.getNonterminalName(nonTerminal));
	}
	
	public void recoverMissingTerminal(int terminal) {
		if(!reportRecovery) return;
		System.out.println("Missing " + EGLNodeNameUtility.getTerminalName(terminal));
	}

	public void recoverInvalidTerminal(String source, int terminal) {
		if(!reportRecovery) return;
		System.out.println('"' + source + "\" is not a valid "+ EGLNodeNameUtility.getTerminalName(terminal));
	}
	
	public void recoverUnexpectedTerminal(String source) {
		if(!reportRecovery) return;
		System.out.println('"' + source + "\" is unexpected");
	}
	
	public void recoverMissingNonTerminal(int nonTerminal) {
		if(!reportRecovery) return;
		System.out.println("Missing " + EGLNodeNameUtility.getNonterminalName(nonTerminal)); 
	}
}
