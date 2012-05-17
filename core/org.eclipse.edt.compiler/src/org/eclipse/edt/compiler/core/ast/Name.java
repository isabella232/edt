/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public abstract class Name extends Expression {
    
    protected String identifier;
    protected Object element;
    
    public static int IMPLICIT_QUALIFIER_DATA_BINDING = 0;
    public static int OVERLOADED_FUNCTION_SET = 1;
    
    public Name(String identifier, int startOffset, int endOffset) {
        super(startOffset, endOffset);
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return NameUtile.getAsName(identifier);
    }
    
    public String getCaseSensitiveIdentifier(){
    	return identifier;
    }
    	    
    public void setElement(Object elem) {
        this.element = elem;
        super.setElement(elem);
    }
    
    public Object resolveElement() {
    	return element;
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
    
    public abstract String getNameComponents();
    
    protected abstract List<String> getNameComponentsList();
    
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
}
