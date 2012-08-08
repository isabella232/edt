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

import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * BooleanLiteral AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class BooleanLiteral extends LiteralExpression {

	private Boolean value;

	public BooleanLiteral(Boolean value, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.value = value;
	}
	
	public Boolean booleanValue() {
		return value;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return BOOLEAN_LITERAL;
	}
	
	public String getCanonicalString() {
		return Boolean.YES == value ? IEGLConstants.KEYWORD_YES : IEGLConstants.KEYWORD_NO;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new BooleanLiteral(value, getOffset(), getOffset() + getLength());
	}
	
	public String getValue() {
		return booleanValue().toString();
	}
}
