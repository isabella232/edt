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
public abstract class AbstractLiteralValue extends AbstractValue implements ILiteralValue {

    private String literalValue;
    
    /**
     * @param literalValue
     * @param startOffset
     * @param endOffset
     */
    public AbstractLiteralValue(String literalValue, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.literalValue = literalValue;
    }
    
    /**
     * @return Returns the literalValue.
     */
    public String getLiteralValue() {
        return literalValue;
    }
}
