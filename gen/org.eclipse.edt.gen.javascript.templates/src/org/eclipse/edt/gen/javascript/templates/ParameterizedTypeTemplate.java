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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;

public class ParameterizedTypeTemplate extends JavascriptTemplate {

	public void genAssignment(ParameterizedType type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genAssignment, (Type) type.getParameterizableType(), ctx, out, args);
	}

	public void genRuntimeTypeName(ParameterizedType type, Context ctx, TabbedWriter out, Object... args) {
		ctx.genSuper(genRuntimeTypeName, ParameterizedType.class, type.getParameterizableType(), ctx, out, args);
	}

	public void genConstructorOptions(ParameterizedType type, Context ctx, TabbedWriter out, Object... args) {
		out.print("\"");
		out.print(ctx.getNativeImplementationMapping(type));
		out.print("\", ");
		ctx.gen(genTypeDependentOptions, type, ctx, out, args);
	}

	public void genBinaryExpression(ParameterizedType type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// for interval type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName((BinaryExpression) args[0]));
		out.print("(ezeProgram, ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
	}

	public void genUnaryExpression(ParameterizedType type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, ((UnaryExpression) args[0]).getExpression(), ctx, out, args);
		// we only need to check for minus sign and if found, we need to change it to .negate()
		if (((UnaryExpression) args[0]).getOperator().equals("-"))
			out.print(".negate()");
	}
}
