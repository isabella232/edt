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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * GetByPositionStatement AST node type.
 * 
 * For direction and optional position, use the following methods:
 *  - is(...)Direction
 *  - hasPosition()
 *  - getPosition()
 * 
 * For the get by position source, use:
 *  - hasFromExpr()
 *  - getFromExpr()
 *  - hasTargetRecords()
 *  - getTargetRecords()
 * 
 * For the 'getinparent' keyword, use:
 *  - isGetInParent()
 * 
 * For options and clauses, invoke accept() with an IASTVisitor that overrides
 * visit() for the following types:
 *  - ForUpdateClause
 *  - IntoClause 
 *  - UsingPCBClause
 *  - WithInlineDLIClause
 *
 * @author Albert Ho
 * @author David Murray
 */
public class GetByPositionStatement extends Statement {
	
	public static abstract class Direction implements Cloneable{
		
		boolean isRelative() { return false; }
		boolean isAbsolute() { return false; }
		
		boolean hasExpression() { return false; }
		Expression getExpression() { return null; }
		
		void setParent(Node parent) {}
		void accept(IASTVisitor visitor) {};
		
		protected abstract Object clone() throws CloneNotSupportedException;
	}
	
	public static abstract class ExpressionDirection extends Direction {
		Expression expr;
		
		public ExpressionDirection( Expression expr ) {
			this.expr = expr;
		}
		
		boolean hasExpression() {
			return true;
		}
		
		Expression getExpression() {
			return expr;
		}
		
		void setParent(Node parent) {
			expr.setParent(parent);
		}
		
		void accept(IASTVisitor visitor) {
			expr.accept(visitor);
		}
		
		protected abstract Object clone() throws CloneNotSupportedException;
	}
	
	public static class RelativeDirection extends ExpressionDirection {
		public RelativeDirection( Expression expr ) {
			super(expr);
		}
		
