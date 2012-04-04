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
package org.eclipse.edt.compiler.internal.core.builder;

/**
 * @author winghong
 */
public class Problem {

    private int startOffset;
    private int endOffset;
    private int severity;
    private int problemKind;
    private String[] inserts;

    public Problem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
        super();
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.severity = severity;
        this.problemKind = problemKind;
        this.inserts = inserts;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public int getProblemKind() {
        return problemKind;
    }
    
    public String[] getInserts() {
    	return inserts;
    }

    public int getSeverity() {
        return severity;
    }

    public int getStartOffset() {
        return startOffset;
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
