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
package org.eclipse.edt.gen.java.templates.egl.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.Type;

public class TimeTypeTemplate extends JavaTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".defaultValue()");
	}

	public void genContainerBasedNewExpression(EGLClass type, Context ctx, TabbedWriter out, NewExpression arg) throws GenerationException {
		ctx.invoke(genRuntimeTypeName, arg.getType(), ctx, out, TypeNameKind.EGLImplementation);
		out.print(".defaultValue(");
		if (arg.getArguments() != null && arg.getArguments().size() > 0) {
			for (Expression argument : arg.getArguments()) {
				ctx.invoke(genExpression, argument, ctx, out);
			}
		} else
			ctx.invoke(genConstructorOptions, arg.getType(), ctx, out);
		out.print(")");
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for time type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
		out.print("(");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
	}
}
