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

/**
 * FunctionInvocation AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class FunctionInvocation extends Expression {

	private Expression target;
	private List funcArgs;	// List of Expressions

	public FunctionInvocation(Expression target, List funcArgs, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.target = target;
		target.setParent(this);
		
		this.funcArgs = setParent(funcArgs);
	}
	
	public Expression getTarget() {
		return target;
	}
	
	public List<Expression> getArguments() {
		return funcArgs;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			target.accept(visitor);
			acceptChildren(visitor, funcArgs);
		}
		visitor.endVisit(this);
	}
	
	public String getCanonicalString() {
		StringBuffer sb = new StringBuffer();
		sb.append(target.getCanonicalString());
		sb.append("()");
		return sb.toString();
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new FunctionInvocation((Expression) target.clone(), cloneList(funcArgs), getOffset(), getOffset() + getLength());
	}
}
