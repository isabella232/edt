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

import org.eclipse.edt.compiler.core.ast.Expression;

/**
 * @author winghong
 */
public class HostVariableValue extends AbstractValue implements IHostVariableValue {

    private String dataAccessString;
    private Expression expression;
    
    /**
     * @param dataAccessString
     * @param startOffset
     * @param endOffset
     */
    public HostVariableValue(String dataAccessString, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.dataAccessString = dataAccessString;
    }
    
    public String getText() {
    	return dataAccessString;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IValue#isHostVariableValue()
     */
    public boolean isHostVariableValue() {
        return true;
    }
    
    public Expression getExpression() {
        return expression;
    }
    
    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