		boolean isRelative() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			return new RelativeDirection((Expression)expr.clone());
		}
	}
	
	public static class AbsoluteDirection extends ExpressionDirection {
		public AbsoluteDirection( Expression expr ) {
			super(expr);
		}
		
		boolean isAbsolute() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException{
			return new AbsoluteDirection((Expression)expr.clone());
		}
	}

	/**
 	 * Get by positions statement directions (typesafe enumeration). The other
 	 * directions are ABSOLUTE and RELATIVE. These two are defined in their
 	 * own classes because they can include additional information.
 	 */
	public static class DefaultDirection extends Direction {
		
		private DefaultDirection() {}
		
		public static final DefaultDirection NEXT = new DefaultDirection();//$NON-NLS-1$
		public static final DefaultDirection PREVIOUS = new DefaultDirection();//$NON-NLS-1$
		public static final DefaultDirection FIRST = new DefaultDirection();//$NON-NLS-1$
		public static final DefaultDirection LAST = new DefaultDirection();//$NON-NLS-1$
		public static final DefaultDirection CURRENT = new DefaultDirection();//$NON-NLS-1$
		
		protected Object clone() throws CloneNotSupportedException{
			// TODO correct?
			return this;
		}
	}
	
	public static abstract class GetByPositionSource implements Cloneable {
		boolean hasExpressions() { return false; }
		boolean hasFromExpr() { return false; }
		
		List getExpressions() { return null; }
		Expression getFromExpr() { return null; }
		
		void setParent(Node parent) {}
		void accept(IASTVisitor visitor) {};
		
		protected abstract Object clone() throws CloneNotSupportedException;
	}
	
	public static class ExpressionListSource extends GetByPositionSource {
		List exprList;
		
		public ExpressionListSource( List exprList ) {
			this.exprList = exprList;
		}
		
		boolean hasExpressions() {
			return true;
		}
		
		List getExpressions() {
			return exprList;
		}
		
		void setParent(Node parent) {
			for( Iterator iter = exprList.iterator(); iter.hasNext(); ) {
				((Node) iter.next()).setParent( parent );
			}
		}
		
		void accept(IASTVisitor visitor) {
			for( Iterator iter = exprList.iterator(); iter.hasNext(); ) {
				((Node) iter.next()).accept( visitor );
			}
		}
		
		protected Object clone() throws CloneNotSupportedException{
			ArrayList newExprList = new ArrayList();
			for (Iterator iter = exprList.iterator(); iter.hasNext();) {
				newExprList.add(((Expression)iter.next()).clone());
			}
			return new ExpressionListSource(newExprList);
		}
	}
	
	public static class ExpressionFromResultSetSource extends GetByPositionSource {
		Expression expr;
		Expression fromExpr;
		
		public ExpressionFromResultSetSource( Expression expr, Expression fromExpr ) {
			this.expr = expr;
			this.fromExpr = fromExpr;
		}
		
		boolean hasExpressions() {
			return true;
		}
		
		List getExpressions() {
			return Arrays.asList( new Expression[] { expr } );
		}
		
		boolean hasFromExpr() {
			return true;
		}
		
		Expression getFromExpr() {
			return fromExpr;
		}
		
		void setParent(Node parent) {
			expr.setParent( parent );
			fromExpr.setParent(parent);
		}
		
		void accept(IASTVisitor visitor) {
			expr.accept( visitor );
			fromExpr.accept(visitor);
		}
		
		protected Object clone() throws CloneNotSupportedException{
			return new ExpressionFromResultSetSource((Expression)expr.clone(), (Expression)fromExpr.clone());
		}
	}
	
	public static class FromResultSetSource extends GetByPositionSource {
		Expression expression;
		
		public FromResultSetSource( Expression expression ) {
			this.expression = expression;
		}
		
		boolean hasFromExpr() {
			return true;
		}
		
		Expression getFromExpr() {
			return expression;
		}
		
		void accept(IASTVisitor visitor) {
			expression.accept(visitor);
		}

		protected Object clone() throws CloneNotSupportedException{
			return new FromResultSetSource((Expression)expression.clone());
		}
	}

	private Direction direction;
	private boolean inparent;
	private GetByPositionStatement.GetByPositionSource getByPositionSource;
	private List getByPositionOptions;	// List of Nodes

	public GetByPositionStatement(GetByPositionStatement.Direction direction, Boolean inparentOpt, GetByPositionStatement.GetByPositionSource getByPositionSource, List getByPositionOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.direction = direction;
		direction.setParent(this);
		this.inparent = inparentOpt.booleanValue();
		this.getByPositionSource = getByPositionSource;
		getByPositionSource.setParent(this);
		this.getByPositionOptions = setParent(getByPositionOptions);
	}
	
	public boolean isNextDirection() {
		return direction == DefaultDirection.NEXT;
	}
	
	public boolean isPreviousDirection() {
		return direction == DefaultDirection.PREVIOUS;
	}
	
	public boolean isFirstDirection() {
		return direction == DefaultDirection.FIRST;
	}
	
	public boolean isLastDirection() {
		return direction == DefaultDirection.LAST;
	}
	
	public boolean isCurrentDirection() {
		return direction == DefaultDirection.CURRENT;	
	}
	
	public boolean isRelativeDirection() {
		return direction.isRelative();	
	}
	
	public boolean isAbsoluteDirection() {
		return direction.isAbsolute();
	}
	
	public boolean hasPosition() {
		return direction.hasExpression();		
	}
	
	public Expression getPosition() {
		return direction.getExpression();
	}
	
	public boolean hasFromExpr() {
		return getByPositionSource.hasFromExpr();
	}
	
	public Expression getFromExpr() {
		return getByPositionSource.getFromExpr();
	}

	public boolean hasTargetRecords() {
		return getByPositionSource.hasExpressions();
	}

	/**
	 * @return A List of Expression objects.
	 */
	public List getTargetRecords() {
		return getByPositionSource.getExpressions();	
	}

	public List getTargets() {
		return getTargetRecords();	
	}

	public boolean isGetInParent() {
		return inparent;
	}
	
	public List getGetByPositionOptions() {
		return getByPositionOptions;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			direction.accept(visitor);
			getByPositionSource.accept(visitor);
			acceptChildren(visitor, getByPositionOptions);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return hasTargetRecords() ? getTargetRecords() : Collections.EMPTY_LIST;
	}
	
	protected Object clone() throws CloneNotSupportedException{
		return new GetByPositionStatement((Direction)direction.clone(), new Boolean(inparent), (GetByPositionSource)getByPositionSource.clone(), cloneList(getByPositionOptions), getOffset(), getOffset() + getLength());
	}
}
