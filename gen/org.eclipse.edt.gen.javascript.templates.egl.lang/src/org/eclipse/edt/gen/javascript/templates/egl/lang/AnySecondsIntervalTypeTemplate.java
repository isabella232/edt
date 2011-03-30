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
import org.eclipse.edt.gen.javascript.templates.IntervalTypeTemplate;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IntervalType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class AnySecondsIntervalTypeTemplate extends IntervalTypeTemplate {

	public void genDefaultValue(IntervalType type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else {
			out.print("new ");
			genRuntimeTypeName(type, ctx, out, RuntimeTypeNameKind.EGLImplementation);
			out.print("(");
			ctx.gen(genConstructorOptions, (EObject) type, ctx, out, args);
			out.print(")");
		}
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// for interval type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
		out.print(getNativeRuntimeOperationName((BinaryExpression) args[0]));
		out.print("(ezeProgram, ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
		out.print(")" + getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
	}
}
