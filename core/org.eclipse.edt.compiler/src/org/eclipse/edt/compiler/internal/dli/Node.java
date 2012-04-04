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
public abstract class Node implements INode {

    private int offset;
    private int length;
    
    private INode parent;
    
    /**
     * @param startOffset
     * @param endOffset
     */
    public Node(int startOffset, int endOffset) {
        super();
        this.offset = startOffset;
        this.length = endOffset - startOffset;
    }
    
    
    /**
     * @return Returns the length.
     */
    public int getLength() {
        return length;
    }
    
    /**
     * @return Returns the offset.
     */
    public int getOffset() {
        return offset;
    }
    
    /**
     * @return Returns the parent.
     */
    public INode getParent() {
        return parent;
    }
    
    /**
     * @param parent The parent to set.
     */
    public void setParent(INode parent) {
        this.parent = parent;
    }
}
