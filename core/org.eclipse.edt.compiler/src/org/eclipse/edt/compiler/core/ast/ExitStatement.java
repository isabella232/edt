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
 * ExitStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ExitStatement extends Statement {
		
	public static abstract class ExitModifier implements Cloneable{
		
		public boolean isProgramExitModifier() { return false; }
		public boolean isRunUnitExitModifier() { return false; }
		public boolean isStackExitModifier() { return false; }	
		public boolean isLabelExitModifier() { return false; }
		
		/**
		 * Optional expression. Should currently only be implemented by
		 * ProgramExitModifier and RununitExitModifier.
		 */
		public Expression getExpression() { return null; }
		
		
		/**
		 * Optional label. Should currently only be implemented by
		 * StackExitModifier and LabelExitModifier.
		 */
		public String getLabel() { return null; }
		
		/**
		 * Sets the parent of any nodes contained within modifier to the
		 * argument Node. Should currently only be implemented by
		 * ProgramExitModifier.
		 */
		public void setParent( Node parent ) {}
		
		/**
		 * Allow visitor to traverse any nodes contained within modifier
		 * to the argument Node. Should currently only be implemented by
		 * ProgramExitModifier.
		 */
		public void accept( IASTVisitor visitor ) {}
		
		protected abstract Object clone() throws CloneNotSupportedException;
	}
	
	/**
 	 * Exit modifiers (typesafe enumeration). The other exit modifiers are
 	 * PROGRAM and STACK. These two are defined in their own classes
 	 * because they can include additional information.
 	 */
	public static class DefaultExitModifier extends ExitModifier {
		
		private DefaultExitModifier() {
		}
		
		public static final DefaultExitModifier CASE = new DefaultExitModifier();
		public static final DefaultExitModifier IF = new DefaultExitModifier();
		public static final DefaultExitModifier WHILE = new DefaultExitModifier();
		public static final DefaultExitModifier FOR = new DefaultExitModifier();
		public static final DefaultExitModifier FOREACH = new DefaultExitModifier();
		
		protected Object clone() throws CloneNotSupportedException{
			return this;
		}
	}
	
	public static class StackExitModifier extends ExitModifier {

		private String label;

		public StackExitModifier(String simpleNameOpt) {
			this.label = simpleNameOpt;
		}		

		public boolean isStackExitModifier() {
			return true;
		}
		
		public String getLabel() {
			return label;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			String newLabel = label != null ? new String(label) : null;
			
			return new StackExitModifier(newLabel);
		}
	}
	
	public static class LabelExitModifier extends ExitModifier {

		private String label;

		public LabelExitModifier(String simpleNameOpt) {
			this.label = simpleNameOpt;
		}		
		
		public boolean isLabelExitModifier() {
			return true;
		}
		
		public String getLabel() {
			return label;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			String newLabel = label != null ? new String(label) : null;
			
			return new LabelExitModifier(newLabel);
		}
	}
	
	public static abstract class OptionalExpressionExitModifier extends ExitModifier {
		protected Expression parenthesizedExprOpt;

		protected OptionalExpressionExitModifier(Expression parenthesizedExprOpt) {
			if(parenthesizedExprOpt != null) {
				this.parenthesizedExprOpt = parenthesizedExprOpt;
			}
		}
		
		public Expression getExpression() {
			return parenthesizedExprOpt;
		}
		
		public void setParent(Node parent) {
			if( parenthesizedExprOpt != null ) {
				parenthesizedExprOpt.setParent( parent );
			}
		}
		
		public void accept(IASTVisitor visitor) {		
			if(parenthesizedExprOpt != null) parenthesizedExprOpt.accept(visitor);
		}
	}
	
	public static class ProgramExitModifier extends OptionalExpressionExitModifier {		
		public ProgramExitModifier(Expression parenthesizedExprOpt) {
			super(parenthesizedExprOpt);
		}
		
		public boolean isProgramExitModifier() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			Expression newParenthesizedExprOpt = parenthesizedExprOpt != null ? (Expression)parenthesizedExprOpt.clone() : null;			
			return new ProgramExitModifier(newParenthesizedExprOpt);
		}
	}
	
	public static class RunUnitExitModifier extends OptionalExpressionExitModifier {		
		public RunUnitExitModifier(Expression parenthesizedExprOpt) {
			super(parenthesizedExprOpt);
		}
		
		public boolean isRunUnitExitModifier() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			Expression newParenthesizedExprOpt = parenthesizedExprOpt != null ? (Expression)parenthesizedExprOpt.clone() : null;			
			return new RunUnitExitModifier(newParenthesizedExprOpt);
		}
	}

	private ExitModifier exitModifierOpt;
	private SettingsBlock settingsBlockOpt;


	public ExitStatement(ExitModifier exitModifierOpt, SettingsBlock settingsBlockOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if( exitModifierOpt != null ) {
			this.exitModifierOpt = exitModifierOpt;
			exitModifierOpt.setParent( this );
		}
		
		if(settingsBlockOpt != null) {
			this.settingsBlockOpt = settingsBlockOpt;
			settingsBlockOpt.setParent(this);
		}
		
	}
	
	public ExitModifier getExitModifierOpt() {
		return exitModifierOpt;
	}
	
	public boolean isExitProgram() {
		return exitModifierOpt != null && exitModifierOpt.isProgramExitModifier();
	}
	
	public Expression getReturnCode() {
		return exitModifierOpt == null ? null : exitModifierOpt.getExpression();
	}
	
	public boolean isExitStack() {
		return exitModifierOpt != null && exitModifierOpt.isStackExitModifier();
	}
	
	public boolean isExitRunUnit() {
		return exitModifierOpt != null && exitModifierOpt.isRunUnitExitModifier();
	}
	
	public String getLabel() {
		return exitModifierOpt == null ? null : exitModifierOpt.getLabel();
	}
	
	public boolean isExitCase() {
		return exitModifierOpt == DefaultExitModifier.CASE;
	}
	
	public boolean isExitIf() {
		return exitModifierOpt == DefaultExitModifier.IF;
	}
	
	public boolean isExitWhile() {
		return exitModifierOpt == DefaultExitModifier.WHILE;
	}
	
	public boolean isExitFor() {
		return exitModifierOpt == DefaultExitModifier.FOR;
	}
	
	public boolean isExitForEach() {
		return exitModifierOpt == DefaultExitModifier.FOREACH;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			if(exitModifierOpt != null) exitModifierOpt.accept(visitor);

			if(settingsBlockOpt != null) {
				settingsBlockOpt.accept(visitor);
			}
		}

		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		ExitModifier newExitModifierOpt = exitModifierOpt != null ? (ExitModifier)exitModifierOpt.clone() : null;
		SettingsBlock newSettingsBlockOpt = settingsBlockOpt != null ? (SettingsBlock)settingsBlockOpt.clone() : null;
		
		return new ExitStatement(newExitModifierOpt, newSettingsBlockOpt, getOffset(), getOffset() + getLength());
	}
	
	public boolean hasSettingsBlock() {
		return settingsBlockOpt != null;
	}
	
	public SettingsBlock getSettingsBlock() {
		return settingsBlockOpt;
	}


}
