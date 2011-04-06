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
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class AnyDecimalTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a specific fixed precision needed
	public void genDefaultValue(FixedPrecisionType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	// this method gets invoked when there is a generic (unknown) fixed precision needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("egl.javascript.BigDecimal.prototype.ZERO");
	}

	public void genDecimalFromSmallintConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDecimalFromIntegerConversion(type, ctx, out, args);
	}

	public void genDecimalFromIntConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDecimalFromIntegerConversion(type, ctx, out, args);
	}

	public void genDecimalFromIntegerConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertIntegerToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject) expr.getEType(), ctx, out, args);
		out.print(")");
	}

	public void genDecimalFromBigintConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDecimalFromDecimalConversion(type, ctx, out, args);
	}

	public void genDecimalFromSmallfloatConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertFloatToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject) expr.getEType(), ctx, out);
		out.print(")");
	}

	public void genDecimalFromFloatConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertFloatToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject) expr.getEType(), ctx, out);
		out.print(")");
	}

	public void genDecimalFromDecimalConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertDecimalToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject) expr.getEType(), ctx, out);
		out.print(", egl.createRuntimeException)");
	}

	public void genDecimalFromStringConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertStringToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject) expr.getEType(), ctx, out);
		out.print(")");
	}

}
