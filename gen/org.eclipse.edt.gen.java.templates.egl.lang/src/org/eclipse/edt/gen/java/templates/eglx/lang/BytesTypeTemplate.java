/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class BytesTypeTemplate extends JavaTemplate {

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaObject);
		out.print("[0]");
	}

	public void genDefaultValue(SequenceType type, Context ctx, TabbedWriter out) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaPrimitive);
		out.print("[");
		out.print(type.getLength());
		out.print("]");
	}

	public void genConstructorOptions(SequenceType type, Context ctx, TabbedWriter out) {
		out.print(type.getLength());
	}

	public void genRuntimeTypeExtension(Type type, Context ctx, TabbedWriter out) {
		out.print("[]");
	}

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (CommonUtilities.isHandledByJavaWithoutCast(arg.getObjectExpr(), arg, ctx)) {
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out, arg.getType());
		} else if (CommonUtilities.isHandledByJavaWithCast(arg.getObjectExpr(), arg, ctx)) {
			out.print("(" + ctx.getPrimitiveMapping(arg.getType()) + ")");
			out.print("(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			out.print(")");
		} else {
			if (arg.getObjectExpr().getType() instanceof FixedPrecisionType) {
				ctx.put(Constants.SubKey_genPrecisionWithTypeDependentOptions, ((FixedPrecisionType)arg.getObjectExpr().getType()).getLength());
			}
			ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
			ctx.remove(Constants.SubKey_genPrecisionWithTypeDependentOptions);
		}
	}

	public void genTypeDependentOptions(SequenceType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		out.print(type.getLength());
		Object precision = ctx.get(Constants.SubKey_genPrecisionWithTypeDependentOptions);
		if ( precision != null ) {
			out.print(", ");
			out.print(precision.toString());
		}
	}

	public void genContainerBasedNewExpression(Type type, Context ctx, TabbedWriter out, Expression arg) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaObject);
		out.print('[');
		if (arg.getType() instanceof SequenceType)
			ctx.invoke(genConstructorOptions, arg.getType(), ctx, out);
		else
			out.print('0');
		out.print(']');
	}
}
