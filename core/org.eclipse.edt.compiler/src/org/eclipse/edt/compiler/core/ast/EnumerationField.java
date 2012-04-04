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
 * EnumerationField AST node type.
 *
 * @author David Murray
 */
public class EnumerationField extends Node {
	
	SimpleName name;
	Expression constantValue;

	public EnumerationField(SimpleName name, Expression constantValueOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.name = name;
		name.setParent(this);
		
		if(constantValueOpt != null) {
			this.constantValue = constantValueOpt;
			constantValue.setParent(this);
		}
	}
	
	public Name getName() {
		return name;
	}
	
	public boolean hasConstantValue() {
		return constantValue != null;
	}
	
	public Expression getConstantValue() {
		return constantValue;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(constantValue != null) {
				constantValue.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new EnumerationField((SimpleName)name.clone(), constantValue == null ? null : (Expression) constantValue.clone(), getOffset(), getOffset() + getLength());
	}
}
