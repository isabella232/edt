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
public class SimpleName extends AbstractName implements IName {

    /**
     * @param name
     * @param startOffset
     * @param endOffset
     */
    public SimpleName(String name, int startOffset, int endOffset) {
        super(name, startOffset, endOffset);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IName#isSimpleName()
     */
    public boolean isSimpleName() {
        return true;
    }
    
}
