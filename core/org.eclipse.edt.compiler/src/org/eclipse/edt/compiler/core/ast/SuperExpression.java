/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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


/**
 * SuperExpression AST node type.
 */
public class SuperExpression extends Expression {
    
	public SuperExpression(int startOffset, int endOffset) {
		super(startOffset, endOffset);		
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public String getCanonicalString() {
		return IEGLConstants.KEYWORD_SUPER;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new SuperExpression(getOffset(), getOffset() + getLength());
	}
}
