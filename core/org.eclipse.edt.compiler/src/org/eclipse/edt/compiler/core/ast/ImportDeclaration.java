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
 * ImportDeclaration AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ImportDeclaration extends Node {

	private Name name;
	boolean isOnDemand;

	public ImportDeclaration(Name name, boolean isOnDemand, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.name = name;
		name.setParent(this);
		this.isOnDemand = isOnDemand;
	}
	
	public Name getName() {
		return name;
	}
	
    public boolean isOnDemand() {
        return isOnDemand;
    }
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ImportDeclaration((Name)name.clone(), isOnDemand, getOffset(), getOffset() + getLength());
	}
}
