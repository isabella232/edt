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
import org.eclipse.edt.mof.egl.GenericType;

public class GenericTypeTemplate extends JavaTemplate {

	public void genRuntimeConstraint(GenericType generic, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeClassTypeName, generic.getClassifier(), ctx, out, TypeNameKind.EGLImplementation);
		if (!generic.getTypeArguments().isEmpty()) {
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				out.print(", ");
				ctx.invoke(genRuntimeClassTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavaObject);
			}
		}
	}

	public void genRuntimeTypeName(GenericType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, generic.getClassifier(), ctx, out, arg);
		if (!generic.getTypeArguments().isEmpty()) {
			out.print("<");
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				ctx.invoke(genRuntimeTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavaObject);
			}
			out.print(">");
		}
	}

	public void genRuntimeClassTypeName(GenericType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, generic.getClassifier(), ctx, out, arg);
		out.print(".class");
	}
}
