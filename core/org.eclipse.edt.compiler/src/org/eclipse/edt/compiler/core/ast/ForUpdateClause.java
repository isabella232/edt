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

/**
 * @author Dave Murray
 */
public abstract class ForUpdateClause extends Node {
	
	public ForUpdateClause(int startOffset, int endOffset) {
		super(startOffset, endOffset);
	}
	
	public boolean hasID() {
		return false;
	}
	
	public String getID() {
		return null;
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	protected abstract Object clone() throws CloneNotSupportedException;
}
