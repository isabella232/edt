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

import org.eclipse.edt.compiler.binding.ConstructorBinding;

/**
 * Constructor AST node type.
 *
 * @author David Murray
 */
public class Constructor extends Node {

	private List parameters;
	private SettingsBlock settingsBlockOpt;
	private boolean isPrivate;
	private List stmts;	
	private ConstructorBinding binding;

	public ConstructorBinding getBinding() {
		return binding;
	}

	public void setBinding(ConstructorBinding binding) {
		this.binding = binding;
	}

	public Constructor(Boolean privateAccessModifierOpt, List parameters, SettingsBlock settingsBlockOpt, List stmts, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.parameters = setParent(parameters);
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		isPrivate = privateAccessModifierOpt.booleanValue();
		
		//abstract constructors will not have stmts
		if (stmts != null) {
			this.stmts = setParent(stmts);
		}
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}

	public List getStmts() {
		return stmts;
	}

	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public boolean hasSettingsBlock() {
	    return settingsBlockOpt != null;
	}
	
	public List getParameters() {
		return parameters;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, parameters);
			if(settingsBlockOpt != null) settingsBlockOpt.accept(visitor);
			
			if (stmts != null) {
				acceptChildren(visitor, stmts);
			}
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		return new Constructor(new Boolean(isPrivate), cloneList(parameters), newSettingsBlockOpt, cloneList(stmts), getOffset(), getOffset() + getLength());
	}
}
