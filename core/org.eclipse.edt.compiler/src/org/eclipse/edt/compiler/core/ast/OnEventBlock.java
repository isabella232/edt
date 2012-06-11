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
 * OnEventBlock AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class OnEventBlock extends Node {
	
	private List fieldsOpt;	// List of Expressions
	private List stmts;	// List of Nodes
	private Expression eventTypeExpr;

	public OnEventBlock(Expression eventTypeExpr, List fieldsOpt, List stmts, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.eventTypeExpr = eventTypeExpr;
		eventTypeExpr.setParent(this);
		this.fieldsOpt = setParent(fieldsOpt);
		this.stmts = setParent(stmts);		
	}
	
	
	public boolean hasStringList() {
		return !fieldsOpt.isEmpty();
	}
	
	/**
	 * Returns a List of Expression objects representing the list of things
	 * that can optionally follow an event kind and a colon.
	 */
	public List<Node> getStringList() {
		return fieldsOpt;
	}
	
	public boolean hasStatements() {
		return !stmts.isEmpty();
	}
	
	public List<Node> getStatements() {
		return stmts;
	}
	
	public Expression getEventTypeExpr() {
		return eventTypeExpr;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, fieldsOpt);
			acceptChildren(visitor, stmts);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new OnEventBlock((Expression) eventTypeExpr.clone(), cloneList(fieldsOpt), cloneList(stmts), getOffset(), getOffset() + getLength());
	}
}
