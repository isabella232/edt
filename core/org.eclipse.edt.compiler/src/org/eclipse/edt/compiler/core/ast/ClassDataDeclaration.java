/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
 * ClassDataDeclaration AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ClassDataDeclaration extends Node {

	private boolean isPrivate;
	private boolean isStatic;
	private List<Name> ID_plus;	// List of SimpleNames
	private Type type;
	private boolean isNullable;
	private SettingsBlock settingsBlockOpt;
	private Expression initializerOpt;
	private boolean isConstant;

	public ClassDataDeclaration(Boolean privateAccessModifierOpt, Boolean staticAccessModifierOpt, List ID_plus, Type type, Boolean isNullable, SettingsBlock settingsBlockOpt, Expression initializerOpt, boolean isConstant, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.isPrivate = privateAccessModifierOpt.booleanValue();
		this.isStatic = staticAccessModifierOpt.booleanValue();
		this.ID_plus = setParent( ID_plus );
		this.type = type;
		type.setParent(this);
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		if(initializerOpt != null) {
			this.initializerOpt = initializerOpt;
			initializerOpt.setParent(this);
		}
		this.isConstant = isConstant;
		this.isNullable = isNullable.booleanValue();
	}
	
	public List<Name> getNames() {
		return ID_plus;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public SettingsBlock getSettingsBlockOpt() {
		return settingsBlockOpt;
	}
	
	public boolean hasInitializer() {
		return initializerOpt != null;
	}
	
	public boolean hasSettingsBlock() {
	    return settingsBlockOpt != null;
	}
	
	public Expression getInitializer() {
		return initializerOpt;
	}
	
	public boolean isConstant() {
		return isConstant;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, ID_plus);
			type.accept(visitor);
			if(settingsBlockOpt != null) settingsBlockOpt.accept(visitor);
			if(initializerOpt != null) initializerOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public boolean isNullable() {
		return isNullable;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		
		Expression newInitializerOpt = initializerOpt != null ? (Expression)initializerOpt.clone() : null;
		
		return new ClassDataDeclaration(new Boolean(isPrivate), new Boolean(isStatic), cloneList(ID_plus), (Type)type.clone(), Boolean.valueOf(isNullable), newSettingsBlockOpt, newInitializerOpt, isConstant, getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		
		if (isPrivate) {
			buf.append("private ");
		}
		if (isStatic) {
			buf.append("static ");
		}
		if (isConstant) {
			buf.append("const ");
		}
		
		boolean first = true;
		for (Name name : ID_plus) {
			if (first) {
				first = false;
			}
			else {
				buf.append(", ");
			}
			buf.append(name.getCaseSensitiveIdentifier());
		}
		buf.append(' ');
		buf.append(type.toString());
		if (isNullable) {
			buf.append('?');
		}
		
		if (settingsBlockOpt != null) {
			buf.append(settingsBlockOpt.toString());
		}
		
		if (initializerOpt != null) {
			buf.append(" = ");
			buf.append(initializerOpt.toString());
		}
		
		buf.append(';');
		
		return buf.toString();
	}
}
