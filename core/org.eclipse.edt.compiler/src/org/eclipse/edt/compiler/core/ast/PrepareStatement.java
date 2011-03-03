/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import java.util.Collections;
import java.util.List;

/**
 * PrepareStatement AST node type.
 * 
 * For prepared statement ID, use getPreparedStatementID().
 * 
 * For from/for options, invoke accept() with an IASTVisitor that
 * overrides visit() for the following types:
 *  - ForExpressionClause
 *  - FromExpressionClause 
 *
 * @author Albert Ho
 * @author David Murray
 */
public class PrepareStatement extends Statement {

	private String ID;
	private List prepareOptions;	// List of Nodes
	
	private List ioObjects;

	public PrepareStatement(String ID, List prepareOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.ID = ID;
		this.prepareOptions = setParent(prepareOptions);
	}
	
	public String getPreparedStatementID() {
		return ID;
	}
	
	public List getPrepareOptions() {
		return prepareOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, prepareOptions);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		if(ioObjects == null) {
			ioObjects = Collections.EMPTY_LIST;
			acceptChildren(new DefaultASTVisitor() {
				public boolean visit(ForExpressionClause forExpressionClause) {
					if(ioObjects == Collections.EMPTY_LIST) {
						ioObjects = new ArrayList();
					}
					ioObjects.add(forExpressionClause.getExpression());
					return false;
				}
			}, prepareOptions);
		}
		return ioObjects;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new PrepareStatement(new String(ID), cloneList(prepareOptions), getOffset(), getOffset() + getLength());
	}
}
