/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.*;

public class ArrayTypeTemplate extends JavaTemplate {

	public Boolean isAssignmentBreakupWanted(ArrayType type, Context ctx, Expression expr) {
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
			// do a callout to allow certain source types to decide to create a boxing expression
			ctx.invoke(genAsExpressionBoxing, arg.getObjectExpr().getType(), ctx, out, arg);
			// then process the conversion operation
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out);
			out.print(")");
		} else {
			out.print("org.eclipse.edt.runtime.java.eglx.lang.EList.ezeCast(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			out.print(", \"");
			out.print(type.getTypeSignature());
			out.print("\")");
		}
	}
	
	public void genIsaExpression(Type type, Context ctx, TabbedWriter out, IsAExpression arg) {
		if (arg.getObjectExpr().getType().getTypeSignature().equalsIgnoreCase(arg.getEType().getTypeSignature())) {
			if (arg.getObjectExpr().isNullable()) {
				out.print("(");
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
				out.print(" == null ? false : true)");
			} else
				out.print("true");
		} else if (arg.getObjectExpr().getType().getClassifier() != null && arg.getEType().getClassifier() != null
			&& arg.getObjectExpr().getType().getClassifier().getTypeSignature().equalsIgnoreCase(arg.getEType().getClassifier().getTypeSignature())) {
			out.print("false");
		} else {
			out.print("org.eclipse.edt.runtime.java.eglx.lang.EList.ezeIsa(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			out.print(", \"");
			out.print(type.getTypeSignature());
			out.print("\")");
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
		ctx.invoke(genRuntimeTypeName, generic.getClassifier(), ctx, out, arg);
		if (!generic.getTypeArguments().isEmpty()) {
			out.print("<");
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				ctx.invoke(genRuntimeTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavaObject);
				ctx.invoke(genRuntimeTypeExtension, generic.getTypeArguments().get(i), ctx, out);
			}
			out.print(">");
		}
	}

	public void genRuntimeClassTypeName(ArrayType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, generic.getClassifier(), ctx, out, arg);
		out.print(".class");
	}

	public Integer genFieldTypeClassName(ArrayType type, Context ctx, TabbedWriter out, Integer arrayDimension) {
		return (Integer) ctx.invoke(genFieldTypeClassName, type.getElementType(), ctx, out, new Integer(arrayDimension + 1));
	}
}
