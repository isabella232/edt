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

/**
 * GotoStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class GotoStatement extends Statement {

	private String ID;

	public GotoStatement(String ID, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.ID = ID;
	}
	
	public String getLabel() {
		return ID;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new GotoStatement(new String(ID), getOffset(), getOffset() + getLength());
	}
}
