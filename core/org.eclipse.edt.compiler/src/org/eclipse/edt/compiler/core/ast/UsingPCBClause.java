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

/**
 * UsingPCBClause AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class UsingPCBClause extends Node {
	
	public static IOStatementClauseInfo INFO = new IOStatementClauseInfo() {
		public String getClauseKeyword() {
			return "";
		}

		public String getContentPrefix() {
			return null;
		}

		public String getContentSuffix() {
			return null;
		}

		public int getContentType() {
			return IOStatementClauseInfo.IDENTIFIER_VALUE;
		}		
	};

	private Expression lvalue;

	public UsingPCBClause(Expression lvalue, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.lvalue = lvalue;
		lvalue.setParent(this);
	}
	
	public Expression getPCB() {
		return lvalue;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			lvalue.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new UsingPCBClause((Expression)lvalue.clone(), getOffset(), getOffset() + getLength());
	}
}
