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
import java.util.Arrays;
import java.util.List;

import org.eclipse.edt.compiler.internal.sql.SQLInfo;


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
	private SQLInfo sqlInfo;

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
     * @see org.eclipse.edt.compiler.core.ast.IDliIoStatement#getTargets()
     */
    public List getTargets() {
        List list = new ArrayList();
        list.add(getRecord());
        return list;
    }
    public SQLInfo getSqlInfo() {
        return sqlInfo;
    }
    public void setSqlInfo(SQLInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }
}
