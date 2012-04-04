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

import java.util.Map;

import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;


/**
 * @author winghong
 */
public abstract class Expression extends Node {
    
    private ITypeBinding typeBinding;
    protected Map attributes;
    
    public Expression(int startOffset, int endOffset) {
        super(startOffset, endOffset);
    }

    public ITypeBinding resolveTypeBinding() {
        return typeBinding;
    }
    
    public IDataBinding resolveDataBinding() {
    	return null;
    }
    
    public void setDataBinding(IDataBinding binding) {
        //default is to do nothing
    }

    public void setTypeBinding(ITypeBinding typeBinding) {
    	if(typeBinding != null && IBinding.NOT_FOUND_BINDING != typeBinding) {
    		if(ITypeBinding.DATAITEM_BINDING == typeBinding.getKind()) {
    			this.typeBinding = ((DataItemBinding) typeBinding).getPrimitiveTypeBinding();
    			return;
    		}
    	}
        this.typeBinding = typeBinding;
    }
    
    public abstract String getCanonicalString();

    public void setAttributeOnName(int attr, Object value) {
    }
    
    public Object getAttributeFromName(int attr) {
    	return null;
    }
    
    public boolean isName() {
    	return false;
    }
    
   protected Object clone() throws CloneNotSupportedException {
		return new CloneNotSupportedException();
	}
}
