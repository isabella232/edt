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
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Type;

public class ArrayTypeTemplate extends JavaTemplate {

	public Boolean isAssignmentBreakupWanted(ArrayType type, Context ctx, String arg, Type rhsType) {
		// types can override this to cause an compound assignment expression to be broken up
		// the arg contains the operation being asked about
		return true;
	}

	public void genConversionOperation(ArrayType type, Context ctx, TabbedWriter out, AsExpression arg) {
		// check to see if a conversion is required
		if (arg.getConversionOperation() != null) {
			out.print(ctx.getNativeImplementationMapping((Classifier) arg.getConversionOperation().getContainer()) + '.');
			out.print(arg.getConversionOperation().getName());
			out.print("(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out);
			out.print(")");
		} else if (ctx.mapsToPrimitiveType(arg.getEType())) {
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeCast(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out);
			out.print(")");
		} else {
			out.print("EglAny.ezeCast(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			out.print(", ");
			ctx.invoke(genRuntimeClassTypeName, type.getClassifier(), ctx, out, TypeNameKind.JavaImplementation);
			out.print(")");
		}
	}

	public void genRuntimeConstraint(ArrayType generic, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeClassTypeName, generic.getClassifier(), ctx, out, TypeNameKind.EGLImplementation);
		if (!generic.getTypeArguments().isEmpty()) {
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				out.print(", ");
				ctx.invoke(genRuntimeClassTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavaObject);
			}
		}
	}

	public void genRuntimeTypeName(ArrayType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, generic.getClassifier(), ctx, out, TypeNameKind.EGLImplementation);
		if (!generic.getTypeArguments().isEmpty()) {
			out.print("<");
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				ctx.invoke(genRuntimeTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavaObject);
			}
			out.print(">");
		}
	}

	public void genRuntimeClassTypeName(ArrayType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, generic.getClassifier(), ctx, out, TypeNameKind.EGLImplementation);
		out.print(".class");
	}

	public void genJsonTypeDependentOptions(ArrayType type, Context ctx, TabbedWriter out) {
		ctx.invoke(genJsonTypeDependentOptions, type.getElementType(), ctx, out);
	}

	public Integer genFieldTypeClassName(ArrayType type, Context ctx, TabbedWriter out, Integer arrayDimension) {
		return (Integer) ctx.invoke(genFieldTypeClassName, type.getElementType(), ctx, out, new Integer(arrayDimension + 1));
	}
}
