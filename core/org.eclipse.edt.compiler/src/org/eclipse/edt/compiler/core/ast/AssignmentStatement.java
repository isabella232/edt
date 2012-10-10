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
 * AssignmentStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class AssignmentStatement extends Statement {

	private Assignment assignment;

	public AssignmentStatement(Assignment assignment, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.assignment = assignment;
		assignment.setParent(this);
	}
	
	public Assignment getAssignment() {
		return assignment;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			assignment.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new AssignmentStatement((Assignment)assignment.clone(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return assignment.toString() + ";";
	}
}
