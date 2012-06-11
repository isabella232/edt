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

import org.eclipse.edt.compiler.binding.ITypeBinding;

/**
 * ArrayType AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ArrayType extends Type {

	private Type elementType;
	private Expression initialSize;
	private org.eclipse.edt.mof.egl.ArrayType arrayType;

	public ArrayType(Type elementType, Expression initialSize, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.elementType = elementType;
		elementType.setParent(this);		
		
		if( initialSize != null ) {
			this.initialSize = initialSize;
			this.initialSize.setParent(this);
		}
	}
	
	public Type getElementType() {
		return elementType;
	}
	
	public boolean hasInitialSize() {
		return initialSize != null;
	}
	
	public Expression getInitialSize() {
		return initialSize;
	}
	
	public int getKind() {
		return ARRAYTYPE;
	}
	
	public boolean isArrayType() {
		return true;
	}

    public org.eclipse.edt.mof.egl.Type resolveType() {
        return arrayType;
    }
    
    public void setType(org.eclipse.edt.mof.egl.ArrayType arrayType) {
        this.arrayType = arrayType;
    }
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			elementType.accept(visitor);
			if( initialSize != null) initialSize.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public String getCanonicalName() {
		return elementType.getCanonicalName() + "[]";
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Expression newInitialSize = initialSize != null ? (Expression)initialSize.clone() : null;
		
		return new ArrayType((Type)elementType.clone(), newInitialSize, getOffset(), getOffset() + getLength());
	}
	
	public Type getBaseType() {
		return elementType.getBaseType();
	}
}
