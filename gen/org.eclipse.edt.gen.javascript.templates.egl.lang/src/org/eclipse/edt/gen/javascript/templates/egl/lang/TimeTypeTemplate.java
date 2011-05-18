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
package org.eclipse.edt.gen.javascript.templates.egl.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class TimeTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print(Constants.JSRT_DTTMLIB_PKG + "currentTime()");
	}

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "L;";
		out.print(signature);
	}

	public void genTimeStampConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
	}

	public void genTimeConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
	}

	public void genStringConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		out.print(Constants.JSRT_DTTMLIB_PKG + "timeValue(");
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		out.print(")");
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// for time type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName((BinaryExpression) args[0]));
		out.print("(ezeProgram, ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
	}
}
