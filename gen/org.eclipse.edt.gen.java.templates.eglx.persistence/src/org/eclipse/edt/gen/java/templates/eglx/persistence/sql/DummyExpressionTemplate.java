package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.eglx.persistence.sql.DummyExpression;

public class DummyExpressionTemplate extends JavaTemplate {
	
	public void genExpression(DummyExpression expr, Context ctx, TabbedWriter out) {
		out.print(expr.getExpr());
	}
	
	public void genAssignment(DummyExpression lhs, Context ctx, TabbedWriter out, Expression rhs, String operator) {
		genExpression(lhs, ctx, out);
		out.print(' ');
		out.print(operator);
		out.print(' ');
		ctx.invoke(genExpression, rhs, ctx, out);
	}
}
