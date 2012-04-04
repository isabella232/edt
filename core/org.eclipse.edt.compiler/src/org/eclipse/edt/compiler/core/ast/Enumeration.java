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

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * Enumeration AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Enumeration extends Part {

	private SettingsBlock settingsBlockOpt;

	public Enumeration(Boolean privateAccessModifierOpt, SimpleName name, SettingsBlock settingsBlockOpt, List enumerationFields, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, enumerationFields, startOffset, endOffset);
		
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
	}
	
	public List getFields() {
		return contents;
	}
	
	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, contents);
			if(settingsBlockOpt != null) {
				settingsBlockOpt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlock = settingsBlockOpt == null ? null : (SettingsBlock) settingsBlockOpt.clone();
		return new Enumeration(new Boolean(isPrivate), (SimpleName)name.clone(), newSettingsBlock, cloneContents(), getOffset(), getOffset() + getLength());
	}
	
	public String getPartTypeName() {
		return IEGLConstants.KEYWORD_ENUMERATION;
	}
	
	public int getPartType() {
		return ENUMERATION;
	}
	
	public boolean hasSubType() {
		return false;
	}

	public Name getSubType() {
		return null;
	}
}
