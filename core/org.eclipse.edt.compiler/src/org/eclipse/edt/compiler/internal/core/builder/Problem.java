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

import java.util.ResourceBundle;

/**
 * @author winghong
 */
public class Problem {

    private int startOffset;
    private int endOffset;
    private int severity;
    private int problemKind;
    private String[] inserts;
    private ResourceBundle bundle;

    public Problem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {
    	this(startOffset, endOffset, severity, problemKind, inserts, DefaultProblemRequestor.RESOURCE_BUNDLE);
    }
    
    public Problem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
        super();
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.severity = severity;
        this.problemKind = problemKind;
        this.inserts = inserts;
        this.bundle = bundle;
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
    
    public ResourceBundle getResourceBundle() {
    	return bundle;
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}

}
