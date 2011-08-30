package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.NewExpression;

public class SQLDataSourceTemplate extends JavaTemplate {

	public void genConstructorOptions(EGLClass dataSourceType, Context ctx, TabbedWriter out) {
		// TODO use standardized constant for accessing generated runtime variable names
		out.print(", org.eclipse.edt.javart.Runtime.getRunUnit()");
	}
	
	public void genContainerBasedNewExpression(EGLClass datasource, Context ctx, TabbedWriter out, NewExpression expr) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavaImplementation);
		out.print("(");
		if (expr.getArguments() != null && expr.getArguments().size() > 0) {
			for (Expression argument : expr.getArguments()) {
				ctx.invoke(genExpression, argument, ctx, out);
			}
		}
		genConstructorOptions(datasource, ctx, out);
		out.print(")");
	}
}
