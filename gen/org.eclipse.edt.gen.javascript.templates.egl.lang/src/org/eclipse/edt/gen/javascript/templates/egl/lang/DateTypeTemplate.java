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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class DateTypeTemplate extends JavascriptDateTemplate {

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else {
			out.print(Constants.JSRT_DATETIME_PKG);
			out.print("currentDate()");
		}
	}
	
	public void genDateFromStringConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print(Constants.JSRT_DATETIME_PKG);
		out.print("dateValue(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(")");
	}

	public void genDateFromSmallintConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDateFromIntConversion(type, ctx, out, args);
	}
	
	public void genDateFromIntConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print(Constants.JSRT_DATETIME_PKG);
		out.print("dateFromInt(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(")");
	}
	
	public void genDateFromBigintConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDateFromNumberConversion(type, ctx, out, args);
	}
	public void genDateFromFloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDateFromNumberConversion(type, ctx, out, args);
	}
	public void genDateFromSmallfloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDateFromNumberConversion(type, ctx, out, args);
	}

	public void genDateFromDecimalConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDateFromNumberConversion(type, ctx, out, args);
	}

	public void genDateFromNumberConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print(Constants.JSRT_DATETIME_PKG);
		out.print("dateFromInt(");
		Expression intExpr = IRUtils.makeExprCompatibleToType(expr.getObjectExpr(), TypeUtils.Type_INT);
		ctx.gen(genExpression, intExpr, ctx, out);
		out.print(")");
	}

	public void genDateFromTimeStampConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
	}

	public void genStringFromDateConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.egl.core.$StrLib.formatDate(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", ");
		out.print("egl.egl.core.$StrLib.defaultDateFormat ");
	}
	



}
