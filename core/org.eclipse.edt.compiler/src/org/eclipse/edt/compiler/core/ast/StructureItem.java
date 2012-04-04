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


/**
 * StructureItem AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class StructureItem extends Node {

	private String levelOpt;
	private SimpleName name;
	private Type type;
	private SettingsBlock settingsBlockOpt;
	private Expression initializerOpt;
	String occursOpt;
	private boolean isFiller;
	private boolean isEmbedded;
	//Since there is no node to attach binding for filler items...also used to hold the structure imported for
	// an embed.
	private IBinding binding;

	public StructureItem(String levelOpt, SimpleName name, Type type, String occursOpt, SettingsBlock settingsBlockOpt, Expression initializerOpt, boolean isFiller, boolean isEmbedded, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.levelOpt = levelOpt;
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
		this.isFiller = isFiller;
		this.isEmbedded = isEmbedded;
	}
	
	public boolean hasLevel() {
		return levelOpt != null;
	}
	
	public String getLevel() {
		return levelOpt;
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
	
	public boolean isFiller() {
		return isFiller;
	}
	
	public boolean isEmbedded() {
		return isEmbedded;
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
	
	public IBinding resolveBinding() {
		if(binding != null) {
			return binding;
		}
		if(!isFiller && !isEmbedded) {
			return getName().resolveBinding();
		}
		return null;
    }
    
    public void setBinding(IBinding binding) {
        this.binding = binding;
    }
	
	protected Object clone() throws CloneNotSupportedException {
		String newLevelOpt = levelOpt != null ? new String(levelOpt) : null;
		
		SimpleName newName = name != null ? (SimpleName)name.clone() : null;
		
		Type newType = type != null ? (Type)type.clone() : null;
		
		String newOccursOpt = occursOpt != null ? new String(occursOpt) : null;
		
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		
		Expression newInitializerOpt = initializerOpt != null ? (Expression)initializerOpt.clone() : null;
		
		return new StructureItem(newLevelOpt, newName, newType, newOccursOpt, newSettingsBlockOpt, newInitializerOpt, isFiller, isEmbedded, getOffset(), getOffset() + getLength());
	}
}
