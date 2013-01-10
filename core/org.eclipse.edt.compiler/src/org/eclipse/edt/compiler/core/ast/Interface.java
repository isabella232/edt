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

import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * Interface AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Interface extends Part {

	private List<Name> extendsOpt;
	private boolean hasExplicitAbstractModifier;

	public Interface(Boolean privateAccessModifierOpt, Boolean explicitAbstractModifier, SimpleName name, List extendsOpt, List interfaceContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, interfaceContents, startOffset, endOffset);

		this.extendsOpt = setParent(extendsOpt);
		this.hasExplicitAbstractModifier = explicitAbstractModifier.booleanValue();
	}
	
	public boolean hasSubType() {
		return false;
	}

	public boolean hasExtendedType() {
		return extendsOpt != null && !extendsOpt.isEmpty();
	}

	/**
	 * @return A List of Name objects
	 */
	public List<Name> getExtendedTypes() {
		return extendsOpt;
	}

	public Name getSubType() {
		return null;
	}
	
	@Override
	public boolean isAbstract() {
		return true;
	}
	
	public boolean hasExplicitAbstractModifier() {
		return hasExplicitAbstractModifier;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, extendsOpt);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new Interface(new Boolean(isPrivate), new Boolean(hasExplicitAbstractModifier), (SimpleName)name.clone(), cloneList(extendsOpt), cloneContents(), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_INTERFACE;
	}
	
	public int getPartType() {
		return INTERFACE;
	}
	
	@Override
	public String toString() {
		return (hasExplicitAbstractModifier ? "abstract " : "") + getPartTypeName() + " " + getName();
	}
}
