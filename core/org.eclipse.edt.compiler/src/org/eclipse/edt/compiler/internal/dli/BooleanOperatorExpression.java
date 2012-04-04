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
public class BooleanOperatorExpression extends AbstractBooleanExpression implements IBooleanOperatorExpression {

    private IBooleanExpression leftOperand;
    private String operator;
    private IBooleanExpression rightOperand;
    
    /**
     * @param leftOperand
     * @param operator
     * @param rightOperand
     * @param startOffset
     * @param endOffset
     */
    public BooleanOperatorExpression(IBooleanExpression leftOperand, String operator, IBooleanExpression rightOperand, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
        
        leftOperand.setParent(this);
        rightOperand.setParent(this);
    }
    
    /**
     * @return Returns the leftOperand.
     */
    public IBooleanExpression getLeftOperand() {
        return leftOperand;
    }
    
    /**
     * @return Returns the operator.
     */
    public String getOperator() {
        return operator;
    }
    
    /**
     * @return Returns the rightOperand.
     */
    public IBooleanExpression getRightOperand() {
        return rightOperand;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IBooleanExpression#isBooleanOperatorExpression()
     */
    public boolean isBooleanOperatorExpression() {
        return true;
    }
    
}
