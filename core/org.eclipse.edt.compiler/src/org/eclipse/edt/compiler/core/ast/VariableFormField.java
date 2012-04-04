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
 * VariableFormField AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class VariableFormField extends FormField {

	private SimpleName name;
	private Type type;

	public VariableFormField(SimpleName name, Type type, SettingsBlock settingsBlockOpt, Expression initializerOpt, int startOffset, int endOffset) {
		super(settingsBlockOpt, initializerOpt, startOffset, endOffset);
		
		this.name = name;
		name.setParent(this);
		this.type = type;
		type.setParent(this);
	}
	
	public Name getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			type.accept(visitor);
			if(settingsBlockOpt != null) settingsBlockOpt.accept(visitor);
			if(initializerOpt != null) initializerOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		
		Expression newInitializerOpt = initializerOpt != null ? (Expression)initializerOpt.clone() : null;
		
		return new VariableFormField((SimpleName)name.clone(), (Type)type.clone(), newSettingsBlockOpt, newInitializerOpt, getOffset(), getOffset() + getLength());
	}
}
