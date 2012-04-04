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

import java.util.Arrays;
import java.util.List;

/**
 * PrintStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class PrintStatement extends Statement {

	private Expression expr;

	public PrintStatement(Expression expr, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
	}
	
	public Expression getTarget() {
		return expr;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return Arrays.asList(new Expression[] {expr});
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new PrintStatement((Expression)expr.clone(), getOffset(), getOffset() + getLength());
	}
}
