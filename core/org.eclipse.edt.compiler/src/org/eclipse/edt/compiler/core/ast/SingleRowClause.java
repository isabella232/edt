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
 * SingleRowClause AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class SingleRowClause extends Node {


	public SingleRowClause(int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new SingleRowClause(getOffset(), getOffset() + getLength());
	}
}
