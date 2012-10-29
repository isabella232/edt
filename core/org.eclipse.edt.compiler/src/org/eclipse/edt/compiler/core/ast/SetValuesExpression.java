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
 * SetValuesExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class SetValuesExpression extends Expression {

	private Expression primary;
	private SettingsBlock settingsBlock;

	public SetValuesExpression(Expression primary, SettingsBlock settingsBlock, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.primary = primary;
		primary.setParent(this);
		this.settingsBlock = settingsBlock;
		settingsBlock.setParent(this);
	}
	
	public Expression getExpression() {
		return primary;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlock;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			primary.accept(visitor);
			settingsBlock.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String getCanonicalString() {
		return primary.getCanonicalString();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new SetValuesExpression((Expression)primary.clone(), (SettingsBlock)settingsBlock.clone(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return primary.toString() + settingsBlock.toString();
	}
}
