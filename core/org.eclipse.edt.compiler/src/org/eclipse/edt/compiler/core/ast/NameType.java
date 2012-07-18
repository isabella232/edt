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

import java.util.Collections;
import java.util.List;


/**
 * NameType AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class NameType extends Type {

	private Name name;
	private List<Expression> arguments;

	public NameType(Name name, List<Expression> arguments, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.name = name;
		name.setParent(this);
		if (arguments == null) {
			arguments = Collections.emptyList();
		}
		this.arguments = setParent(arguments);

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
	
	public org.eclipse.edt.mof.egl.Type resolveType() {
		return name.resolveType();
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, arguments);			
		}
		visitor.endVisit(this);
	}
	
	public String getCanonicalName() {
		return name.getCanonicalName();
	}
	
	public boolean hasArguments() {
		return arguments != null && arguments.size() > 0;
	}
	
	
	public List<Expression> getArguments() {
		return arguments;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new NameType((Name)name.clone(), cloneList(arguments), getOffset(), getOffset() + getLength());
	}
	
	public Type getBaseType() {
		return this;
	}
}
