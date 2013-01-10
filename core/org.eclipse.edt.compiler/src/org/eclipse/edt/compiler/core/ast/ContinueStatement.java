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



/**
 * ContinueStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ContinueStatement extends Statement {
	
	
	public static abstract class ContinueModifier {
		public String getLabel() {return null;}
		public boolean hasLabel() {return false;}
		
		protected abstract Object clone() throws CloneNotSupportedException;
	}
	
	/**
 	 * Keyword continue modifiers (typesafe enumeration).
 	 */
	public static class DefaultContinueModifier extends ContinueModifier {
		
		private DefaultContinueModifier() {}
		
		public static final ContinueModifier FOR = new DefaultContinueModifier();//$NON-NLS-1$
		public static final ContinueModifier FOREACH = new DefaultContinueModifier();//$NON-NLS-1$
		public static final ContinueModifier WHILE = new DefaultContinueModifier();//$NON-NLS-1$
		
		protected Object clone() throws CloneNotSupportedException{
			return this;
		}
	}
	
	public static class LabelContinueModifier extends ContinueModifier {
		private String label;
		
		public LabelContinueModifier(String label) {
			this.label = label;
		}
		
		public boolean hasLabel() {
			return true;
		}
		
		public String getLabel() {
			return label;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			String newLabel = label != null ? new String(label) : null;
			
			return new LabelContinueModifier(newLabel);
		}
	}

	private ContinueModifier continueModifierOpt;

	public ContinueStatement(ContinueModifier continueModifierOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if(continueModifierOpt != null) {
			this.continueModifierOpt = continueModifierOpt;
		}
	}

	public boolean isContinueFor() {
		return continueModifierOpt == DefaultContinueModifier.FOR;
	}
	
	public boolean isContinueForEach() {
		return continueModifierOpt == DefaultContinueModifier.FOREACH;
	}
	
	public boolean isContinueWhile() {
		return continueModifierOpt == DefaultContinueModifier.WHILE;
	}
	
	public boolean isContinueLabel() {
		return continueModifierOpt == null ? false : continueModifierOpt.hasLabel();
	}
	
	public String getLabel() {
		return continueModifierOpt == null ? null : continueModifierOpt.getLabel();
	}
	
	public void accept(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ContinueStatement(continueModifierOpt, getOffset(), getOffset() + getLength());
	}
}
