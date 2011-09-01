package org.eclipse.edt.compiler.core.ast;

public class WithExpressionClause extends WithClause {

	Expression expr;
	
	public WithExpressionClause(Expression expr, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		this.expr = expr;
		expr.setParent(this);
	}

	public Expression getExpression() {
		return expr;
	}
	
	public void accept(IASTVisitor visitor) {		
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new WithExpressionClause((Expression)expr.clone(), getOffset(), getOffset() + getLength());
	}
}
