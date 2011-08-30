/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
 * Constructor AST node type.
 *
 * @author David Murray
 */
public class Constructor extends Node {

	private List parameters;
	private SettingsBlock settingsBlockOpt;
	private boolean isPrivate;

	public Constructor(Boolean privateAccessModifierOpt, List parameters, SettingsBlock settingsBlockOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.parameters = setParent(parameters);
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		isPrivate = privateAccessModifierOpt.booleanValue();
	}
	
	public boolean isPrivate() {
		return isPrivate;
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
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		return new Constructor(new Boolean(isPrivate), cloneList(parameters), newSettingsBlockOpt, getOffset(), getOffset() + getLength());
	}
}
