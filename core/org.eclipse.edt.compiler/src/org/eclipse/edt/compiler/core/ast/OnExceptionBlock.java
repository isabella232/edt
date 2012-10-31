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

import java.util.List;

/**
 * OnExceptionBlock AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class OnExceptionBlock extends Node {

	private List stmts;	// List of Nodes
	private SimpleName id;
	private Type type;
	private boolean isNullable;

	public OnExceptionBlock(List stmts, SimpleName id, Type type, Boolean isNullable, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.stmts = setParent(stmts);
		this.id = id;
		id.setParent(this);
		this.type = type;
		type.setParent(this);
		this.isNullable = isNullable.booleanValue();
	}
	
	public List<Node> getStmts() {
		return stmts;
	}
	
	public Name getExceptionName() {
		return id;
	}
	
	public Type getExceptionType() {
		return type;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, stmts);
			id.accept(visitor);
			type.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public boolean isNullable() {
		return isNullable;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SimpleName newName = (SimpleName) id.clone();
		Type newType = (Type) type.clone();
		return new OnExceptionBlock(cloneList(stmts), newName, newType, Boolean.valueOf(isNullable), getOffset(), getOffset() + getLength());
	}
}
