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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ExecuteStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ExecuteStatement extends Statement {
	

	private List executeOptions;	// List of Symbols
	
	private List ioObjects;

	public ExecuteStatement(List executeOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
				
		this.executeOptions = setParent(executeOptions);
	}
		
		
	public List getExecuteOptions() {
		return executeOptions;
	}
	
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {			
			acceptChildren(visitor, executeOptions);			
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
			}, executeOptions);
		}
		return ioObjects;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ExecuteStatement(cloneList(executeOptions), getOffset(), getOffset() + getLength());
	}
}
