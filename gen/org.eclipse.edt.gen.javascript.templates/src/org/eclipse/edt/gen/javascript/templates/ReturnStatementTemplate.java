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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ReturnStatementTemplate extends JavascriptTemplate {

	public void genStatementBody(ReturnStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		FunctionMember func = stmt.getFunctionMember();
		if (func != null) {
			List<FunctionParameter> parms = new ArrayList<FunctionParameter>();
			for (FunctionParameter parm : func.getParameters()) {
				if (CommonUtilities.isBoxedParameterType(parm, ctx))
					parms.add(parm);
			}
			if (!parms.isEmpty()) {
				out.print(eze$$func);
				out.print(".call(");
				out.print(caller);
				out.print(",");
				ctx.foreach(parms, ',', genName, ctx, out);
				out.println(");");
			}
		}
		out.print("return ");
		Expression expr = stmt.getExpression();
		if (expr != null) {
			expr = IRUtils.makeExprCompatibleToType(expr, stmt.getFunctionMember().getType());
			ctx.gen(genExpression, expr, ctx, out, args);
		}
	}
}
