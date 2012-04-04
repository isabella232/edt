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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

/**
 * NameType AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class NameType extends Type {

	private Name name;

	public NameType(Name name, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.name = name;
		name.setParent(this);
	}
	
	public Name getName() {
		return name;
	}
	
	public int getKind() {
		return NAMETYPE;
	}
	
	public boolean isNameType() {
		return true;
	}
	
	public ITypeBinding resolveTypeBinding() {
		IBinding binding = name.resolveBinding();
		if(binding == IBinding.NOT_FOUND_BINDING || binding == null) {
			return null;
		}
		return binding.isTypeBinding() ? (ITypeBinding) binding : null;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public String getCanonicalName() {
		return name.getCanonicalName();
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new NameType((Name)name.clone(), getOffset(), getOffset() + getLength());
	}
	
	public Type getBaseType() {
		return this;
	}
}
