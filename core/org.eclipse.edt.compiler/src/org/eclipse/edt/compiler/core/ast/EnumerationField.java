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
	private SettingsBlock settingsBlockOpt;

	public EnumerationField(SimpleName name, Expression constantValueOpt, SettingsBlock settingsBlockOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.name = name;
		name.setParent(this);
		
		if(constantValueOpt != null) {
			this.constantValue = constantValueOpt;
			constantValue.setParent(this);
		}
		
		if (settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			this.settingsBlockOpt.setParent(this);
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
	
	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(constantValue != null) {
				constantValue.accept(visitor);
			}
			if(settingsBlockOpt != null) {
				settingsBlockOpt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlock = settingsBlockOpt == null ? null : (SettingsBlock) settingsBlockOpt.clone();
		return new EnumerationField((SimpleName)name.clone(), constantValue == null ? null : (Expression) constantValue.clone(),
				newSettingsBlock, getOffset(), getOffset() + getLength());
	}
}
