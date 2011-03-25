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
import org.eclipse.edt.mof.egl.Field;

public class ArrayTypeTemplate extends TypeTemplate {

	public void genInstantiation(ArrayType type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length == 0 || args[0] == null)
			out.print("null");
		else if (args[0] instanceof Field && ((Field) args[0]).hasSetValuesBlock()) {
			genRuntimeTypeName(type, ctx, out, TypeNameKind.JavaImplementation);
			out.print("()");
		} else
			out.print("null");
	}

	public void genRuntimeConstraint(ArrayType generic, Context ctx, TabbedWriter out, Object... args) {
		genRuntimeTypeName(generic.getClassifier(), ctx, out, TypeNameKind.EGLImplementation);
		if (!generic.getTypeArguments().isEmpty()) {
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				out.print(".class, ");
				ctx.gen(genRuntimeTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavaObject);
			}
		}
		out.print(".class");
	}

	public void genRuntimeTypeName(ArrayType generic, Context ctx, TabbedWriter out, Object... args) {
		genRuntimeTypeName(generic.getClassifier(), ctx, out, args);
		if (!generic.getTypeArguments().isEmpty()) {
			out.print('<');
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				ctx.gen(genRuntimeTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavaObject);
			}
			out.print('>');
		}
	}
}
