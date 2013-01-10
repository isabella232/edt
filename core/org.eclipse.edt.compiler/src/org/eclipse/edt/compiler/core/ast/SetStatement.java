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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SetStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class SetStatement extends Statement {

	private List setTarget_plus;	// List of Expressions
	private List id_plus;	// List of Strings

	public SetStatement(List setTarget_plus, List id_plus, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.setTarget_plus = setParent(setTarget_plus);
		this.id_plus = id_plus;
	}
	
	/**
	 * @return A list of Expression objects
	 */
	public List getSetTargets() {
		return setTarget_plus;
	}
	
	/**
	 * @return A list of String objects
	 */
	public List getStates() {
		return id_plus;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, setTarget_plus);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException{
		ArrayList newId_Plus = new ArrayList();
		for (Iterator iter = id_plus.iterator(); iter.hasNext();) {
			newId_Plus.add(new String((String)iter.next()));
		}
		return new SetStatement(cloneList(setTarget_plus), newId_Plus, getOffset(), getOffset() + getLength());
	}
}
