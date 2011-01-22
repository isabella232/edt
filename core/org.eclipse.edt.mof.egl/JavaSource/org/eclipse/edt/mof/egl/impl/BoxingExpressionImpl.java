package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;

public class BoxingExpressionImpl extends ExpressionImpl implements BoxingExpression {
	private static int Slot_expr=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_expr += offset;
	}

	@Override
	public Expression getExpr() {
		return (Expression)slotGet(Slot_expr);
	}

	@Override
	public void setExpr(Expression expr) {
		slotSet(Slot_expr, expr);
	}
	
	@Override
	public Type getType() {
		return getExpr().getType();
	}

	@Override
	public boolean isNullable() {
		return getExpr().isNullable();
	}

	
}