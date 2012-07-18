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
	private SimpleName idOpt;
	private Type typeOpt;
	private boolean isNullable;

	public OnExceptionBlock(List stmts, SimpleName idOpt, Type typeOpt, Boolean isNullable, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.stmts = setParent(stmts);
		if(idOpt != null) {
			this.idOpt = idOpt;
			idOpt.setParent(this);
		}
		if(typeOpt != null) {
			this.typeOpt = typeOpt;
			typeOpt.setParent(this);
		}
		this.isNullable = isNullable.booleanValue();
	}
	
	public List<Node> getStmts() {
		return stmts;
	}
	
	public boolean hasExceptionDeclaration() {
		return idOpt != null;
	}
	
	public Name getExceptionName() {
		return idOpt;
	}
	
	public Type getExceptionType() {
		return typeOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, stmts);
			if(hasExceptionDeclaration()) {
				idOpt.accept(visitor);
				typeOpt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	public boolean isNullable() {
		return isNullable;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SimpleName newName = idOpt == null ? null : (SimpleName) idOpt.clone();
		Type newType = typeOpt == null ? null : (Type) typeOpt.clone();
		return new OnExceptionBlock(cloneList(stmts), newName, newType, Boolean.valueOf(isNullable), getOffset(), getOffset() + getLength());
	}
}
