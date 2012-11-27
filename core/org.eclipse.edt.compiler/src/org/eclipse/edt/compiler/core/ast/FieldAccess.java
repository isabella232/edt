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

import org.eclipse.edt.mof.utils.NameUtile;


/**
 * FieldAccess AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class FieldAccess extends Expression {

	private Expression primary;
	private String ID;
	
	private Object element;

	public FieldAccess(Expression primary, String ID, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.primary = primary;
		primary.setParent(this);
		this.ID = ID;
	}
	
	public Expression getPrimary() {
		return primary;
	}
	
	public String getID() {
        return NameUtile.getAsName(ID);
	}
	
	public String getCaseSensitiveID() {
		return ID;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			primary.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void setElement(Object elem) {
		this.element = elem;
		super.setElement(elem);
	}
	
	@Override
	public Object resolveElement() {
		return element;
	}
	
	@Override
	public String getCanonicalString() {
		return getPrimary().getCanonicalString() + "." + ID;
	}
	
	@Override
	public void setAttributeOnName(int attr, Object value) {
    	primary.setAttributeOnName(attr, value);
    }
	
    public Object getAttributeFromName(int attr) {
    	return primary.getAttributeFromName(attr);
    }

	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new FieldAccess((Expression)primary.clone(), new String(ID), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return getPrimary().toString() + "." + ID;
	}
}
