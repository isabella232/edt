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
package org.eclipse.edt.compiler.core.ast;

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;

/**
 * @author winghong
 */
public abstract class PrimitiveType extends Type {
	
	protected Primitive prim;
	protected PrimitiveTypeBinding typeBinding;

    public PrimitiveType(Primitive prim, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        
        this.prim = prim;
    }

    public boolean isPrimitiveType() {
        return true;
    }
    
    public int getKind() {
        return PRIMITIVETYPE;
    }

    public ITypeBinding resolveTypeBinding() {
        if (typeBinding == null) {
           if (hasPrimLength())  {
               if (hasPrimDecimals()) {
                   typeBinding = PrimitiveTypeBinding.getInstance(prim, Integer.parseInt(getPrimLength()), Integer.parseInt(getPrimDecimals()));
               }
               else {
                   typeBinding = PrimitiveTypeBinding.getInstance(prim, Integer.parseInt(getPrimLength()));
               }
           }
           else {
               if (hasPrimPattern()) {
                   typeBinding = PrimitiveTypeBinding.getInstance(prim, getPrimPattern());
               }
               else {
                   typeBinding = PrimitiveTypeBinding.getInstance(prim);
               }
           }
        }
        return typeBinding;
    }
    
    public Primitive getPrimitive() {
    	return prim;
    }
    
    public abstract boolean hasPrimLength();
    public abstract boolean hasPrimDecimals();
    public abstract boolean hasPrimPattern();
    
    public abstract String getPrimLength();
    public abstract String getPrimDecimals();
    public abstract String getPrimPattern();
    
    public void accept(IASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
    
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append( prim.getName() );
		return buf.toString();
	}
	
	public String getCanonicalName() {
		return prim.getName();
	}
	
	protected abstract Object clone() throws CloneNotSupportedException;
	
	public Type getBaseType() {
		return this;
	}
}
