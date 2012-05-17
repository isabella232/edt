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

import java.util.Arrays;
import java.util.List;


/**
 * ShowStatement AST node type.
 *
 * For target, use getPageRecordOrForm().
 * 
 * For clauses, invoke accept() with an IASTVisitor that overrides
 * visit() for the following types:
 *  - ReturningToInvocationTargetClause
 *  - PassingClause
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ShowStatement extends Statement {

	private Expression expr;
	private List showOptions;	// List of Nodes
	private SettingsBlock settingsBlockOpt;
	
	public ShowStatement(Expression expr, List showOptions, SettingsBlock settingsBlockOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.showOptions = setParent(showOptions);
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
	}
	
	public Expression getPageRecordOrForm() {
		return expr;
	}
	
	public List<Node> getShowOptions() {
		return showOptions;
	}
	
	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			acceptChildren(visitor, showOptions);
			if(settingsBlockOpt != null) {
				settingsBlockOpt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return Arrays.asList(new Expression[] {expr});
	}	
	
	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		return new ShowStatement((Expression)expr.clone(), cloneList(showOptions), newSettingsBlockOpt, getOffset(), getOffset() + getLength());
	}
	
}
