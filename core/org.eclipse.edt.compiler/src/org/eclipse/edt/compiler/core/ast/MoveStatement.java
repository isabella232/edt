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
import java.util.Iterator;
import java.util.List;

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
		
		public boolean isByName() {
			return this == BYNAME;
		}
		
		public boolean isByPosition() {
			return this == BYPOSITION;
		}
		
		public boolean isForAll() {
			return this == FORALL;
		}
		
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
	private List moveModifiers;	// List of MoveModifiers

	public MoveStatement(Expression expr, Expression lvalue, List moveModifiers, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.lvalue = lvalue;
		lvalue.setParent(this);
		this.moveModifiers = setMoveModifierParent(moveModifiers);
	}
	
	public Expression getSource() {
		return expr;
	}
	
	public Expression getTarget() {
		return lvalue;
	}
	
	public List getMoveModifiers() {
		return moveModifiers;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			lvalue.accept(visitor);
			acceptMoveModifierChildren(visitor, moveModifiers);
		}
		visitor.endVisit(this);
	}
	
	private List setMoveModifierParent( List children ) {
        for(Iterator iter = children.iterator(); iter.hasNext();) {
            MoveModifier next = (MoveModifier) iter.next();
            next.setParent(this);
        }
        return children;
    }
	
	protected static void acceptMoveModifierChildren(IASTVisitor visitor, List children) {
        for(Iterator iter = children.iterator(); iter.hasNext();) {
            ((MoveModifier) iter.next()).accept(visitor);
        }
    }
	
	protected Object clone() throws CloneNotSupportedException{
		ArrayList newMoveModifiers = new ArrayList();
		for (Iterator iter = moveModifiers.iterator(); iter.hasNext();) {
			newMoveModifiers.add(((MoveModifier)iter.next()).clone());
		}
		return new MoveStatement((Expression)expr.clone(), (Expression)lvalue.clone(), newMoveModifiers, getOffset(), getOffset() + getLength());
	}
}
