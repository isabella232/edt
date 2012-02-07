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

import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.binding.CallStatementBinding;


/**
 * CallStatement AST node type.
 * 
 * For name and arguments, use getName() and getArguments()
 * 
 * For norefresh and externallydefined, invoke accept() with an
 * IASTVisitor that overrides visit() for the following types:
 *  - ExternallyDefinedClause
 *  - NoRefreshClause 
 *
 * @author Albert Ho
 * @author David Murray
 */
public class CallStatement extends Statement {

	private Expression expr;
	private List exprs;	// List of Expressions
	private SettingsBlock settingsBlockOpt;
	private Expression usingOpt;
	private CallSynchronizationValues callSyncOpt;
	
	private CallStatementBinding statementBinding;

	public CallStatement(Expression expr, List exprs, Expression usingOpt, CallSynchronizationValues callSyncOpt, SettingsBlock settingsBlockOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		/*
		 * When there is an empty argument list (eg. call pgm() ...), the parser produces
		 * a function invocation instead of a name for the 'expr' in the statement. When
		 * there is one or more arguments, this does not occur. The following accounts for
		 * the first case.
		 */
		if(exprs == null && expr instanceof FunctionInvocation) {
			FunctionInvocation fInvocation = (FunctionInvocation) expr;
			expr = fInvocation.getTarget();
			exprs = fInvocation.getArguments();
		}
		
		this.expr = expr;
		expr.setParent(this);
		if(exprs != null) {
			this.exprs = setParent(exprs);
		}
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		
		if (usingOpt != null) {
			this.usingOpt = usingOpt;
			this.usingOpt.setParent(this);
		}
		
		if(callSyncOpt != null){
			this.callSyncOpt = callSyncOpt;
			this.callSyncOpt.setParent(this);
		}
				
	}
	
	public Expression getInvocationTarget() {
		return expr;
	}
	
	/**
	 * @deprecated Use getInvocationTarget() instead
	 */
	public Name getName() {
		throw new RuntimeException();
	}
	
	public boolean hasArguments() {
		return exprs != null && !exprs.isEmpty();
	}
	
	public List getArguments() {
		return exprs;
	}
	
	/**
	 * @deprecated Options are specified in settings block now. This returns empty list.
	 */
	public List getCallOptions() {
		return Collections.EMPTY_LIST;
	}
	
	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}
	
	public CallSynchronizationValues getCallSynchronizationValues(){
		return callSyncOpt;
	}

	public Expression getUsing(){
		return usingOpt;
	}

	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			if(exprs != null) {
				acceptChildren(visitor, exprs);	
			}			
			if(usingOpt != null){
				usingOpt.accept(visitor);
			}
			if(callSyncOpt != null){
				callSyncOpt.accept(visitor);
			}
			if(settingsBlockOpt != null) {
				settingsBlockOpt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
		
	protected Object clone() throws CloneNotSupportedException {
		List newArguments = exprs != null ? cloneList(exprs) : null;
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		Expression newUsingOpt = usingOpt != null ? (Expression)usingOpt.clone() : null;
		CallSynchronizationValues newCallSyncOpt = callSyncOpt != null ? (CallSynchronizationValues)callSyncOpt.clone() : null;
		return new CallStatement((Expression)expr.clone(), newArguments, newUsingOpt, newCallSyncOpt, newSettingsBlockOpt, getOffset(), getOffset() + getLength());
	}
	
	public CallStatementBinding getStatementBinding() {
        return statementBinding;
    }
	
    public void setStatementBinding(CallStatementBinding statementBinding) {
        this.statementBinding = statementBinding;
    }
}
