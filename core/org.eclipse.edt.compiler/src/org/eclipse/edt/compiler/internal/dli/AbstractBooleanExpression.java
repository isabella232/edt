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
package org.eclipse.edt.compiler.internal.dli;

/**
 * @author winghong
 */
public abstract class AbstractBooleanExpression extends Node implements IBooleanExpression {
    
    /**
     * @param startOffset
     * @param endOffset
     */
    public AbstractBooleanExpression(int startOffset, int endOffset) {
        super(startOffset, endOffset);
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IBooleanExpression#isBooleanOperatorExpression()
     */
    public boolean isBooleanOperatorExpression() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IBooleanExpression#isCondition()
     */
    public boolean isCondition() {
        return false;
    }

}
