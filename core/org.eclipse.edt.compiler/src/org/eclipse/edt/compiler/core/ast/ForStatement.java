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

import java.util.Arrays;
import java.util.List;

/**
 * ForStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ForStatement extends Statement {
	
	public static class ForStep implements Cloneable{
		Expression expr;
		
		public ForStep( Expression expr ) {
			this.expr = expr;
		}
		
		boolean isIncrement() { return false; }
		boolean isDecrement() { return false; }
		
		Expression getExpression() {
			return expr;
		}
		
		void setParent( Node parent ) {
			expr.setParent( parent );
		}
		
		void accept( IASTVisitor visitor ) {
			expr.accept( visitor );
		}
		
		protected Object clone() throws CloneNotSupportedException{
			return new ForStep((Expression)expr.clone());
		}
	}
	
	public static class IncrementForStep extends ForStep {
		public IncrementForStep(Expression expr) { super( expr ); }		
		boolean isIncrement() { return true; }
		
		protected Object clone() throws CloneNotSupportedException{
			return new IncrementForStep((Expression)expr.clone());
		}
	}
	
	public static class DecrementForStep extends ForStep {
		public DecrementForStep(Expression expr) { super(expr); }
		boolean isDecrement() { return true; }
		
		protected Object clone() throws CloneNotSupportedException{
			return new DecrementForStep((Expression)expr.clone());
		}
	}

	private Expression lvalue;
	private Expression fromExprOpt;
	private Expression expr;
	private ForStep stepOpt;
	private List stmts;	// List of Nodes
	private SimpleName declarationName;
	private Type declarationType;

	public ForStatement(Expression lvalue, SimpleName declarationName, Type declarationType, Expression fromExprOpt, Expression expr, ForStep stepOpt, List stmts, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if(lvalue != null) {
			this.lvalue = lvalue;
			lvalue.setParent(this);
		}
		if(declarationName != null) {
			this.declarationName = declarationName;
			declarationName.setParent(this);
		}
		if(declarationType != null) {
			this.declarationType = declarationType;
			declarationType.setParent(this);
		}
		if(fromExprOpt != null) {
			this.fromExprOpt = fromExprOpt;
			fromExprOpt.setParent(this);
		}
		this.expr = expr;
		expr.setParent(this);
		if(stepOpt != null) {
			this.stepOpt = stepOpt;
			stepOpt.setParent(this);
		}
		this.stmts = setParent(stmts);
	}
	
	public boolean hasVariableDeclaration() {
		return declarationName != null;
	}
	
	/**
	 * Returns the counter expression if this is a for statement that does not
	 * include a variable declaration. Otherwise, returns null.
	 */
	public Expression getCounterVariable() {
		return lvalue;
	}
	
	/**
	 * Returns the declaration name if this is a for statement that
	 * include a variable declaration. Otherwise, returns null.
	 */
	public Name getVariableDeclarationName() {
		return declarationName;
	}
	
	/**
	 * Returns the declaration type if this is a for statement that
	 * include a variable declaration. Otherwise, returns null.
	 */
	public Type getVariableDeclarationType() {
		return declarationType;
	}
	
	public boolean hasFromIndex() {
		return fromExprOpt != null;
	}
	
	public Expression getFromIndex() {
		return fromExprOpt;
	}
	
	public Expression getEndIndex() {
		return expr;
	}
	
	public boolean hasPositiveDelta() {
		return stepOpt != null && stepOpt.isIncrement();
	}
	
	public boolean hasNegativeDelta() {
		return stepOpt != null && stepOpt.isDecrement();
	}
	
	public Expression getDeltaExpression() {
		return stepOpt == null ? null : stepOpt.getExpression();
	}
	
	public List<Node> getStmts() {
		return stmts;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			if(lvalue == null) {
				declarationName.accept(visitor);
				declarationType.accept(visitor);
			}
			else {
				lvalue.accept(visitor);				
			}
			if(fromExprOpt != null) fromExprOpt.accept(visitor);
			expr.accept(visitor);
			if(stepOpt != null) stepOpt.accept(visitor);
			acceptChildren(visitor, stmts);
		}
		visitor.endVisit(this);
	}
	
	public boolean canIncludeOtherStatements() {
		return true;
	}
	
	public List getStatementBlocks() {
		return Arrays.asList(new List[] {stmts});
	}
	
	protected Object clone() throws CloneNotSupportedException{
		Expression newFromExprOpt = fromExprOpt != null ? (Expression)fromExprOpt.clone() : null;
		
		ForStep newStepOpt = stepOpt != null ? (ForStep)stepOpt.clone() : null;
		
		Expression lval = lvalue == null ? null : (Expression) lvalue.clone();
		SimpleName declName = declarationName == null ? null : (SimpleName) declarationName.clone();
		Type declType = declarationType == null ? null : (Type) declarationType.clone();
		return new ForStatement(lval, declName, declType, newFromExprOpt, (Expression)expr.clone(), newStepOpt, cloneList(stmts), getOffset(), getOffset() + getLength());
	}
}
