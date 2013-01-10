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
 * ForUpdateWithIDClause AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ForUpdateWithIDClause extends ForUpdateClause {

	private String IDOpt;

	public ForUpdateWithIDClause(String IDOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.IDOpt = IDOpt;
	}
	
	public boolean hasID() {
		return IDOpt != null;
	}
	
	public String getID() {
		return IDOpt;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		String newIDOpt = IDOpt != null ? new String(IDOpt) : null;
		
		return new ForUpdateWithIDClause(newIDOpt, getOffset(), getOffset() + getLength());
	}
}
