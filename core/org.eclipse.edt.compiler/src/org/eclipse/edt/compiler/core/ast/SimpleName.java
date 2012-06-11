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

import java.util.ArrayList;
import java.util.List;


/**
 * SimpleName AST node type.
 * 
 * @author Albert Ho
 * @author David Murray
 */
public class SimpleName extends Name {

	public SimpleName(String ID, int startOffset, int endOffset) {
		super(ID, startOffset, endOffset);
	}

	public boolean isSimpleName() {
		return true;
	}

	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public String getCanonicalName() {
		return identifier;
	}

	protected StringBuffer getCanonicalNameBuffer() {
		return new StringBuffer(identifier);
	}

	public String getNameComponents() {
		return getIdentifier();
	}

	protected List getNameComponentsList() {
		ArrayList list = new ArrayList();
		list.add(identifier);
		return list;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new SimpleName(identifier, getOffset(), getOffset() + getLength());
	}
}
