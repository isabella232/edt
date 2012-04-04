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
public class DLIModel extends Node implements IDLIModel {

    private List statements;
    private List problems;
    
    /**
     * @param statements
     * @param problems
     */
    public DLIModel(List statements, List problems, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.statements = statements;
        this.problems = problems;
        
        for(Iterator iter = statements.iterator(); iter.hasNext();) {
            INode node = (INode) iter.next();
            node.setParent(this);
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IDLIModel#getStatements()
     */
    public List getStatements() {
        return statements;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.pgm.dli.IDLIModel#getProblems()
     */
    public List getProblems() {
        return problems;
    }
}
