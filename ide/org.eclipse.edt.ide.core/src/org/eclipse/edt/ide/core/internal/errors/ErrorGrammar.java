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

import java.util.ArrayList;

/**
 * @author winghong
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ErrorGrammar extends ErrorBaseParser {
	private ErrorGrammar() {
		super();
		production_tab = production_table();
		action_tab = action_table();
		reduce_tab = reduce_table();
	}
	
	private static ErrorGrammar instance;
	
	public static ErrorGrammar getInstance() {
		if(instance == null) {
			instance = new ErrorGrammar();
			instance.populateTerminalCandidatesTable();
		}
		return instance;
	}

	public int getTerminalAction(int parseState, TerminalNode terminalNode) {
		return get_action(parseState, terminalNode.terminalType);
	}
	
	public int getTerminalAction(int parseState, int terminalType) {
		return get_action(parseState, terminalType);
	}
	
	public int getGotoState(int parseState, NonTerminalNode nonTerminalNode) {
		return get_reduce(parseState, nonTerminalNode.nonTerminalType);
	}

	public int getHandleSize(int ruleNumber) {
		return production_tab[ruleNumber][1];
	}
	
	public int getLHS(int ruleNumber) {
		return production_tab[ruleNumber][0];
	}
	
	public int getStartState() {
		return start_state();
	}
	
	// Information about terminal candidates
	private short[][] terminalCandidates;

	private void populateTerminalCandidatesTable() {
		terminalCandidates = new short[_action_table.length][];
		for (int i = 0; i < _action_table.length; i++) {
			ArrayList terminalList = new ArrayList(_action_table[i].length);
			for (int j = 0; j < _action_table[i].length; j+=2) {
				if(_action_table[i][j] > 1 && _action_table[i][j+1] != 0) { // EOF = 0 and erorr = 1
					terminalList.add(new Short(_action_table[i][j]));
				}
			}
			terminalCandidates[i] = new short[terminalList.size()];
			for (int j = 0; j < terminalList.size(); j++) {
				terminalCandidates[i][j] = ((Short)terminalList.get(j)).shortValue();
			}
		}
	}
	
	public short[] getTerminalCandidates(int state) {
		return terminalCandidates[state];
	}
	
	// Information about nonterminal candidates
	public short[] getNonTerminalCandidates(int state) {
		if(state == 0) return new short[] { ErrorNodeTypes.packageDeclarationOpt };
		
		short[] result = new short[_reduce_table[state].length / 2 - 1];
		for (int i = 0; i < result.length; i++) {
			result[i] = _reduce_table[state][i*2];
		}
		return result;
	}
	
	// TODO The following are experimental:
	public boolean isLRZeroReduceState(int state) {
		return getLRZeroReduceRule(state) >= 0;
	}
	
	/**
	 * @param state
	 * @return -1 if the state is not an LRZeroReduceState
	 */
	public int getLRZeroReduceRule(int state) {
		// A state is an LR(0) state if all REDUCE actions reduces to the same rule
		// We are only concerning ourselves with REDUCE actions so that more default
		// reductions can occur
		int position = 1;
		int maxPosition = action_tab[state].length - 2;

		// Find position of first reduce action
		while(position < maxPosition) {
			int action = action_tab[state][position];
			if(action < 0) {
				break;
			}
			position += 2;
		}
		
		if(position < maxPosition) {
			// Found our reduce action 
			int reduceAction = action_tab[state][position];
				
			// Check to see whether the remaining reduce actions all reduces by the same rule
			for(position += 2; position < maxPosition; position += 2) {
				int action = action_tab[state][position];
				if(action < 0 && action != reduceAction) {
					return -1;
				}
			}
			
			return -reduceAction - 1; 
		}
		else {
			// Didn't find any reduce actions	
			return -1;
		}
	}
}
