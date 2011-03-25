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
import org.eclipse.edt.gen.javascript.templates.JavascriptNumberTemplate;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;

public class AnyDecimalTypeTemplate extends JavascriptNumberTemplate {

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("egl.javascript.BigDecimal.prototype.ZERO");
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// for decimal type, always use the runtime
		out.print("(");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
		out.print(").");
		out.print(getNativeRuntimeOperationName((BinaryExpression) args[0]));
		out.print("(");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
		out.print(")" + getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		ctx.gen(genExpression, ((UnaryExpression) args[0]).getExpression(), ctx, out, args);
		// we only need to check for minus sign and if found, we need to change it to .negate()
		if (((UnaryExpression) args[0]).getOperator().equals("-"))
			out.print(".negate()");
	}
	
	public void genDecimalFromSmallintConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDecimalFromIntegerConversion(type, ctx, out, args);
	}
	
	public void genDecimalFromIntConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDecimalFromIntegerConversion(type, ctx, out, args);
	}
	
	public void genDecimalFromIntegerConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertIntegerToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out, args);
		out.print(')');
	}

	
	public void genDecimalFromBigintConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genDecimalFromDecimalConversion(type, ctx, out, args);
	}
	
	public void genDecimalFromSmallfloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertFloatToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out);
		out.print(')');
	}

	public void genDecimalFromFloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertFloatToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out);
		out.print(')');
	}
	

	public void genDecimalFromDecimalConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertDecimalToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out);
		out.print(", egl.createRuntimeException)");
	}
	
	public void genDecimalFromStringConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertStringToDecimal(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		ctx.gen(genTypeDependentOptions, (EObject)expr.getEType(), ctx, out);
		out.print(')');
	}


}
