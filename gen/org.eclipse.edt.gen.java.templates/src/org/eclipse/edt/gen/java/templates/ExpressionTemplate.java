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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;

public abstract class ExpressionTemplate extends JavaTemplate {

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, expr, ctx, out, args);
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
}
