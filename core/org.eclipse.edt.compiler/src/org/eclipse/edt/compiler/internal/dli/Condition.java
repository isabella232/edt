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
public class Condition extends AbstractBooleanExpression implements ICondition {

    private IName fieldName;
    private String operator;
    private IValue value;
    
    /**
     * @param fieldName
     * @param operator
     * @param value
     * @param startOffset
     * @param endOffset
     */
    public Condition(IName fieldName, String operator, IValue value, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.fieldName = fieldName;
        this.operator = operator;
        this.value = value;
        
        fieldName.setParent(this);
        value.setParent(this);
    }
    
    /**
     * @return Returns the fieldName.
     */
    public IName getFieldName() {
        return fieldName;
    }

    /**
     * @return Returns the operator.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @return Returns the value.
     */
    public IValue getValue() {
        return value;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IBooleanExpression#isCondition()
     */
    public boolean isCondition() {
        return true;
    }
    
}
