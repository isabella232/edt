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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * ThisExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ThisExpression extends Expression {
    
	public ThisExpression(int startOffset, int endOffset) {
		super(startOffset, endOffset);		
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String getCanonicalString() {
		return IEGLConstants.KEYWORD_THIS;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new ThisExpression(getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return getCanonicalString();
	}
	
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
    
    public Object getAttribute(int attr) {
    	return attributes == null ? null : attributes.get(new Integer(attr));
    }

}
