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
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;

public class DictionaryTypeTemplate extends JavaScriptTemplate {

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out) {
		String signature = "y;";
		out.print(signature);
	}

	public void genInstantiation(EGLClass type, Context ctx, TabbedWriter out, Field arg) {
		if (arg.hasSetValuesBlock()) {
			ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavascriptPrimitive);
			out.print("()");
		} else {
			out.print("null");
		}
	}

	public void genInstantiation(EGLClass type, Context ctx, TabbedWriter out) {
		out.print("null");
	}
}
