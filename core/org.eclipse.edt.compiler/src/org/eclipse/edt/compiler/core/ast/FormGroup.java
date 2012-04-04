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
 * FormGroup AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class FormGroup extends Part {

	public FormGroup(Boolean privateAccessModifierOpt, SimpleName name, List formGroupContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, formGroupContents, startOffset, endOffset);
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			acceptChildren(visitor, contents);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new FormGroup(new Boolean(isPrivate), (SimpleName)name.clone(), cloneContents(), getOffset(), getOffset() + getLength());
	}
	
	public boolean isGeneratable(){
		return true;
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_FORMGROUP;
	}
	
	public int getPartType() {
		return FORMGROUP;
	}
	
	public boolean hasSubType() {
		return false;
	}

	public Name getSubType() {
		return null;
	}
}
