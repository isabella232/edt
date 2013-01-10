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
 * TypeLiteralExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class TypeLiteralExpression extends Expression {

	private Type type;

	public TypeLiteralExpression(Type type, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.type = type;
		type.setParent(this);
	}
	
	public Type getType() {
		return type;
	}

	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			type.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public String getCanonicalString() {
		return type.getCanonicalName() + ".type";
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new TypeLiteralExpression((Type)type.clone(), getOffset(), getOffset() + getLength());
	}
}
