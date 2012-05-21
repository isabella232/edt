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

import java.util.List;

/**
 * ForwardStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ForwardStatement extends Statement {
	
	public static class ForwardTarget implements Cloneable {
		Expression expr;
		
		ForwardTarget( Expression expr ) {
			this.expr = expr;
		}
		
		boolean isToURL() { return false; }
		boolean isToLabel() { return false; }
		Expression getExpression() { return expr; }
		
		void setParent( Node parent ) { expr.setParent(parent); }
		void accept( IASTVisitor visitor ) { expr.accept(visitor); }
		
		protected Object clone() throws CloneNotSupportedException{
			return new ForwardTarget((Expression)expr.clone());
		}
	}
	
	public static class DefaultForwardTarget extends ForwardTarget {
		public DefaultForwardTarget( Expression expr ) {
			super(expr);
		}
		
		protected Object clone() throws CloneNotSupportedException{
			return new ForwardTarget((Expression)expr.clone());
		}
	}
	
	public static class ToLabelForwardTarget extends ForwardTarget {
		public ToLabelForwardTarget( Expression expr ) {
			super(expr);
		}
		
		boolean isToLabel() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			return new ToLabelForwardTarget((Expression)expr.clone());
		}
	}
	
	public static class ToURLForwardTarget extends ForwardTarget {
		public ToURLForwardTarget( Expression expr ) {
			super(expr);
		}
		
		boolean isToURL() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			return new ToURLForwardTarget((Expression)expr.clone());
		}
	}

	private List args;
	private ForwardTarget forwardTargetOpt;
	private List forwardOptions;	// List of Nodes

	public ForwardStatement(List args, ForwardTarget forwardTargetOpt, List forwardOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.args = setParent(args);		
		if(forwardTargetOpt != null) {
			this.forwardTargetOpt = forwardTargetOpt;
			forwardTargetOpt.setParent( this );
		}
		this.forwardOptions = setParent(forwardOptions);
	}
	
	public List<Node> getArguments() {
		return args;
	}
	
	public boolean isForwardToLabel() {
		return forwardTargetOpt != null && forwardTargetOpt.isToLabel();
	}
	
	public boolean isForwardToURL() {
		return forwardTargetOpt != null && forwardTargetOpt.isToURL();
	}
	
	public boolean hasForwardTarget() {
		return forwardTargetOpt != null;
	}
	
	public Expression getForwardTarget() {
		return forwardTargetOpt.getExpression();
	}
	
	public List<Node> getForwardOptions() {
		return forwardOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
		    acceptChildren(visitor, args);
		    if(forwardTargetOpt != null) forwardTargetOpt.accept(visitor);
			acceptChildren(visitor, forwardOptions);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException{
		ForwardTarget newForwardTargetOpt = forwardTargetOpt != null ? (ForwardTarget)forwardTargetOpt.clone() : null;
		return new ForwardStatement(cloneList(args), newForwardTargetOpt, cloneList(forwardOptions), getOffset(), getOffset() + getLength());
	}
}
