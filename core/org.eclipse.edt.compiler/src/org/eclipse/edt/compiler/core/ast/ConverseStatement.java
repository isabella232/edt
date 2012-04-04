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
 * ConverseStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ConverseStatement extends Statement {

	private Expression expr;
	private Name withNameOpt;

	public ConverseStatement(Expression expr, Name withNameOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		if(withNameOpt != null) {
			this.withNameOpt = withNameOpt;
			withNameOpt.setParent(this);
		}
	}
	
	public Expression getTarget() {
		return expr;
	}
	
	public boolean isConversingPageLabel() {
		return withNameOpt != null;
	}
	
	public Name getPageLabelName() {
		return withNameOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			if(withNameOpt != null) withNameOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return Arrays.asList(new Expression[] {expr});
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newWithNameOpt = withNameOpt != null ? (Name)withNameOpt.clone() : null;
		
		return new ConverseStatement((Expression)expr.clone(), newWithNameOpt, getOffset(), getOffset() + getLength());
	}

	public Name getWithNameOpt() {
		return withNameOpt;
	}
}
