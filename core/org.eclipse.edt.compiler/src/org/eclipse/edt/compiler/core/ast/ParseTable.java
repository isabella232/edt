/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.core.ast;

/**
 * @author winghong
 */
public abstract class ParseTable {
    
    public static int[] SCOPE_CLOSERS = new int[] {
        NodeTypes.RPAREN,
        NodeTypes.RBRACKET,
        NodeTypes.RCURLY,
        NodeTypes.END
    };
    
    public static int[][] LANGUAGE_CONSTRUCTS = new int[][] {
        new int[] { NodeTypes.classContent, NodeTypes.classContent_plus, NodeTypes.classContent_star },
        new int[] { NodeTypes.handlerContent, NodeTypes.handlerContent_plus, NodeTypes.handlerContent_star },
        new int[] { NodeTypes.functionParameter, NodeTypes.functionParameter_plus, NodeTypes.functionParameter_star },
        new int[] { NodeTypes.importDecl, NodeTypes.importDecl_plus, NodeTypes.importDecl_star },
        new int[] { NodeTypes.interfaceContent, NodeTypes.interfaceContent_plus, NodeTypes.interfaceContent_star },
        new int[] { NodeTypes.externalTypeContent, NodeTypes.externalTypeContent_plus, NodeTypes.externalTypeContent_star },
        new int[] { NodeTypes.part, NodeTypes.part_plus, NodeTypes.part_star },
        new int[] { NodeTypes.programParameter, NodeTypes.programParameter_plus, NodeTypes.programParameter_star },
        new int[] { NodeTypes.setting, NodeTypes.setting_plus, NodeTypes.setting_star },
        new int[] { NodeTypes.stmt, NodeTypes.stmt_plus, NodeTypes.stmt_star },
        new int[] { NodeTypes.structureContent, NodeTypes.structureContent_plus, NodeTypes.structureContent_star },
        // TODO should other statement kind of stuff be here too?
        new int[] { NodeTypes.eventBlock, NodeTypes.eventBlock_plus, NodeTypes.eventBlock_star },
    };

    protected static short[][] production_tab;
    protected static short[][] action_tab;
    protected static short[][] reduce_tab;
    
    protected static short[][] terminalCandidates;
    protected static short[][] nonTerminalCandidates;

    static {
        Parser parser = new Parser();
        production_tab = parser.production_table();
        action_tab = parser.action_table();
        reduce_tab = parser.reduce_table();
        
        initTerminalCandidates();
        initNonTerminalCandidates();
    }
    
    protected boolean isConstructPlus(int nonTerminalType) {
        for(int i = 0; i < LANGUAGE_CONSTRUCTS.length; i++) {
            if(nonTerminalType == LANGUAGE_CONSTRUCTS[i][1]) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isConstructStar(int nonTerminalType) {
        for(int i = 0; i < LANGUAGE_CONSTRUCTS.length; i++) {
            if(nonTerminalType == LANGUAGE_CONSTRUCTS[i][2]) {
                return true;
            }
        }
        return false;
    }

    protected static final boolean isScopeCloser(int terminalType) {
        for(int i = 0; i < SCOPE_CLOSERS.length; i++) {
            if(SCOPE_CLOSERS[i] == terminalType) return true;
        }
        return false;
    }
    
    private static final void initTerminalCandidates() {
        terminalCandidates = new short[action_tab.length][];
        for(int i = 0; i < terminalCandidates.length; i++) {
            // Calculate the number of terminal candidates after filtering out scope closers
            int numCandidates = 0;
            
            short[] actions = action_tab[i];
            for(int j = 0; j < actions.length / 2 - 1; j++) {
                if(!isScopeCloser(actions[j * 2])) {
                    numCandidates++;
                }
            }
            
            // Put it into the candidates array
            int index = 0;
            short[] candidates = new short[numCandidates];
            for(int j = 0; j < actions.length / 2 - 1; j++) {
                if(!isScopeCloser(actions[j * 2])) {
                    candidates[index++] = actions[j * 2];
                }
            }
            
            // Finish this particular state 
            terminalCandidates[i] = candidates;
        }
    }
    
    protected final void printTerminalCandidates(int state) {
        short[] candidates = terminalCandidates[state];
        for(int i = 0; i < candidates.length; i++) {
            String terminalName = NodeNameUtility.getTerminalName(candidates[i]);
            if(get_action(state, candidates[i]) > 0) {
                System.out.println("Shift on " + terminalName);
            }
            else {
                System.out.println("Reduce on " + terminalName);
            }
        }
    }
    
    protected final short[] getTerminalCandidates(int state) {
        return terminalCandidates[state];
    }
    
    private static final void initNonTerminalCandidates() {
        nonTerminalCandidates = new short[reduce_tab.length][];
        for(int i = 0; i < nonTerminalCandidates.length; i++) {
            short[] gotos = reduce_tab[i];
            short[] candidates = new short[gotos.length / 2 - 1];
            for(int j = 0; j < candidates.length; j++) {
                candidates[j] = gotos[j * 2];
            }
            nonTerminalCandidates[i] = candidates;
        }
    }
    
    protected final void printNonTermianlCandidates(int state) {
        short[] candidates = nonTerminalCandidates[state];
        for(int i = 0; i < candidates.length; i++) {
            String nonTerminalName = NodeNameUtility.getNonterminalName(candidates[i]);
            System.out.println("Goto on " + nonTerminalName);
        }
    }
    
    protected final short[] getNonTerminalCandidates(int state) {
        return nonTerminalCandidates[state];
    }

    protected final short get_action(int state, int sym) {
        short tag;
        int first, last, probe;
        short[] row = action_tab[state];

        /* linear search if we are < 10 entries */
        if (row.length < 20)
            for (probe = 0; probe < row.length; probe++) {
                /* is this entry labeled with our Symbol or the default? */
                tag = row[probe++];
                if (tag == sym || tag == -1) {
                    /* return the next entry */
                    return row[probe];
                }
            }
        /* otherwise binary search */
        else {
            first = 0;
            last = (row.length - 1) / 2 - 1; /* leave out trailing default entry */
            while (first <= last) {
                probe = (first + last) / 2;
                if (sym == row[probe * 2])
                    return row[probe * 2 + 1];
                else if (sym > row[probe * 2])
                    first = probe + 1;
                else
                    last = probe - 1;
            }

            /* not found, use the default at the end */
            return row[row.length - 1];
        }

        /* shouldn't happened, but if we run off the end we return the 
        default (error == 0) */
        return 0;
    }

    protected final short get_reduce(int state, int sym) {
        short tag;
        short[] row = reduce_tab[state];

        /* if we have a null row we go with the default */
        if (row == null)
            return -1;

        for (int probe = 0; probe < row.length; probe++) {
            /* is this entry labeled with our Symbol or the default? */
            tag = row[probe++];
            if (tag == sym || tag == -1) {
                /* return the next entry */
                return row[probe];
            }
        }
        /* if we run off the end we return the default (error == -1) */
        return -1;
    }
    
}
