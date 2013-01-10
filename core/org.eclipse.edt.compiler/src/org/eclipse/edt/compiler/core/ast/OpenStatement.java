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
import java.util.Collections;
import java.util.List;

/**
 * OpenStatement AST node type.
 * 
 * For result set identifier, use:
 *  - getResultSetID()
 * 
 * For hold/scroll options, use:
 *  - hasHold(); 
 *  - hasScroll()
 * 
 * For clauses, invoke accept() with an IASTVisitor that overrides 
 * visit() for the following types: 
 *  - IntoClause 
 *  - ForExpressionClause 
 *  - ForUpdateClause  
 *  - UsingClause
 *  - UsingKeysClause
 *  - WithInlineSQLClause
 *  - WithIDClause    
 *
 * @author Albert Ho
 * @author David Murray
 */
public class OpenStatement extends Statement {

	private Expression resultSet;
	private boolean isHold;
	private boolean isScroll;
	private List openTargets;	// List of Nodes
	
	private List ioObjects;

	public OpenStatement(Expression resultSet, Boolean[] openModifierOpt, List openTargets, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.resultSet = resultSet;
		
		// openModifierOpt is guaranteed by the action code in the parser
		// to be a two-element array
		isHold = openModifierOpt[0].booleanValue();
		isScroll = openModifierOpt[1].booleanValue();
		
		this.resultSet.setParent(this);
		this.openTargets = setParent(openTargets);
	}
	
	public Expression getResultSet() {
		return resultSet;
	}

	public boolean hasHold() {
		return isHold;
	}
	
	public boolean hasScroll() {
		return isScroll;
	}
	
	public List getOpenTargets() {
		return openTargets;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			resultSet.accept(visitor);
			acceptChildren(visitor, openTargets);
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
			}, openTargets);
		}
		return ioObjects;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new OpenStatement((Expression)resultSet.clone(), new Boolean[]{new Boolean(isHold), new Boolean(isScroll)}, cloneList(openTargets), getOffset(), getOffset() + getLength());
	}
}
