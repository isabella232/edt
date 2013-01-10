/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.core.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author winghong
 */
public class Node implements Cloneable{

    private int offset;
    private int length;
    
    private Node parent;
    
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
    public Node getParent() {
        return parent;
    }
    
    /**
     * @param parent The parent to set.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    protected List setParent(List children) {
        for(Iterator iter = children.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            node.setParent(this);
        }
        return children;
    }

    /**
     * @param visitor
     */
    public void accept(IASTVisitor visitor) {
        
    }    
    
    protected static void acceptChildren(IASTVisitor visitor, List children) {
        for(Iterator iter = children.iterator(); iter.hasNext();) {
            ((Node) iter.next()).accept(visitor);
        }
    }
    
    protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
    
    protected ArrayList cloneList(List oldList) throws CloneNotSupportedException {
    	
    	if (oldList == null) {
    		return null;
    	}
    	
		ArrayList newList = new ArrayList();
		for (Iterator iter = oldList.iterator(); iter.hasNext();) {
			newList.add(((Node)iter.next()).clone());
		}
		return newList;
	}
}
