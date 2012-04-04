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


/**
 * Handler AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Handler extends Part {

	private Name partSubTypeOpt;
	private List implementsOpt;


	public Handler(Boolean privateAccessModifierOpt, SimpleName name, List implementsOpt, Name partSubTypeOpt, List classContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, classContents, startOffset, endOffset);
		
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
	
	public boolean isGeneratable(){
		return true;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, implementsOpt);
			if(partSubTypeOpt != null) partSubTypeOpt.accept(visitor);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Name newPartSubTypeOpt = partSubTypeOpt != null ? (Name)partSubTypeOpt.clone() : null;
		
		return new Handler(new Boolean(isPrivate), (SimpleName)name.clone(), cloneList(implementsOpt), newPartSubTypeOpt, cloneContents(), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_HANDLER;
	}
	
	public int getPartType() {
		return HANDLER;
	}

	/**
	 * @return A List of Name objects
	 */
	public List getImplementedInterfaces() {
		return implementsOpt;
	}
}
