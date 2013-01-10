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
import java.util.Arrays;
import java.util.List;

/**
 * ReplaceStatement AST node type.
 * 
 * For target, use getRecord().
 * 
 * For clauses, invoke accept() with an IASTVisitor that overrides
 * visit() for the following types:
 *  - ExternallyDefinedClause
 *  - PassingClause 
 *  - ReturningToNameClause
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ReplaceStatement extends Statement {

	private Expression expr;
	private List replaceOptions;	// List of Nodes

	public ReplaceStatement(Expression expr, List replaceOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.replaceOptions = setParent(replaceOptions);
	}
	
	public Expression getRecord() {
		return expr;
	}
	
	public List getReplaceOptions() {
		return replaceOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			acceptChildren(visitor, replaceOptions);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return Arrays.asList(new Expression[] {expr});
	}
	

	protected Object clone() throws CloneNotSupportedException {
		return new ReplaceStatement((Expression)expr.clone(), cloneList(replaceOptions), getOffset(), getOffset() + getLength());
	}

    /* (non-Javadoc)
     */
    public List getTargets() {
        List list = new ArrayList();
        list.add(getRecord());
        return list;
    }
}
