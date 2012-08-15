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

/**
 * ReturnsDeclaration AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ReturnsDeclaration extends Node {

	private Type type;
	private boolean isNullable;

	public ReturnsDeclaration(Type type, Boolean isNullable, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.type = type;
		type.setParent(this);
		this.isNullable = isNullable.booleanValue();
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isNullable() {
		return isNullable;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			type.accept(visitor);
		}
		visitor.endVisit(this);      
    }  
	   
	protected Object clone() throws CloneNotSupportedException {
		return new ReturnsDeclaration((Type)type.clone(), new Boolean(isNullable), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return "returns(" + type.toString() + (isNullable ? "?" : "") + ")";
	}
}
