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
 * FunctionInvocationStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class FunctionInvocationStatement extends Statement {

	private FunctionInvocation functionInvocation;

	public FunctionInvocationStatement(FunctionInvocation functionInvocation, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.functionInvocation = functionInvocation;
		functionInvocation.setParent(this);
	}
	
	public FunctionInvocation getFunctionInvocation() {
		return functionInvocation;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			functionInvocation.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new FunctionInvocationStatement((FunctionInvocation)functionInvocation.clone(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return functionInvocation.toString() + ";";
	}
}
