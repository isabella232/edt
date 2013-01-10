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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.GenericType;

public class GenericTypeTemplate extends JavaScriptTemplate {

	public void genRuntimeTypeName(GenericType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, generic.getClassifier(), ctx, out, arg);
		if (!generic.getTypeArguments().isEmpty()) {
			out.print("<");
			for (int i = 0; i < generic.getTypeArguments().size(); i++) {
				ctx.invoke(genRuntimeTypeName, generic.getTypeArguments().get(i), ctx, out, TypeNameKind.JavascriptObject);
			}
			out.print(">");
		}
	}
//	public void genFieldInfoTypeName(GenericType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
//		ctx.invoke(genRuntimeTypeName, generic, ctx, out, arg);
//	}
}
