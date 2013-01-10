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


/**
 * @author winghong
 */
public abstract class Type extends Node {
    
    public static final int NAMETYPE = 0;
    public static final int ARRAYTYPE = 1;
    
    public Type(int startOffset, int endOffset) {
        super(startOffset, endOffset);
    }

    public boolean isNameType() {
        return false;
    }
    
    public boolean isArrayType() {
        return false;
    }
    
    public abstract int getKind();
    
    public abstract org.eclipse.edt.mof.egl.Type resolveType();
    
    public abstract String getCanonicalName();
    
    protected abstract Object clone() throws CloneNotSupportedException;
    
    public abstract Type getBaseType();
}
