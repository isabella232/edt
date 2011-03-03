/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

	public Interface(Boolean privateAccessModifierOpt, SimpleName name, List interfaceContents, int startOffset, int endOffset) {
		super(privateAccessModifierOpt, name, interfaceContents, startOffset, endOffset);
	}
	
	public boolean hasSubType() {
		return false;
	}
	
	public Name getSubType() {
		return null;
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
		return new Interface(new Boolean(isPrivate), (SimpleName)name.clone(), cloneContents(), getOffset(), getOffset() + getLength());
	}

	public String getPartTypeName() {
		return  IEGLConstants.KEYWORD_INTERFACE;
	}
	
	public int getPartType() {
		return INTERFACE;
	}
}
