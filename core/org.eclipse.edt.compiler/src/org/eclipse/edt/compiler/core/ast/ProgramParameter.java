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

/**
 * ProgramParameter AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ProgramParameter extends Parameter {

	public ProgramParameter(SimpleName name, Type type, int startOffset, int endOffset) {
		super(name, type, startOffset, endOffset);
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			name.accept(visitor);
			type.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ProgramParameter((SimpleName)name.clone(), (Type)type.clone(), getOffset(), getOffset() + getLength());
	}
}
