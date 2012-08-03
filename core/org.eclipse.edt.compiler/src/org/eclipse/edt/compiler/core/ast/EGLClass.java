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

import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;


public class EGLClass extends Part {

	private Name partSubTypeOpt;
	private List implementsOpt;
	private Name extendsOpt;


	public EGLClass(Boolean privateAccessModifierOpt, SimpleName name, Name extendsOpt, List implementsOpt, Name partSubTypeOpt, List classContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, classContents, startOffset, endOffset);
		
		if(extendsOpt != null) {
			this.extendsOpt = extendsOpt;
			extendsOpt.setParent(this);
		}

		if(partSubTypeOpt != null) {
			this.partSubTypeOpt = partSubTypeOpt;
			partSubTypeOpt.setParent(this);
		}
		this.implementsOpt = setParent(implementsOpt);
	}
	
	public boolean hasSubType() {
		return partSubTypeOpt != null;
	}
	
	public Name getSubType() {
		return partSubTypeOpt;
	}
	
	public Name getExtends() {
		return extendsOpt;
	}
	
	public boolean isGeneratable(){
		return true;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			if(extendsOpt != null) extendsOpt.accept(visitor);
			acceptChildren(visitor, implementsOpt);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newExtendsOpt = extendsOpt != null ? (Name)extendsOpt.clone() : null;
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		return new EGLClass(new Boolean(isPrivate), (SimpleName)name.clone(), newExtendsOpt, cloneList(implementsOpt), newPartSubTypeOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_CLASS;
	}
	
	public int getPartType() {
		return CLASS;
	}

	/**
	 * @return A List of Name objects
	 */
	public List<Name> getImplementedInterfaces() {
		return implementsOpt;
	}
}
