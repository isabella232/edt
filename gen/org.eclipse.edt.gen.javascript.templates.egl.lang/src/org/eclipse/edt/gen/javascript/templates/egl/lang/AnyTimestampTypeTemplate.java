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
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavascriptDateTemplate;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class AnyTimestampTypeTemplate extends JavascriptDateTemplate {

	public void genDefaultValue(TimestampType type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else {
			out.print(Constants.JSRT_DATETIME_PKG);
			out.print("currentTimeStamp(");
			genTypeDependentOptions(type, ctx, out, args);
			out.print(")");
		}
	}

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out, Object... args) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		out.print(quoted(pattern));
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// for timestamp type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
		out.print(getNativeRuntimeOperationName((BinaryExpression) args[0]));
		out.print("(ezeProgram, ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
		out.print(")" + getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
	}
	
	public void genTimeStampFromStringConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print(Constants.JSRT_DATETIME_PKG);
		out.print("timeStampValueWithPattern(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", ");
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out);
		out.print(")");
	}
	
	public void genTimeStampFromDateConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print(Constants.JSRT_DATETIME_PKG);
		out.print("extend(");
		out.print(getEglNameForType(expr.getObjectExpr().getType()));
		out.print(", ");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", ");
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out);
		out.print(")");
	}

	public void genTimeStampFromTimeStampConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print(Constants.JSRT_DATETIME_PKG);
		out.print("extend(");
		out.print(getEglNameForType(type));
		out.print(", ");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", ");
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out);
		out.print(", egl.createRuntimeException)");
	}

	public void genStringFromTimeStampConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.egl.core.$StrLib.formatTimeStamp(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", ");
		out.print("egl.egl.core.$StrLib.defaultTimeStampFormat )");
	}
	

}
