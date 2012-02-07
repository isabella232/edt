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

public class CallSynchronizationValues extends Node {
	private CallbackTarget returnTo;
	private CallbackTarget onException;
	private CallReturns returns;

	public CallSynchronizationValues(CallbackTarget returnTo, CallbackTarget onException, CallReturns returns, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.returnTo = returnTo;
		this.onException = onException;
		this.returns = returns;
		
		if (returnTo != null) {
			returnTo.setParent(this);
		}
		
		if (onException != null) {
			onException.setParent(this);
		}
		
		if (returns != null) {
			returns.setParent(this);
		}
	}
	
	public CallbackTarget getReturnTo() {
		return returnTo;
	}


	public CallbackTarget getOnException() {
		return onException;
	}


	public CallReturns getReturns() {
		return returns;
	}

	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			
			if (getReturnTo() != null) {
				getReturnTo().accept(visitor);
			}
			if (getOnException() != null) {
				getOnException().accept(visitor);
			}
			if (getReturns() != null) {
				getReturns().accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		CallbackTarget newReturnTo = null;
		CallbackTarget newOnException = null;
		CallReturns newReturns = null;
		
		if (returnTo != null) {
			newReturnTo = (CallbackTarget)returnTo.clone();
		}
		if (onException != null) {
			newOnException = (CallbackTarget)onException.clone();
		}
		if (returns != null) {
			newReturns = (CallReturns) returns.clone();
		}
		
		return new CallSynchronizationValues(newReturnTo, newOnException, newReturns, getOffset(), getOffset() + getLength());
	}

}
