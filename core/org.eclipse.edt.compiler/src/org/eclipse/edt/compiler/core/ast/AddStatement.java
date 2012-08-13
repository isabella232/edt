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
 * AddStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class AddStatement extends Statement {

	private List<Expression> expr_plus;	// List of Expressions
	private List<Node> addOptions;	// List of Nodes

	public AddStatement(List expr_plus, List addOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr_plus = setParent(expr_plus);
		this.addOptions = setParent(addOptions);
	}
	
	public List<Expression> getTargets() {
		return expr_plus;
	}
	
	public List<Node> getOptions() {
		return addOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, expr_plus);
			acceptChildren(visitor, addOptions);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return expr_plus;
	}
	
	public List getIOClauses() {
		return addOptions;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new AddStatement(cloneList(expr_plus), cloneList(addOptions), getOffset(), getOffset() + getLength());
	}
}
