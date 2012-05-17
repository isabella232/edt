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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * OpenUIStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class OpenUIStatement extends Statement {

	private SettingsBlock settingsBlockOpt;
	private List expr_plus;	// List of Expressions
	private List bindOpt;	// List of Lists
	private boolean hasBindClause;
	private List eventBlocks;	// List of OnEventBlocks
	
	public OpenUIStatement(SettingsBlock settingsBlockOpt, List expr_plus, List bindOpt, List eventBlocks, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		this.expr_plus = setParent(expr_plus);
		if(bindOpt == null) {
			this.bindOpt = Collections.EMPTY_LIST;
			hasBindClause = false;
		}
		else {
			this.bindOpt = setParent(bindOpt);
			hasBindClause = true;
		}
		this.eventBlocks = setParent(eventBlocks);
	}
	
	public boolean hasOpenAttributes() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getOpenAttributes() {
		return settingsBlockOpt;
	}
	
	/**
	 * @return A list of Expression objects.
	 */
	public List<Node> getOpenableElements() {
		return expr_plus;
	}
	
	public boolean hasBindClause() {
		return hasBindClause;
	}
	
	/**
	 * @return A list of Expression objects.
	 */
	public List<Node> getBindClauseVariables() {
		return bindOpt;
	}
	
	public List<OnEventBlock> getEventBlocks() {
		return eventBlocks;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			if(settingsBlockOpt != null) settingsBlockOpt.accept(visitor);
			acceptChildren(visitor, expr_plus);
			acceptChildren(visitor, bindOpt);
			acceptChildren(visitor, eventBlocks);
		}
		visitor.endVisit(this);
	}
	
	public boolean canIncludeOtherStatements() {
		return true;
	}
	
	public List getStatementBlocks() {
		List result = new ArrayList();
		for(Iterator iter = eventBlocks.iterator(); iter.hasNext();) {
			result.add(((OnEventBlock) iter.next()).getStatements());
		}
		return result;
	}

	protected Object clone() throws CloneNotSupportedException {
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		List newBindOpt = hasBindClause ? cloneList(bindOpt) : null;
		
		return new OpenUIStatement(newSettingsBlockOpt, cloneList(expr_plus), newBindOpt, cloneList(eventBlocks), getOffset(), getOffset() + getLength());
	}
}
