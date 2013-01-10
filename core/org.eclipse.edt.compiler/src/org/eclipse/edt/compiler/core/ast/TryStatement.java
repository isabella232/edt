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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TryStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class TryStatement extends Statement {

	private List stmts;	// List of Nodes
	private List onExceptionBlocks;

	public TryStatement(List stmts, List onExceptionBlocks, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.stmts = setParent(stmts);
		this.onExceptionBlocks = setParent(onExceptionBlocks);
	}
	
	public List<Node> getStmts() {
		return stmts;
	}
	
	public List<Node> getOnExceptionBlocks() {
		return onExceptionBlocks;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, stmts);
			acceptChildren(visitor, onExceptionBlocks);
		}
		visitor.endVisit(this);
	}
	
	public boolean canIncludeOtherStatements() {
		return true;
	}
	
	public List getStatementBlocks() {
		List result = new ArrayList();
		result.add(stmts);
		for(Iterator iter = onExceptionBlocks.iterator(); iter.hasNext();) {
			result.add(((OnExceptionBlock) iter.next()).getStmts());
		}
		return result;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new TryStatement(cloneList(stmts), cloneList(onExceptionBlocks), getOffset(), getOffset() + getLength());
	}
}
