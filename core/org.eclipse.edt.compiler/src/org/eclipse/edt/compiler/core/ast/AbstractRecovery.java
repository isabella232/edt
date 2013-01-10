/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.util.Stack;

import java_cup.runtime.Symbol;



/**
 * @author winghong
 */
public abstract class AbstractRecovery {
    
    protected ParseStack errorStack;
    protected ITokenStream tokenStream;
    
    protected Stack realStack;
    
    protected ISyntaxErrorRequestor problemRequestor;
    
    public AbstractRecovery(ParseStack errorStack, Stack realStack, ITokenStream tokenStream, ISyntaxErrorRequestor problemRequestor) {
        super();
        
        this.errorStack = errorStack;
        this.tokenStream = tokenStream;
        
        this.realStack = realStack;
        
        this.problemRequestor = problemRequestor;
        
        performTrial();
    }
    
    protected Symbol getLastRealSymbol(Stack realStack2) {
		for(int i = realStack2.size()-1; i >= 0; i--) {
			Symbol next = (Symbol) realStack2.get(i);
			if(next.left != next.right && next.left >= 0 && next.right >= 0) {
				return next;
			}
		}
		return (Symbol) realStack2.peek();
	}

    protected abstract void performTrial();
    
    public abstract float getMisspellingIndex();
    
    public abstract int getParseCheckDistance();
    
    public abstract int performRecovery();
}
