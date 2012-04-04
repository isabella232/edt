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
public class NonTerminal extends StackSymbol {

    public NonTerminal(int nonTerminalType, int left, int right, int parseState) {
        super(nonTerminalType, left, right, parseState);
    }

    public boolean isTerminal() {
        return false;
    }

    public boolean isNonTerminal() {
        return true;
    }
    
    public String toString() {
        return NodeNameUtility.getNonterminalName(symbolType);
    }

}
