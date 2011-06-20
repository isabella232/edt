package org.eclipse.edt.mof.egl;

public interface TernaryExpression extends MultiOperandExpression {
	Expression getFirst();
	Expression getSecond();
	Expression getThird();
	
	void setFirst(Expression expr);
	void setSecond(Expression expr);
	void setThird(Expression expr);

}
