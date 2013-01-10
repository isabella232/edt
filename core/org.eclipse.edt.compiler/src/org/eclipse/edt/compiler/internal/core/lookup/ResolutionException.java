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
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.core.ast.Node;

/**
 * @author winghong
 */
public class ResolutionException extends Exception {

    private static final long serialVersionUID = 3690471424653799990L;

    private int startOffset;
    private int endOffset;
    private int problemKind;
    private String[] inserts;

    public ResolutionException(int startOffset, int endOffset, int problemKind, String[] inserts) {
        super();
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.problemKind = problemKind;
        this.inserts = inserts;
    }
    
    public ResolutionException(int startOffset, int endOffset, int problemKind) {
    	this(startOffset, endOffset, problemKind, new String[0]);
    }

    public ResolutionException(Node astNode, int problemKind, String[] inserts) {
		this(astNode.getOffset(), astNode.getOffset() + astNode.getLength(), problemKind, inserts);
	}
    
    public ResolutionException(Node astNode, int problemKind) {
		this(astNode, problemKind, new String[0]);
	}

    public int getEndOffset() {
        return endOffset;
    }

    public int getStartOffset() {
        return startOffset;
    }
    
    public int getProblemKind() {
		return problemKind;
	}
	
	public String[] getInserts() {
		return inserts;
	}
}
