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
	protected Object element;

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
	
	@Override
	public void setElement(Object elem) {
		super.setElement( elem );
		this.element = elem;
	}
	
	@Override
	public Object resolveElement() {
		return this.element;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			target.accept(visitor);
			acceptChildren(visitor, funcArgs);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String getCanonicalString() {
		StringBuilder sb = new StringBuilder();
		sb.append(target.getCanonicalString());
		sb.append("()");
		return sb.toString();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new FunctionInvocation((Expression) target.clone(), cloneList(funcArgs), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append(target.toString());
		buf.append('(');
		
		boolean first = true;
		for (Object arg : funcArgs) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(arg.toString());
		}
		
		buf.append(')');
		return buf.toString();
	}
}
