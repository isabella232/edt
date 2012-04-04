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
import java.util.Iterator;
import java.util.List;

/**
 * CaseStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class CaseStatement extends Statement {

	private Expression parenthesizedExprOpt;
	private List whenClauses;	// List of WhenClauses
	private OtherwiseClause defaultClauseOpt;

	public CaseStatement(Expression parenthesizedExprOpt, List whenClauses, OtherwiseClause defaultClauseOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if(parenthesizedExprOpt != null) {
			this.parenthesizedExprOpt = parenthesizedExprOpt;
			parenthesizedExprOpt.setParent(this);
		}
		this.whenClauses = setParent(whenClauses);
		if(defaultClauseOpt != null) {
			this.defaultClauseOpt = defaultClauseOpt;
			defaultClauseOpt.setParent(this);
		}
	}
	
	public boolean hasCriterion() {
		return parenthesizedExprOpt != null;
	}
	
	public Expression getCriterion() {
		return parenthesizedExprOpt;
	}
	
	public List getWhenClauses() {
		return whenClauses;
	}
	
	public boolean hasOtherwiseClause() {
		return defaultClauseOpt != null;
	}
	
	public OtherwiseClause getDefaultClause() {
		return defaultClauseOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			if(parenthesizedExprOpt != null) parenthesizedExprOpt.accept(visitor);			
			acceptChildren(visitor, whenClauses);
			if(defaultClauseOpt != null) defaultClauseOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public boolean canIncludeOtherStatements() {
		return true;
	}
	
	public List getStatementBlocks() {
		List result = new ArrayList();
		for(Iterator iter = whenClauses.iterator(); iter.hasNext();) {
			result.add(((WhenClause) iter.next()).getStmts());
		}
		if(defaultClauseOpt != null) {
			result.add(defaultClauseOpt.getStatements());
		}
		return result;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Expression newParenthesizedExprOpt = parenthesizedExprOpt != null ? (Expression)parenthesizedExprOpt.clone() : null;
		
		OtherwiseClause newDefaultClauseOpt = defaultClauseOpt != null ? (OtherwiseClause)defaultClauseOpt.clone() : null;
		
		return new CaseStatement(newParenthesizedExprOpt, cloneList(whenClauses), newDefaultClauseOpt, getOffset(), getOffset() + getLength());
	}
}
