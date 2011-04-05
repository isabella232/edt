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
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Expression;

public class AsExpressionTemplate extends JavaTemplate {

	public void genExpression(AsExpression asExpr, Context ctx, TabbedWriter out, Object... args) {
		if (isHandledByJavaWithoutCast(asExpr.getObjectExpr(), asExpr, ctx)) {
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
		} else if (isHandledByJavaWithCast(asExpr.getObjectExpr(), asExpr, ctx)) {
			out.print("(" + ctx.getPrimitiveMapping(asExpr.getType()) + ")");
			out.print("(");
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
			out.print(")");
		} else if (asExpr.getConversionOperation() != null) {
			// a conversion is required
			out.print(ctx.getNativeImplementationMapping((Classifier) asExpr.getConversionOperation().getContainer()) + '.');
			out.print(asExpr.getConversionOperation().getName());
			out.print("(ezeProgram, ");
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
			ctx.gen(genTypeDependentOptions, asExpr.getEType(), ctx, out, args);
			out.print(")");
		} else if (ctx.mapsToPrimitiveType(asExpr.getEType())) {
			ctx.gen(genRuntimeTypeName, asExpr.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeCast(");
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
			ctx.gen(genTypeDependentOptions, asExpr.getEType(), ctx, out, args);
			out.print(")");
		} else {
			out.print("AnyObject.ezeCast(");
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
			out.print(", ");
			ctx.gen(genRuntimeTypeName, asExpr.getEType(), ctx, out, TypeNameKind.JavaImplementation);
			out.print(".class)");
		}
	}

	private boolean isHandledByJavaWithoutCast(Expression src, AsExpression tgt, Context ctx) {
		// nullables will never be handled by java natives
		if (src.isNullable() || tgt.isNullable())
			return false;
		if (!ctx.mapsToPrimitiveType(src.getType()) || !ctx.mapsToPrimitiveType(tgt.getType()))
			return false;
		String srcString = ctx.getPrimitiveMapping(src.getType());
		String tgtString = ctx.getPrimitiveMapping(tgt.getType());
		// check see to see it is safe to allow java to handle this conversion
		int srcIndex = getJavaAllowedType(srcString);
		int tgtIndex = getJavaAllowedType(tgtString);
		if (srcIndex >= 0 && tgtIndex >= 0 && srcIndex == tgtIndex)
			return true;
		else
			return false;
	}

	private boolean isHandledByJavaWithCast(Expression src, AsExpression tgt, Context ctx) {
		// nullables will never be handled by java natives
		if (src.isNullable() || tgt.isNullable())
			return false;
		if (!ctx.mapsToPrimitiveType(src.getType()) || !ctx.mapsToPrimitiveType(tgt.getType()))
			return false;
		String srcString = ctx.getPrimitiveMapping(src.getType());
		String tgtString = ctx.getPrimitiveMapping(tgt.getType());
		// check see to see it is safe to allow java to handle this conversion
		int srcIndex = getJavaAllowedType(srcString);
		int tgtIndex = getJavaAllowedType(tgtString);
		if (srcIndex >= 0 && tgtIndex >= 0 && srcIndex != tgtIndex)
			return true;
		else
			return false;
	}

	public int getJavaAllowedType(String value) {
		if (value.equals("short"))
			return 0;
		else if (value.equals("int"))
			return 1;
		else if (value.equals("long"))
			return 2;
		else if (value.equals("float"))
			return 3;
		else if (value.equals("double"))
			return 4;
		else
			return -1;
	}
}
