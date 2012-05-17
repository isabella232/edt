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
	private CallbackTarget callbackOpt;
	private CallbackTarget errorCallbackOpt;
	
	public CallStatement(Expression expr, List exprs, SettingsBlock settingsBlockOpt, CallbackTarget callbackOpt, CallbackTarget errorCallbackOpt, int startOffset, int endOffset) {
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
		
		if(callbackOpt != null){
			this.callbackOpt = callbackOpt;
			this.callbackOpt.setParent(this);
		}
		
		if(errorCallbackOpt != null){
			this.errorCallbackOpt = errorCallbackOpt;
			this.errorCallbackOpt.setParent(this);
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
	
	public List<Node> getArguments() {
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
	
	public CallbackTarget getCallbackTarget(){
		return callbackOpt;
	}
	
	public CallbackTarget getErrorCallbackTarget() {
		return errorCallbackOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			if(exprs != null) {
				acceptChildren(visitor, exprs);	
			}			
			if(settingsBlockOpt != null) {
				settingsBlockOpt.accept(visitor);
			}
			if(callbackOpt != null){
				callbackOpt.accept(visitor);
			}
			if(errorCallbackOpt != null){
				errorCallbackOpt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
		
	protected Object clone() throws CloneNotSupportedException {
		List newArguments = exprs != null ? cloneList(exprs) : null;
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		CallbackTarget newCallbackOpt = callbackOpt != null ? (CallbackTarget)callbackOpt.clone() : null;
		CallbackTarget newErrorCallbackOpt = errorCallbackOpt != null ? (CallbackTarget)errorCallbackOpt.clone() : null;
		return new CallStatement((Expression)expr.clone(), newArguments, newSettingsBlockOpt, newCallbackOpt, newErrorCallbackOpt, getOffset(), getOffset() + getLength());
	}
	
}
