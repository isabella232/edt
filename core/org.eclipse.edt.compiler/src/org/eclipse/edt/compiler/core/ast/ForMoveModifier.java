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
 * ForMoveModifier AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ForMoveModifier extends MoveModifier {

	private Expression expr;

	public ForMoveModifier(Expression expr) {
		this.expr = expr;
	}
	
	public boolean isFor() {
		return true;
	}
	
	public void setParent(Node parent) {
		expr.setParent( parent );
	}
	
	public Expression getExpression() {
		return expr;
	}
	
	public void accept(IASTVisitor visitor) {
		expr.accept(visitor);
	}
	
	protected Object clone() throws CloneNotSupportedException{
		return new ForMoveModifier((Expression)expr.clone());
	}
}
