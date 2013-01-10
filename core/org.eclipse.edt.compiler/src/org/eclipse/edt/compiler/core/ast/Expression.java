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

import java.util.Map;

import org.eclipse.edt.mof.egl.Member;


/**
 * @author winghong
 */
public abstract class Expression extends Node {
    
    private org.eclipse.edt.mof.egl.Type type;
    private boolean bindAttempted;
    protected Map attributes;
    
    public Expression(int startOffset, int endOffset) {
        super(startOffset, endOffset);
    }

    public org.eclipse.edt.mof.egl.Type resolveType() {
        return type;
    }
    
    public Object resolveElement() {
    	return null;
    }
    
    public Member resolveMember() {
    	if (resolveElement() instanceof Member) {
    		return (Member) resolveElement();
    	}
    	return null;
    }
    
    public void setElement(Object elem) {
        setBindAttempted(true);
    }

    public void setMember(Member member) {
       setElement(member);
    }

    public void setType(org.eclipse.edt.mof.egl.Type type) {
        this.type = type;
        setBindAttempted(true);
    }
    
    public abstract String getCanonicalString();
    
    public String getCaseSensitiveID() {
    	String can = getCanonicalString();
    	if (can != null) {
    		int index = can.lastIndexOf(".");
    		if (index < 0) {
    			return can;
    		}
    		return can.substring(index + 1);
    	}
    	return null;
    }

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
   
   public boolean isBindAttempted() {
	   return bindAttempted;
   }
   
   public void setBindAttempted(boolean bool) {
	   bindAttempted = bool;
   }
}
