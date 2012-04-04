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

import java.util.List;

import org.eclipse.edt.compiler.internal.dli.DLIInfo;
import org.eclipse.edt.compiler.internal.sql.SQLInfo;


/**
 * AddStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class AddStatement extends Statement implements IDliIOStatement{

	private List expr_plus;	// List of Expressions
	private List addOptions;	// List of Nodes
	private DLIInfo dliInfo;
	private SQLInfo sqlInfo;

	public AddStatement(List expr_plus, List addOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr_plus = setParent(expr_plus);
		this.addOptions = setParent(addOptions);
	}
	
	public List getTargets() {
		return expr_plus;
	}
	
	public List getOptions() {
		return addOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, expr_plus);
			acceptChildren(visitor, addOptions);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return expr_plus;
	}
	
	public List getIOClauses() {
		return addOptions;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new AddStatement(cloneList(expr_plus), cloneList(addOptions), getOffset(), getOffset() + getLength());
	}
    public DLIInfo getDliInfo() {
        return dliInfo;
    }
    public void setDliInfo(DLIInfo dliInfo) {
        this.dliInfo = dliInfo;
    }
    public SQLInfo getSqlInfo() {
        return sqlInfo;
    }
    public void setSqlInfo(SQLInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }
}
