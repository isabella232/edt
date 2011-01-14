/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
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
 * FromResultSetClause AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class FromResultSetClause extends Node {

	private String ID;

	public FromResultSetClause(String ID, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.ID = ID;
	}
	
	public String getResultSetID() {
		return ID;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new FromResultSetClause(new String(ID), getOffset(), getOffset() + getLength());
	}
}
