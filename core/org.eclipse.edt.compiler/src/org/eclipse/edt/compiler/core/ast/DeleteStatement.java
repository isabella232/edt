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
import java.util.Collections;
import java.util.List;

/**
 * DeleteStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class DeleteStatement extends Statement {

	private Expression expr;
	private FromOrToExpressionClause dataSource;
	private List deleteOptions;

	public DeleteStatement(Expression expr, FromOrToExpressionClause dataSource, List deleteOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		if (expr != null) {
			expr.setParent(this);
		}
		
		this.dataSource = dataSource;
		dataSource.setParent(this);
		
		this.deleteOptions = setParent(deleteOptions);
	}
	
	public Expression getTarget() {
		return expr;
	}
	
	public FromOrToExpressionClause getDataSource() {
		return dataSource;
	}

	public List getOptions() {
		return deleteOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			
			if (expr != null) {
				expr.accept(visitor);
			}
			dataSource.accept(visitor);
			acceptChildren(visitor, deleteOptions);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		if (expr == null) {
			return Collections.EMPTY_LIST;
		}
		return Arrays.asList(new Expression[] {expr});
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Expression exprClone = null;
		if (expr != null) {
			exprClone = (Expression)expr.clone();
		}
		return new DeleteStatement(exprClone, (FromOrToExpressionClause)dataSource.clone(), cloneList(deleteOptions), getOffset(), getOffset() + getLength());
	}

    /* (non-Javadoc)
     */
    public List getTargets() {
        List list = new ArrayList();
        if (getTarget() != null) {
        	list.add(getTarget());
        }
        return list;
    }
}
