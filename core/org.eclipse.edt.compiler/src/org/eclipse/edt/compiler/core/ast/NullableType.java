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
 * NullableType AST node type.
 *
 * @author David Murray
 */
public class NullableType extends Type {

	private Type baseType;

	public NullableType(Type baseType, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.baseType = baseType;
		baseType.setParent(this);		
	}
	
	public Type getBaseType() {
		return baseType;
	}
	
	public int getKind() {
		return NULLABLETYPE;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			baseType.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public String getCanonicalName() {
		return baseType.getCanonicalName() + "?";
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new NullableType((Type)baseType.clone(), getOffset(), getOffset() + getLength());
	}
	
	public ITypeBinding resolveTypeBinding() {
		ITypeBinding tBinding = baseType.resolveTypeBinding();
		return tBinding == null || tBinding == IBinding.NOT_FOUND_BINDING ?
			null : tBinding.getNullableInstance();
	}
	
	public boolean isNullableType() {
		return true;
	}
}
