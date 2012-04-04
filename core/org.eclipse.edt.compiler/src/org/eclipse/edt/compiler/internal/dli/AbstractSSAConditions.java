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
public abstract class AbstractSSAConditions extends Node implements ISSAConditions {

    /**
     * @param startOffset
     * @param endOffset
     */
    public AbstractSSAConditions(int startOffset, int endOffset) {
        super(startOffset, endOffset);
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.ISSAConditions#isBooleanExpressionSSAConditions()
     */
    public boolean isBooleanExpressionSSAConditions() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.ISSAConditions#isValueExpressionSSAConditions()
     */
    public boolean isValueExpressionSSAConditions() {
        return false;
    }

}
