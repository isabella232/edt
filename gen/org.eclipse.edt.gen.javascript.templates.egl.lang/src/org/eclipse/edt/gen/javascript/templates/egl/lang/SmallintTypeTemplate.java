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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Type;

public class SmallintTypeTemplate extends IntTypeTemplate {

	public void genSmallintFromDecimalConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertDecimalToSmallint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", egl.createRuntimeException)");
	}

	public void genSmallintFromBigintConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genSmallintFromDecimalConversion(type, ctx, out, args);
	}

	public void genSmallintFromIntConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertNumberToSmallint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", egl.createRuntimeException)");
	}

	public void genSmallintFromFloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertFloatToSmallint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(')');
	}

	public void genSmallintFromSmallfloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertFloatToSmallint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(')');
	}

	public void genSmallintFromStringConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertStringToSmallint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(')');
	}

}
