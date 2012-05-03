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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.SubstringAccess;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;

public class ParameterizableTypeTemplate extends JavaScriptTemplate {

	public void genSubstringAccess(ParameterizableType type, Context ctx, TabbedWriter out, SubstringAccess arg) {
		out.print(ctx.getNativeImplementationMapping(arg.getType()) + ".substring(");
		ctx.invoke(genExpression, arg.getStringExpression(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, arg.getStart(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, arg.getEnd(), ctx, out);
		out.print(")");
	}

	public void genBinaryExpression(ParameterizableType type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for decimal type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
		out.print("(ezeProgram, ");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
	}

	public void genUnaryExpression(ParameterizableType type, Context ctx, TabbedWriter out, UnaryExpression arg) {
		// we only need to check for minus sign and if found, we need to change it to .negate()
		if (arg.getOperator().equals("-")) {
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
			out.print(".negate()");
		} else if (arg.getOperator().equals("~")) {
			out.print("(");
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
			out.print(".negate()");
			out.print(" - 1)");
		} else
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
	}
}
