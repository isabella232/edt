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
 * Function AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class NestedFunction extends Node {

	private SimpleName name;
	private boolean isPrivate;
	private boolean isStatic;
	private boolean isAbstract;
	private List functionParameters;	// List of Symbols
	private ReturnsDeclaration returnsOpt;
	private List stmts;	// List of Symbols
	
	public NestedFunction(Boolean privateAccessModifierOpt, Boolean staticAccessModifierOpt, SimpleName name, List functionParameters, ReturnsDeclaration returnsOpt, List stmts, boolean isAbstract, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.name = name;
		name.setParent(this);
		isPrivate = privateAccessModifierOpt.booleanValue();
		isStatic = staticAccessModifierOpt.booleanValue();
		this.isAbstract = isAbstract;		
		this.functionParameters = setParent(functionParameters);
		if(returnsOpt != null) {
			this.returnsOpt = returnsOpt;
			returnsOpt.setParent(this);
		}
		this.stmts = setParent(stmts);
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public Name getName() {
		return name;
	}
	
	public List getFunctionParameters() {
		return functionParameters;
	}
	
	public boolean hasReturnType() {
		return returnsOpt != null;
	}
	
	public Type getReturnType() {
		return returnsOpt.getType();
	}
	
	public ReturnsDeclaration getReturnDeclaration(){
		return returnsOpt;
	}	
	
	public boolean returnTypeIsSqlNullable() {
		return returnsOpt.isSqlNullable();
	}
	
	public List getStmts() {
		return stmts;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, functionParameters);
			if(returnsOpt != null) returnsOpt.accept(visitor);
			acceptChildren(visitor, stmts);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		ReturnsDeclaration newReturnsOpt = returnsOpt != null ? (ReturnsDeclaration)returnsOpt.clone() : null;
		
		return new NestedFunction(new Boolean(isPrivate), new Boolean(isStatic), (SimpleName)name.clone(), cloneList(functionParameters), newReturnsOpt, cloneList(stmts), isAbstract, getOffset(), getOffset() + getLength());
	}
}
