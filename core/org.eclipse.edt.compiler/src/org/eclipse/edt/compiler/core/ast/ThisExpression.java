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

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;


/**
 * ThisExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ThisExpression extends Expression {
    
	private IDataBinding dataBinding;

	public ThisExpression(int startOffset, int endOffset) {
		super(startOffset, endOffset);		
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public String getCanonicalString() {
		return IEGLConstants.KEYWORD_THIS;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ThisExpression(getOffset(), getOffset() + getLength());
	}
	
	public IDataBinding resolveDataBinding() {
		return dataBinding;
	}
	
	public void setDataBinding(IDataBinding dataBinding) {
		this.dataBinding = dataBinding;
	}
	

}
