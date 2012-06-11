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

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * IntoClause AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class IntoClause extends Node {
	
	public static IOStatementClauseInfo INFO = new IOStatementClauseInfo() {
		public String getClauseKeyword() {
			return IEGLConstants.KEYWORD_INTO;
		}

		public String getContentPrefix() {
			return null;
		}

		public String getContentSuffix() {
			return null;
		}

		public int getContentType() {
			return IOStatementClauseInfo.LIST_VALUE;
		}		
	};

	private List expr_plus;	// List of Expressions

	public IntoClause(List expr_plus, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr_plus = setParent(expr_plus);
	}
	
	public List<Node> getExpressions() {
		return expr_plus;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, expr_plus);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new IntoClause(cloneList(expr_plus), getOffset(), getOffset() + getLength());
	}
}
