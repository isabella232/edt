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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.ClassTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.GenericType;

public class ListTypeTemplate extends ClassTemplate {

	public void genInstantiation(GenericType type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length == 0 || args[0] == null)
			out.print("null");
		else if (args[0] instanceof Field && ((Field) args[0]).hasSetValuesBlock()) {
			genRuntimeTypeName(type, ctx, out, args);
			out.print("()");
		} else {
			out.print("null");
		}
	}

	public void genRuntimeConstraint(GenericType generic, Context ctx, TabbedWriter out, Object... args) {
		genRuntimeTypeName(generic.getClassifier(), ctx, out, args);
		if (!generic.getTypeArguments().isEmpty()) {
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				out.print(".class, ");
				ctx.gen(genRuntimeTypeName, generic.getTypeArguments().get(i), ctx, out, RuntimeTypeNameKind.JavascriptObject);
			}
		}
		out.print(".class");
	}

	public void genRuntimeTypeName(GenericType type, Context ctx, TabbedWriter out, Object... args) {
		// first print out the list type
		super.genRuntimeTypeName(type, ctx, out, args);
		// only create <list> is there is a list
		if (type.getTypeArguments() != null && type.getTypeArguments().size() > 0) {
			out.print('<');
			for (int i = 0; i < type.getTypeArguments().size(); i++) {
				ctx.gen(genRuntimeTypeName, type.getTypeArguments().get(i), ctx, out, RuntimeTypeNameKind.JavascriptObject);
			}
			out.print('>');
		}
	}
}
