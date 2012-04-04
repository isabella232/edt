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
 * Interface AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ExternalType extends Part {

	private List extendsOpt;
	private Name partSubTypeOpt;

	public ExternalType(Boolean privateAccessModifierOpt, SimpleName name, List extendsOpt, Name partSubTypeOpt, List externalTypeContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, externalTypeContents, startOffset, endOffset);
		
		this.extendsOpt = setParent(extendsOpt);
		
		if(partSubTypeOpt != null) {
			this.partSubTypeOpt = partSubTypeOpt;
			partSubTypeOpt.setParent(this);
		}
	}
	
	public boolean hasExtendedType() {
		return extendsOpt != null && !extendsOpt.isEmpty();
	}
	
	/**
	 * @return A List of Name objects
	 */
	public List getExtendedTypes() {
		return extendsOpt;
	}
	
	public boolean hasSubType() {
		return partSubTypeOpt != null;
	}
	
	public Name getSubType() {
		return partSubTypeOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, extendsOpt);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		return new ExternalType(new Boolean(isPrivate), (SimpleName)name.clone(), cloneList(extendsOpt), newPartSubTypeOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_EXTERNALTYPE;
	}
	
	public int getPartType() {
		return EXTERNALTYPE;
	}
}
