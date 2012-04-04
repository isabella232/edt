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
public class Terminal extends StackSymbol {
    
    public Terminal(int terminalType, int left, int right, int parseState) {
        super(terminalType, left, right, parseState);
    }

    public boolean isTerminal() {
        return true;
    }

    public boolean isNonTerminal() {
        return false;
    }
    
    public String toString() {
        return NodeNameUtility.getTerminalName(symbolType);
    }

}
