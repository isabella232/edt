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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Type;


public class IntTypeTemplate extends JavascriptNumberTemplate {

	public void genIntFromSmallfloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertFloatToInt(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(')');
	}
	
	public void genIntFromFloatConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertFloatToInt(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(')');
	}
	
	public void genIntFromBigintConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genIntFromDecimalConversion(type, ctx, out, args);
	}

	public void genIntFromDecimalConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertDecimalToInt(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(", egl.createRuntimeException)");
	}
	
	public void genIntFromStringConversion(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression)args[0];
		out.print("egl.convertStringToInt(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(')');
	}

}
