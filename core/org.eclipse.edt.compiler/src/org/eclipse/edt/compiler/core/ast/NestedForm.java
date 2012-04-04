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

/**
 * NestedForm AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class NestedForm extends Node {

	private boolean isPrivate;
	private SimpleName name;
	private Name partSubTypeOpt;
	private List formContents;	// List of Nodes

	public NestedForm(Boolean privateAccessModifierOpt, SimpleName name, Name partSubTypeOpt, List formContents, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.isPrivate = privateAccessModifierOpt.booleanValue();
		this.name = name;
		name.setParent(this);
		if(partSubTypeOpt != null) {
			this.partSubTypeOpt = partSubTypeOpt;
			partSubTypeOpt.setParent(this);
		}
		this.formContents = setParent(formContents);
	}	
	
	public Name getName() {
		return name;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public boolean hasSubType() {
		return partSubTypeOpt != null;
	}
	
	public Name getSubType() {
		return partSubTypeOpt;
	}
	
	public List getContents() {
		return formContents;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			acceptChildren(visitor, formContents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		return new NestedForm(new Boolean(isPrivate), (SimpleName)name.clone(), newPartSubTypeOpt, cloneList(formContents), getOffset(), getOffset() + getLength());
	}
}
