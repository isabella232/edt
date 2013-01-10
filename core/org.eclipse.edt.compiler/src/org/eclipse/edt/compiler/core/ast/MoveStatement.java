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
 * MoveStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class MoveStatement extends Statement {
	
	/**
 	 * Move modifiers (typesafe enumeration). The other move modifier is
 	 * FOR. FOR is defined in its own class because is includes an
 	 * additional Expression.
 	 */
	public static class DefaultMoveModifier extends MoveModifier {
		
		private DefaultMoveModifier() {}
		
		public static final DefaultMoveModifier BYNAME = new DefaultMoveModifier();//$NON-NLS-1$
		public static final DefaultMoveModifier BYPOSITION = new DefaultMoveModifier();//$NON-NLS-1$
		public static final DefaultMoveModifier FORALL = new DefaultMoveModifier();//$NON-NLS-1$
		public static final DefaultMoveModifier WITHV60COMPAT = new DefaultMoveModifier();//$NON-NLS-1$
		
		@Override
		public boolean isByName() {
			return this == BYNAME;
		}
		
		@Override
		public boolean isByPosition() {
			return this == BYPOSITION;
		}
		
		@Override
		public boolean isForAll() {
			return this == FORALL;
		}
		
		@Override
		public boolean isWithV60Compat() {
			return this == WITHV60COMPAT;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			// TODO correct?
			return this;
		}
	}

	private Expression expr;
	private Expression lvalue;
	private MoveModifier moveModifierOpt;

	public MoveStatement(Expression expr, Expression lvalue, MoveModifier moveModifierOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.lvalue = lvalue;
		lvalue.setParent(this);
		this.moveModifierOpt = setMoveModifierParent(moveModifierOpt);
	}
	
	public Expression getSource() {
		return expr;
	}
	
	public Expression getTarget() {
		return lvalue;
	}
	
	public MoveModifier getMoveModifierOpt() {
		return moveModifierOpt;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			lvalue.accept(visitor);
			if (moveModifierOpt != null) {
				moveModifierOpt.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	private MoveModifier setMoveModifierParent( MoveModifier mod ) {
		if (mod != null) {
			mod.setParent(this);
		}
        return mod;
    }
	
	@Override
	protected Object clone() throws CloneNotSupportedException{
		MoveModifier newMod = null;
		if (moveModifierOpt != null) {
			newMod = (MoveModifier)moveModifierOpt.clone();
		}
		return new MoveStatement((Expression)expr.clone(), (Expression)lvalue.clone(), newMod, getOffset(), getOffset() + getLength());
	}
}
