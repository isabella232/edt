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

import java.util.Iterator;
import java.util.List;

/**
 * @author winghong
 */
public class Statement extends Node implements IStatement {
    
    private String dliFunction;
    private List segmentSearchArguments;
    
    /**
     * @param dliFunctionCode
     * @param segmentSearchArguments
     * @param pcbClause
     * @param startOffset
     * @param endOffset
     */
    public Statement(String dliFunctionCode, List segmentSearchArguments, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.dliFunction = dliFunctionCode;
        this.segmentSearchArguments = segmentSearchArguments;
        
        for(Iterator iter = segmentSearchArguments.iterator(); iter.hasNext();) {
            INode node = (INode) iter.next();
            node.setParent(this);
        }
    }
    
    /**
     * @return Returns the dliFunction
     */
    public String getDLIFunction() {
        return dliFunction;
    }
    
    /**
     * @return Returns the segmentSearchArguments.
     */
    public List getSegmentSearchArguments() {
        return segmentSearchArguments;
    }
}
