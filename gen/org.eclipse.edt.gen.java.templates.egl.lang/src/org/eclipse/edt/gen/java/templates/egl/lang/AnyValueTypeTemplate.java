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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class AnyValueTypeTemplate extends JavaTemplate {

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		// check to see if a conversion is required
		if (arg.getConversionOperation() != null) {
			out.print(ctx.getNativeImplementationMapping((Classifier) arg.getConversionOperation().getContainer()) + '.');
			out.print(arg.getConversionOperation().getName());
			out.print("(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			unboxCheck( arg.getObjectExpr(), ctx, out );
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out);
			out.print(")");
		} else if (ctx.mapsToPrimitiveType(arg.getEType())) {
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeCast(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			unboxCheck( arg.getObjectExpr(), ctx, out );
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out);
			out.print(")");
		} else {
			out.print("EglAny.ezeCast(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			unboxCheck( arg.getObjectExpr(), ctx, out );
			out.print(", ");
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.JavaImplementation);
			out.print(".class)");
		}
	}

	private void unboxCheck( Expression expr, Context ctx, TabbedWriter out )
	{
		if ( CommonUtilities.isBoxedOutputTemp( expr, ctx ) )
		{
			out.print(".ezeUnbox()");
		}
	}
}
