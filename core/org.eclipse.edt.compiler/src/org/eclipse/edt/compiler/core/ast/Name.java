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

import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public abstract class Name extends Expression {
    
    protected String identifier;
    protected IBinding binding;
    
    public static int IMPLICIT_QUALIFIER_DATA_BINDING = 0;
    public static int INAPPLICABLE_ANNOTATION_TYPE_BINDING = 1;
    public static int OVERLOADED_FUNCTION_SET = 2;
    
    public Name(String identifier, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return InternUtil.intern(identifier);
    }
    
    public String getCaseSensitiveIdentifier(){
    	return InternUtil.internCaseSensitive(identifier);
    }
    
	public IDataBinding resolveDataBinding() {
	    if (binding == IBinding.NOT_FOUND_BINDING) {
	        return IBinding.NOT_FOUND_BINDING;
	    }
		return (binding != null && binding.isDataBinding()) ? (IDataBinding) binding : null;
	}
	
    public IBinding resolveBinding() {
        return binding;
    }
    
    public void setBinding(IBinding binding) {
        this.binding = binding;
    }
    
    public boolean isSimpleName() {
        return false;
    }
    
    public boolean isQualifiedName() {
        return false;
    }
    
    public String toString() {
        return getCanonicalName();
    }
    
    public String getCanonicalString() {
		return getCanonicalName();
	}
    
    public abstract String getCanonicalName();
    
    protected abstract StringBuffer getCanonicalNameBuffer();
    
    public abstract String[] getNameComponents();
    
    protected abstract List getNameComponentsList();
    
    public void setAttributeOnName(int attr, Object value) {
    	setAttribute(attr, value);
    }
    
    public void setAttribute(int attr, Object value) {
    	if(attributes == null) {
    		attributes = new HashMap();
    	}
    	attributes.put(new Integer(attr), value);
    }
    
    public Object getAttributeFromName(int attr) {
    	return getAttribute(attr);
    }
    
    /**
     * @param attr One of: Name.IMPLICIT_QUALIFIER_DATA_BINDING
     * @return
     */
    public Object getAttribute(int attr) {
    	return attributes == null ? null : attributes.get(new Integer(attr));
    }
    
	public boolean isName() {
		return true;
	}
	
	protected abstract Object clone() throws CloneNotSupportedException;

	public abstract void copyBindingsTo(Name anotherName);
}
