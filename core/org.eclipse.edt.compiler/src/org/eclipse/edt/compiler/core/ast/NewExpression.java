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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.egl.Constructor;


/**
 * NewExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class NewExpression extends Expression {

	private Type type;
	private SettingsBlock settingsBlockOpt;
	
	private org.eclipse.edt.mof.egl.Constructor constructor;

	public NewExpression(Type type, SettingsBlock settingsBlockOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.type = type;
		type.setParent(this);
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
	}
	
	public Type getType() {
		return type;
	}
	
    public String getCanonicalString() {
  		return IEGLConstants.KEYWORD_NEW + " " + type.getCanonicalName();
    }
    
	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public org.eclipse.edt.mof.egl.Constructor resolveConstructor() {
		return constructor;
	}
	
	public void setConstructor(Constructor constructor) {
		this.constructor = constructor;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			type.accept(visitor);
			if(settingsBlockOpt != null) settingsBlockOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		
		return new NewExpression((Type)type.clone(), newSettingsBlockOpt, getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append("new ");
		buf.append(type.toString());
		if (settingsBlockOpt != null) {
			buf.append(settingsBlockOpt.toString());
		}
		return buf.toString();
	}
}
