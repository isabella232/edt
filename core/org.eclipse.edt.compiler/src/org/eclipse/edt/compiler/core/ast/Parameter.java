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
 * ProgramParameter AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Parameter extends Node {

	protected SimpleName name;
	protected Type type;

	public Parameter(SimpleName name, Type type, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
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
	
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
