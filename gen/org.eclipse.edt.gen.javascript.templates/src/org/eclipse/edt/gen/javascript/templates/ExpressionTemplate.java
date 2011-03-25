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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;

public abstract class ExpressionTemplate extends JavascriptTemplate {

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, expr, ctx, out, args);
	}

	public void genRHSExpression(Expression expr, Context ctx, TabbedWriter out, Object... args) {
		LHSExpr lhsExpr = (LHSExpr)args[0];
		if (!lhsExpr.isNullable() && expr.isNullable()) {
			out.print("(function(x){ return x != null ? (x) : ");
			ctx.gen(genDefaultValue, lhsExpr.getType(), ctx, out);
			out.print("; }");
			out.print('(');
			ctx.gen(genExpression, expr, ctx, out);
			out.print(')');
		}
		else 
			ctx.gen(genExpression, expr, ctx, out);
	}
	
	public void genRHSAssignment(Expression expr, Context ctx, TabbedWriter out, Object... args) {	
		Expression rhs = (Expression)args[0];
		out.print(" = ");
		ctx.gen(genRHSExpression, rhs, ctx, out, expr);			
	}


	public String getOperationName(String op) {
		if (op.equals("=="))
			return "EQUALS";
		else if (op.equals("="))
			return "ASSIGN";
		else if (op.equals("+"))
			return "PLUS";
		else if (op.equals("-"))
			return "MINUS";
		else if (op.equals("/"))
			return "DIVIDE";
		else if (op.equals("*"))
			return "TIMES";
		else if (op.equals("**"))
			return "TIMESTIMES";
		else if (op.equals("&&"))
			return "AND";
		else if (op.equals("||"))
			return "OR";
		else if (op.equals("&"))
			return "BITAND";
		else if (op.equals("|"))
			return "BITOR";
		else if (op.equals("%"))
			return "MOD";
		else
			return "UNKNOWN";
	}
	
	public String stripLeadingZeroes(String value) 
	{
		String minus = "";
		if (value.charAt(0) == '-')
		{
			value = value.substring(1);
			minus = "-";
		}
		while (value.charAt(0) == '0' && value.length()>1)
		{
			value = value.substring(1);
		}
		return minus + value;
	}

}
