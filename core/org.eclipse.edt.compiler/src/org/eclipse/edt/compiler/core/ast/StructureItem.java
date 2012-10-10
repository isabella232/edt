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

import org.eclipse.edt.mof.egl.Member;


/**
 * StructureItem AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class StructureItem extends Node {

	private SimpleName name;
	private Type type;
	private boolean isNullable;
	private SettingsBlock settingsBlockOpt;
	private Expression initializerOpt;
	String occursOpt;
	//Since there is no node to attach binding for filler items...also used to hold the structure imported for
	// an embed.
	private Member member;

	public StructureItem(SimpleName name, Type type, Boolean isNullable, String occursOpt, SettingsBlock settingsBlockOpt, Expression initializerOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if(name != null) {
			this.name = name;
			name.setParent(this);
		}
		if(type != null) {
			this.type = type;
			type.setParent(this);
		}
		this.occursOpt = occursOpt;
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		if(initializerOpt != null) {
			this.initializerOpt = initializerOpt;
			initializerOpt.setParent(this);
		}
		this.isNullable = isNullable.booleanValue();
	}
	
	public Name getName() {
		return name;
	}
	
	public boolean hasType() {
		return type != null;
	}
	
	public Type getType() {
		return type;
	}
	
	/**
	 * Warning: only use for untyped structure items. For typed structure items,
	 * this will return false and getType() will be an ArrayType AST.
	 */
	public boolean hasOccurs() {
		return occursOpt != null;
	}
	
	/**
	 * Warning: only use for untyped structure items. For typed structure items,
	 * this will return null and getType() will be an ArrayType AST.
	 */
	public String getOccurs() {
		return occursOpt;
	}
	
	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public boolean hasInitializer() {
		return initializerOpt != null;
	}
	
	public Expression getInitializer() {
		return initializerOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			if( name != null) name.accept(visitor);
			if( type != null) type.accept(visitor);
			if(settingsBlockOpt != null) settingsBlockOpt.accept(visitor);
			if(initializerOpt != null) initializerOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	       
	public boolean isNullable() {
		return isNullable;
	}

	
	protected Object clone() throws CloneNotSupportedException {
		SimpleName newName = name != null ? (SimpleName)name.clone() : null;
		
		Type newType = type != null ? (Type)type.clone() : null;
		
		String newOccursOpt = occursOpt != null ? new String(occursOpt) : null;
		
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		
		Expression newInitializerOpt = initializerOpt != null ? (Expression)initializerOpt.clone() : null;
		
		return new StructureItem(newName, newType, Boolean.valueOf(isNullable), newOccursOpt, newSettingsBlockOpt, newInitializerOpt, getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		
		if (name != null) {
			buf.append(name);
			buf.append(' ');
		}
		
		if (type != null) {
			buf.append(type.toString());
		}
		
		if (occursOpt != null) {
			buf.append('[');
			buf.append(occursOpt);
			buf.append(']');
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
