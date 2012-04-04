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
public abstract class AbstractName extends Node implements IName {

    private String name;
    
    /**
     * @param name
     * @param startOffset
     * @param endOffset
     */
    public AbstractName(String name, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.name = name;
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IName#isQuotedName()
     */
    public boolean isQuotedName() {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IName#isSimpleName()
     */
    public boolean isSimpleName() {
        return false;
    }
}
