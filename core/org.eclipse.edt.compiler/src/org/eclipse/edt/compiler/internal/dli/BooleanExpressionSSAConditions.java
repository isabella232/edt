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
public class BooleanExpressionSSAConditions extends AbstractSSAConditions implements IBooleanExpressionSSAConditions {

    private IBooleanExpression booleanExpression;
    
    /**
     * @param booleanExpressions
     */
    public BooleanExpressionSSAConditions(IBooleanExpression booleanExpressions, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.booleanExpression = booleanExpressions;
        booleanExpressions.setParent(this);
    }
    
    /**
     * @return Returns the booleanExpression.
     */
    public IBooleanExpression getBooleanExpression() {
        return booleanExpression;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.ISSAConditions#isBooleanExpressionSSAConditions()
     */
    public boolean isBooleanExpressionSSAConditions() {
        return true;
    }

}
