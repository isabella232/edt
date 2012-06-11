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



/**
 * TransferStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class TransferStatement extends Statement {
	
	/**
 	 * Transfer targets (typesafe enumeration)
 	 */
	public static class TransferTarget {
		
		private TransferTarget() {}
		
		public static final TransferTarget PROGRAM = new TransferTarget();//$NON-NLS-1$
		public static final TransferTarget TRANSACTION = new TransferTarget();//$NON-NLS-1$
	}

	private TransferTarget transferTargetOpt;
	private Expression expr;
	private Expression passingRecordOpt;
	private SettingsBlock settingsBlockOpt;
	
	public TransferStatement(TransferTarget transferTargetOpt, Expression expr, Expression passingRecordOpt, SettingsBlock settingsBlockOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.transferTargetOpt = transferTargetOpt;
		this.expr = expr;
		expr.setParent(this);
		if(passingRecordOpt != null) {
			this.passingRecordOpt = passingRecordOpt;
			passingRecordOpt.setParent(this);
		}
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
	}
	
	public boolean isToProgram() {
		return transferTargetOpt == TransferTarget.PROGRAM || transferTargetOpt == null;
	}
	
	public boolean isToTransaction() {
		return transferTargetOpt == TransferTarget.TRANSACTION;
	}
	
	public Expression getInvocationTarget() {
		return expr;
	}
	
	/**
	 * @deprecated Use getInvocationTarget() instead
	 */
	public Name getTargetName() {
		throw new RuntimeException();
	}
	
	public boolean hasPassingRecord() {
		return passingRecordOpt != null;
	}
	
	public Expression getPassingRecord() {
		return passingRecordOpt;
	}
	
	/**
	 * @deprecated Options are specified in settings block now. This returns false.
	 */
	public boolean isExternallyDefined() {
		return false;
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
			if(passingRecordOpt != null) passingRecordOpt.accept(visitor);
			if(settingsBlockOpt != null) settingsBlockOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Expression newPassingRecordOpt = passingRecordOpt != null ? (Expression)passingRecordOpt.clone() : null;
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		
		return new TransferStatement(transferTargetOpt, (Expression)expr.clone(), newPassingRecordOpt, newSettingsBlockOpt, getOffset(), getOffset() + getLength());
	}
}
