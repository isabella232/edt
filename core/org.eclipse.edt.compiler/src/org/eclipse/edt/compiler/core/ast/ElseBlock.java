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
 * ElseBlock AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ElseBlock extends Node {

	private List stmts;	// List of Nodes

	public ElseBlock(List stmts, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.stmts = setParent(stmts);
	}
	
	public List getStmts() {
		return stmts;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, stmts);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ElseBlock(cloneList(stmts), getOffset(), getOffset() + getLength());
	}
}
