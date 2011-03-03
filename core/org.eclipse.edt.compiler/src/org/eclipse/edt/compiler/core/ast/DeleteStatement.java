/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.internal.dli.DLIInfo;
import org.eclipse.edt.compiler.internal.sql.SQLInfo;


/**
 * DeleteStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class DeleteStatement extends Statement implements IDliIOStatement {

	private Expression expr;
	private List deleteOptions;
	private SQLInfo sqlInfo;
	private DLIInfo dliInfo;

	public DeleteStatement(Expression expr, List deleteOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.deleteOptions = setParent(deleteOptions);
	}
	
	public Expression getTarget() {
		return expr;
	}
	
	public List getOptions() {
		return deleteOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			acceptChildren(visitor, deleteOptions);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return Arrays.asList(new Expression[] {expr});
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new DeleteStatement((Expression)expr.clone(), cloneList(deleteOptions), getOffset(), getOffset() + getLength());
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

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.core.ast.IDliIoStatement#getTargets()
     */
    public List getTargets() {
        List list = new ArrayList();
        list.add(getTarget());
        return list;
    }
}
